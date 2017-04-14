package com.markLogic.bigTop.jackson.examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.markLogic.bigTop.jackson.Product;

public class PojoToJsonExample {
    public static void main(String[] args) {
        PojoToJsonExample obj = new PojoToJsonExample();
        obj.run();
    }

    private void run() {
        ObjectMapper mapper = new ObjectMapper();

        Product product = createProductA1();

        try {
            // Convert object to JSON string and save into a file directly
            mapper.writeValue(new File("product.json"), product);

            // Convert object to JSON string
            String jsonInString = mapper.writeValueAsString(product);
            System.out.println(jsonInString);

            // Convert object to JSON string and pretty print
            jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(product);
            System.out.println(jsonInString);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Product createProductA1() {
        Product product = new Product();
        product.setName("widget");
        product.setCost(0.01f);
        product.setPrice(4.99f);
        List<String> properties = new ArrayList<String>();
        properties.add("red");
        properties.add("small");
        product.setProperties(properties);
        return product;

    }

}
