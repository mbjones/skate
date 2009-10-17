package org.jsc.client;

import java.util.ArrayList;
import java.util.Date;

import org.jsc.client.event.RosterChangeEvent;
import org.jsc.client.event.RosterChangeHandler;
import org.jsc.client.event.SkatingClassChangeEvent;
import org.jsc.client.event.SkatingClassChangeHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;

public class ManageScreen extends BaseScreen implements SkatingClassChangeHandler, ClickHandler {

    private HorizontalPanel screen;
    private VerticalPanel classesPanel;
    private VerticalPanel rosterPanel;
    private ClassListModel sessionClassList;
    private Grid classesGrid;
    private SkaterRegistrationServiceAsync regService;
    private int selectedClassRowIndex;
    private Grid rosterGrid;
    private Label classLabel;
    private ArrayList<RosterEntry> currentRoster;

    /**
     * Create the Management screen for managing class rosters and levels 
     * passed. This screen is only accessed by coaches and administrators.
     * @param loginSession
     * @param eventBus
     * @param sessionClassList
     */
    public ManageScreen(LoginSession loginSession, HandlerManager eventBus, ClassListModel sessionClassList) {
        super(loginSession, eventBus);
        this.sessionClassList = sessionClassList;
        selectedClassRowIndex = 0;
        
        this.setScreenTitle("Manage Classes");
        layoutScreen();
        this.setContentPanel(screen);
        
        // Register as a handler for Skating class changes, and handle those changes
        eventBus.addHandler(SkatingClassChangeEvent.TYPE, this);
    }
    
    /**
     * Lay out the user interface widgets on the screen.
     */
    private void layoutScreen() {
        this.setScreenTitle("Manage Classes");
        this.setStyleName("jsc-twopanel-screen");
        
        screen = new HorizontalPanel();
        
        createClassListPanel();
        Label spacer = new Label("");
        spacer.addStyleName("jsc-spacer");
        createRosterPanel();
        screen.add(classesPanel);
        screen.add(spacer);
        screen.add(rosterPanel);
    }
    
    /**
     * Fill in the GUI for the Login screen
     */
    private void createClassListPanel() {
        classesPanel = new VerticalPanel();
        classesPanel.addStyleName("jsc-leftpanel");
        
        classesGrid = new Grid(0, 3);
        
        // Add a header row to the table
        Label sessionLabel = new Label("Session");
        Label classLabel = new Label("Class");
        Label dayLabel = new Label("Day");
        Widget[] labels= {sessionLabel, classLabel, dayLabel};
        addRowToGrid(classesGrid, labels);
        
        classesPanel.add(classesGrid);
        
        addClickHandler(classesGrid);
    }
    
