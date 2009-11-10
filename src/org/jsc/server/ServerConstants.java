package org.jsc.server;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A class to load server-side configurable properties at runtime.
 * @author Matt Jones
 *
 */
public class ServerConstants {
    private static final String BUNDLE_NAME = "org.jsc.server.jscdb-server";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME);

    private ServerConstants() {
    }

    /**
     * Get the value of a property to be used on the server side implementation.
     * @param key the name of the property
     * @return the String value of the property
     */
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
