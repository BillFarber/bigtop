package com.markLogic.bigTop.jackson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ResourceHelper {
    public static List<String> getResourceFiles( String path ) throws IOException {
        List<String> filenames = new ArrayList<String>();

        try(
          InputStream in = getResourceAsStream( path );
          BufferedReader br = new BufferedReader( new InputStreamReader( in ) ) ) {
          String resource;

          while( (resource = br.readLine()) != null ) {
            filenames.add( resource );
          }
        }

        return filenames;
      }

    public static InputStream getResourceAsStream( String resource ) {
      final InputStream in = getContextClassLoader().getResourceAsStream( resource );
      return in == null ? ResourceHelper.class.getResourceAsStream( resource ) : in;
    }

    private static ClassLoader getContextClassLoader() {
      return Thread.currentThread().getContextClassLoader();
    }

}
