package org.jsc.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * A set of panels to allow users to register for a class.  Collects the information
 * needed for registration, and then redirects the user to PayPal for payment.
 * @author Matt Jones
 */
public class RegisterScreen extends BaseScreen {

    private static final String MERCHANT_ID = "339U3JVK2X4E6";
    private static final String PAYPAL_URL = "https://www.sandbox.paypal.com/cgi-bin/webscr";
    private static final String PAYPAL_HEADER_IMAGE = "http://juneauskatingclub.org/sites/all/themes/jsc/images/salamander1/jsc-header-bkg-paypal.png";
    private static final String PAYPAL_RETURN_URL = "http://reg.juneauskatingclub.org";
    private static final String PAYPAL_CANCEL_URL = "http://reg.juneauskatingclub.org";
    private static final String PAYMENT_DATA_ID = "U7fGZVYSiD6KerUAg_PhVMlmWIkK1MM2WazdncZQz_v4Dx4HIpre8iyz92e";
    private static final int EARLY_PRICE_GRACE_DAYS = 2;
    private static final double MILLISECS_PER_DAY = 24*60*60*1000;
    private static final double EARLY_PRICE = 70.00;
    private static final double STANDARD_PRICE = 80.00;
    
    private static final String HTML_STRUCTURE = "<div id=\"explainstep\"></div><div id=\"wizard\"></div>";
    private static final String DISCOUNT_EXPLANATION = "<p class=\"jsc-text\">Because it helps with planning our class sizes, <b>we offer a discount for those who register early</b> (more than " + EARLY_PRICE_GRACE_DAYS + " days before the session starts).</p>";
    private static final String STEP_1 = "<div id=\"explainstep\"><p class=\"jsc-step\">Step 1: Choose a class</p><p class=\"jsc-text\">After you choose a class, you will be prompted to make payment through PayPal.</p>" + DISCOUNT_EXPLANATION + "</div>";
    private static final String STEP_2 = "<div id=\"explainstep\"><p class=\"jsc-step\">Step 2: Process payment</p><p class=\"jsc-text\">You must make your payment using PayPal by clicking on the button below.  <b>Your registration is <em>not complete</em></b> until after you have completed payment.</p><p class=\"jsc-text\">When you click \"Pay Now\" below, you will be taken to the PayPal site to make payment.  PayPal will allow you to pay by credit card or using your bank account, among other options.  Once the payment has been made, you will be returned to this site and your registration will be complete.</p></div>";
    
    private ClassListModel sessionClassList;
    // sessionClassLabels has the same data as sessionClassList but is keyed on 
    // the classid for ease of lookup of the label associated with a class
    private TreeMap<String, String> sessionClassLabels;
    
    private HorizontalPanel screen;
    private HorizontalPanel outerRegPanel;
    private HTMLPanel regPanel;
    private Grid reggrid;
    private HTMLPanel gridWrapper;
    private Label feeLabel;
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
    private double cost;    
    private SkaterRegistrationServiceAsync regService;
    private String costFormatted;
    private int numGridRows;

