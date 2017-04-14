package com.markLogic.bigTop.jackson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;

public class LoadSampleData {

	private static final Logger logger = LoggerFactory.getLogger(LoadSampleData.class);

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

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		LoadSampleData obj = new LoadSampleData();
		if (args.length > 0) {
			obj.loadFileSystemData(args[0]);
		} else {
			logger.error("You must specify the path to the data directory");
		}
		client.release();
	}

	private void loadFileSystemData(String dataFolderPath) throws JsonParseException, JsonMappingException, IOException {
		File dataFolder = new File(dataFolderPath);
		File[] listOfCollectionFolders = dataFolder.listFiles();
		for (int i = 0; i < listOfCollectionFolders.length; i++) {
			logger.info("Loading Collection " + listOfCollectionFolders[i].getName());
			File[] productFiles = listOfCollectionFolders[i].listFiles();
			for (int j = 0; j < productFiles.length; j++) {
				InputStream dataIS = new FileInputStream(productFiles[j]);
				Product product = mapper.readValue(dataIS, Product.class);
				writeJacksonDocument(product, productFiles[j].getName(), listOfCollectionFolders[i].getName());
				logger.info("Loaded product: " + productFiles[j].getName());
			}
		}
	}

	private void writeJacksonDocument(Product product, String productFilename, String collection) {
		try {
			DocumentMetadataHandle metaHandle = new DocumentMetadataHandle();
			DocumentCollections collections = metaHandle.getCollections();
			collections.add(BASE_PRODUCT_COLLECTION_URI);
			collections.add(BASE_PRODUCT_COLLECTION_URI + collection);
			DocumentPermissions permissions = metaHandle.getPermissions();
			permissions.add(BIGTOP_ADMIN_ROLE, DocumentMetadataHandle.Capability.UPDATE);
			permissions.add(BIGTOP_READER_ROLE_BASE + collection, DocumentMetadataHandle.Capability.READ);

			// Including metadata in the initial "writeAs" call throws an
			// exception - this is a bug in the Java client.
			JSONDocumentManager jsonDocManager = client.newJSONDocumentManager();
			jsonDocManager.writeAs(BASE_PRODUCT_URI + productFilename, product);
			jsonDocManager.writeMetadata(BASE_PRODUCT_URI + productFilename, metaHandle);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
