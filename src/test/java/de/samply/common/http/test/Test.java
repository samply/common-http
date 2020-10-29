/*
 * Copyright (c) 2017. Medizinische Informatik in der Translationalen Onkologie,
 * Deutsches Krebsforschungszentrum in Heidelberg
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */
package de.samply.common.http.test;

import java.net.MalformedURLException;
import java.util.HashMap;

import com.sun.jersey.api.client.Client;
import de.samply.common.http.HttpConnector;
import de.samply.common.http.HttpConnectorException;

/**
 * Testing, testing, 1...2...3...
 */
public class Test {

    /**
     * @param args
     */
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        HashMap<String, String> config = new HashMap<>();
        config.put(HttpConnector.PROXY_HTTP_HOST, "127.0.0.1");
        config.put(HttpConnector.PROXY_HTTP_PORT, "80");
        config.put(HttpConnector.PROXY_HTTP_USERNAME, "foo");
        config.put(HttpConnector.PROXY_HTTP_PASSWORD, "bar");

        try {
            // Initiate the connector
            HttpConnector hc = new HttpConnector(config);

            // Add http-auth credentials
            hc.addHttpAuth("http://dev02.imbei.uni-mainz.de", "foo", "bar");

            // Define headers
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Accept", "application/xml");

            // Do some action, here:
            // get from a given url with set headers
            // if you want a CloseableHttpResponse instead, just call doAction
            // instead
            HashMap<String, Object> ret = hc
                    .doActionHashMap(
                            "get",
                            "https://dev02.imbei.uni-mainz.de/ldm/ldm/centralsearch/sites/e/teiler/teilerE/uploadStats",
                            headers, null, "application/xml", null, false,
                            false);
            System.out.println("SC = " + ret.get("statuscode"));
            System.out.println("Body = " + ret.get("body"));

            // To get a prepared Jersey client for a certain url
            Client jerseyClient = hc
                    .getJerseyClient("http://osse-register.de", false);

            // to get a prepared ApacheHTTP ClosableHttpClient for a certain url
            org.apache.http.impl.client.CloseableHttpClient apacheClient = hc
                    .getHttpClient("http://osse-register.de");

        } catch (HttpConnectorException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

