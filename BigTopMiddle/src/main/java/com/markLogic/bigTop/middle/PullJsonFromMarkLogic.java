package com.markLogic.bigTop.middle;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.markLogic.bigTop.middle.marklogicDomain.Product;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.JacksonDatabindHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

public class PullJsonFromMarkLogic {

    private static String ML_HOST = "marktom.bigtop.local";
    private static Integer ML_REST_PORT = 8011;
    private static Authentication ML_AUTHENTICATION = Authentication.BASIC;
    private static DatabaseClient client;
    private static ObjectMapper mapper = new ObjectMapper();
    private static SSLContext sslContext = null;

    static {
        try {
            sslContext = SSLContext.getInstance("TLSv1");
            // no client certs
            KeyManager[] keyManagers = null;

            // Trust anyone.
            TrustManager[] trustManagers;
            trustManagers = new TrustManager[] {
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
            };
            sslContext.init(keyManagers, trustManagers, null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }
    
    public PullJsonFromMarkLogic(String username, String password) {
        DatabaseClientFactory.getHandleRegistry().register(JacksonDatabindHandle.newFactory(mapper, Product.class));
        client = DatabaseClientFactory.newClient(ML_HOST, ML_REST_PORT, username, password, ML_AUTHENTICATION, sslContext);
    }

    public Product getProduct(String uri) {
    	return readJacksonDocument(uri);
    }

    public List<String> search(String q) {
        QueryManager queryMgr = client.newQueryManager();
        StringQueryDefinition qd = queryMgr.newStringDefinition();
        qd.setCriteria("");
        SearchHandle sh = new SearchHandle();

        SearchHandle searchResults = queryMgr.search(qd, sh);
        List<String> resultUris = new ArrayList<String>();
        System.out.println("Result Ct: " + searchResults.getTotalResults());
        for (MatchDocumentSummary summary : searchResults.getMatchResults()) {
        	resultUris.add(summary.getUri());
        }

        return resultUris;
    }

    private static Product readJacksonDocument(String uri) {
        JSONDocumentManager jsonDocumentManager = client.newJSONDocumentManager();
        return jsonDocumentManager.readAs(uri, Product.class);
    }


}
