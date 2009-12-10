package org.jsc.client;

import java.util.ArrayList;

import org.jsc.client.event.SkatingClassChangeEvent;
import org.jsc.client.event.SkatingClassChangeHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;

/**
 * A screen that is used by coaches and administrators to manage class rosters, 
 * and to mark student level passed, and sections.
 * 
 * @author Matt Jones
 */
public class ManageScreen extends BaseScreen implements SkatingClassChangeHandler, ClickHandler {

    protected HorizontalPanel screen;
    private VerticalPanel classesPanel;
    protected VerticalPanel rosterPanel;
    private ClassListModel sessionClassList;
    private Grid classesGrid;
    private SkaterRegistrationServiceAsync regService;
    private int selectedClassRowIndex;
    private Grid rosterGrid;
    private Label classLabel;
    protected ArrayList<RosterEntry> currentRoster;
    private RosterScreen roster;
    private boolean layoutForPrinting;

    /**
     * Create the Management screen for managing class rosters and levels 
     * passed. This screen is only accessed by coaches and administrators.
     * @param loginSession
     * @param eventBus
     * @param sessionClassList
     */
    public ManageScreen(LoginSession loginSession, HandlerManager eventBus, 
            ClassListModel sessionClassList, RosterScreen roster) {
        super(loginSession, eventBus);
        this.sessionClassList = sessionClassList;
        this.roster = roster;
        this.layoutForPrinting = false;
        selectedClassRowIndex = 0;
        
        this.setScreenTitle("Manage Classes");
        layoutScreen();
        this.setContentPanel(screen);
        
        // Register as a handler for Skating class changes, and handle those changes
        eventBus.addHandler(SkatingClassChangeEvent.TYPE, this);
    }
    
    /**
     * Constructor that is used when building the RosterScreen.
     * @param loginSession
     * @param eventBus
     */
    public ManageScreen(LoginSession loginSession, HandlerManager eventBus) {
        super(loginSession, eventBus);
        this.layoutForPrinting = true;
    }
    
