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

package com.talkdesk.geo;

import com.talkdesk.geo.Exception.GeoResolverException;
import com.talkdesk.geo.util.DBConnector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Console;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

public class PhoneNumberGeoMap {

    private static Console console = System.console();
    private static Log log = LogFactory.getLog(PhoneNumberGeoMap.class);

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, GeoResolverException {
        // create Options object
        DBConnector connector = new DBConnector();
        GeoCodeResolver resolver = new GeoCodeResolver(connector.getDefaultDBconnection());
        ArrayList list = new ArrayList(Arrays.asList(args));
        Hashtable<String, Double> infoTable = new Hashtable<String, Double>();

        if (list.contains("--same-country-only")) {
            list.remove("--same-country-only");
            infoTable = resolver.buildInfoTable(list, true);
        } else {
            infoTable = resolver.buildInfoTable(list, false);
        }

        String phoneNumber = resolver.getClosestNumber(infoTable);

        System.out.println(phoneNumber);

    }
}
