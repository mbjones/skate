package org.jsc.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import org.jsc.client.event.RosterChangeEvent;
import org.jsc.client.event.RosterChangeHandler;
import org.jsc.client.event.SkatingClassChangeEvent;
import org.jsc.client.event.SkatingClassChangeHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
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
 * A specialization of BaseScreen to represent the "My Classes" screen in the 
 * application.  This user interface class provides a way to browse through the
 * classes for a student.
 * 
 * @author Matt Jones
 */
public class MyClassesScreen extends BaseScreen {
    
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
    private RosterModel rosterModel;
    
    // sessionClassLabels has the same data as sessionClassList but is keyed on 
    // the classid for ease of lookup of the label associated with a class
    private TreeMap<String, String> sessionClassLabels;
    
    private HorizontalPanel screen;
    private HorizontalPanel outerRegPanel;
    private HTMLPanel regPanel;
    private Grid classesGrid;
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
    
    public MyClassesScreen(LoginSession loginSession, HandlerManager eventBus,
            ClassListModel sessionClassList, RosterModel rosterModel) {
        super(loginSession);
        
        this.sessionClassList = sessionClassList;
        this.rosterModel = rosterModel;
        sessionClassLabels = new TreeMap<String, String>();
        layoutScreen();
        this.setContentPanel(screen);
        regService = GWT.create(SkaterRegistrationService.class);
        
        // Register as a handler for Skating class changes, and handle those changes
        eventBus.addHandler(RosterChangeEvent.TYPE, new RosterChangeHandler(){
            public void onRosterChange(RosterChangeEvent event) {
                updateRosterTable(event);
            }
        });
    }
    
    /**
     * Whenever the roster changes, update the grid of classes
     * @param event the event signaling that the roster has changed
     */
    private void updateRosterTable(RosterChangeEvent event) {
        // Remove all of the rows from the table, except the header
        int rows = classesGrid.getRowCount();
        // TODO: some problem with stepping through these rows and removing them
        for (int i = rows-1; i > 0; i--) {
            GWT.log("Removing table row: " + i, null);
            classesGrid.removeRow(i);
        }
        // Add all of the new roster entries into the table
        for (RosterEntry entry : event.getRoster()) {
            // TODO: Get the class label
            Label classNameLabel = new Label(Long.toString(entry.getClassid()));
            // TODO: some problem parsing the date string -- use GWTDatFormat I think
            //Label datePaidLabel = new Label(entry.getPayment_date().toString());
            Label datePaidLabel = new Label("2021-10-19");
            Label amountPaidLabel = new Label(Double.toString(entry.getPayment_amount()));
            Label levelPassedLabel = new Label(entry.getLevelpassed());
            addToGrid(classNameLabel, datePaidLabel, amountPaidLabel, levelPassedLabel);
            // TODO: assign an appropriate CSS style to the row
        }
    }

    /**
     * Lay out the user interface widgets on the screen.
     */
    private void layoutScreen() {
        this.setScreenTitle("My Classes");
        this.setStyleName("jsc-onepanel-screen");
        
        screen = new HorizontalPanel();
        
        outerRegPanel = new HorizontalPanel();
        outerRegPanel.addStyleName("jsc-rightpanel");
                
        classesGrid = new Grid(0, 4);
      
        HTMLTable.CellFormatter fmt = classesGrid.getCellFormatter();
        
        // Add a header row to the table
        Label classNameLabel = new Label("Class Name");
        Label datePaidLabel = new Label("Date Paid");
        Label amountPaidLabel = new Label("Amount Paid");
        Label levelPassedLabel = new Label("Level Passed");
        addToGrid(classNameLabel, datePaidLabel, amountPaidLabel, levelPassedLabel);

        // Set the css style for each row
        for (int row=0; row < classesGrid.getRowCount(); row++) {
            fmt.addStyleName(row, 0,  "jsc-fieldlabel");
            fmt.addStyleName(row, 1,  "jsc-field");
        }
        
        regPanel = new HTMLPanel(HTML_STRUCTURE);
        regPanel.addAndReplaceElement(new HTMLPanel(STEP_1), "explainstep");
        gridWrapper = new HTMLPanel("<div id=\"wizard\"></div>");
        gridWrapper.add(classesGrid, "wizard");
        regPanel.addAndReplaceElement(gridWrapper, "wizard");
        outerRegPanel.add(regPanel);
        screen.add(outerRegPanel);
    }
    
    /**
     * Add the given widget to the grid table. Four columns are set up, and
     * each widget is assigned to one column.
     * @param widget0 the widget to display in column 0
     * @param widget1 the widget to display in column 1
     * @param widget2 the widget to display in column 2
     * @param widget3 the widget to display in column 3
     */
    private void addToGrid(Widget widget0, Widget widget1, Widget widget2, Widget widget3) {
        int newRow = classesGrid.insertRow(classesGrid.getRowCount());
        classesGrid.setWidget(newRow, 0, widget0);
        classesGrid.setWidget(newRow, 1, widget1);
        classesGrid.setWidget(newRow, 2, widget2);
        classesGrid.setWidget(newRow, 3, widget3);
    }
    
    /**
     * Remove the current list of classes from the box and replace with the 
     * classes that are currently present in sessionClassList.
     */
    /*
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
*/
    /**
     * Register for a class by creating a RosterEntry object from the form input
     * and pass it to the remote registration service.
     */
    /*
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
*/
}
