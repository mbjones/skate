package org.jsc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A screen for collecting user information to create a new account.
 * 
 * @author Matthew Jones
 *
 */
public class SettingsScreen extends BaseScreen {

    private HorizontalPanel screen;
    private TextBox fnameField;
    private TextBox mnameField;
    private TextBox lnameField;
    private TextBox emailField;
    private TextBox birthdayField;
    private TextBox homephoneField;
    private PasswordTextBox password1Field;
    private PasswordTextBox password2Field;
    private Button accountButton;
    
    // TODO: move regService to a separate class, one instance for the client
    private SkaterRegistrationServiceAsync regService;
    
    /**
     * Construct the screen.
     *
     */
    public SettingsScreen(LoginSession loginSession) {
        super(loginSession);
        layoutScreen();
        this.setContentPanel(screen);
        regService = GWT.create(SkaterRegistrationService.class);
    }
    
    /**
     * Lay out the user interface widgets on the screen.
     */
    private void layoutScreen() {
        this.setScreenTitle("Account Details");
        this.setStyleName("jsc-onepanel-screen");
        
        screen = new HorizontalPanel();
        
        HorizontalPanel accountPanel = new HorizontalPanel();
        accountPanel.addStyleName("jsc-rightpanel");
        
        int numrows = 9;
        
        Grid g = new Grid(numrows, 2);

        HTMLTable.CellFormatter fmt = g.getCellFormatter();
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
        
        accountButton = new Button("Create Account");
        accountButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                createAccount();
            }
        });
        g.setWidget(8, 0, accountButton);

        // Set the css style for each row
        for (int row=0; row < numrows; row++) {
            fmt.addStyleName(row, 0,  "jsc-fieldlabel");
            fmt.addStyleName(row, 1,  "jsc-field");
        }
        
        accountPanel.add(g);
        
        screen.add(accountPanel);
    }
    
    /**
     * When we switch to the settings screen, update our form to reflect our login session.
     */
    protected void updateScreen() {
        if (loginSession.isAuthenticated()) {
            populateFields();
            accountButton.setText("Save");
        } else {
            populateFields();
            accountButton.setText("Create Account");
        }
    }
    
    private void populateFields() {
        Person person;
        if (loginSession.isAuthenticated()) {
            person = loginSession.getPerson();
            fnameField.setText(person.getFname());
            mnameField.setText(person.getMname());
            lnameField.setText(person.getLname());
            emailField.setText(person.getEmail());
            homephoneField.setText(person.getHomephone());
            birthdayField.setText(person.getBday());
            password1Field.setText("");
            password2Field.setText("");
        } else {
            fnameField.setText("");
            mnameField.setText("");
            lnameField.setText("");
            emailField.setText("");
            homephoneField.setText("");
            birthdayField.setText("");
            password1Field.setText("");
            password2Field.setText("");
        }
    }
    
    /**
     * Called when the create account button is pressed, and contacts the server
     * to update the account information in the database.
     */
    private void createAccount() {
        GWT.log("Creating account...", null);
    
        // Gather information from the form
        String fname = fnameField.getText();
        String mname = mnameField.getText();
        String lname = lnameField.getText();
        String email = emailField.getText();
        String birthday = birthdayField.getText();
        String homephone = homephoneField.getText();
        String pw1 = password1Field.getText();
        String pw2 = password2Field.getText();
        
        // Validate necessary input, making sure required fields are included
        boolean isValid = true;
        if (fname == null || lname == null || email == null ||
                birthday == null || homephone == null) {
            isValid = false;
            GWT.log("A field is invalid", null);
        }

        // We only need a password if its a new account or the user is
        // providing a new one; in either case, the retyped password must match
        if (!loginSession.isAuthenticated() || pw1 != null) {
            if (pw1 == null || pw2 == null || !pw1.equals(pw2)) {
                isValid = false;
                GWT.log("Password invalid", null);
            }
        }
        
        // create Person object
        Person person = null;
        if (isValid) {
            person = new Person(fname, mname, lname);
            if (loginSession.isAuthenticated()) {
                person.setPid(loginSession.getPerson().getPid());
            } else {
                // Set the PID to 0 to indicate this is an update
                person.setPid(0);
            }
            person.setEmail(email);
            person.setBday(birthday);
            person.setHomephone(homephone);
            if (pw1 != null) {
                person.setPassword1(pw1);
            }
        } else {
            // TODO: Insufficient data was provided to register, so send an error
            GWT.log("Account NOT created: " + fname + " " + mname + " " + lname, null);
            return;
        }
        
        // Initialize the service proxy.
        if (regService == null) {
            regService = GWT.create(SkaterRegistrationService.class);
        }

        // Set up the callback object.
        AsyncCallback<Long> callback = new AsyncCallback<Long>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
                GWT.log("Failed to create account.", null);
            }

            public void onSuccess(Long pid) {
                GWT.log("Account created: " + pid.longValue(), null);
                if (loginSession.getPerson().getPid() == pid.longValue()) {
                    GWT.log("Settings updated.", null);
                    // TODO: post a message to the screen saying it was saved
                } else {
                    // Change our application state to the login screen
                    History.newItem("signout");
                }
            }
        };

        // Make the call to the registration service.
        regService.createAccount(person, callback);

        // TODO: handle both insert and update operations
        GWT.log("Account created for: " + fname + " " + mname + " " + lname, null);
    }
}
