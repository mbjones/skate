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
 * @author Matt Jones
 */
public class ConfirmScreen extends BaseScreen {

    private static final String CONFIRM_MESSAGE = "<p class=\"jsc-text\">Thank you for registering.  Your payment in the amount shown below will be processed by PayPal.</p><p class=\"jsc-text\">Please review and keep the information in the table below for your records.  It is the receipt for your registration.</p></div>";

    
    // ?tx=89009871KK074674R&st=Completed&amt=77.00&cc=USD&cm=&item_number=7
    // item_number is blank if coming from a cart rather than a single item purchase
    private String rosterId;
    private String transactionId;
    private String amountPaid;
    private String status;
    private Label rosterIdField;
    private Label transactionIdField;
    private Label amountPaidField;
    private Label statusField;
    private Button accountButton;
    private HorizontalPanel screen;

    
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
        //g.setWidget(0, 0, new Label("Registration #:"));
        //rosterIdField = new Label("1234");
        //g.setWidget(0, 1, rosterIdField);
        g.setWidget(1, 0, new Label("Transaction #:"));
        transactionIdField = new Label("8TE412ER4091Q12EC72MMN");
        g.setWidget(1, 1, transactionIdField);
        g.setWidget(2, 0, new Label("Amount paid:"));
        amountPaidField = new Label("$77.00");
        g.setWidget(2, 1, amountPaidField);
        g.setWidget(3, 0, new Label("Payment status:"));
        statusField = new Label("Completed");
        g.setWidget(3, 1, statusField);

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
        String fname = rosterIdField.getText();
        String mname = transactionIdField.getText();
        String lname = amountPaidField.getText();

        // We only need a password if its a new account or the user is
        // providing a new one; in either case, the retyped password must match
        boolean isValid = true;
        if (!loginSession.isAuthenticated()) {
            isValid = false;
            setMessage("User is not logged in.");
            return;
        }
    }

    /**
     * @return the rosterId
     */
    public String getRosterId() {
        return rosterId;
    }

    /**
     * @param rosterId the rosterId to set
     */
    public void setRosterId(String rosterId) {
        this.rosterId = rosterId;
        rosterIdField.setText(rosterId);
    }

    /**
     * @return the transactionId
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId the transactionId to set
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        transactionIdField.setText(transactionId);
    }

    /**
     * @return the amountPaid
     */
    public String getAmountPaid() {
        return amountPaid;
    }

    /**
     * @param amountPaid the amountPaid to set
     */
    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
        amountPaidField.setText(amountPaid);
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
        statusField.setText(status);
    }
}
