package de.samply.common.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.configuration.Configuration;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.apache.connector.ApacheHttpClientBuilderConfigurator;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.internal.DefaultJacksonJaxbJsonProvider;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connector class to provide apache http connectors and jersey http connectors includes http and
 * https proxy support.
 */
public class HttpConnector {

  /**
   * The password for the https proxy.
   */
  public static final String PROXY_HTTPS_PASSWORD = "proxy.https.password";
  /**
   * The username for the https proxy.
   */
  public static final String PROXY_HTTPS_USERNAME = "proxy.https.username";
  /**
   * The password for the http proxy.
   */
  public static final String PROXY_HTTP_PASSWORD = "proxy.http.password";
  /**
   * The username for the http proxy.
   */
  public static final String PROXY_HTTP_USERNAME = "proxy.http.username";
  /**
   * The port of the https proxy.
   */
  public static final String PROXY_HTTPS_PORT = "proxy.https.port";
  /**
   * The hostname of the https proxy.
   */
  public static final String PROXY_HTTPS_HOST = "proxy.https.host";
  /**
   * The port of the http proxy.
   */
  public static final String PROXY_HTTP_PORT = "proxy.http.port";
  /**
   * The hostname of the http proxy.
   */
  public static final String PROXY_HTTP_HOST = "proxy.http.host";

  /**
   * The protocol of the http proxy.
   */
  public static final String PROXY_HTTP_PROTOCOL = "proxy.http.protocol";
  /**
   * The protocol of the http proxy.
   */
  public static final String PROXY_HTTPS_PROTOCOL = "proxy.https.protocol";

  /**
   * The hostname of the http proxy.
   */
  public static final String USER_AGENT = "http.useragent";
  public static final String PROXY_BYPASS_PRIVATE_NETWORKS = "proxy.bypass.private.networks";
  private static final String PROTOCOL_HTTP = "http";
  private static final String PROTOCOL_HTTPS = "https";
  /**
   * The logger.
   */
  private static final Logger logger = LoggerFactory.getLogger(HttpConnector.class);

  /**
   * The http proxy url.
   */
  private String httpProxyUrl;

  /**
   * The http proxy port.
   */
  private String httpProxyPort;

  /**
   * The https proxy url.
   */
  private String httpsProxyUrl;

  /**
   * The https proxy port.
   */
  private String httpsProxyPort;

  /**
   * The http proxy username.
   */
  private String httpProxyUsername;

  /**
   * The http proxy password.
   */
  private String httpProxyPassword;

  /**
   * The https proxy username.
   */
  private String httpsProxyUsername;

  /**
   * The https proxy password.
   */
  private String httpsProxyPassword;

  /**
   * The http proxy protocol.
   */
  private String httpProxyProtocol = PROTOCOL_HTTP;

  /**
   * The https proxy protocol.
   */
  private String httpsProxyProtocol = PROXY_HTTPS_PROTOCOL;

  /**
   * The Credentials Provider.
   */
  private CredentialsProvider credentialsProvider;

  /**
   * The httpc.
   */
  private CloseableHttpClient httpc;

  /**
   * The httpsc.
   */
  private CloseableHttpClient httpsc;

  /**
   * The JAX-RS client for http requests.
   */
  private Client httpClient;


  /**
   * The JAX-RS client for https requests.
   */
  private Client httpsClient;

  /**
   * The HttpClientBuilder used for http request.
   */
  private HttpClientBuilder httpClientBuilder;

  /**
   * The HttpClientBuilder used for https request.
   */
  private HttpClientBuilder httpsClientBuilder;

  /**
   * The User Agent String that will be sent with each request.
   */
  private String userAgent;

  /**
   * Set to true, if addresses in a private network should use a direct route.
   */
  private boolean bypassProxyForPrivateNetworks = false;

  /**
   * (Optional) list of additional custom headers to set for each client.
   */
  private List<Header> customHeaders;

  private int timeout = 0;

  /**
   * Instantiates a new http connector.
   */
  public HttpConnector() {
    initHttpConnector(null, null, null);
  }

  /**
   * Instantiates a new http connector.
   *
   * @param config the config
   */
  public HttpConnector(Configuration config) {
    initHttpConnector(config, null, null, null);
  }

  /**
   * Instantiates a new http connector.
   *
   * @param config the config
   */
  public HttpConnector(HashMap<String, String> config) {
    initHttpConnector(config, null, null, null);
  }

  /**
   * Instantiates a new http connector with provided Credentials Provider.
   *
   * @param config              the config
   * @param credentialsProvider the preconfigured Credentials Provider
   */
  public HttpConnector(HashMap<String, String> config, CredentialsProvider credentialsProvider) {
    initHttpConnector(config, credentialsProvider, null, null);
  }

  /**
   * Instantiates a new http connector with provided Credentials Provider and a timeout.
   *
   * @param config              the config
   * @param credentialsProvider the preconfigured Credentials Provider
   * @param timeout             timeout in seconds
   */
  public HttpConnector(HashMap<String, String> config, CredentialsProvider credentialsProvider,
      int timeout) {
    initHttpConnector(config, credentialsProvider, timeout, null);
  }

  /**
   * Instantiates new http connector with samply configuration.
   *
   * @param config Samply Configuration
   */
  public HttpConnector(de.samply.common.config.Configuration config) {
    initHttpConnector(config, null, null, null);
  }

  /**
   * Instantiates new http connector with samply configuration.
   *
   * @param config          Samply Configuration
   * @param followRedirects if redirect must be performed or not
   */
  public HttpConnector(de.samply.common.config.Configuration config, boolean followRedirects) {
    initHttpConnector(config, null, null, followRedirects);
  }

  /**
   * Instantiates new http connector with samply proxy configuration.
   *
   * @param config Samply Proxy (no keystore settings!)
   */
  public HttpConnector(de.samply.common.config.Proxy config) {
    initHttpConnector(config, null, null, null);
  }

