package de.samply.common.http.test;

import jakarta.ws.rs.client.Client;
import java.net.MalformedURLException;
import java.util.HashMap;

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
                    .getJakartaClient("http://osse-register.de", false);

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

