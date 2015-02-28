package com.talkdesk.geo.util;/*
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {

    private String username;
    private String password;
    private String url;
    private Connection connection;
    private String driver;

    public DBConnector() throws GeoResolverException {
        init();
    }

    private void init() throws GeoResolverException {
        Properties properties = new Properties();

        FileInputStream fileInputStream;

        File file = new File("./conf/geo.properties");

        //if database configuration is not present in conf fallback to default.
        try {
            if (file.exists()) {
                fileInputStream = new FileInputStream("./conf/geo.properties");
            } else {
                ClassLoader classLoader = getClass().getClassLoader();
                fileInputStream = new FileInputStream(classLoader.getResource("geo.properties").getFile());

            }
            properties.load(fileInputStream);

            username = properties.getProperty("username");
            password = properties.getProperty("password");
            driver = properties.getProperty("driver");
            url = properties.getProperty("url");

            fileInputStream.close();
        } catch (IOException e) {
            throw new GeoResolverException("Error while loading property files ", e);
        }
    }

    /**
     * Get Database connection for reuse
     *
     * @param dbUrl
     * @param username
     * @param password
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection getDBConnection(String dbUrl, String username, String password) throws
            GeoResolverException {
        try {
            Class.forName(driver);
            if (connection == null)
                connection = DriverManager.
                        getConnection(dbUrl, username, password);
        } catch (ClassNotFoundException e) {
            throw new GeoResolverException("Could not load driver class", e);
        } catch (SQLException e) {
            throw new GeoResolverException("Error while obtaining the connection", e);
        }
        return connection;
    }

    /**
     * Get the connection for default parameters present in geo.properties
     *
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public Connection getDefaultDBconnection() throws GeoResolverException {
        if (url == null || url.isEmpty()) {
            System.out.println("URL need to be valid and cannot be empty");
            return null;
        }
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        return this.getDBConnection(this.url, this.username, this.password);
    }

}
