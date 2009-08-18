package org.jsc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A screen for confirming payment for a registration event. This form is
 * called when PayPal sends back confirmation information with the PayPal
 * transaction id.
 * 
 * @author Matthew Jones
 */
public class ConfirmScreen extends BaseScreen {

    private static final String CONFIRM_MESSAGE = "<p class=\"jsc-text\">Thank you for registering.  Your payment in the amount shown below will be processed by PayPal.</p><p class=\"jsc-text\">Please review and keep the information in the table below for your records.  It is the receipt for your registration.</p></div>";

    
    // ?tx=89009871KK074674R&st=Completed&amt=77.00&cc=USD&cm=&item_number=7
    private HorizontalPanel screen;
    private Label rosterId;
    private Label transactionId;
    private Label amountPaid;
    private Label status;

    private Button accountButton;
    
    // TODO: move regService to a separate class, one instance for the client
    private SkaterRegistrationServiceAsync regService;
    
    /**
     * Construct the screen.
     *
     */
    public ConfirmScreen(LoginSession loginSession) {
        super(loginSession);
        layoutScreen();
        this.setContentPanel(screen);
        regService = GWT.create(SkaterRegistrationService.class);
    }
    
    /**
     * Lay out the user interface widgets on the screen.
     */
    private void layoutScreen() {
        this.setScreenTitle("Registration Receipt");
        this.setStyleName("jsc-onepanel-screen");
        
        screen = new HorizontalPanel();
        
        VerticalPanel accountPanel = new VerticalPanel();
        accountPanel.addStyleName("jsc-rightpanel");
        
        // Post a thank you message and explanation
        HTMLPanel confirmPanel = new HTMLPanel(CONFIRM_MESSAGE);
        accountPanel.add(confirmPanel);
        
        // Post a table of confirmation data (a receipt)
        int numrows = 4;
        Grid g = new Grid(numrows, 2);
        HTMLTable.CellFormatter fmt = g.getCellFormatter();
        g.setWidget(0, 0, new Label("Registration #:"));
        rosterId = new Label("1234");
        g.setWidget(0, 1, rosterId);
        g.setWidget(1, 0, new Label("Transaction #:"));
        transactionId = new Label("8TE412ER4091Q12EC72MMN");
        g.setWidget(1, 1, transactionId);
        g.setWidget(2, 0, new Label("Amount paid:"));
        amountPaid = new Label("$77.00");
        g.setWidget(2, 1, amountPaid);
        g.setWidget(3, 0, new Label("Payment status:"));
        status = new Label("Completed");
        g.setWidget(3, 1, status);
        
//        accountButton = new Button("Create Account");
//        accountButton.addClickHandler(new ClickHandler() {
//            public void onClick(ClickEvent event) {
//                createAccount();
//            }
//        });
//        g.setWidget(8, 0, accountButton);

        // Set the css style for each row
        for (int row=0; row < numrows; row++) {
            fmt.addStyleName(row, 0,  "jsc-fieldlabel");
            fmt.addStyleName(row, 1,  "jsc-field");
        }
        
        accountPanel.add(g);
        
        screen.add(accountPanel);
    }
    
    /**
     * Called when the create account button is pressed, and contacts the server
     * to update the account information in the database.
     */
    private void createAccount() {
        GWT.log("Creating account...", null);
    
        // Gather information from the form
        String fname = rosterId.getText();
        String mname = transactionId.getText();
        String lname = amountPaid.getText();
        
        // Validate necessary input, making sure required fields are included
//        boolean isValid = true;
//        String[] fields = {fname, lname, email, birthday, homephone};
//        if (fieldMissing( fields )) {
//            isValid = false;
//            setMessage("Missing required information. Please fill in all fields.");
//            return;
//        }

        // We only need a password if its a new account or the user is
        // providing a new one; in either case, the retyped password must match
        boolean isValid = true;
        if (!loginSession.isAuthenticated()) {
            isValid = false;
            setMessage("User is not logged in.");
            return;
        }
        
        /*// create Person object
        Person person = null;
        if (isValid) {
            //clearMessage();
            person = new Person(fname, mname, lname);
            if (loginSession.isAuthenticated()) {
                person.setPid(loginSession.getPerson().getPid());
                person.setPassword(loginSession.getPerson().getPassword());
            } else {
                // Set the PID to 0 to indicate this is an update
                person.setPid(0);
            }
            if (loginSession.isAuthenticated() && !email.equals(loginSession.getPerson().getEmail())) {
                person.setEmail(loginSession.getPerson().getEmail());
                person.setNewEmail(email);
            } else {
                person.setEmail(email);
            }
            
            person.setBday(birthday);
            person.setHomephone(homephone);
            if (pw1 != null) {
                person.setNewPassword(pw1);
            }
        } else {
            GWT.log("Account NOT created: " + fname + " " + mname + " " + lname, null);
            return;
        }
        
        // Initialize the service proxy.
        if (regService == null) {
            regService = GWT.create(SkaterRegistrationService.class);
        }

        // Set up the callback object.
        AsyncCallback<Person> callback = new AsyncCallback<Person>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
                GWT.log("Failed to create account.", null);
            }

            public void onSuccess(Person newPerson) {
                if (newPerson == null) {
                    // Failure on the remote end.
                    setMessage("Failed to create or update account.");
                    return;
                }
                
                GWT.log("Account created: " + newPerson.getPid(), null);
                if (loginSession.isAuthenticated() &&
                        loginSession.getPerson().getPid() == newPerson.getPid()) {
                    setMessage("Settings saved.");
                    // Update the loginSession with the new Person object
                    loginSession.setPerson(newPerson);
                } else {
                    // Change our application state to the login screen
                    setMessage("Account created. Please sign in.");
                    History.newItem("signout");
                }
            }
        };

        // Make the call to the registration service.
        regService.createAccount(person, callback);*/
    }
    
    /**
     * Check if each String in the array is non-null and has length > 0.
     * @param fields the array of fields to be checked
     * @return true if any field is null or zero length, otherwise false
     */
    private boolean fieldMissing(String[] fields) {
        boolean isMissing = false;
        for (String field : fields) {
            if (field == null || field.length() == 0) {
                isMissing = true;
                return isMissing;
            }
        }
        return isMissing;
    }
}
