package com.markLogic.bigTop.jackson;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;

public class LoadSampleData {

    private static final Logger logger = LoggerFactory.getLogger(LoadSampleData.class);

    private static String DATA_RESOURCE_PATH = "/data/product/";
	private static String BASE_PRODUCT_URI = "/product/";
	private static String BASE_PRODUCT_COLLECTION_URI = "http://com.marklogic/bigtop/product";
	private static String BIGTOP_ADMIN_ROLE = "BigTopAdminRole";
	private static String BIGTOP_READER_ROLE_BASE = "BigTopReaderRole_";
	
    private static DatabaseClient client;
    private static ObjectMapper mapper = new ObjectMapper();

    static {
    	try {
			client = MarkLogicClientFactory.getMarkLogicClient();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public static void main(String[] args) {
        LoadSampleData obj = new LoadSampleData();
        obj.loadResourceData();
    }

    private void loadResourceData() {
        try {
            List<String> collections = ResourceHelper.getResourceFiles(DATA_RESOURCE_PATH);
            for (String collection : collections) {
            	logger.info("Loading Collection: " + collection);
                List<String> productFilenames = ResourceHelper.getResourceFiles(DATA_RESOURCE_PATH + collection);
                for (String productFilename : productFilenames) {
                	InputStream fileIS  = ResourceHelper.getResourceAsStream(DATA_RESOURCE_PATH + collection + "/" + productFilename);
                    Product product = mapper.readValue(fileIS, Product.class);
                    writeJacksonDocument(product, productFilename,collection);
                	logger.info("Loaded product: " + productFilename);
                }
            }
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    	logger.info("Loading Complete");
    }

    private void writeJacksonDocument(Product product, String productFilename, String collection) {
        try {
            DocumentMetadataHandle metaHandle = new DocumentMetadataHandle();
            DocumentCollections collections = metaHandle.getCollections();
            collections.add(BASE_PRODUCT_COLLECTION_URI);
            collections.add(BASE_PRODUCT_COLLECTION_URI + collection);
            DocumentPermissions permissions = metaHandle.getPermissions();
            permissions.add(BIGTOP_ADMIN_ROLE, DocumentMetadataHandle.Capability.UPDATE);
            permissions.add(BIGTOP_READER_ROLE_BASE+collection, DocumentMetadataHandle.Capability.READ);

            // Including metadata in the initial "writeAs" call throws an exception - this is a bug in the Java client.
            JSONDocumentManager jsonDocManager = client.newJSONDocumentManager();
            jsonDocManager.writeAs(BASE_PRODUCT_URI + productFilename, product);
            jsonDocManager.writeMetadata(BASE_PRODUCT_URI + productFilename, metaHandle);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
