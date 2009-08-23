package org.jsc.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsc.client.event.SkatingClassChangeEvent;
import org.jsc.client.event.SkatingClassChangeHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The SkaterData web application supports registration and payment for 
 * JSC skating classes.
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SkaterData implements EntryPoint, ValueChangeHandler {

    private LoginSession loginSession;
    private Map<java.lang.String,java.util.List<java.lang.String>> params;
    private HeaderPanel header;
    private ContentPanel content;
    private LoginScreen login;
    private SettingsScreen settings;
    private MyClassesScreen myclasses;
    private RegisterScreen register;
    private ManageScreen manage;
    private ConfirmScreen confirm;

    private ClassListModel sessionClassList;
    private TextBox searchText = new TextBox();
    private Button searchButton = new Button("Search");
    private FlexTable classesTable = new FlexTable();
    private FlexTable rosterTable = new FlexTable();
    private ArrayList<String> rosterList = new ArrayList<String>();
    //private Panel leftPanel;
    //private Panel rightPanel;
    private HandlerManager eventBus;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

        // Create our event bus for handling application events
        eventBus = new HandlerManager(null);
        
        // Create a login session, which initially is not logged in
        loginSession = new LoginSession();
        
        sessionClassList = new ClassListModel(eventBus, loginSession);

        // Create our header with internal toolbar
        header = new HeaderPanel(loginSession);
        header.setTitle("Juneau Skating Club");
   
        // Create the screens to be used in the application
        content = new ContentPanel();
        content.setStyleName("jsc-content");
        
        // Store the parameters passed into the page
        params = Window.Location.getParameterMap();
        for (Map.Entry<String, List<String>> param : params.entrySet()) {
            GWT.log(param.getKey() + " " + param.getValue().toString(), null);
        }
        
        login = new LoginScreen(loginSession);
        settings = new SettingsScreen(loginSession);
        myclasses = new MyClassesScreen(loginSession);
        register = new RegisterScreen(loginSession, sessionClassList);
        manage = new ManageScreen(loginSession);
        confirm = new ConfirmScreen(loginSession);
        
        //leftPanel = createLeftPanel();
        //rightPanel = createRightPanel();

        // Set the default view screen to be login
        content.setScreen(login);

        // Get rid of scrollbars, and clear out the window's built-in margin,
        // because we want to take advantage of the entire client area.
        //Window.enableScrolling(false);
        Window.setMargin("0px");

        // Add our header and content containers to the root panel of the window
        RootPanel root = RootPanel.get();
        root.add(header);
        root.add(content);
        root.setStylePrimaryName("jsc-root-window");
        
        // Register as a handler for Skating class changes, and handle those changes
        eventBus.addHandler(SkatingClassChangeEvent.TYPE, new SkatingClassChangeHandler(){
            public void onClassChange(SkatingClassChangeEvent event) {
                register.updateClassListBox();
            }
        });
        
        // Add history listener
        History.addValueChangeHandler(this);

        // Now that we've setup our listener, fire the initial history state.
        History.fireCurrentHistoryState();
    }
    
    /**
     * This method is called whenever the application's history changes, and 
     * we use it to track application state. 
     */
    public void onValueChange(ValueChangeEvent event) {
        Object historyToken = event.getValue();
        
        if (!loginSession.isAuthenticated() &! historyToken.equals("settings")
                &! historyToken.equals("confirm") &! historyToken.equals("cancel")) {
            historyToken = "signout";
        }
        
        //Check if this is an IPT response from PayPal, and if so redirect to the confirm page
        List<String> txList = params.get("tx");
        if (txList != null && txList.size() > 0) {
            GWT.log("Transaction id: " + txList.get(0), null);
            confirm.setTransactionId(params.get("tx").get(0));
            confirm.setStatus(params.get("st").get(0));
            confirm.setAmountPaid(params.get("amt").get(0));
            confirm.setRosterId(params.get("item_number").get(0));
            historyToken = "confirm";
        }
        
        if (historyToken.equals("settings")) {
            settings.updateScreen();
            content.setScreen(settings);
        } else if (historyToken.equals("signout")) {
            loginSession.setAuthenticated(false);
            content.setScreen(login);
        } else if (historyToken.equals("myclasses")) {
            content.setScreen(myclasses);
        } else if (historyToken.equals("register")) {
            sessionClassList.refreshClassList();
            content.setScreen(register);
        } else if (historyToken.equals("manage")) {
            content.setScreen(manage);
        } else if (historyToken.equals("confirm")) {
            content.setScreen(confirm);
        } else if (historyToken.equals("cancel")) {
            content.setScreen(manage);
        }
        updateMessageFields();
        header.updateStatus();
    }

    /**
     * When the screen changes, update the message Label on each screen so they
     * are synchronized.
     */
    private void updateMessageFields() {
        settings.clearMessage();
        settings.updateMessage();
        login.updateMessage();
        myclasses.updateMessage();
        register.updateMessage();
        manage.updateMessage();
        confirm.updateMessage();
    }
    
    /**
     * Create and lay out the components for the left side content
     * Currently unused; keeping code as example of table layout and manipulation
     */
