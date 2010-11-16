package org.jsc.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.datepicker.client.CalendarModel;
import com.google.gwt.user.datepicker.client.MonthSelector;

public class MonthAndYearSelector extends MonthSelector {

    /** 
     * A simple {@link MonthSelector} used for the date picker.
     */
    private static String NAME = "datePicker";
    private PushButton backButton;
    private PushButton forwardButton;
    private PushButton yearBackButton;
    private PushButton yearForwardButton;
    private Grid grid;
    private int priorYearCol = 0;
    private int priorMonthCol = 1;
    private int monthCol = 2;
    private int nextMonthCol = 3;
    private int nextYearCol = 4;
    private CalendarModel calendarModel;
    private YearDatePicker picker;

    public void setModel(CalendarModel model) {
        this.calendarModel = model;
    }

    public void setPicker(YearDatePicker picker) {
        this.picker = picker;
    }

    @Override
    protected void refresh() {
        String formattedMonth = getModel().formatCurrentMonth();
        grid.setText(0, monthCol, formattedMonth);
    }

    @Override
    protected void setup() {
        // Set up backwards. 
        backButton = new PushButton();
        backButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                addMonths(-1);
            }
        });
        backButton.getUpFace().setHTML("&lsaquo;");
        backButton.setStyleName(NAME + "PreviousButton");
        forwardButton = new PushButton();
        forwardButton.getUpFace().setHTML("&rsaquo;");
        forwardButton.setStyleName(NAME + "NextButton");
        forwardButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                addMonths(+1);
            }
        });
        // Set up backwards year 
        yearBackButton = new PushButton();
        yearBackButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                addMonths(-12);
            }
        });
        yearBackButton.getUpFace().setHTML("&laquo;");
        yearBackButton.setStyleName(NAME + "PreviousButton");
        yearForwardButton = new PushButton();
        yearForwardButton.getUpFace().setHTML("&raquo;");
        yearForwardButton.setStyleName(NAME + "NextButton");
        yearForwardButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                addMonths(+12);
            }
        });
        // Set up grid. 
        grid = new Grid(1, 5);
        grid.setWidget(0, priorYearCol, yearBackButton);
        grid.setWidget(0, priorMonthCol, backButton);
        grid.setWidget(0, nextMonthCol, forwardButton);
        grid.setWidget(0, nextYearCol, yearForwardButton);
        CellFormatter formatter = grid.getCellFormatter();
        formatter.setStyleName(0, monthCol, NAME + "Month");
        formatter.setWidth(0, priorYearCol, "1");
        formatter.setWidth(0, priorMonthCol, "1");
        formatter.setWidth(0, monthCol, "100%");
        formatter.setWidth(0, nextMonthCol, "1");
        formatter.setWidth(0, nextYearCol, "1");
        grid.setStyleName(NAME + "MonthSelector");
        initWidget(grid);
    }

    public void addMonths(int numMonths) {
        calendarModel.shiftCurrentMonth(numMonths);
        picker.refreshComponents();
    }
}