    /**
     * A handler that monitors the classesGrid table and responds when a new selection is made.
     * @param classesGrid the grid of classes to monitor
     */
    private void addClickHandler(Grid classesGrid) {
        classesGrid.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                HTMLTable table = (HTMLTable)event.getSource();
                int row = table.getCellForEvent(event).getRowIndex();
                RowFormatter rf = table.getRowFormatter();
                rf.removeStyleName(selectedClassRowIndex, "jsc-row-selected");
                selectedClassRowIndex = row;
                if (row > 0) {
                    rf.addStyleName(row, "jsc-row-selected");
                }
                refreshClassRoster(row);
                GWT.log("Table was clicked on row: " + row, null);
            }
        });
    }

    /**
     * Remove the previous list of classes and add the new list to the table whenever
     * the classListModel is changed.
     */
    private void updateClassListTable() {
        // Remove all of the rows from the table, except the header row
        int rows = classesGrid.getRowCount();
        for (int i = rows-1; i > 0; i--) {
            GWT.log("Removing table row: " + i, null);
            classesGrid.removeRow(i);
        }
        
        // Reset the selected class row marker
        selectedClassRowIndex = 0;
        
        // Add all of the new roster entries into the table
        for (SessionSkatingClass curClass : sessionClassList.getClassList()) {
            
            if (curClass == null) {
                GWT.log("Expected SessionSkatingClass was not found in model", null);
                break;
            }
            Label sessionLabel = new Label(curClass.getSeason() + " (" +
                    curClass.getSessionNum() + ")");
            Label classLabel = new Label(curClass.getClassType());
            Label dayLabel = new Label(curClass.getDay().substring(0, 3)); 
            Widget[] labels= {sessionLabel, classLabel, dayLabel};
            addRowToGrid(classesGrid, labels);
        }
    }

    public void onClassChange(SkatingClassChangeEvent event) {
        updateClassListTable();
    }
    
    /**
     * Set up the roster panel to display students in a selected class.
     */
    private void createRosterPanel() {
        rosterPanel = new VerticalPanel();
        rosterPanel.addStyleName("jsc-rightpanel");
        classLabel = new Label("Select a class from the list to see the class roster.");
        classLabel.addStyleName("jsc-step");
        rosterPanel.add(classLabel);
        rosterGrid = new Grid(0, 8);
        
        // Add a header row to the table
        Label sectionLabel = new Label("Section");
        Label skaterNameLabel = new Label("Skater");
        Label levelPassedLabel = new Label("Level Passed");
        Label statusLabel = new Label("Payment Status");
        Label saveLabel = new Label(" ");
        Label cancelLabel = new Label(" ");
        Label rosterIdLabel = new Label(" ");
        Label paymentIdLabel = new Label(" ");

        Widget[] widgets = {sectionLabel, skaterNameLabel, levelPassedLabel, statusLabel, saveLabel, cancelLabel, rosterIdLabel, paymentIdLabel};
        addRowToGrid(rosterGrid, widgets);
        rosterPanel.add(rosterGrid);
    }

    /**
     * Add the given widget to the grid table. Four columns are set up, and
     * each widget is assigned to one column and assigned a CSS style.  The 
     * number of widgets in the array should match the number of columns in the
     * grid.
     * 
     * @param grid the grid to which a row should be added
     * @param widgets the set of widgets to be added, one per grid column
     */
    private void addRowToGrid(Grid grid, Widget[] widgets) {
        int newRow = grid.insertRow(grid.getRowCount());
        int column = 0;
        for (Widget widget : widgets) {
            grid.setWidget(newRow, column, widget);
            column++;
        }

        HTMLTable.CellFormatter fmt = grid.getCellFormatter();
        for (int i = 0; i < grid.getColumnCount(); i++) {
            if (newRow == 0) {
                // The header row has its own style
                fmt.addStyleName(newRow, i,  "jsc-tablehead");
            } else {
                fmt.addStyleName(newRow, i,  "jsc-tablecell");
            }
        }
    }
    
    /**
     * Update the class roster display whenever a new row is selected. The row
     * represents the row of the class listing table, which starts at 1.  
     * @param row
     */
    private void refreshClassRoster(int row) {
        ArrayList<SessionSkatingClass> classes = sessionClassList.getClassList(); 
        SessionSkatingClass curClass = classes.get(row-1);
        classLabel.setText(curClass.formatClassLabel());
        
        // Initialize the service proxy.
        if (regService == null) {
            regService = GWT.create(SkaterRegistrationService.class);
        }

        // Set up the callback object.
        AsyncCallback<ArrayList<RosterEntry>> callback = new AsyncCallback<ArrayList<RosterEntry>>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
                GWT.log("Failed to get roster.", caught);
            }

            public void onSuccess(ArrayList<RosterEntry> newRoster) {
                if (newRoster == null) {
                    // Failure on the remote end.
                    GWT.log("Error finding the roster.", null);
                    return;
                } else {
                    currentRoster = newRoster;
                    updateRosterTable(newRoster);
                }
            }
        };

        // Make the call to the registration service.
        regService.getClassRoster(loginSession, curClass.getClassId(), callback);
    }
    
    private void updateRosterTable(ArrayList<RosterEntry> newRoster) {
        
        // Remove all of the rows from the table, except the header row
        int rows = rosterGrid.getRowCount();
        for (int i = rows-1; i > 0; i--) {
            GWT.log("Removing table row: " + i, null);
            rosterGrid.removeRow(i);
        }
        
        // Add all of the new roster entries into the table
        for (RosterEntry entry : newRoster) {
            SessionSkatingClass curClass = sessionClassList.getSkatingClass(entry.getClassid());
            if (curClass == null) {
                GWT.log("Expected SessionSkatingClass was not found in model", null);
                break;
            }
            TextBox sectionBox = new TextBox();
            sectionBox.setText(entry.getSection());
            Label skaterNameLabel = new Label(entry.getGivenname() + " " + entry.getSurname());
            TextBox levelPassedBox = new TextBox();
            levelPassedBox.setText(entry.getLevelpassed());
            levelPassedBox.setMaxLength(3);
            Label paymentStatusLabel = new Label(entry.getPaypal_status());
            
            Button saveButton = new Button("Save");
            final long currentRosterId = entry.getRosterid();
            saveButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    requestSaveRoster(currentRosterId, event);
                }
            });
            saveButton.addStyleName("jsc-button-right");
            
            Widget cancelWidget;
            if (entry.getPaypal_status().equals("Pending")) {
                Button cancelButton = new Button("Delete");
                final long currentPaymentId = entry.getPaymentid();
                cancelButton.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        requestCancelInvoice(currentPaymentId);
                    }
                });
                cancelButton.addStyleName("jsc-button-right");
                cancelWidget = cancelButton;
            } else {
                cancelWidget = new Label(" ");
            }

            Label toolsLabel = new Label("Cancel");
            Hidden rosterIdHidden = new Hidden(new Long(entry.getRosterid()).toString());
            Hidden paymentIdHidden = new Hidden(Long.toString(entry.getPaymentid()));

            Widget[] widgets = {sectionBox, skaterNameLabel, levelPassedBox, paymentStatusLabel, saveButton, cancelWidget, rosterIdHidden, paymentIdHidden};
            addRowToGrid(rosterGrid, widgets);
        }
    }
    
    private void requestSaveRoster(long currentRosterId, ClickEvent event) {
        Button source = (Button)event.getSource();
        Grid rosterGrid = (Grid)source.getParent();
        int row = rosterGrid.getCellForEvent(event).getRowIndex();
        TextBox tb = (TextBox)rosterGrid.getWidget(row, 2);
        String newLevel = tb.getValue();
        tb = (TextBox)rosterGrid.getWidget(row, 0);
        String newSection = tb.getValue();
        GWT.log("Value is: " + newLevel + " " + newSection, null);
        
        // Initialize the service proxy.
        if (regService == null) {
            regService = GWT.create(SkaterRegistrationService.class);
        }

        // Set up the callback object.
        AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
                GWT.log("Failed to save the roster changes.", caught);
            }

            public void onSuccess(Boolean resultFlag) {
                GWT.log("Save roster returned: " + resultFlag, null);
                if (!resultFlag) {
                    //refreshClassRoster(selectedClassRowIndex);
                    setMessage("Error saving changes. Please report this problem to the registrar.");
                }
            }
        };

        // Make the call to the registration service.
        regService.saveRoster(loginSession, currentRosterId, newLevel, newSection, callback);
    }
    
    private void requestCancelInvoice(long paymentid) {
        // TODO Auto-generated method stub
        GWT.log("CANCEL requested for invoice: " + paymentid, null);
        String prompt = "Are you sure you want to delete " +
                "all registration entries for invoice " +
                paymentid + "?";
        ConfirmDialog confirm = new ConfirmDialog(prompt, this);
        confirm.setIdentifier(paymentid);
        confirm.setModal(true);
        confirm.center();
        confirm.show();
    }
    
    /**
     * This handler method is a callback that is registered with the confirmation
     * dialog and is called when one of the two buttons is pressed -- Yes or No.
     */
    public void onClick(ClickEvent event) {
        Button source = (Button)event.getSource();
        ConfirmDialog confirm = (ConfirmDialog)source.getParent().getParent().getParent().getParent();
        if (source.getText().equals("Yes")) {
            long paymentid = confirm.getIdentifier();
            finishCancelInvoice(paymentid);
        }
        confirm.hide();
    }
    
    /**
     * After the user has confirmed that an invoice should be deleted, connect to
     * the remote service and delete the entries from the membership, roster,
     * and payment tables.
     * @param paymentid the identifier of the invoice to be deleted
     */
    private void finishCancelInvoice(long paymentid) {
        GWT.log("Cancel confirmed for invoice: " + paymentid, null);
        
        // Initialize the service proxy.
        if (regService == null) {
            regService = GWT.create(SkaterRegistrationService.class);
        }

        // Set up the callback object.
        AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
                GWT.log("Failed to cancel the invoice.", caught);
            }

            public void onSuccess(Boolean resultFlag) {
                GWT.log("Cancel invoice returned: " + resultFlag, null);
                if (resultFlag) {
                    refreshClassRoster(selectedClassRowIndex);
                }
            }
        };

        // Make the call to the registration service.
        regService.cancelInvoice(loginSession, paymentid, callback);
    }
}