  private void initHttpConnector(Configuration config, CredentialsProvider credentialsProvider,
      Integer timeout, Boolean followRedirects) {
    setConfiguration(config);
    initHttpConnector(credentialsProvider, timeout, followRedirects);
  }

  private void initHttpConnector(HashMap<String, String> config,
      CredentialsProvider credentialsProvider, Integer timeout, Boolean followRedirects) {
    setConfiguration(config);
    initHttpConnector(credentialsProvider, timeout, followRedirects);
  }

  private void initHttpConnector(de.samply.common.config.Proxy config,
      CredentialsProvider credentialsProvider, Integer timeout, Boolean followRedirects) {
    setConfiguration(config);
    initHttpConnector(credentialsProvider, timeout, followRedirects);
  }

  private void initHttpConnector(de.samply.common.config.Configuration config,
      CredentialsProvider credentialsProvider, Integer timeout, Boolean followRedirects) {
    setConfiguration(config);
    initHttpConnector(credentialsProvider, timeout, followRedirects);
  }


  private void initHttpConnector(CredentialsProvider credentialsProvider, Integer timeout,
      Boolean followRedirects) {

    customHeaders = new ArrayList<>();

    this.credentialsProvider =
        (credentialsProvider != null) ? credentialsProvider : initializeCredentialsProvider();
    if (timeout != null) {
      this.timeout = timeout;
    }

    if (followRedirects != null) {
      initClients(followRedirects);
    } else {
      initClients();
    }

  }


  private void setConfiguration(Configuration config) {

    httpProxyUrl = config.getString(PROXY_HTTP_HOST);
    httpProxyPort = config.getString(PROXY_HTTP_PORT);
    httpsProxyUrl = config.getString(PROXY_HTTPS_HOST);
    httpsProxyPort = config.getString(PROXY_HTTPS_PORT);
    httpProxyProtocol = config.getString(PROXY_HTTP_PROTOCOL);
    if (httpProxyProtocol == null) {
      httpProxyProtocol = PROTOCOL_HTTP;
    }

    httpProxyUsername = config.getString(PROXY_HTTP_USERNAME);
    httpProxyPassword = config.getString(PROXY_HTTP_PASSWORD);
    httpsProxyUsername = config.getString(PROXY_HTTPS_USERNAME);
    httpsProxyPassword = config.getString(PROXY_HTTPS_PASSWORD);
    httpsProxyProtocol = config.getString(PROXY_HTTPS_PROTOCOL);
    if (httpsProxyProtocol == null) {
      httpsProxyProtocol = PROTOCOL_HTTPS;
    }

    if (config.getString(USER_AGENT) != null) {
      userAgent = config.getString(USER_AGENT);
    }

    bypassProxyForPrivateNetworks = config.getBoolean(PROXY_BYPASS_PRIVATE_NETWORKS, false);

  }

  private void setConfiguration(HashMap<String, String> config) {

    httpProxyUrl = config.get(PROXY_HTTP_HOST);
    httpProxyPort = config.get(PROXY_HTTP_PORT);
    httpsProxyUrl = config.get(PROXY_HTTPS_HOST);
    httpsProxyPort = config.get(PROXY_HTTPS_PORT);
    httpProxyProtocol = config.get(PROXY_HTTP_PROTOCOL);
    if (httpProxyProtocol == null) {
      httpProxyProtocol = PROTOCOL_HTTP;
    }

    httpProxyUsername = config.get(PROXY_HTTP_USERNAME);
    httpProxyPassword = config.get(PROXY_HTTP_PASSWORD);
    httpsProxyUsername = config.get(PROXY_HTTPS_USERNAME);
    httpsProxyPassword = config.get(PROXY_HTTPS_PASSWORD);
    httpsProxyProtocol = config.get(PROXY_HTTPS_PROTOCOL);
    if (httpsProxyProtocol == null) {
      httpsProxyProtocol = PROTOCOL_HTTPS;
    }

    if (config.get(USER_AGENT) != null) {
      userAgent = config.get(USER_AGENT);
    }

    try {
      bypassProxyForPrivateNetworks = Boolean
          .parseBoolean(config.get(PROXY_BYPASS_PRIVATE_NETWORKS));
    } catch (Exception e) {
      bypassProxyForPrivateNetworks = false;
    }

  }

  private void setConfiguration(de.samply.common.config.Proxy config) {

    httpProxyUrl = config.getHttp().getUrl().getHost();
    httpProxyPort = config.getHttp().getUrl().getPort() + "";
    httpsProxyUrl = config.getHttps().getUrl().getHost();
    httpsProxyPort = config.getHttps().getUrl().getPort() + "";

    httpProxyUsername = config.getHttp().getUsername();
    httpProxyPassword = config.getHttp().getPassword();
    httpsProxyUsername = config.getHttps().getUsername();
    httpsProxyPassword = config.getHttps().getPassword();

    httpProxyProtocol = config.getHttp().getUrl().getProtocol();
    httpsProxyProtocol = config.getHttps().getUrl().getProtocol();

    if (config.getBypassProxyOnPrivateNetwork() != null) {
      bypassProxyForPrivateNetworks = config.getBypassProxyOnPrivateNetwork();
    } else if (config.getNoProxyHosts() != null && !config.getNoProxyHosts().getHost().isEmpty()) {
      bypassProxyForPrivateNetworks = true;
    }

  }

