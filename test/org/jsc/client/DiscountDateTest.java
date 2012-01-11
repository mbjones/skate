package org.jsc.client;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

public class DiscountDateTest extends TestCase {

    public void testDiscounts() {
        // Test when discount expires today
        Date today = new Date(System.currentTimeMillis());
        double discount = calculateBSDiscount(today);
        assertEquals(10d, discount);
        
        // Test when discount expires yesterday
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterDate = yesterday.getTime();
        discount = calculateBSDiscount(yesterDate);
        assertEquals(0d, discount);
        
        // Test when discount expires tomorrow
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        Date tomorrowDate = tomorrow.getTime();
        discount = calculateBSDiscount(tomorrowDate);
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
            Date now = Calendar.getInstance().getTime();
            if (now.before(discountDate)) {
                bsDiscount = 10d;
            }
        }
        
        return bsDiscount;            
    }
}
