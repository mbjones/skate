package org.jsc.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsc.client.event.RosterChangeEvent;
import org.jsc.client.event.RosterChangeHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * A specialization of BaseScreen to represent the "My Classes" screen in the 
 * application.  This user interface class provides a way to browse through the
 * classes for a student.
 * 
 * @author Matt Jones
 */
public class MyClassesScreen extends BaseScreen {
    
    private ClassListModel sessionClassList;
    private HorizontalPanel screen;
    private HorizontalPanel outerRegPanel;
    private Grid classesGrid;
    
    /**
     * Construct the screen to display the user's list of skating classes.
     * 
     * @param loginSession the session for this user
     * @param eventBus the eventBus used to register listeners
     * @param sessionClassList the list of classes to be used for display information
     */
    public MyClassesScreen(LoginSession loginSession, HandlerManager eventBus,
            ClassListModel sessionClassList) {
        super(loginSession, eventBus);
        
        this.sessionClassList = sessionClassList;
        layoutScreen();
        this.setContentPanel(screen);
        
        // Register as a handler for Skating class changes, and handle those changes
        eventBus.addHandler(RosterChangeEvent.TYPE, new RosterChangeHandler(){
            public void onRosterChange(RosterChangeEvent event) {
                updateRosterTable(event);
            }
        });
    }

    /**
     * Lay out the user interface widgets on the screen.
     */
    private void layoutScreen() {
        this.setScreenTitle("My Classes");
        this.setStyleName("jsc-onepanel-screen");
        
        screen = new HorizontalPanel();
        
        outerRegPanel = new HorizontalPanel();
        outerRegPanel.addStyleName("jsc-rightpanel");
                
        classesGrid = new Grid(0, 5);
              
        // Add a header row to the table
        Label classNameLabel = new Label("Class Name");
        Label levelPassedLabel = new Label("Level Passed");
        Label paymentIdLabel = new Label("Payment #");
        Label statusLabel = new Label("Payment Status");
        Label payButtonLabel = new Label(" ");
        addToGrid(classNameLabel, levelPassedLabel, paymentIdLabel, statusLabel, payButtonLabel);
        
        outerRegPanel.add(classesGrid);
        screen.add(outerRegPanel);
    }
    
    /**
     * Whenever the roster changes, update the grid of classes
     * @param event the event signaling that the roster has changed
     */
    private void updateRosterTable(RosterChangeEvent event) {
        // Remove all of the rows from the table, except the header row
        int rows = classesGrid.getRowCount();
        for (int i = rows-1; i > 0; i--) {
            GWT.log("Removing table row: " + i, null);
            classesGrid.removeRow(i);
        }
        
        // Add all of the new roster entries into the table
        for (RosterEntry entry : event.getRoster()) {
            SessionSkatingClass curClass = sessionClassList.getSkatingClass(entry.getClassid());
            if (curClass == null) {
                GWT.log("Expected SessionSkatingClass was not found in model", null);
                break;
            }
            Label classNameLabel = new Label(curClass.formatClassLabel());
            Label levelPassedLabel = new Label(entry.getLevelpassed());
            Label paymentIdLabel = new Label(Long.toString(entry.getPaymentid()));
            Label paymentStatusLabel = new Label(entry.getPaypal_status());
            GWT.log("Paypal_status is: " + entry.getPaypal_status(), null);
            Widget paymentFormButton;
            if (entry.getPaypal_status().equals("Pending")) {
                // Look up the payment invoices
                RegistrationResults results = assembleInvoiceData(entry.getPaymentid(), event.getRoster());
                
                // Construct the payment form
                double discount = 0;
                if (sessionClassList.getSkatingClass(entry.getClassid()).getClassType().startsWith("FS")) {
                    boolean isMember = results.isMembershipCreated() || loginSession.getPerson().isMember();
                    discount = RegisterScreen.calculateFSDiscount(results.getEntriesCreated().size(), isMember);
                    
                } else {
                    Date startDate = null;
                    String sessionStart = sessionClassList.getClassList().get(0).getStartDate();
                    if (sessionStart != null) {
                        DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd");
                        startDate = fmt.parse(sessionStart);
                    }
                    discount = RegisterScreen.calculateBSDiscount(startDate);
                }
                StringBuffer form = RegisterScreen.createPayPalForm(results, sessionClassList, discount);
                paymentFormButton = new HTMLPanel(form.toString());
            } else {
                paymentFormButton = new Label(" ");
            }
            addToGrid(classNameLabel, levelPassedLabel, paymentIdLabel, paymentStatusLabel, paymentFormButton);
        }
    }
    
    private RegistrationResults assembleInvoiceData(long paymentid, List<RosterEntry> rosterList) {
        RegistrationResults results = new RegistrationResults();
        results.setPaymentId(paymentid);
        // Loop through the rosters and find those with matching paymentid, and
        // add each to the results that need to be invoiced.
        ArrayList<RosterEntry> entriesToBeInvoiced = new ArrayList<RosterEntry>();
        for (RosterEntry entry : rosterList) {
            if (entry.getPaymentid() == paymentid) {
                entriesToBeInvoiced.add(entry);
            }
        }
        results.setEntriesCreated(entriesToBeInvoiced);
        
        // TODO: look up the membership payment status if relevant for this paymentid,
        // and also add it to the results if needed
        return results;
        
    }

    /**
     * Add the given widget to the grid table. Four columns are set up, and
     * each widget is assigned to one column and assigned a CSS style.
     * @param widget0 the widget to display in column 0
     * @param widget1 the widget to display in column 1
     * @param widget2 the widget to display in column 2
     * @param widget3 the widget to display in column 3
     * @param widget4 the widget to display in column 4
     */
    private void addToGrid(Widget widget0, Widget widget1, Widget widget2, Widget widget3, Widget widget4) {
        int newRow = classesGrid.insertRow(classesGrid.getRowCount());
        classesGrid.setWidget(newRow, 0, widget0);
        classesGrid.setWidget(newRow, 1, widget1);
        classesGrid.setWidget(newRow, 2, widget2);
        classesGrid.setWidget(newRow, 3, widget3);
        classesGrid.setWidget(newRow, 4, widget4);

        HTMLTable.CellFormatter fmt = classesGrid.getCellFormatter();
        for (int i = 0; i < 5; i++) {
            if (newRow == 0) {
                // The header row has its own style
                fmt.addStyleName(newRow, i,  "jsc-tablehead");
            } else {
                fmt.addStyleName(newRow, i,  "jsc-tablecell");
            }
        }
    }
}