/*
    private Panel createLeftPanel() {
        VerticalPanel leftPanel = new VerticalPanel();

        // Create a panel to contain the search widgets
        HorizontalPanel searchBox = new HorizontalPanel();
        searchBox.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
        searchBox.add(searchText);
        searchBox.add(searchButton);

        final DialogBox dialogBox = createDialog();
        searchButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.center();
                dialogBox.show();
            }
        });

        // Create the class listing panel
        VerticalPanel classList = new VerticalPanel();
        classList.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
        classList.add(classesTable);

        // Add the searchBox and classList to the leftPanel
        leftPanel.add(new HTML("<p class=\"label\">Classes</p>"));
        leftPanel.add(searchBox);
        leftPanel.add(classList);
        leftPanel.addStyleName("jsc-leftpanel");
        populateClassesList();

        return leftPanel;
    }
*/
    /**
     * Create an alert dialog to display
     */
/*
    private DialogBox createDialog() {
        // Create the dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Hide or Show the list");
        dialogBox.setAnimationEnabled(true);
        Button closeButton = new Button("Do it now!");
        VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.setWidth("100%");
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
        dialogVPanel.add(closeButton);

        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                //rightPanel.setVisible(!rightPanel.isVisible());
                dialogBox.hide();
            }
        });

        // Set the contents of the Widget
        dialogBox.setWidget(dialogVPanel);

        //classList.refreshClassList();

        return dialogBox;
    }
*/
    /**
     * Create and lay out the widgets for the right side content
     * Currently unused; keeping code as example of table layout and manipulation
     */
/*
    private Panel createRightPanel() {
        VerticalPanel rightPanel = new VerticalPanel();
        // Add the class details to the right panel
        rightPanel.add(new HTML("<p class=\"label\">Class Detail</p>"));
        rightPanel.addStyleName("jsc-rightpanel");
        rightPanel.add(rosterTable);
        long classid = 1;
        populateRoster(classid);

        return rightPanel;
    }

    private void populateClassesList() {

        classList.refreshClassList();

        classesTable.setText(0, 0, "Class");
        classesTable.setText(0, 1, "Coach");
        classesTable.getRowFormatter().setStyleName(0, "classes-ListHeader");

        // add the class to the list
        Iterator it = classList.iterator();
        while (it.hasNext()) {
            int row = classesTable.getRowCount();
            SessionSkatingClass sc = (SessionSkatingClass)it.next();
            long classid = sc.getClassId();
            classesTable.setText(row, 0, sc.getClassType());
            classesTable.setText(row, 1, sc.getInstructorFullName());
            // add button to remove this class from the list
            Button removeButton = new Button(new Long(classid).toString());
            classesTable.setWidget(row, 2, removeButton);
        }
        classesTable.addTableListener(new TableListener() {
            public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
                if (cell == 2) {
                    //classList.remove(row);
                    classesTable.removeRow(row);
                }
            }
        });
    }

    private void populateRoster(long classid) {

        rosterTable.setText(0, 0, "Name");
        rosterTable.setText(0, 1, "Email");
        rosterTable.setText(0, 2, "Level");
        rosterTable.getRowFormatter().setStyleName(0, "roster-ListHeader");

        // add the class to the list
        for (int x = 1; x < 20; x++) {
            int row = rosterTable.getRowCount();
            final String name = "Matthew B. Jones";
            final String email = "mbjones.89@gmail.com";
            final String level = "AS-" + x;

            rosterList.add(name);
            rosterTable.setText(row, 0, name);
            rosterTable.setText(row, 1, email);
            rosterTable.setText(row, 2, level);

            // add button to remove this class from the list
            Button removeClass = new Button("x");
            removeClass.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    int removedIndex = rosterList.indexOf(name);
                    rosterList.remove(removedIndex);
                    rosterTable.removeRow(removedIndex+1);
                }
            });
            rosterTable.setWidget(row, 3, removeClass);
        }   
    }
*/
}
