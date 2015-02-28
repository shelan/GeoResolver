package com.talkdesk.geo;
/*
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

import com.talkdesk.geo.Exception.GeoResolverException;
import com.talkdesk.geo.util.DBConnector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GeoCodeRepositoryBuilder {

    public static final Log log = LogFactory.getLog(GeoCodeRepositoryBuilder.class);
    Connection connection;

    private Connection connectToDatabase() throws
            ClassNotFoundException, SQLException, IOException {
        DBConnector connector = new DBConnector();
        Connection connection = connector.getDefaultDBconnection();
        if (log.isDebugEnabled())
            log.debug("Connected to Database successfully");
        return connection;
    }

    /**
     * geonameid         : integer id of record in geonames database
     * name              : name of geographical point (utf8) varchar(200)
     * asciiname         : name of geographical point in plain ascii characters, varchar(200)
     * alternatenames    : alternatenames, comma separated, ascii names automatically transliterated, convenience attribute from alternatename table, varchar(10000)
     * latitude          : latitude in decimal degrees (wgs84)
     * longitude         : longitude in decimal degrees (wgs84)
     * feature class     : see http://www.geonames.org/export/codes.html, char(1)
     * feature code      : see http://www.geonames.org/export/codes.html, varchar(10)
     * country code      : ISO-3166 2-letter country code, 2 characters
     * cc2               : alternate country codes, comma separated, ISO-3166 2-letter country code, 60 characters
     * admin1 code       : fipscode (subject to change to iso code), see exceptions below, see file admin1Codes.txt for display names of this code; varchar(20)
     * admin2 code       : code for the second administrative division, a county in the US, see file admin2Codes.txt; varchar(80)
     * admin3 code       : code for third level administrative division, varchar(20)
     * admin4 code       : code for fourth level administrative division, varchar(20)
     * population        : bigint (8 byte int)
     * elevation         : in meters, integer
     * dem               : digital elevation model, srtm3 or gtopo30, average elevation of 3''x3'' (ca 90mx90m) or 30''x30'' (ca 900mx900m) area in meters, integer. srtm processed by cgiar/ciat.
     * timezone          : the timezone id (see file timeZone.txt) varchar(40)
     * modification date : date of last modification in yyyy-MM-dd format
     *
     * @param inputFile
     * @throws IOException
     */
    private void populateData(String inputFile) throws GeoResolverException {

        try {
            if (connection == null)
                connection = connectToDatabase();

            Path file = FileSystems.getDefault().getPath(inputFile);
            Charset charset = Charset.forName("UTF-8");
            BufferedReader inputStream = Files.newBufferedReader(file, charset);
            String buffer;
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement("INSERT INTO geocodes (ID , CITY_NAME, LATITUDE, LONGITUDE, COUNTRY_CODE)" +
                    " VALUES (?,?,?,?,?)");
            while ((buffer = inputStream.readLine()) != null) {
                String[] values = buffer.split("\t");

                preparedStatement.setInt(1, Integer.parseInt(values[0].trim()));
                preparedStatement.setString(2, values[1].trim());
                preparedStatement.setFloat(3, Float.parseFloat(values[4].trim()));
                preparedStatement.setFloat(4, Float.parseFloat(values[5].trim()));
                preparedStatement.setString(5, values[8].trim());

                preparedStatement.execute();

            }
        } catch (SQLException e) {
            throw new GeoResolverException("Error while executing SQL query", e);
        } catch (ClassNotFoundException e) {
            throw new GeoResolverException("Class not found for driver", e);
        } catch (IOException e) {
            throw new GeoResolverException("Error while accessing input file", e);
        }
        log.info("Finished populating Database.");
        //should close all the connections for memory leaks.
    }

    public void populateCountryData(String inputFile) throws GeoResolverException {
        try {
            if (connection == null)
                connection = connectToDatabase();

            Path file = FileSystems.getDefault().getPath(inputFile);
            Charset charset = Charset.forName("UTF-8");
            BufferedReader inputStream = Files.newBufferedReader(file, charset);
            String buffer;
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement("INSERT INTO countrycodes (COUNTRY_CODE , LATITUDE, LONGITUDE, NAME)" +
                    " VALUES (?,?,?,?)");
            while ((buffer = inputStream.readLine()) != null) {
                String[] values = buffer.split(",");

                preparedStatement.setString(1, values[0].trim());
                preparedStatement.setFloat(2, Float.parseFloat(values[1].trim()));
                preparedStatement.setFloat(3, Float.parseFloat(values[2].trim()));
                preparedStatement.setString(4, values[3].trim());

                preparedStatement.execute();

            }
        } catch (SQLException e) {
            throw new GeoResolverException("Error while executing SQL query", e);
        } catch (IOException e) {
            throw new GeoResolverException("Error while accessing input file", e);
        } catch (ClassNotFoundException e) {
            throw new GeoResolverException("Class not found for driver", e);
        }

        log.info("Finished populating Database.");
        //should close all the connections for memory leaks.
    }


    public static void main(String[] args) throws GeoResolverException {
        GeoCodeRepositoryBuilder geoCodeRepositoryBuilder = new GeoCodeRepositoryBuilder();
      //  geoCodeRepositoryBuilder.populateData("/Users/shelan/projects/talkdesk/allCountries.txt");
        geoCodeRepositoryBuilder.populateCountryData("/Users/shelan/Downloads/country-lat-lang.csv");
    }

}