  private void setConfiguration(de.samply.common.config.Configuration config) {

    if (config != null && config.getProxy() != null) {
      if (config.getProxy().getHttp() != null
          && config.getProxy().getHttp().getUrl() != null) {

        httpProxyProtocol = config.getProxy().getHttp().getUrl().getProtocol();
        httpProxyUrl = config.getProxy().getHttp().getUrl().getHost();
        httpProxyPort = config.getProxy().getHttp().getUrl().getPort() + "";

        httpProxyUsername = config.getProxy().getHttp().getUsername();
        httpProxyPassword = config.getProxy().getHttp().getPassword();

      }

      if (config.getProxy().getHttps() != null
          && config.getProxy().getHttps().getUrl() != null) {
        httpsProxyProtocol = config.getProxy().getHttps().getUrl().getProtocol();
        httpsProxyUrl = config.getProxy().getHttps().getUrl().getHost();
        httpsProxyPort = config.getProxy().getHttps().getUrl().getPort() + "";

        httpsProxyUsername = config.getProxy().getHttps().getUsername();
        httpsProxyPassword = config.getProxy().getHttps().getPassword();

      }

      if (config.getProxy().getBypassProxyOnPrivateNetwork() != null) {
        bypassProxyForPrivateNetworks = config.getProxy().getBypassProxyOnPrivateNetwork();
      } else if (config.getProxy().getNoProxyHosts() != null
          && !config.getProxy().getNoProxyHosts().getHost().isEmpty()) {
        bypassProxyForPrivateNetworks = true;
      }
    }

  }

  /**
   * Initializes the clients If no https proxy is configured, both clients are the same.
   */
  private void initClients() {
    initClients(true);
  }

  private void initClients(boolean followRedirects) {
    httpClientBuilder = initializeHttpClientBuilder(httpProxyProtocol, followRedirects);
    httpc = httpClientBuilder.build();
    httpClient = initJaxRsClient(httpClientBuilder);
    if (httpProxyUrl == null && httpsProxyUrl == null) {
      httpsClientBuilder = httpClientBuilder;
      httpsc = httpc;
      httpsClient = httpClient;
    } else {
      if (httpsProxyUrl == null
          || httpProxyPort.equalsIgnoreCase(httpsProxyPort)
          && httpProxyUrl.equalsIgnoreCase(httpsProxyUrl)) {
        httpsClientBuilder = httpClientBuilder;
        httpsc = httpc;
        httpsClient = httpClient;
      } else {
        httpsClientBuilder = initializeHttpClientBuilder(PROTOCOL_HTTPS, followRedirects);
        httpsc = httpsClientBuilder.build();
        httpsClient = initJaxRsClient(httpsClientBuilder);
      }
    }
  }


  /**
   * Builds a JAX-RS Client based on a HttpClientBuilder.
   *
   * @param clientBuilderConfigurator the builder used for configuring the Client
   */
  private Client initJaxRsClient(HttpClientBuilder clientBuilderConfigurator) {
    org.glassfish.jersey.client.ClientConfig clientConfig =
        new org.glassfish.jersey.client.ClientConfig();
    clientConfig
        .register((ApacheHttpClientBuilderConfigurator) notNeeded -> (clientBuilderConfigurator));
    clientConfig.connectorProvider(new ApacheConnectorProvider());
    return ClientBuilder.newClient(clientConfig);
  }

  /**
   * Closes the clients.
   *
   * @throws IOException ioException
   */
  public void closeClients() throws IOException {
    if (httpc != null) {
      httpc.close();
    }

    if (httpsc != null) {
      httpsc.close();
    }
  }

  /**
   * Returns the result of a request submitted as ClosableHttpResponse NB: Do not forget to close
   * the Response when done with it.
   *
   * @param action                        action method as string: get, post
   * @param url                           The target URL
   * @param headers                       HashMap{@literal <}String,String{@literal >} of headers to
   *                                      be added
   * @param params                        HashMap{@literal <}String,String{@literal >} of params to
   *                                      be added to the URL
   * @param mediaType                     Content-type of the request (defaults to
   *                                      application/json)
   * @param request                       Request string
   * @param preemptiveAuth                Whether or not to send preemptive web authorization
   * @param preemptiveProxyAuthentication Whether or not to send preemptive proxy authorization
   * @return HashMap{@literal <}String, Object{@literal >} Hashmap of: String body, Header[]
   *        headers, int statuscode
   * @throws HttpConnectorException the http connector exception
   */
  @SuppressWarnings("deprecation")
  public CloseableHttpResponse doAction(String action, String url,
      HashMap<String, String> headers, HashMap<String, String> params,
      String mediaType, String request, Boolean preemptiveAuth,
      Boolean preemptiveProxyAuthentication) throws HttpConnectorException {
    return doAction(action, url, headers, params, mediaType, request, preemptiveAuth,
        preemptiveProxyAuthentication, 0);
  }

