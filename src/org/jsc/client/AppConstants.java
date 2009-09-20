package org.jsc.client;

public class AppConstants {
//    public static final String MERCHANT_ID = "48H3N28JSP2VN"; // sandbox testing merchantid
    public static final String MERCHANT_ID = "4STDKBE3NBV64"; // real JSC account merchantid
    
//    public static final String PAYPAL_URL = "https://www.sandbox.paypal.com/cgi-bin/webscr";
    public static final String PAYPAL_URL = "https://www.paypal.com/cgi-bin/webscr";

    public static final String PAYPAL_HEADER_IMAGE = "http://juneauskatingclub.org/sites/all/themes/jsc/images/salamander1/jsc-header-bkg-paypal.png";
    public static final String PAYPAL_RETURN_URL = "http://reg.juneauskatingclub.org";
//    public static final String PAYPAL_RETURN_URL = "http://reg.juneauskatingclub.org/jscdbtest";
    public static final String PAYPAL_CANCEL_URL = "http://reg.juneauskatingclub.org";
//    public static final String PAYPAL_CANCEL_URL = "http://reg.juneauskatingclub.org/jscdbtest";
    public static final int EARLY_PRICE_GRACE_DAYS = 2;
    public static final double MILLISECS_PER_DAY = 24*60*60*1000;
    public static final double EARLY_PRICE = 70.00;
    public static final double STANDARD_PRICE = 80.00;
    public static final double FS_PRICE = 80.00;
    public static final double MEMBERSHIP_PRICE = 60.00;
    public static final double MEMBERSHIP_DISCOUNT = 5.00;
}
