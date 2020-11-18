UNMERGED BRANCH -CHANGES FROM OTHER SAMPLY TEAMS MISSING
# Samply HTTP Library

## General information

LIB to easily use both apache and jersey http connectory

## Usage
```
// define proxy if you have one (you can also use apache configuration instead of map)
HashMap<String, String> config = new HashMap<>();
config.put(HttpConnector.PROXY_HTTP_HOST, "123.123.123.123");
config.put(HttpConnector.PROXY_HTTP_PORT, "80");
config.put(HttpConnector.PROXY_HTTP_USERNAME, "foo");
config.put(HttpConnector.PROXY_HTTP_PASSWORD, "bar");

HttpConnector hc = new HttpConnector(config);
```

```
// Add http-auth credentials
hc.addHttpAuth("http://someserver.somedomain.de", "foo", "bar");
```
```
// Define headers
HashMap<String, String> headers = new HashMap<String, String>();
headers.put("Accept", "application/xml");
```
```
// Do some action, here: GET from a given url with set headers
// if you want a CloseableHttpResponse instead, just call doAction instead
HashMap<String, Object> ret = hc.doActionHashMap(
    "get",
    "https://someserver.somedomain.de/api/v2/uploadStats",
    headers, null, "application/xml", null, false, false);

System.out.println("SC = " + ret.get("statuscode"));
System.out.println("Body = " + ret.get("body"));
```
```
// To get a prepared Jersey client for a certain url
com.sun.jersey.api.client.Client jerseyClient = hc.getJerseyClient("http://osse-register.de", false);
```

```
// to get a prepared ApacheHTTP ClosableHttpClient for a certain url
org.apache.http.impl.client.CloseableHttpClient apacheClient = hc.getHttpClient("http://osse-register.de");
```

## Build

Use maven to build the jar:

```
mvn clean install
```

 ## License
        
 Copyright 2020 The Samply Development Community
        
 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
        
 http://www.apache.org/licenses/LICENSE-2.0
        
 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