  /**
   * Returns the result of a request submitted as ClosableHttpResponse NB: Do not forget to close
   * the Response when done with it.
   *
   * @param action                        action method as string: get, post
   * @param url                           The target URL
   * @param headers                       HashMap{@literal <}String,String{@literal >} of headers to
   *                                      be added
   * @param params                        HashMap{@literal <}String,String{@literal >} of params to
   *                                      be added to the URL
   * @param mediaType                     Content-type of the request (defaults to
   *                                      application/json)
   * @param request                       Request string
   * @param preemptiveAuth                Whether or not to send preemptive web authorization
   * @param preemptiveProxyAuthentication Whether or not to send preemptive proxy authorization
   * @param retryCount                    how many times to retry; 0 means no retries
   * @return HashMap{@literal <}String, Object{@literal >} Hashmap of: String body, Header[]
   *        headers, int statuscode
   * @throws HttpConnectorException the http connector exception
   */
  @SuppressWarnings("deprecation")
  public CloseableHttpResponse doAction(String action, String url,
      HashMap<String, String> headers, HashMap<String, String> params,
      String mediaType, String request, Boolean preemptiveAuth,
      Boolean preemptiveProxyAuthentication, int retryCount) throws HttpConnectorException {
    URL targetUrl;
    CloseableHttpClient httpClient;

    if (mediaType == null) {
      mediaType = MediaType.APPLICATION_JSON;
    }

    try {
      targetUrl = new URL(url);
    } catch (MalformedURLException e) {
      StackTraceElement[] stacktrace = e.getStackTrace();
      throw new HttpConnectorException(stacktrace[2]
          + ": Malformed URL provided by " + stacktrace[3]
          + " with URL = " + url);
    }
    int targetPort = targetUrl.getPort();
    HttpHost proxy = null;
    if (targetPort < 0) {
      targetPort = (targetUrl.getProtocol().equalsIgnoreCase(PROTOCOL_HTTPS)) ? 443 : 80;
    }

    if (targetUrl.getProtocol().equalsIgnoreCase(PROTOCOL_HTTPS)) {
      httpClient = getHttpClientForHttps();
      if (preemptiveProxyAuthentication) {
        proxy = getHttpProxy();
      }
    } else {
      httpClient = getHttpClientForHttp();
      if (preemptiveProxyAuthentication) {
        proxy = getHttpsProxy();
      }
    }

    if (httpClient == null) {
      throw new HttpConnectorException(
          "HttpConnector : httpclient is null");
    }

    HttpHost target = new HttpHost(targetUrl.getHost(), targetPort,
        targetUrl.getProtocol());

    AuthCache authCache = new BasicAuthCache();
    boolean addAuthCache = false;
    if (preemptiveProxyAuthentication) {
      if (proxy == null) {
        throw new HttpConnectorException(
            "HttpConnector : preemptiveProxy expected, but proxy is null");
      }

      // Generate BASIC scheme object and add it to the local auth cache
      BasicScheme basicAuthProxy = new BasicScheme(ChallengeState.PROXY);
      authCache.put(proxy, basicAuthProxy);
      logger.debug("AuthCache: proxy host "
          + authCache.get(proxy).toString());
      addAuthCache = true;
    }
    if (preemptiveAuth) {
      // Generate BASIC scheme object and add it to the local auth cache
      BasicScheme basicAuthTarget = new BasicScheme(ChallengeState.TARGET);
      authCache.put(target, basicAuthTarget);
      logger.debug("AuthCache: target host "
          + authCache.get(target).toString());
      addAuthCache = true;
    }

    HttpClientContext localContext = HttpClientContext.create();
    // Add AuthCache to the execution context
    if (addAuthCache) {
      localContext.setAuthCache(authCache);
    }
    if (credentialsProvider != null) {
      localContext.setCredentialsProvider(credentialsProvider);
    }
    localContext.setCookieStore(new BasicCookieStore());

    if (action.equalsIgnoreCase(HttpMethod.PUT)) {
      HttpPut httpPut = new HttpPut(url);
      logger.debug("request line:" + httpPut.getRequestLine());
      StringEntity entity;

      entity = new StringEntity(request, StandardCharsets.UTF_8);
      entity.setContentType(mediaType);

      httpPut.setHeader(HttpHeaders.CONTENT_TYPE, mediaType);
      httpPut.setEntity(entity);

      // does it make sense to add params to a put?
      // if(params != null && !params.isEmpty()) {
      // List<NameValuePair> transparams = new ArrayList<NameValuePair>();
      // for(Entry<String, String> param: params.entrySet())
      // transparams.add(new BasicNameValuePair(param.getKey(),
      // param.getValue()));
      // httpPut.setEntity(new UrlEncodedFormEntity(transparams,
      // Consts.UTF_8));
      // }

      if (headers != null && !headers.isEmpty()) {
        for (Entry<String, String> header : headers.entrySet()) {
          httpPut.addHeader(header.getKey(), header.getValue());
        }
      }

      return executeRequest(target, httpPut, localContext, retryCount);
    } else if (action.equalsIgnoreCase(HttpMethod.POST)) {
      HttpPost httpPost = new HttpPost(url);
      logger.debug("request line:" + httpPost.getRequestLine());
      StringEntity entity;

      entity = new StringEntity(request, StandardCharsets.UTF_8);
      entity.setContentType(mediaType);

      httpPost.setHeader(HttpHeaders.CONTENT_TYPE, mediaType);
      httpPost.setEntity(entity);

      // does it make sense to add params to a put?
      // if(params != null && !params.isEmpty()) {
      // List<NameValuePair> transparams = new ArrayList<NameValuePair>();
      // for(Entry<String, String> param: params.entrySet())
      // transparams.add(new BasicNameValuePair(param.getKey(),
      // param.getValue()));
      // httpPut.setEntity(new UrlEncodedFormEntity(transparams,
      // Consts.UTF_8));
      // }

      if (headers != null && !headers.isEmpty()) {
        for (Entry<String, String> header : headers.entrySet()) {
          httpPost.addHeader(header.getKey(), header.getValue());
        }
      }

      return executeRequest(target, httpPost, localContext, retryCount);
    } else if (action.equalsIgnoreCase(HttpMethod.GET)) {

      if (params != null && !params.isEmpty()) {
        if (!url.endsWith("?")) {
          url += "?";
        }

        List<NameValuePair> transparams = new ArrayList<>();
        for (Entry<String, String> param : params.entrySet()) {
          transparams.add(new BasicNameValuePair(param.getKey(),
              param.getValue()));
        }
        String paramString = URLEncodedUtils.format(transparams,
            StandardCharsets.UTF_8);
        url += paramString;
      }

      HttpGet httpGet = new HttpGet(url);

      if (headers != null && !headers.isEmpty()) {
        for (Entry<String, String> header : headers.entrySet()) {
          httpGet.addHeader(header.getKey(), header.getValue());
        }
      }

      return executeRequest(target, httpGet, localContext, retryCount);
    }

    throw new HttpConnectorException("Action method " + action
        + " is not supported by this connector");
  }

  /**
   * Returns the result of a request submitted as a HashMap with the fields: body: The Response body
   * as String statuscode: The Response statuscode as int headers: The Response headers as
   * Header[].
   *
   * @param action          action method as string: get, post
   * @param url             The target url
   * @param headers         HashMap{@literal <}String,String{@literal >} of headers to be added
   * @param params          HashMap{@literal <}String,String{@literal >} of params to be added to
   *                        the url
   * @param contentType     Content-type of the request (defaults to application/json)
   * @param request         Request string
   * @param preemptiveAuth  Whether or not to send preemptive web authorization
   * @param preemptiveProxy Whether or not to send preemptive proxy authorization
   * @return HashMap{@literal <}String, Object{@literal >} Hashmap of: String body, Header[]
   *        headers, int statuscode
   * @throws HttpConnectorException the http connector exception
   */
  public HashMap<String, Object> doActionHashMap(String action, String url,
      HashMap<String, String> headers, HashMap<String, String> params,
      String contentType, String request, Boolean preemptiveAuth,
      Boolean preemptiveProxy) throws HttpConnectorException {

    return doActionHashMap(action, url, headers, params, contentType, request, preemptiveAuth,
        preemptiveProxy, 0);
  }

