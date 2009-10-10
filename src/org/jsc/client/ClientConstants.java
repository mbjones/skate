package org.jsc.client;

import java.util.HashMap;

public class ClientConstants {
    private static final String BUNDLE_NAME = "org.jsc.client.jscdb-client"; //$NON-NLS-1$

    //private static final String CLIENT_PAYPAL_URL="https://www.paypal.com/cgi-bin/webscr";
    private static final String CLIENT_PAYPAL_URL="https://www.sandbox.paypal.com/cgi-bin/webscr";
    // private static final String CLIENT_MERCHANT_ID="4STDKBE3NBV64";
    private static final String CLIENT_MERCHANT_ID="48H3N28JSP2VN";
    // private static final String CLIENT_PAYPAL_CANCEL_URL="http://reg.juneauskatingclub.org";
    private static final String CLIENT_PAYPAL_CANCEL_URL="http://reg.juneauskatingclub.org/jscdbtest";
    // private static final String CLIENT_PAYPAL_RETURN_URL="http://reg.juneauskatingclub.org";
    private static final String CLIENT_PAYPAL_RETURN_URL="http://reg.juneauskatingclub.org/jscdbtest";
    private static final String CLIENT_PAYPAL_HEADER_IMAGE="http://juneauskatingclub.org/sites/all/themes/jsc/images/salamander1/jsc-header-bkg-paypal.png";

//    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
//            .getBundle(BUNDLE_NAME);
    private static HashMap<String, String> constants = new HashMap<String, String>();
    static {
        constants.put("CLIENT_PAYPAL_URL", CLIENT_PAYPAL_URL);
        constants.put("CLIENT_MERCHANT_ID", CLIENT_MERCHANT_ID);
        constants.put("CLIENT_PAYPAL_CANCEL_URL", CLIENT_PAYPAL_CANCEL_URL);
        constants.put("CLIENT_PAYPAL_RETURN_URL", CLIENT_PAYPAL_RETURN_URL);
        constants.put("CLIENT_PAYPAL_HEADER_IMAGE", CLIENT_PAYPAL_HEADER_IMAGE);
    }
    
    private  ClientConstants() {
    }

    public static String getString(String key) {
//        try {
//            return RESOURCE_BUNDLE.getString(key);
//        } catch (MissingResourceException e) {
//            return '!' + key + '!';
//        }
        return constants.get(key);
    }
}
