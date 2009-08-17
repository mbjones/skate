package org.jsc.client;

import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
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
                register();
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
        // This is the format of the return field, which is needed to get return data back 
        // about transactions.  The URL should include transaction information
        // "<input type=\"hidden\" name=\"cmd\" value=\"_s-xclick\">" +
        // "<input type=\"hidden\" name=\"hosted_button_id\" value=\"1059849\">" +
        // "<input type=\"hidden\" name=\"cpp_header_image\" value=\"http://juneauskatingclub.org/sites/all/themes/jsc/images/salamander1/jsc-header-bkg-paypal.png\">" +
        String paymentDataIdentity = "U7fGZVYSiD6KerUAg_PhVMlmWIkK1MM2WazdncZQz_v4Dx4HIpre8iyz92e";
        
        String testForm = 
            "<form action=\"https://www.sandbox.paypal.com/cgi-bin/webscr\" method=\"post\">" +
            "<input type=\"hidden\" name=\"cmd\" value=\"_xclick\">" +
            "<input type=\"hidden\" name=\"business\" value=\"james_1202254981_biz@gmail.com\">" +
            "<input type=\"hidden\" name=\"item_name\" value=\"JSC Skating class registration\">" +
            "<input type=\"hidden\" name=\"currency_code\" value=\"USD\">" +
            "<input type=\"hidden\" name=\"item_number\" value=\"987654\">" +
            "<input type=\"hidden\" name=\"amount\" value=\"60\">" +
            "<input type=\"hidden\" name=\"no_note\" value=\"1\">" +
            "<input type=\"hidden\" name=\"no_shipping\" value=\"1\">" +
            "<input type=\"hidden\" name=\"return\" value=\"http://juneauskatingclub.org/home\">" +
            "<input type=\"hidden\" name=\"cancel_return\" value=\"http://juneauskatingclub.org/registration\">" +
            "<input type=\"image\" src=\"https://www.sandbox.paypal.com/en_US/i/btn/btn_paynowCC_LG.gif\" border=\"0\" name=\"submit\" alt=\"PayPal - The safer, easier way to pay online!\">" +
            "<img alt=\"\" border=\"0\" src=\"https://www.sandbox.paypal.com/en_US/i/scr/pixel.gif\" width=\"1\" height=\"1\">" +
            "</form>";
        
        HTMLPanel paymentPanel = new HTMLPanel(testForm);
        screen.add(paymentPanel);
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

    /**
     * Register for a class by setting a RosterEntry object from the form and
     * pass it to the remote registration service.
     */
    private void register() {
        GWT.log("Registering for a class...", null);
        
        if (loginSession.isAuthenticated()) {
            // Gather information from the form
            String selectedClassId = classField.getValue(classField.getSelectedIndex());
            Person registrant = loginSession.getPerson();
            RosterEntry entry = new RosterEntry();
            entry.setClassid(new Long(selectedClassId).longValue());
            entry.setPid(registrant.getPid());
            entry.setPayment_amount(75.00);
            
            // Initialize the service proxy.
            if (regService == null) {
                regService = GWT.create(SkaterRegistrationService.class);
            }

            // Set up the callback object.
            AsyncCallback<RosterEntry> callback = new AsyncCallback<RosterEntry>() {
                public void onFailure(Throwable caught) {
                    // TODO: Do something with errors.
                    GWT.log("Failed to register the RosterEntry.", null);
                }

                public void onSuccess(RosterEntry newEntry) {
                    if (newEntry == null) {
                        // Failure on the remote end.
                        setMessage("Error registering for the class.");
                        return;
                    } else {
                        setMessage("Success. Thank you for registering. ("
                                + newEntry.getRosterid() + ")");
                    }
                }
            };

            // Make the call to the registration service.
            regService.register(loginSession.getPerson(), entry, callback);
            
        } else {
            GWT.log("Error: Can not register without first signing in.", null);
        }
    }
}
