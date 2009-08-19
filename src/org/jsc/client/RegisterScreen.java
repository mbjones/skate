package org.jsc.client;

import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
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

    private static final String MERCHANT_ID = "339U3JVK2X4E6";
    private static final String PAYPAL_URL = "https://www.sandbox.paypal.com/cgi-bin/webscr";
    private static final String PAYPAL_HEADER_IMAGE = "http://juneauskatingclub.org/sites/all/themes/jsc/images/salamander1/jsc-header-bkg-paypal.png";
    private static final String PAYPAL_RETURN_URL = "http://reg.juneauskatingclub.org";
    private static final String PAYPAL_CANCEL_URL = "http://reg.juneauskatingclub.org";
    private static final String PAYMENT_DATA_ID = "U7fGZVYSiD6KerUAg_PhVMlmWIkK1MM2WazdncZQz_v4Dx4HIpre8iyz92e";
    private static final String PRICE = "77";
    
    private static final String HTML_STRUCTURE = "<div id=\"explainstep\"></div><div id=\"wizard\"></div>";
    private static final String STEP_1 = "<div id=\"explainstep\"><p class=\"jsc-step\">Step 1: Choose a class</p><p class=\"jsc-text\">After you choose a class, you will be prompted to make payment through PayPal.</p><p class=\"jsc-text\">Registrion fee: <b>$77.00</b></p></div>";
    private static final String STEP_2 = "<div id=\"explainstep\"><p class=\"jsc-step\">Step 2: Process payment</p><p class=\"jsc-text\">Registrion fee: <b>$77.00</b></p><p class=\"jsc-text\">Please make your payment using PayPal by clicking on the button below.  Your registration is <em>not complete</em> until after you have completed payment.</p><p class=\"jsc-text\">When you click \"Pay Now\" below, you will be taken to the PayPal site to make payment.  PayPal will allow you to pay by credit card or using your bank account, among other options.  Once the payment has been made, you will be returned to this site and your registration will be complete.</p></div>";
    
    // classList stores the classes keyed on the name string for name-based sorting
    private TreeMap<String, String> classList;
    // classKeyList has the same data as classList but is keyed on the classid for ease of lookup
    private TreeMap<String, String> classKeyList;
    
    private HorizontalPanel screen;
    private HorizontalPanel outerRegPanel;
    private HTMLPanel regPanel;
    private Grid reggrid;
    private HTMLPanel gridWrapper;
    private ListBox classField;
//    private TextBox fnameField;
//    private TextBox mnameField;
//    private TextBox lnameField;
//    private TextBox emailField;
//    private TextBox birthdayField;
//    private TextBox homephoneField;
//    private PasswordTextBox password1Field;
//    private PasswordTextBox password2Field;
    private Button registerButton;
    
    private SkaterRegistrationServiceAsync regService;

    public RegisterScreen(LoginSession loginSession) {
        super(loginSession);
        classList = new TreeMap<String, String>();
        classKeyList = new TreeMap<String, String>();
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
        
        outerRegPanel = new HorizontalPanel();
        outerRegPanel.addStyleName("jsc-rightpanel");
        
        int numrows = 9;
        
        reggrid = new Grid(numrows, 2);

        HTMLTable.CellFormatter fmt = reggrid.getCellFormatter();
        reggrid.setWidget(0, 0, new Label("Class:"));
        classField = new ListBox();
        updateClassListBox();
        classField.setVisibleItemCount(1);
        reggrid.setWidget(0, 1, classField);
        
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
        registerButton = new Button("Go to Step 2");
        registerButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                register();
            }
        });
        reggrid.setWidget(8, 1, registerButton);

        // Set the css style for each row
        for (int row=0; row < numrows; row++) {
            fmt.addStyleName(row, 0,  "jsc-fieldlabel");
            fmt.addStyleName(row, 1,  "jsc-field");
        }
        
        regPanel = new HTMLPanel(HTML_STRUCTURE);
        regPanel.addAndReplaceElement(new HTMLPanel(STEP_1), "explainstep");
        gridWrapper = new HTMLPanel("<div id=\"wizard\"></div>");
        gridWrapper.add(reggrid, "wizard");
        regPanel.addAndReplaceElement(gridWrapper, "wizard");
        outerRegPanel.add(regPanel);
        screen.add(outerRegPanel);
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
                    // Assign the classList, and populate the classKeyList by iterating
                    // over all keys and reversing the keys and values in the new Map
                    classList = list;
                    for (Map.Entry<String, String> curClass : classList.entrySet()) {
                        classKeyList.put(curClass.getValue(), curClass.getKey());
                    }
                    updateClassListBox();
                    //regPanel.addAndReplaceElement(gridWrapper, "wizard");
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
                        clearMessage();
                        String testForm = 
                            "<form id=\"wizard\" action=\""+ PAYPAL_URL + "\" method=\"post\">" +
                            "<input type=\"hidden\" name=\"cmd\" value=\"_xclick\">" +
                            "<input type=\"hidden\" name=\"business\" value=\"" + MERCHANT_ID + "\">" +
                            "<input type=\"hidden\" name=\"item_name\" value=\"" + classKeyList.get(new Long(newEntry.getClassid()).toString()) + "\">" +
                            "<input type=\"hidden\" name=\"currency_code\" value=\"USD\">" +
                            "<input type=\"hidden\" name=\"item_number\" value=\""+ newEntry.getRosterid() +"\">" +
                            "<input type=\"hidden\" name=\"amount\" value=\"" + PRICE + "\">" +
                            "<input type=\"hidden\" name=\"no_note\" value=\"1\">" +
                            "<input type=\"hidden\" name=\"no_shipping\" value=\"1\">" +
                            "<input type=\"hidden\" name=\"cpp_header_image\" value=\""+ PAYPAL_HEADER_IMAGE + "\">" +
                            "<input type=\"hidden\" name=\"return\" value=\"" + PAYPAL_RETURN_URL + "\">" +
                            "<input type=\"hidden\" name=\"cancel_return\" value=\"" + PAYPAL_CANCEL_URL + "\">" +
                            "<input type=\"image\" src=\"https://www.sandbox.paypal.com/en_US/i/btn/btn_paynow_LG.gif\" border=\"0\" name=\"submit\" alt=\"PayPal - The safer, easier way to pay online!\">" +
                            "<img alt=\"\" border=\"0\" src=\"https://www.sandbox.paypal.com/en_US/i/scr/pixel.gif\" width=\"1\" height=\"1\">" +
                            "</form>";
                        
                        regPanel.addAndReplaceElement(new HTMLPanel(STEP_2), "explainstep");
                        regPanel.addAndReplaceElement(new HTMLPanel(testForm), "wizard");
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
