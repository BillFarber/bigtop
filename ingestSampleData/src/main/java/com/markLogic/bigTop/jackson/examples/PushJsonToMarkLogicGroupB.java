package com.markLogic.bigTop.jackson.examples;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.markLogic.bigTop.jackson.Product;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.JacksonDatabindHandle;
import com.marklogic.client.io.StringHandle;

public class PushJsonToMarkLogicGroupB {

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
        PushJsonToMarkLogicGroupB obj = new PushJsonToMarkLogicGroupB();
        obj.run();
    }

    private void run() {
        try {
            Product product = mapper.readValue(new File("product.json"), Product.class);
            writeJacksonDocument(product);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeJacksonDocument(Product product) {
        try {
            DocumentMetadataHandle metaHandle = new DocumentMetadataHandle();
            DocumentCollections collections = metaHandle.getCollections();
            collections.add("product");
            DocumentPermissions permissions = metaHandle.getPermissions();
            permissions.add("BigTopAdminRole", DocumentMetadataHandle.Capability.UPDATE);
            permissions.add("BigTopReaderBRole", DocumentMetadataHandle.Capability.READ);

            String productJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(product);
            TextDocumentManager textDocMgr = client.newTextDocumentManager();
            textDocMgr.writeAs("/product/productB1a.json", metaHandle, productJson);

            // Including metadata throws an exception - this is a bug in the Java client.
            JSONDocumentManager jsonDocManager = client.newJSONDocumentManager();
            jsonDocManager.writeAs("/product/productB1b.json", product);

            StringHandle productHandle = new StringHandle(productJson);
            jsonDocManager.write("/product/productB1c.json", metaHandle, productHandle);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


}
