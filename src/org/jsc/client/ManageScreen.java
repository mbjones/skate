package org.jsc.client;

import java.util.ArrayList;

import org.jsc.client.event.RosterChangeEvent;
import org.jsc.client.event.RosterChangeHandler;
import org.jsc.client.event.SkatingClassChangeEvent;
import org.jsc.client.event.SkatingClassChangeHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;

public class ManageScreen extends BaseScreen implements SkatingClassChangeHandler {

    private HorizontalPanel screen;
    private VerticalPanel classesPanel;
    private VerticalPanel rosterPanel;
    private ClassListModel sessionClassList;
    private Grid classesGrid;
    private SkaterRegistrationServiceAsync regService;
    private int selectedClassRowIndex;
    private Grid rosterGrid;
    private Label classLabel;

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
                updateClassRoster(row);
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
        StringBuffer intro = new StringBuffer();
        intro.append("<p class=\"jsc-text\">Select a class from the list to see the class roster.</p>");
        HTMLPanel introHTML = new HTMLPanel(intro.toString());
        rosterPanel.add(introHTML);
        classLabel = new Label(" ");
        rosterPanel.add(classLabel);
        rosterGrid = new Grid(0, 4);
        
        // Add a header row to the table
        Label skaterNameLabel = new Label("Skater");
        Label levelPassedLabel = new Label("Level Passed");
        Label paymentIdLabel = new Label("Invoice #");
        Label statusLabel = new Label("Payment Status");
        Widget[] widgets = {skaterNameLabel, levelPassedLabel, paymentIdLabel, statusLabel};
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
    private void updateClassRoster(int row) {
        ArrayList<SessionSkatingClass> classes = sessionClassList.getClassList(); 
        SessionSkatingClass curClass = classes.get(row-1);
        classLabel.setText(curClass.formatClassLabel());
    }
}
