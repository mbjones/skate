package org.jsc.client;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

public class DiscountDateTest extends TestCase {

    public void testDiscounts() {
        Date today = new Date(System.currentTimeMillis());
        double discount = calculateBSDiscount(today);
        assertEquals(10d, discount);
    }
    
    private double calculateBSDiscount(Date discountDate) {
        
        double multiclassDiscount = 0;        
        // Calculate the basic skills discount
        double bsDiscount = 0;
        if (discountDate != null) {
            discountDate.setHours(23);
            discountDate.setMinutes(59);
            discountDate.setSeconds(59);
            SimpleDateFormat df = new SimpleDateFormat();
            System.out.println(df.format(discountDate));
            Date now = Calendar.getInstance().getTime();
            if (now.before(discountDate)) {
                bsDiscount = 10d;
            }
        }
        
        return bsDiscount;            
    }
}