  /**
   * Returns the result of a request submitted as a HashMap with the fields: body: The Response body
   * as String statuscode: The Response statuscode as int headers: The Response headers as
   * Header[].
   *
   * @param action          action method as string: get, post
   * @param url             The target url
   * @param headers         HashMap{@literal <}String,String{@literal >} of headers to be added
   * @param params          HashMap{@literal <}String,String{@literal >} of params to be added to
   *                        the url
   * @param contentType     Content-type of the request (defaults to application/json)
   * @param request         Request string
   * @param preemptiveAuth  Whether or not to send preemptive web authorization
   * @param preemptiveProxy Whether or not to send preemptive proxy authorization
   * @param retryCount      how many times to retry; 0 means no retries
   * @return HashMap{@literal <}String, Object{@literal >} Hashmap of: String body, Header[]
   *        headers, int statuscode
   * @throws HttpConnectorException the http connector exception
   */
  public HashMap<String, Object> doActionHashMap(String action, String url,
      HashMap<String, String> headers, HashMap<String, String> params,
      String contentType, String request, Boolean preemptiveAuth,
      Boolean preemptiveProxy, int retryCount) throws HttpConnectorException {
    CloseableHttpResponse response1 = doAction(action, url, headers,
        params, contentType, request, preemptiveAuth, preemptiveProxy, retryCount);
    HashMap<String, Object> response = new HashMap<>();

    try {
      response.put("body", EntityUtils.toString(response1.getEntity()));
      response.put("statuscode", response1.getStatusLine()
          .getStatusCode());
      response.put("headers",
          response1.getHeaders(HttpHeaders.CONTENT_TYPE));
      response1.close();
    } catch (IOException e) {
      StackTraceElement[] stacktrace = e.getStackTrace();
      throw new HttpConnectorException(stacktrace[2]
          + ": IOException, method called by " + stacktrace[3]);
    }
    return response;
  }

  /**
   * Execute request.
   *
   * @param target       the target
   * @param request      the request
   * @param localContext the local context
   * @param retryCount   how many times to retry; 0 means no retries
   * @return the closeable http response
   * @throws HttpConnectorException the http connector exception
   */
  private CloseableHttpResponse executeRequest(HttpHost target,
      HttpRequest request, HttpClientContext localContext, int retryCount)
      throws HttpConnectorException {
    CloseableHttpResponse response;

    DefaultHttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(retryCount,
        false);
    boolean retry = true;
    int retries = 0;
    StackTraceElement[] stacktrace = new StackTraceElement[0];
    while (retry) {
      try {
        response = getHttpClient(target).execute(target, request,
            localContext);
        return response;
      } catch (IOException e) {
        retry = retryHandler.retryRequest(e, ++retries, localContext);

        logger.debug("executeRequest failed, retrying. Attempt #" + retries, e);
        stacktrace = e.getStackTrace();

      }

    }
    throw new HttpConnectorException(stacktrace[2]
        + ": IOException, method called by " + stacktrace[3]);

  }

  /**
   * Adds the trailing slash.
   *
   * @param in the in
   * @return the string
   */
  public String addTrailingSlash(String in) {
    return in.replaceAll("/+$", "") + "/";
  }

  /**
   * Removes the duplicate slashes.
   *
   * @param in the in
   * @return the string
   */
  public String removeDuplicateSlashes(String in) {
    return in.replaceAll("/+", "/");
  }

  private String buildHttpProxyUrl() {

    URIBuilder uriBuilder = new URIBuilder();
    uriBuilder.setScheme(httpProxyProtocol);
    uriBuilder.setHost(httpProxyUrl);
    if (httpProxyPort != null && httpProxyPort.length() > 0) {
      uriBuilder.setPort(new Integer(httpProxyPort));
    }

    return uriBuilder.toString();

  }

  /**
   * Transforms a apache httpclient to a jakarta client also enables POJO MAPPING as feature.
   *
   * @param httpClient              the http client
   * @param failOnUnknownProperties if the client shall fail on deserialisation on unknown or
   *                                unignorable properties
   * @return com.sun.jersey.api.client.Client
   */
  public Client getClient(CloseableHttpClient httpClient, Boolean failOnUnknownProperties) {

    ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
    clientConfig.register(JacksonJsonProvider.class);
    clientConfig.register(new BasicCookieStore());
    clientConfig.register(httpClient);
    if (httpProxyUrl != null && httpProxyUrl.length() > 0) {
      clientConfig.property(ClientProperties.PROXY_URI, buildHttpProxyUrl());
      clientConfig.property(ClientProperties.PROXY_USERNAME, httpProxyUsername);
      clientConfig.property(ClientProperties.PROXY_PASSWORD, httpProxyPassword);
    }

    if (!failOnUnknownProperties) {
      JacksonJsonProvider jacksonJsonProvider = new JacksonJaxbJsonProvider()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      clientConfig.register(jacksonJsonProvider);
    }

    return ClientBuilder.newClient(clientConfig);

  }

  /**
   * Get an jakarta Client.
   *
   * @param httpClient the httpclient
   * @return httpclient
   * @see HttpConnector#getClient(CloseableHttpClient, Boolean) This client will fail on unknown
   *        properties.
   */
  public Client getClient(CloseableHttpClient httpClient) {
    return getClient(httpClient, true);
  }

  /**
   * Returns a jakarta client for http connections. This client will fail on unknown properties.
   *
   * @return jakarta.ws.rs.client.Client
   */
  public Client getJakartaClientForHttp() {
    return getClient(getHttpClientForHttp(), true);
  }

