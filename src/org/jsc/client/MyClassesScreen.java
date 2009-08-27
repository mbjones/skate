package org.jsc.client;

import org.jsc.client.event.RosterChangeEvent;
import org.jsc.client.event.RosterChangeHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Grid;
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
        super(loginSession);
        
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
                
        classesGrid = new Grid(0, 4);
              
        // Add a header row to the table
        Label classNameLabel = new Label("Class Name");
        Label datePaidLabel = new Label("Confirmation #");
        Label amountPaidLabel = new Label("Amount Paid");
        Label levelPassedLabel = new Label("Level Passed");
        addToGrid(classNameLabel, datePaidLabel, amountPaidLabel, levelPassedLabel);
        
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
            // TODO: set the paypal_tx here
            Label paymentConfirmLabel = new Label(" ");
            NumberFormat numFmt = NumberFormat.getFormat("$#,##0.00");
            double amountPaid = entry.getPayment_amount();
            Label amountPaidLabel = new Label(numFmt.format(amountPaid));
            Label levelPassedLabel = new Label(entry.getLevelpassed());
            addToGrid(classNameLabel, paymentConfirmLabel, amountPaidLabel, levelPassedLabel);
        }
    }
    
    /**
     * Add the given widget to the grid table. Four columns are set up, and
     * each widget is assigned to one column and assigned a CSS style.
     * @param widget0 the widget to display in column 0
     * @param widget1 the widget to display in column 1
     * @param widget2 the widget to display in column 2
     * @param widget3 the widget to display in column 3
     */
    private void addToGrid(Widget widget0, Widget widget1, Widget widget2, Widget widget3) {
        int newRow = classesGrid.insertRow(classesGrid.getRowCount());
        classesGrid.setWidget(newRow, 0, widget0);
        classesGrid.setWidget(newRow, 1, widget1);
        classesGrid.setWidget(newRow, 2, widget2);
        classesGrid.setWidget(newRow, 3, widget3);
        HTMLTable.CellFormatter fmt = classesGrid.getCellFormatter();
        if (newRow == 0) {
            // The header row has its own style
            fmt.addStyleName(newRow, 0,  "jsc-tablehead");
            fmt.addStyleName(newRow, 1,  "jsc-tablehead");
            fmt.addStyleName(newRow, 2,  "jsc-tablehead");
            fmt.addStyleName(newRow, 3,  "jsc-tablehead");
        } else {
            fmt.addStyleName(newRow, 0,  "jsc-tablecell");
            fmt.addStyleName(newRow, 1,  "jsc-tablecell");
            fmt.addStyleName(newRow, 2,  "jsc-tablecell");
            fmt.addStyleName(newRow, 3,  "jsc-tablecell");
        }
    }
}
