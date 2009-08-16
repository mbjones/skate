package org.jsc.client;

import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

public class RegisterScreen extends BaseScreen {

    private TreeMap<String, String> classList;
    private HorizontalPanel screen;
    private ListBox classField;
    private TextBox fnameField;
    private TextBox mnameField;
    private TextBox lnameField;
    private TextBox emailField;
    private TextBox birthdayField;
    private TextBox homephoneField;
    private PasswordTextBox password1Field;
    private PasswordTextBox password2Field;
    private Button registerButton;
    
    private SkaterRegistrationServiceAsync regService;

    public RegisterScreen(LoginSession loginSession) {
        super(loginSession);
        classList = new TreeMap<String, String>();
        layoutScreen();
        this.setContentPanel(screen);
        regService = GWT.create(SkaterRegistrationService.class);
    }
    
    /**
     * Lay out the user interface widgets on the screen.
     */
    private void layoutScreen() {
        this.setScreenTitle("Register");
        this.setStyleName("jsc-onepanel-screen");
        
        screen = new HorizontalPanel();
        
        HorizontalPanel accountPanel = new HorizontalPanel();
        accountPanel.addStyleName("jsc-rightpanel");
        
        int numrows = 9;
        
        Grid g = new Grid(numrows, 2);

        HTMLTable.CellFormatter fmt = g.getCellFormatter();
        g.setWidget(0, 0, new Label("Class:"));
        classField = new ListBox();
        updateClassListBox();
        classField.setVisibleItemCount(1);
        g.setWidget(0, 1, classField);
        
        /*
        g.setWidget(0, 0, new Label("First Name:"));
        fnameField = new TextBox();
        g.setWidget(0, 1, fnameField);
        g.setWidget(1, 0, new Label("Middle Name:"));
        mnameField = new TextBox();
        g.setWidget(1, 1, mnameField);
        g.setWidget(2, 0, new Label("Last Name:"));
        lnameField = new TextBox();
        g.setWidget(2, 1, lnameField);
        g.setWidget(3, 0, new Label("Email:"));
        emailField = new TextBox();
        g.setWidget(3, 1, emailField);
        g.setWidget(4, 0, new Label("Birth date:"));
        birthdayField = new TextBox();
        g.setWidget(4, 1, birthdayField);
        g.setWidget(5, 0, new Label("Phone:"));
        homephoneField = new TextBox();
        g.setWidget(5, 1, homephoneField);
        g.setWidget(6, 0, new Label("Password:"));
        password1Field = new PasswordTextBox();
        g.setWidget(6, 1, password1Field);
        g.setWidget(7, 0, new Label("Re-type password:"));
        password2Field = new PasswordTextBox();
        g.setWidget(7, 1, password2Field);
        */
        registerButton = new Button("Register");
        registerButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                //register();
            }
        });
        g.setWidget(8, 1, registerButton);

        // Set the css style for each row
        for (int row=0; row < numrows; row++) {
            fmt.addStyleName(row, 0,  "jsc-fieldlabel");
            fmt.addStyleName(row, 1,  "jsc-field");
        }
        
        accountPanel.add(g);
        
        screen.add(accountPanel);
        
    }

    /**
     * Remove the current list of classes from the box and replace with the 
     * classes that are currently present in classList.
     */
    private void updateClassListBox() {
        classField.clear();
        for (Map.Entry<String, String> curClass : classList.entrySet()) {
            classField.addItem(curClass.getKey(), curClass.getValue());
        }
    }
    
    /**
     * Look up the current list of classes from the registration servlet and
     * store it for use in the UI later.
     */
    protected void getClassList() {
        // Initialize the service proxy.
        if (regService == null) {
            regService = GWT.create(SkaterRegistrationService.class);
        }

        // Set up the callback object.
        AsyncCallback<TreeMap<String,String>> callback = new AsyncCallback<TreeMap<String,String>>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
                GWT.log("Failed to get list of classes.", null);
            }

            public void onSuccess(TreeMap<String,String> list) {
                if (list == null) {
                    // Failure on the remote end.
                    setMessage("Error finding the list of classes.");
                    return;
                } else {
                    classList = list;
                    updateClassListBox();
                }
            }
        };

        // Make the call to the registration service.
        regService.getClassList(loginSession.getPerson(), callback);
    }

}