  /**
   * Returns a jakarta client for http connections.
   *
   * @param failOnUnknownProperties if or not to fail on unknown properties
   * @return jakarta.ws.rs.client.Client
   */
  public Client getJakartaClientForHttp(Boolean failOnUnknownProperties) {
    return getClient(getHttpClientForHttp(), failOnUnknownProperties);
  }

  /**
   * Returns a jakarta client for https connections. This client will fail on unknown properties.
   *
   * @return jakarta.ws.rs.client.Client
   */
  public Client getJakartaClientForHttps() {
    return getClient(getHttpClientForHttps(), true);
  }

  /**
   * Returns a jakarta client for https connections.
   *
   * @param failOnUnknownProperties if or not to fail on unknown properties
   * @return jakarta.ws.rs.client.Client
   */
  public Client getJakartaClientForHttps(Boolean failOnUnknownProperties) {
    return getClient(getHttpClientForHttps(), failOnUnknownProperties);
  }

  /**
   * Returns a jakarta client for a given httphost.
   *
   * @param target                  the target
   * @param failOnUnknownProperties if or not to fail on unknown properties
   * @return jakarta.ws.rs.client.Client
   */
  public Client getJakartaClient(HttpHost target, Boolean failOnUnknownProperties) {
    return getClient(getHttpClient(target), failOnUnknownProperties);
  }

  /**
   * Returns a jakarta client for a given httphost. This client will fail on unknown properties
   *
   * @param target the target
   * @return jakarta.ws.rs.client.Client
   */
  public Client getJakartaClient(HttpHost target) {
    return getClient(getHttpClient(target), true);
  }

  /**
   * Returns a jakarta client for a given url or Scheme. This client will fail on unknown properties
   *
   * @param urlOrScheme the url or scheme
   * @return jakarta.ws.rs.client.Client
   */
  public Client getJakartaClient(String urlOrScheme) {
    return getClient(getHttpClient(urlOrScheme), true);
  }

  /**
   * Returns a jakarta client for a given url or Scheme.
   *
   * @param urlOrScheme             the url or scheme
   * @param failOnUnknownProperties if or not to fail on unknown properties
   * @return jakarta.ws.rs.client.Client
   */
  public Client getJakartaClient(String urlOrScheme, Boolean failOnUnknownProperties) {
    return getClient(getHttpClient(urlOrScheme), failOnUnknownProperties);
  }

  /**
   * Returns a jakarta client for a given java.net.URL This client will fail on unknown properties.
   *
   * @param url the url
   * @return jakarta.ws.rs.client.Client
   */
  public Client getJakartaClient(URL url) {
    return getClient(getHttpClient(url), true);
  }

  /**
   * Returns a jakarta client for a given java.net.URL
   *
   * @param url                     the url
   * @param failOnUnknownProperties if or not to fail on unknown properties
   * @return the jakarta client
   */
  public Client getJakartaClient(URL url, Boolean failOnUnknownProperties) {
    return getClient(getHttpClient(url), failOnUnknownProperties);
  }

  /**
   * Return a JAX-RS {@link Client} build with {@link org.glassfish.jersey} and {@link
   * ApacheConnectorProvider}. The client will fail then receiving unknown properties in result
   *
   * @param url The url which the client should connect to
   * @return the JAX-RS Client
   */
  public Client getJaxRsClient(URL url) {
    return getJaxRsClient(url.getProtocol(), true);
  }

  /**
   * Return a JAX-RS {@link Client} build with {@link org.glassfish.jersey} and {@link
   * ApacheConnectorProvider}.
   *
   * @param url                     The url which the client should connect to.
   * @param failOnUnknownProperties Wether to fail or ignore unknown properties in the result.
   * @return the JAX-RS Client
   */
  public Client getJaxRsClient(URL url, Boolean failOnUnknownProperties) {
    return getJaxRsClient(url.getProtocol(), failOnUnknownProperties);
  }

  /**
   * Return a JAX-RS {@link Client} build with {@link org.glassfish.jersey} and {@link
   * ApacheConnectorProvider}. The client will fail then receiving unknown properties in result
   *
   * @param urlOrScheme either the protocol or the url for which the client will be used
   * @return the JAX-RS Client
   */
  public Client getJaxRsClient(String urlOrScheme) {
    return getJaxRsClient(urlOrScheme, true);
  }

  /**
   * Returns a JAX-RS {@link Client} build with {@link org.glassfish.jersey} and {@link
   * ApacheConnectorProvider}.
   *
   * @param urlOrScheme             either the protocol or the url for which the client will be
   *                                used
   * @param failOnUnknownProperties if or not to fail on unknown properties
   * @return the JAX-RS Client
   */
  public Client getJaxRsClient(String urlOrScheme,
      Boolean failOnUnknownProperties) {
    try {
      URL url = new URL(urlOrScheme);
      if (url.getProtocol().equalsIgnoreCase(PROTOCOL_HTTP)) {
        return httpClient;
      } else if (url.getProtocol().equalsIgnoreCase(PROTOCOL_HTTPS)) {
        return httpsClient;
      }
    } catch (MalformedURLException e) {
      if (urlOrScheme.equalsIgnoreCase(PROTOCOL_HTTP)) {
        return httpClient;
      } else if (urlOrScheme.equalsIgnoreCase(PROTOCOL_HTTPS)) {
        return httpsClient;
      }
    }
    return null;
  }

  /**
   * Returns the proxy needed for the given target.
   *
   * @param target A web-url / link
   * @return HttpHost The proxy to be used
   * @throws MalformedURLException string not well formed
   */
  public HttpHost getProxy(String target) throws MalformedURLException {
    URL targetUrl = new URL(target);

    return (targetUrl.getProtocol().equalsIgnoreCase(PROTOCOL_HTTPS)) ? getHttpsProxy()
        : getHttpProxy();
  }

