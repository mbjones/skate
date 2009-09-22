package org.jsc.client;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ClientConstants {
    private static final String BUNDLE_NAME = "org.jsc.client.jscdb-client"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME);

    private ClientConstants() {
    }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
