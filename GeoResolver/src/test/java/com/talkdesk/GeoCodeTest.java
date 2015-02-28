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

package com.talkdesk;

import com.talkdesk.geo.Exception.GeoResolverException;
import com.talkdesk.geo.GeoCodeResolver;
import com.talkdesk.geo.util.DBConnector;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;

public class GeoCodeTest {

    Connection connection;

    @BeforeClass
    public void init() throws GeoResolverException{
        DBConnector connector = new DBConnector();
        connection = connector.getDefaultDBconnection();
        // System.out.println(con);
    }

    @Test
    public void geoCodeResolving() throws GeoResolverException {
        GeoCodeResolver codeResolver = new GeoCodeResolver(connection);

        ArrayList<String> list = new ArrayList<String>();
        list.add("+12018840000");
        list.add("+15148710000");
        list.add("+14159690000");
        Hashtable<String, Double> infoList = codeResolver.buildInfoTable(list, false);
        String info = codeResolver.getClosestNumber(infoList);
        System.out.println(info);

        list.clear();

        list.add("+351265120000");
        list.add("+351222220000");
        list.add("+351211230000");
        Hashtable<String, Double> infoList2 = codeResolver.buildInfoTable(list, false);
        String info2 = codeResolver.getClosestNumber(infoList2);
        System.out.println(info2);

        list.clear();

        list.add("+33975180000");
        list.add("+441732600000");
        list.add("+14159690000");
        Hashtable<String, Double> infoList3 = codeResolver.buildInfoTable(list, false);
        String info3 = codeResolver.getClosestNumber(infoList3);
        System.out.println(info3);

        list.clear();

        list.add("+351211230000");
        list.add("+18009970000");
        list.add("+448008080000");
        Hashtable<String, Double> infoList4 = codeResolver.buildInfoTable(list, false);
        String info4 = codeResolver.getClosestNumber(infoList4);
        System.out.println(info4);

    }

    @Test
    public void geoCodeResolvingWithFiltering() throws GeoResolverException {

        GeoCodeResolver codeResolver = new GeoCodeResolver(connection);

        ArrayList<String> list = new ArrayList<String>();
        list.add("+12018840000");
        list.add("+15148710000");
        list.add("+14159690000");
        Hashtable<String, Double> infoList = codeResolver.buildInfoTable(list, true);
        String info = codeResolver.getClosestNumber(infoList);
        System.out.println(info);

        ArrayList<String> list2 = new ArrayList<String>();
        list.add("+33975180000");
        list.add("+441732600000");
        list.add("+14159690000");
        Hashtable<String, Double> infoList2 = codeResolver.buildInfoTable(list, true);
        String info2 = codeResolver.getClosestNumber(infoList);
        System.out.println(info2);

        //Assert.assertEquals();
    }

    @Test
    public void geoCodeResolvingforNoCity() throws GeoResolverException {
        GeoCodeResolver codeResolver = new GeoCodeResolver(connection);

        ArrayList<String> list = new ArrayList<String>();
        list.add("+351211230000");
        list.add("+18009970000");
        list.add("+448008080000");
        Hashtable<String, Double> infoList = codeResolver.buildInfoTable(list, true);
        String info = codeResolver.getClosestNumber(infoList);
        System.out.println(info);
    }

}
