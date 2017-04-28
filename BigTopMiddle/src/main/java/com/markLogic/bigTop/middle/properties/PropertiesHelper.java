package com.markLogic.bigTop.middle.properties;

import java.io.InputStream;

public class PropertiesHelper {

    public static InputStream getResourceAsStream( String resource ) {
      final InputStream in = getContextClassLoader().getResourceAsStream( resource );
      return in == null ? getResourceAsStream( resource ) : in;
    }

    private static ClassLoader getContextClassLoader() {
      return Thread.currentThread().getContextClassLoader();
    }

}
