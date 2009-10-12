package org.jsc.client;

import org.jsc.client.event.SkatingClassChangeEvent;
import org.jsc.client.event.SkatingClassChangeHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ManageScreen extends BaseScreen implements SkatingClassChangeHandler {

    private HorizontalPanel screen;
    private VerticalPanel classesPanel;
    private VerticalPanel rosterPanel;
    private ClassListModel sessionClassList;
    private Grid classesGrid;
    private SkaterRegistrationServiceAsync regService;

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
        addToGrid(sessionLabel, classLabel, dayLabel);
        
        classesPanel.add(classesGrid);
    }
    
    /**
     * Add the given widget to the grid table. Four columns are set up, and
     * each widget is assigned to one column and assigned a CSS style.
     * @param widget0 the widget to display in column 0
     * @param widget1 the widget to display in column 1
     * @param widget2 the widget to display in column 2
     */
    private void addToGrid(Widget widget0, Widget widget1, Widget widget2) {
        int newRow = classesGrid.insertRow(classesGrid.getRowCount());
        classesGrid.setWidget(newRow, 0, widget0);
        classesGrid.setWidget(newRow, 1, widget1);
        classesGrid.setWidget(newRow, 2, widget2);

        HTMLTable.CellFormatter fmt = classesGrid.getCellFormatter();
        for (int i = 0; i < 3; i++) {
            if (newRow == 0) {
                // The header row has its own style
                fmt.addStyleName(newRow, i,  "jsc-tablehead");
            } else {
                fmt.addStyleName(newRow, i,  "jsc-tablecell");
            }
        }
    }

    /**
     * Set up the roster panel to display students in a selected class.
     */
    private void createRosterPanel() {
        rosterPanel = new VerticalPanel();
        rosterPanel.addStyleName("jsc-rightpanel");
        StringBuffer intro = new StringBuffer();
        intro.append("<p class=\"jsc-text\">Class roster goes here. Get from My Classes template.</p>");
        HTMLPanel introHTML = new HTMLPanel(intro.toString());
        rosterPanel.add(introHTML);
    }
    
    private void updateClassListTable() {
        // Remove all of the rows from the table, except the header row
        int rows = classesGrid.getRowCount();
        for (int i = rows-1; i > 0; i--) {
            GWT.log("Removing table row: " + i, null);
            classesGrid.removeRow(i);
        }
        
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
            addToGrid(sessionLabel, classLabel, dayLabel);
        }
    }

    public void onClassChange(SkatingClassChangeEvent event) {
        updateClassListTable();
    }
    
}