    public RegisterScreen(LoginSession loginSession, ClassListModel sessionClassList) {
        super(loginSession);
        this.sessionClassList = sessionClassList;
        sessionClassLabels = new TreeMap<String, String>();
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
        
        numGridRows = 0;
        
        reggrid = new Grid(11, 2);

        HTMLTable.CellFormatter fmt = reggrid.getCellFormatter();
        
        feeLabel = new Label("");
        addToGrid("Registration fee:", feeLabel);

        addToGrid(" ", new Label(" "));
        
        classField = new ListBox();
        classField.setVisibleItemCount(1);
        addToGrid("Class:", classField);
        
        addToGrid(" ", new Label(" "));
        
        CheckBox fsArtistry = new CheckBox();
        fsArtistry.setValue(false, false);
        addToGrid("Figure Skating Artistry:", fsArtistry);

        CheckBox fsMitf = new CheckBox();
        fsMitf.setValue(false, false);
        addToGrid("Figure Skating Moves in the Field:", fsMitf);
        
        CheckBox fsSynchro = new CheckBox();
        fsSynchro.setValue(false, false);
        addToGrid("Figure Skating Synchro:", fsSynchro);
        
        CheckBox fsClubIce = new CheckBox();
        fsClubIce.setValue(false, false);
        addToGrid("Figure Skating Club Ice:", fsClubIce);
        
        addToGrid(" ", new Label(" "));
        
        CheckBox membership = new CheckBox();
        membership.setValue(false, false);
        addToGrid("JSC Club Membership:", membership);
        
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
        addToGrid(" ", registerButton);

        // Set the css style for each row
        for (int row=0; row < numGridRows; row++) {
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
     * Add the given widget to the grid table along with a label.  the Label is
     * placed in column 1 of the grid, and the widget in column 2.
     * @param label the label to display in column 1
     * @param widget the widget to display in column 2
     */
    private void addToGrid(String label, Widget widget) {
        reggrid.setWidget(numGridRows, 0, new Label(label));
        reggrid.setWidget(numGridRows, 1, widget);
        numGridRows++;
    }
    
    /**
     * Remove the current list of classes from the box and replace with the 
     * classes that are currently present in sessionClassList.
     */
    protected void updateRegistrationScreenDetails() {
        // Change the list of classes to reflect the new data
        classField.clear();
        sessionClassLabels = new TreeMap<String, String>();
        ArrayList<SessionSkatingClass> list = sessionClassList.getClassList();
        if (list != null) {
            for (SessionSkatingClass curClass : list) {
                GWT.log("SessionClass: " + (new Long(curClass.getClassId()).toString()) + " " + curClass.getClassType(), null);
                StringBuffer classLabel = new StringBuffer(curClass.getSeason());
                classLabel.append(" Session ").append(curClass.getSessionNum());
                classLabel.append(" ").append(curClass.getClassType());
                classLabel.append(" (").append(curClass.getDay());
                classLabel.append(" ").append(curClass.getTimeslot()).append(")");
                classField.addItem(classLabel.toString(), new Long(curClass.getClassId()).toString());
                sessionClassLabels.put(new Long(curClass.getClassId()).toString(), classLabel.toString());
            }
        }
        
        cost = STANDARD_PRICE;
        String costExplanation = "";
        String sessionStart = list.get(0).getStartDate();
        DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd");
        if (sessionStart != null) {
            NumberFormat numfmt = NumberFormat.getCurrencyFormat();
            Date startDate = fmt.parse(sessionStart);
            Date today = new Date(System.currentTimeMillis());
            if (today.before(startDate) && 
               (startDate.getTime() - today.getTime() > EARLY_PRICE_GRACE_DAYS*MILLISECS_PER_DAY)) {
                cost = EARLY_PRICE;
                String normalPrice = numfmt.format(STANDARD_PRICE);
                costExplanation = " [Early registration discount applies (normally " + normalPrice + ").]";
            } else {
                cost = STANDARD_PRICE; 
                costExplanation = " (Standard price applied.)";
            }
            
            costFormatted = numfmt.format(cost);
            String feeText = costFormatted + costExplanation;
            feeLabel.setText(feeText);
        }
    }

    /**
     * Register for a class by creating a RosterEntry object from the form input
     * and pass it to the remote registration service.
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
            entry.setPayment_amount(cost);
            
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
                        // TODO: Need to reset the form and return to the main menu
                        return;
                    } else {
                        clearMessage();
                        String testForm = 
                            "<form id=\"wizard\" action=\""+ PAYPAL_URL + "\" method=\"post\">" +
                            "<input type=\"hidden\" name=\"cmd\" value=\"_xclick\">" +
                            "<input type=\"hidden\" name=\"business\" value=\"" + MERCHANT_ID + "\">" +
                            "<input type=\"hidden\" name=\"item_name\" value=\"" + sessionClassLabels.get(new Long(newEntry.getClassid()).toString()) + "\">" +
                            "<input type=\"hidden\" name=\"currency_code\" value=\"USD\">" +
                            "<input type=\"hidden\" name=\"item_number\" value=\""+ newEntry.getRosterid() +"\">" +
                            "<input type=\"hidden\" name=\"amount\" value=\"" + cost + "\">" +
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