  /**
   * Returns the https proxy.
   *
   * @return HttpHost The https proxy to be used
   */
  private HttpHost getHttpsProxy() {
    if (httpsProxyUrl == null || httpsProxyUrl.equals("")) {
      return null;
    }

    int myHttpsProxyPort;
    try {
      myHttpsProxyPort = Integer.parseInt(httpsProxyPort);
      if (myHttpsProxyPort < 0) {
        myHttpsProxyPort = 443;
      }
    } catch (Exception e) {
      myHttpsProxyPort = 443;
    }
    return new HttpHost(httpsProxyUrl, myHttpsProxyPort);
  }

  /**
   * Returns the http proxy.
   *
   * @return HttpHost The http proxy to be used
   */
  private HttpHost getHttpProxy() {
    if (httpProxyUrl == null || httpProxyUrl.equals("")) {
      return null;
    }

    int myHttpProxyPort;
    try {
      myHttpProxyPort = Integer.parseInt(httpProxyPort);
      if (myHttpProxyPort < 0) {
        myHttpProxyPort = 80;
      }
    } catch (Exception e) {
      myHttpProxyPort = 80;
    }
    return new HttpHost(httpProxyUrl, myHttpProxyPort);
  }

  /**
   * Gets the Credentials Provider.
   *
   * @return the Credentials Provider
   */
  public CredentialsProvider getCp() {
    return credentialsProvider;
  }

  /**
   * Sets the Credentials Provider and reinitialized the clients.
   *
   * @param credentialsProvider the new Credentials Provider.
   */
  public void setCp(CredentialsProvider credentialsProvider) {
    this.credentialsProvider = credentialsProvider;
    initClients();
  }

  /**
   * Get the current User Agent.
   *
   * @return the userAgent
   */
  public String getUserAgent() {
    return userAgent;
  }