    /**
     * Lay out the user interface widgets on the screen.
     */
    protected void layoutScreen() {
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
        classesGrid.addStyleName("jsc-pointer-cursor");
        
        // Add a header row to the table
        Label sessionLabel = new Label("Session");
        Label classLabel = new Label("Class");
        Label dayLabel = new Label("Day");
        Widget[] labels= {sessionLabel, classLabel, dayLabel};
        addRowToGrid(classesGrid, labels);
        
        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.addStyleName("jsc-classes-scroll");
        scrollPanel.add(classesGrid);
        classesPanel.add(scrollPanel);
        
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
    protected void createRosterPanel() {
        rosterPanel = new VerticalPanel();
        rosterPanel.addStyleName("jsc-rightpanel");
        classLabel = new Label("Select a class from the list to see the class roster.");
        classLabel.addStyleName("jsc-step");
        rosterPanel.add(classLabel);
        Button printButton = new Button("Print Roster");
        printButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                printRoster();
            }
        });
        printButton.addStyleName("jsc-button-right");
        if (!layoutForPrinting) {
            rosterPanel.add(printButton);
        }
        
        int columns = 9;
        if (layoutForPrinting) {
            columns += 1;
        }
        rosterGrid = new Grid(0, columns);
        
        // Add a header row to the table
        Label sectionLabel = new Label("Section");
        Label skaterNameLabel = new Label("Skater");
        Label statusLabel = new Label("Payment");
        Widget w1, w2, w3, w4, w5, w6;
        Label moveLabel =new Label("Move to:");
        if (layoutForPrinting) {
            w1 = new Label("Wk 1");
            w2 = new Label("Wk 2");
            w3 = new Label("Wk 3");
            w4 = new Label("Wk 4");
            w5 = new Label("Wk 5");
            w6 = new Label("Wk 6");
        } else {
            w1 = new Hidden("rosterid");
            w2 = new Hidden("paymentid");
            w3 = new Hidden(" ");
            w4 = new Hidden(" ");
            w5 = new Hidden(" ");
            w6 = new Hidden(" ");
        }
   
        Label levelPassedLabel = null;
        if (layoutForPrinting) {
            levelPassedLabel = new Label("Max Level");
        } else {
            levelPassedLabel = new Label("Level Passed");
        }
        Label saveLabel = new Label(" ");
        Label deleteLabel = new Label(" ");
        
        if (layoutForPrinting) {
            Widget[] widgets = {sectionLabel, skaterNameLabel, statusLabel,  
                    w1, w2, w3, w4, w5, w6, levelPassedLabel};
            addRowToGrid(rosterGrid, widgets);
        } else {
            Widget[] widgets = {sectionLabel, skaterNameLabel, statusLabel,  
                    w1, w2, levelPassedLabel, saveLabel, deleteLabel, moveLabel};
            addRowToGrid(rosterGrid, widgets);
        }
        rosterPanel.add(rosterGrid);
    }

    /**
     * Add the given set of widgets to the grid table. One column is set up for
     * each widget, which is assigned to the column and assigned a CSS style.
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
     * @param row the index in the classesGrid indicating which roster to display
     */
    private void refreshClassRoster(int row) {
        clearMessage();
        ArrayList<SessionSkatingClass> classes = sessionClassList.getClassList(); 
        final SessionSkatingClass curClass = classes.get(row-1);
        //classLabel.setText(curClass.formatClassLabel());
        
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
                    updateRosterTable(newRoster, curClass);
                }
            }
        };

        // Make the call to the registration service.
        regService.getClassRoster(loginSession, curClass.getClassId(), callback);
    }
    
    /**
     * Update the roster table to reflect a new class selection.  Whenever a new
     * roster needs to be displayed (usually from the user clicking
     * on a new class in the classes list), clear all of the roster rows of the 
     * table, update the class label, and then populate the table with rows from
     * the new roster.
     * @param newRoster the array of RosterEntrys to be displayed
     * @param curClass the SkatingSessionClass that is being displayed
     */
    protected void updateRosterTable(ArrayList<RosterEntry> newRoster, 
            SessionSkatingClass curClass) {
        
        // Update the class label
        classLabel.setText(curClass.formatClassLabel());

        // Remove all of the rows from the table, except the header row
        int rows = rosterGrid.getRowCount();
        for (int i = rows-1; i > 0; i--) {
            GWT.log("Removing table row: " + i, null);
            rosterGrid.removeRow(i);
        }
        
        // Add all of the new roster entries into the table
        for (RosterEntry entry : newRoster) {
            Widget sectionBox = null;
            if (layoutForPrinting) {
                sectionBox = new Label(entry.getSection());
            } else {
                sectionBox = new TextBox();
                ((TextBox)sectionBox).setText(entry.getSection());
                sectionBox.addStyleName("jsc-text-box-short");
            }
            
            Label skaterNameLabel = new Label(entry.getGivenname() + " " + entry.getSurname());
            Label paymentStatusLabel = new Label(entry.getPaypal_status());

            Label week1Label = new Label(" ");
            Label week2Label = new Label(" ");
            Label week3Label = new Label(" ");
            Label week4Label = new Label(" ");
            Label week5Label = new Label(" ");
            Label week6Label = new Label(" ");
            
            Widget levelPassedBox = null;
            if (layoutForPrinting) {
                levelPassedBox = new Label(entry.getMaxLevel());
            } else {
                levelPassedBox = new TextBox();
                ((TextBox)levelPassedBox).setText(entry.getLevelpassed());
                ((TextBox)levelPassedBox).setMaxLength(3);
                levelPassedBox.addStyleName("jsc-text-box-short");    
            }
            
            Widget saveWidget = null;
            if (!layoutForPrinting) {
                Button saveButton = new Button("Save");
                final long currentRosterId = entry.getRosterid();
                saveButton.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        requestSaveRoster(currentRosterId, event);
                    }
                });
                saveButton.addStyleName("jsc-button-right");
                saveWidget = saveButton;
            } else {
                saveWidget = new Label(" ");
            }
            
            Widget deleteWidget;
            if (!layoutForPrinting && entry.getPaypal_status().equals("Pending")) {
                Button cancelButton = new Button("Delete");
                final long currentPaymentId = entry.getPaymentid();
                cancelButton.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        requestCancelInvoice(currentPaymentId);
                    }
                });
                cancelButton.addStyleName("jsc-button-right");
                deleteWidget = cancelButton;
            } else {
                deleteWidget = new Label(" ");
            }
            
            Widget rosterIdWidget = null;
            Widget paymentIdWidget = null;
            if (layoutForPrinting) {
                rosterIdWidget = new Label(" ");
                paymentIdWidget = new Label(" ");
            } else {
                Hidden rosterIdHidden = new Hidden(new Long(entry.getRosterid()).toString());
                Hidden paymentIdHidden = new Hidden(Long.toString(entry.getPaymentid()));
                rosterIdWidget = rosterIdHidden;
                paymentIdWidget = paymentIdHidden;
            }

            //Label moveMenu = new Label("Move menu goes here");
            ListBox moveMenu = createClassListBox(entry.getClassid());
            
            if (layoutForPrinting) {
                Widget[] widgets = {sectionBox, skaterNameLabel, paymentStatusLabel, 
                        rosterIdWidget, paymentIdWidget, week3Label, week4Label, week5Label, 
                        week6Label, levelPassedBox};
                addRowToGrid(rosterGrid, widgets);
            } else {
                Widget[] widgets = {sectionBox, skaterNameLabel, paymentStatusLabel, 
                        rosterIdWidget, paymentIdWidget, levelPassedBox, saveWidget, deleteWidget, moveMenu};
                addRowToGrid(rosterGrid, widgets);

            }
        }
    }
    
    /**
     * Handle the request to save changes to a roster entry by calling the remote
     * registration service and passing off the request data and login information.
     * @param currentRosterId the roster entry to be changed
     * @param event the event that was clicked, from which form field data can be retrieved
     */
    private void requestSaveRoster(long currentRosterId, ClickEvent event) {
        Button source = (Button)event.getSource();
        Grid rosterGrid = (Grid)source.getParent();
        int row = rosterGrid.getCellForEvent(event).getRowIndex();
        TextBox tb = (TextBox)rosterGrid.getWidget(row, 5);
        String newLevel = tb.getValue();
        tb = (TextBox)rosterGrid.getWidget(row, 0);
        String newSection = tb.getValue();
        GWT.log("Section value is: " + newLevel + " " + newSection, null);
        ListBox lb = (ListBox)rosterGrid.getWidget(row, 8);
        String selectedClassId = lb.getValue(lb.getSelectedIndex());

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
                if (resultFlag) {
                    refreshClassRoster(selectedClassRowIndex);
                    setMessage("Changes saved.");
                } else {
                    refreshClassRoster(selectedClassRowIndex);
                    setMessage("Error saving changes. Please check that you provided a valid level code (e.g., BS1, BS2).");
                }
            }
        };

        // Make the call to the registration service.
        regService.saveRoster(loginSession, currentRosterId, newLevel, newSection, selectedClassId, callback);
    }
    
    /**
     * Put up a confirmation dialog when a request to cancel a registration
     * entry is made.
     * @param paymentid the payment to be canceled
     */
    private void requestCancelInvoice(long paymentid) {
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

    /**
     * Switch to the screen for printing rosters after updating it by passing in
     * the currently selected roster and class.
     */
    private void printRoster() {
        GWT.log("Print roster rows: " + currentRoster.size(), null);
        // Switch to the screen for printing rosters, but before doing so
        // update it by passing in the currentRoster to be displayed along with 
        // a reference to the current selected class to be displayed
        ArrayList<SessionSkatingClass> classes = sessionClassList.getClassList();
        SessionSkatingClass curClass = classes.get(selectedClassRowIndex-1);
        roster.updateRosterTable(currentRoster, curClass);
        History.newItem("roster");
    }
    
    private ListBox createClassListBox(long classid) {
        ListBox classField = new ListBox();
        classField.addItem("Select new class", "0");
        
        ArrayList<SessionSkatingClass> list = sessionClassList.getClassList();
        
        if (list != null) {
            for (SessionSkatingClass curClass : list) {
                // Only include the active session in the dropdown list
                if (curClass.isCurrentSeason()) {
                    // Only add item to list if student is not registered
                    if (classid == curClass.getClassId()) {
                        GWT.log("Skipping class in menu: " + curClass.getClassId(), null);
                    } else {
                        String classLabel = curClass.formatShortClassLabel();
                        classField.addItem(classLabel, new Long(curClass.getClassId()).toString());
                    }
                }
            }
        }
        return classField;
    }
}
