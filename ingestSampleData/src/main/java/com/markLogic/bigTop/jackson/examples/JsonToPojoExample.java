package com.markLogic.bigTop.jackson.examples;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.markLogic.bigTop.jackson.Product;

public class JsonToPojoExample {

    public static void main(String[] args) {
        JsonToPojoExample obj = new JsonToPojoExample();
        obj.run();
    }

    private void run() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Convert JSON string from file to Object
            Product product = mapper.readValue(new File("product.json"), Product.class);
            System.out.println(product);

            // Convert JSON string to Object
            String jsonInString = "{\"name\":\"widget\",\"cost\":0.01,\"price\":4.99,\"properties\":[\"red\",\"small\"]}";
            Product product1 = mapper.readValue(jsonInString, Product.class);
            System.out.println(product1);

            //Pretty print
            String prettyProduct1 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(product1);
            System.out.println(prettyProduct1);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