  /**
   * Set the User Agent.
   *
   * @param userAgent the userAgent to set
   */
  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
    initClients();
  }

  /**
   * Returns the CloseableHttpClient based on a HttpHost.
   *
   * @param target HttpHost
   * @return CloseableHttpClient
   */
  public CloseableHttpClient getHttpClient(HttpHost target) {
    if (target == null) {
      return null;
    }

    return (target.getSchemeName().startsWith(PROTOCOL_HTTPS)) ? getHttpClientForHttps()
        : getHttpClientForHttp();
  }

  /**
   * Returns the CloseableHttpClient based on a given URL.
   *
   * @param url URL
   * @return CloseableHttpClient
   */
  public CloseableHttpClient getHttpClient(URL url) {
    if (url == null) {
      return null;
    }

    return (url.getProtocol().equalsIgnoreCase(PROTOCOL_HTTPS)) ? getHttpClientForHttps()
        : getHttpClientForHttp();
  }

  /**
   * Returns the CloseableHttpClient based on a given url or scheme.
   *
   * @param urlOrScheme The web-link or scheme (http, https)
   * @return CloseableHttpClient
   */
  public CloseableHttpClient getHttpClient(String urlOrScheme) {
    URL target;
    try {
      target = new URL(urlOrScheme);
    } catch (MalformedURLException e) {
      if (urlOrScheme.equalsIgnoreCase(PROTOCOL_HTTPS)) {
        return getHttpClientForHttps();
      } else if (urlOrScheme.equalsIgnoreCase(PROTOCOL_HTTP)) {
        return getHttpClientForHttp();
      } else {
        return null;
      }
    }

    return getHttpClient(target);
  }

  /**
   * Returns the CloseableHttpClient (with http proxy if proxy is used).
   *
   * @return CloseableHttpClient
   */
  @Deprecated
  public CloseableHttpClient getHttpc() {
    return httpc;
  }

  /**
   * Returns the CloseableHttpClient (with http proxy if proxy is used).
   *
   * @return CloseableHttpClient
   */
  public CloseableHttpClient getHttpClientForHttp() {
    return httpc;
  }

  /**
   * Returns the CloseableHttpClient (with https proxy if proxy is used).
   *
   * @return CloseableHttpClient
   */
  @Deprecated
  public CloseableHttpClient getHttpsc() {
    return httpsc;
  }

  /**
   * Returns the CloseableHttpClient (with https proxy if proxy is used).
   *
   * @return CloseableHttpClient
   */
  public CloseableHttpClient getHttpClientForHttps() {
    return httpsc;
  }

  /**
   * Initialize closeable http client.
   *
   * @param protocol        the protocol
   * @param followRedirects if redirect must be performed or not
   * @return the closeable http client
   */
  private HttpClientBuilder initializeHttpClientBuilder(String protocol,
      boolean followRedirects) {
    if (userAgent != null && userAgent.length() > 0) {
      //logger.debug("Setting user agent header to: " + userAgent);
      Header header = new BasicHeader(HttpHeaders.USER_AGENT, userAgent);
      Iterator<Header> iterator = customHeaders.iterator();

      while (iterator.hasNext()) {
        Header h = iterator.next();
        if (h.getName().equalsIgnoreCase(header.getName())) {
          iterator.remove();
        }
      }
      customHeaders.add(header);
    }

    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    // Increase max total connection to 200
    cm.setMaxTotal(200);
    // Increase default max connection per route to 20
    cm.setDefaultMaxPerRoute(20);

    Lookup<AuthSchemeProvider> authProviders = RegistryBuilder
        .<AuthSchemeProvider>create()
        .register(AuthSchemes.BASIC, new BasicSchemeFactory(StandardCharsets.UTF_8))
        .register(AuthSchemes.NTLM, new NTLMSchemeFactory())
        .register(AuthSchemes.DIGEST, new DigestSchemeFactory(StandardCharsets.UTF_8))
        .register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory())
        .register(AuthSchemes.KERBEROS, new KerberosSchemeFactory())
        .build();

    HttpRoutePlanner routePlanner;
    RequestConfig.Builder configBuilder = RequestConfig.custom()
        .setRedirectsEnabled(followRedirects);
    if (timeout > 0) {
      configBuilder.setConnectTimeout(timeout * 1000)
          .setConnectionRequestTimeout(timeout * 1000)
          .setSocketTimeout(timeout * 1000);
    }
    RequestConfig config = configBuilder.build();

    HttpHost proxy = (protocol.equalsIgnoreCase(PROTOCOL_HTTP)) ? getHttpProxy() : getHttpsProxy();
    if (proxy != null) {
      routePlanner = new DefaultProxyRoutePlanner(proxy) {
        @Override
        public HttpRoute determineRoute(final HttpHost host,
            final HttpRequest request, final HttpContext context)
            throws HttpException {
          String hostname = host.getHostName();
          if (isLoopbackAddress(hostname) || (bypassProxyForPrivateNetworks && isLocalAddress(
              hostname))) {
            // Return direct route
            return new HttpRoute(host);
          }
          return super.determineRoute(host, request, context);
        }
      };
      return HttpClients.custom()
          .setDefaultAuthSchemeRegistry(authProviders)
          .setDefaultCredentialsProvider(credentialsProvider)
          .setDefaultHeaders(customHeaders)
          .setRoutePlanner(routePlanner)
          .setProxy(proxy)
          .setConnectionManager(cm)
          .setDefaultRequestConfig(config);
    } else {
      return HttpClients.custom()
          .setDefaultAuthSchemeRegistry(authProviders)
          .setDefaultHeaders(customHeaders)
          .setDefaultCredentialsProvider(credentialsProvider)
          .setConnectionManager(cm)
          .setDefaultRequestConfig(config);
    }
  }

  /**
   * Adds the http auth.
   *
   * @param url      the url
   * @param username the username
   * @param password the password
   * @throws MalformedURLException the malformed url exception
   */
  public void addHttpAuth(String url, String username, String password)
      throws MalformedURLException {
    URL targetUrl;
    int targetPort;
    targetUrl = new URL(url);
    targetPort = targetUrl.getPort();

    credentialsProvider.setCredentials(new AuthScope(targetUrl.getHost(), targetPort),
        new UsernamePasswordCredentials(username, password));
    initClients();
  }

  /**
   * Add Credentials for a given Auth Scope.
   *
   * @param authScope   the auth scope
   * @param credentials the credentials
   */
  public void addCredentials(AuthScope authScope, Credentials credentials) {
    credentialsProvider.setCredentials(authScope, credentials);
    initClients();
  }

  /**
   * Initialize credentials provider.
   *
   * @return the credentials provider
   */
  private CredentialsProvider initializeCredentialsProvider() {
    int myHttpProxyPort;
    int myHttpsProxyPort;

    try {
      myHttpProxyPort = Integer.parseInt(httpProxyPort);
    } catch (Exception e) {
      myHttpProxyPort = 80;
    }

    try {
      myHttpsProxyPort = Integer.parseInt(httpsProxyPort);
    } catch (Exception e) {
      myHttpsProxyPort = 443;
    }

    CredentialsProvider credsProvider = new BasicCredentialsProvider();

    // if (overrideAuthScheme) {
    // credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST,
    // AuthScope.ANY_PORT, AuthScheme.BASIC.toString()), new
    // UsernamePasswordCredentials(csUsername, csPass));
    // } else {
    // credsProvider.setCredentials(new AuthScope(csURL.getHost(), csPort,
    // AuthScheme.BASIC.toString()), new
    // UsernamePasswordCredentials(csUsername, csPass));
    // }

    if (httpProxyUrl != null && httpProxyUrl.length() > 0) {
      credsProvider.setCredentials(new AuthScope(httpProxyUrl,
              myHttpProxyPort, AuthScope.ANY_REALM, AuthSchemes.BASIC),
          new UsernamePasswordCredentials(httpProxyUsername,
              httpProxyPassword));

    }

    if (httpsProxyUrl != null && httpsProxyUrl.length() > 0) {
      credsProvider.setCredentials(new AuthScope(httpsProxyUrl,
              myHttpsProxyPort, AuthScope.ANY_REALM, AuthSchemes.BASIC),
          new UsernamePasswordCredentials(httpsProxyUsername,
              httpsProxyPassword));
    }

    logger.debug("CredentialsProvider initialized");
    return credsProvider;
  }

  /**
   * Check if a given IPv4 Address in its string representation belongs to a private network
   * (according to RFC 1918). Use to check if proxies should be bypassed.
   *
   * @param ipAddress the IP address to check as a String
   * @return true if it belongs to a private network false otherwise
   */
  private boolean isLocalAddress(String ipAddress) {
    try {
      InetAddress ad = InetAddress.getByName(ipAddress);
      return ad.isSiteLocalAddress();
    } catch (UnknownHostException e) {
      logger
          .trace("Caught an Exception while trying to check if an address is local. Return false.");
      return false;
    }
  }

  /**
   * Check if a given IPv4 Address in its string representation is a loopback address. Use to check
   * if proxies should be bypassed.
   *
   * @param ipAddress the IP address to check as a String
   * @return true if it is a loopback address false otherwise
   */
  private boolean isLoopbackAddress(String ipAddress) {
    try {
      InetAddress ad = InetAddress.getByName(ipAddress);
      return ad.isLoopbackAddress();
    } catch (UnknownHostException e) {
      logger.trace(
          "Caught an Exception while trying to check if an address is a loopback address. "
              + "Return false.");
      return false;
    }
  }


  public List<Header> getCustomHeaders() {
    return customHeaders;
  }

  public void setCustomHeaders(List<Header> customHeaders) {
    this.customHeaders = customHeaders;
    initClients();
  }

  /**
   * Add custom Header and init client.
   *
   * @param customHeader the custom header
   */
  public void addCustomHeader(Header customHeader) {
    Iterator<Header> iterator = customHeaders.iterator();

    while (iterator.hasNext()) {
      Header header = iterator.next();
      if (header.getName().equalsIgnoreCase(customHeader.getName())) {
        iterator.remove();
      }

    }
    customHeaders.add(customHeader);
    initClients();
  }

  public void addCustomHeader(String key, String value) {
    addCustomHeader(new BasicHeader(key, value));
  }
}
