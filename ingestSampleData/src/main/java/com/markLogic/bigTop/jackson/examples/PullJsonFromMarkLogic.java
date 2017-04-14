package com.markLogic.bigTop.jackson.examples;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.markLogic.bigTop.jackson.Product;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.JacksonDatabindHandle;

public class PullJsonFromMarkLogic {

    private static String ML_HOST = "marktom.bigtop.local";
    private static Integer ML_REST_PORT = 8011;
    private static String ML_USERNAME = "bigtopadmin";
    private static String ML_PASSWORD = "december";
    private static Authentication ML_AUTHENTICATION = Authentication.BASIC;
    private static DatabaseClient client;
    private static ObjectMapper mapper = new ObjectMapper();

    static {
        DatabaseClientFactory.getHandleRegistry().register(JacksonDatabindHandle.newFactory(mapper, Product.class));
        client = DatabaseClientFactory.newClient(ML_HOST, ML_REST_PORT, ML_USERNAME, ML_PASSWORD, ML_AUTHENTICATION);
    }

    public static void main(String[] args) {
        PullJsonFromMarkLogic obj = new PullJsonFromMarkLogic();
        obj.run();
    }

    private void run() {
        Product product = readJacksonDocument("/product/productA1b.json");
        System.out.println(product);
    }

    private static Product readJacksonDocument(String uri) {
        JSONDocumentManager jsonDocumentManager = client.newJSONDocumentManager();
        return jsonDocumentManager.readAs(uri, Product.class);
    }


}
