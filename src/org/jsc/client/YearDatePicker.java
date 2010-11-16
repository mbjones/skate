package org.jsc.client;

import com.google.gwt.user.datepicker.client.CalendarModel;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.user.datepicker.client.DefaultCalendarView;

public class YearDatePicker extends DatePicker {
    public YearDatePicker() {
        super(new MonthAndYearSelector(), new DefaultCalendarView(),
                new CalendarModel());
        MonthAndYearSelector monthSelector = (MonthAndYearSelector) this
                .getMonthSelector();
        monthSelector.setPicker(this);
        monthSelector.setModel(this.getModel());
    }

    public void refreshComponents() {
        super.refreshAll();
    }
}
