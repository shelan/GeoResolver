package com.talkdesk.geo;/*
 * Copyright (c) 2015 Shelan Perera
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import com.talkdesk.geo.Exception.GeoResolverException;
import com.talkdesk.geo.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;

public class GeoCodeResolver {

    Connection connection;

    public static final Log log = LogFactory.getLog(GeoCodeResolver.class);

    public GeoCodeResolver(Connection connection) {
        this.connection = connection;
    }

    public String getTownFromNumber(String phoneNumber) throws GeoResolverException {
        Phonenumber.PhoneNumber number = null;
        try {
            number = PhoneNumberUtil.getInstance().parseAndKeepRawInput(phoneNumber,
                    Constants.DEFAULT_COUNTRY);
        } catch (NumberParseException e) {
            throw new GeoResolverException("Error while parsing phone number", e);
        }
        String desc = PhoneNumberOfflineGeocoder.getInstance().getDescriptionForNumber(
                number, new Locale("en", "US"));
        String country = PhoneNumberUtil.getInstance().getRegionCodeForNumber(number);
        String[] location_array = desc.split(",");
        if (location_array != null)
            return country + ":" + location_array[0];
        else
            return country;
    }

    public GeoInfo getLocationInfo(String townName, String countryCode) throws GeoResolverException {
        String query;
        PreparedStatement statement;
        GeoInfo geoInfo = null;
        try {
            if (townName != null && countryCode != null) {
                query = "SELECT * FROM GEOCODES WHERE CITY_NAME = ? AND COUNTRY_CODE = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, townName);
                statement.setString(2, countryCode);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    double latitude = resultSet.getDouble("LATITUDE");
                    double longitude = resultSet.getDouble("LONGITUDE");
                    String country_code = resultSet.getString("COUNTRY_CODE");
                    geoInfo = new GeoInfo(townName, latitude, longitude, country_code);
                }
                //countryCode cannot be null so when we cannot get info based on town we fall back to town
            }
            if (countryCode != null && geoInfo == null) {
                query = "SELECT * FROM COUNTRYCODES WHERE COUNTRY_CODE = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, countryCode);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    double latitude = resultSet.getDouble("LATITUDE");
                    double longitude = resultSet.getDouble("LONGITUDE");
                    String country_code = resultSet.getString("COUNTRY_CODE");
                    geoInfo = new GeoInfo(townName, latitude, longitude, country_code);
                    //
                }
            }

        } catch (SQLException e) {
            throw new GeoResolverException("Error while executing query to match phone number's details", e);
        }
        return geoInfo;
    }

    public double calculateDistance(GeoInfo firstTown, GeoInfo secondTown) throws GeoResolverException {
        if (firstTown == null || secondTown == null) {
            log.error("Town information cannot be determined");
            throw new GeoResolverException("first town = " + firstTown +
                    "second town = " + secondTown);
        }
        LatLng point1 = new LatLng(firstTown.getLatitude(), firstTown.getLongitude());
        LatLng point2 = new LatLng(secondTown.getLatitude(), secondTown.getLongitude());
        return LatLngTool.distance(point1, point2, LengthUnit.KILOMETER);
    }

    public Hashtable<String, Double> buildInfoTable(ArrayList<String> phoneNumberList, boolean countryFilteringEnabled)
            throws GeoResolverException {
        Hashtable infoTable = new Hashtable();
        //this is the reference no for which we will calculate cost against
        String firstNo = phoneNumberList.get(0);

        String firstNotownDesc = getTownFromNumber(firstNo);
        String[] information = firstNotownDesc.split(":");

        String firstNoTown = null;
        String firstNoCountryCode;


        if (information.length == 2) {
            firstNoCountryCode = firstNotownDesc.split(":")[0];
            firstNoTown = firstNotownDesc.split(":")[1];
        }//when only country code is available
        else {
            firstNoCountryCode = firstNotownDesc.split(":")[0];
        }

        GeoInfo firstTownInfo = getLocationInfo(firstNoTown, firstNoCountryCode);

        for (int i = 1; i < phoneNumberList.size(); i++) {
            String phoneNo = phoneNumberList.get(i);
            String townDesc = getTownFromNumber(phoneNo);
            //town description will be town:countryCode
            String[] townInfo = townDesc.split(":");

            String town = null;
            String countryCode;

            if (townInfo.length == 2) {
                countryCode = townInfo[0];
                town = townInfo[1];
            } else {
                countryCode = townInfo[0];
            }
            //if the phone no is not in the same location we filter out.
            if (countryFilteringEnabled && !firstNoCountryCode.equals(countryCode)) {
                continue;
            }
            GeoInfo secondTownInfo = getLocationInfo(town, countryCode);
            Double distance = calculateDistance(firstTownInfo, secondTownInfo);
            infoTable.put(phoneNo, distance);

        }
        return infoTable;
    }

    public String getClosestNumber(Hashtable<String, Double> numberList) throws GeoResolverException {
        String closest = "";
        Double lowestDistance = Double.MAX_VALUE;

        for (String key : numberList.keySet()) {
            Double currentDistance = numberList.get(key);
            if (currentDistance < lowestDistance) {
                lowestDistance = currentDistance;
                closest = key;
            }
        }

        return !closest.isEmpty() ? getTownFromNumber(closest) + " - " + closest : null;
    }

}
