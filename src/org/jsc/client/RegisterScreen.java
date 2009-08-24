package org.jsc.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A set of panels to allow users to register for a class.  Collects the information
 * needed for registration, and then redirects the user to PayPal for payment.
 * @author Matt Jones
 */
public class RegisterScreen extends BaseScreen implements ValueChangeHandler {

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
    private static final String PRICE_EXPLANATION = "<div id=\"explainstep\"><p class=\"jsc-text\">After you choose a class, you will be prompted to make payment through PayPal.</p>" + DISCOUNT_EXPLANATION + "</div>";
    private static final String PAYPAL_EXPLANATION = "<div id=\"explainstep\"><p class=\"jsc-text\">You must make your payment using PayPal by clicking on the button below.  <b>Your registration is <em>not complete</em></b> until after you have completed payment.</p><p class=\"jsc-text\">When you click \"Pay Now\" below, you will be taken to the PayPal site to make payment.  PayPal will allow you to pay by credit card or using your bank account, among other options.  Once the payment has been made, you will be returned to this site and your registration will be complete.</p></div>";
    private static final String BS_EXPLANATION = "Basic Skills is our Learn to Skate program. These group lessons are based on the United States Figure Skating's (USFSA) Basic Skills Program. This is a nationwide, skills-based, graduated series of instruction for youth and adult skaters. This program is designed to teach all skaters the fundamentals of skating.";
    private static final String FS_EXPLANATION = "Figure Skating is a one- to five-day-a-week program for skaters that have completed all of the Basic Skills Levels (8) or Adult Levels (4). Students work individually with their coach to develop their skills. Please only sign up for Figure Skating Classes if you have graduated from the Basic Skills program.";

    private static final String STEP_1 = "Step 1: Choose a class";
    private static final String STEP_2 = "Step 2: Process payment";
    
    private ClassListModel sessionClassList;
    // sessionClassLabels has the same data as sessionClassList but is keyed on 
    // the classid for ease of lookup of the label associated with a class
    private TreeMap<String, String> sessionClassLabels;
    
    private HorizontalPanel screen;
    private VerticalPanel outerVerticalPanel;
    private HorizontalPanel outerHorizPanel;
    private RadioButton bsRadio;
    private RadioButton fsRadio;
    private HTMLPanel bsHTMLPanel;
    private VerticalPanel bsClassChoicePanel;
    private VerticalPanel bsPaymentPanel;
    private VerticalPanel bsLeftPanel;
    private VerticalPanel fsRightPanel;
    private Label stepLabel;
    private Grid basicSkillsGrid;
    private Grid figureSkatingGrid;
    private HTMLPanel gridWrapper;
    private Label feeLabel;
    private ListBox classField;
    private Button registerButton;
    private double cost;    
    private SkaterRegistrationServiceAsync regService;
    private String costFormatted;

    /**
     * Construct the Registration view and controller used to display a form for
     * registering for skating classes.
     * @param loginSession the authenticated login session for submissions to the remote service
     * @param sessionClassList the model of skating classes
     */
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
                        
        basicSkillsGrid = new Grid(0, 2);
        
        feeLabel = new Label("");
        addToBSGrid("Registration fee:", feeLabel);

        addToBSGrid(" ", new Label(" "));
        
        classField = new ListBox();
        classField.setVisibleItemCount(1);
        addToBSGrid("Class:", classField);
        
        bsClassChoicePanel = new VerticalPanel();
        bsClassChoicePanel.add(new HTMLPanel(PRICE_EXPLANATION));
        bsClassChoicePanel.add(basicSkillsGrid);
        
        bsPaymentPanel = new VerticalPanel();
        bsPaymentPanel.setVisible(false);
        
        bsHTMLPanel = new HTMLPanel(HTML_STRUCTURE);
//        bsHTMLPanel.addAndReplaceElement(new HTMLPanel(PRICE_EXPLANATION), "explainstep");
//        gridWrapper = new HTMLPanel("<div id=\"wizard\"></div>");
//        gridWrapper.add(basicSkillsGrid, "wizard");
//        bsHTMLPanel.addAndReplaceElement(gridWrapper, "wizard");
        bsLeftPanel = new VerticalPanel();
        Label bscTitle = new Label("Basic Skills Classes");
        bscTitle.addStyleName("jsc-fieldlabel-left");
        bsLeftPanel.add(bscTitle);
        Label bscDescription = new Label(BS_EXPLANATION);
        bscDescription.addStyleName("jsc-text");
        bsLeftPanel.add(bscDescription);
//        bsLeftPanel.add(bsHTMLPanel);
        bsLeftPanel.add(bsClassChoicePanel);
        bsLeftPanel.add(bsPaymentPanel);
        
        fsRightPanel = new VerticalPanel();
        Label fscTitle = new Label("Figure Skating Classes");
        fscTitle.addStyleName("jsc-fieldlabel-left");
        fsRightPanel.add(fscTitle);
        Label fscDescription = new Label(FS_EXPLANATION);
        fscDescription.addStyleName("jsc-text");
        fsRightPanel.add(fscDescription);
        figureSkatingGrid = new Grid(0, 2);
        fsRightPanel.add(figureSkatingGrid);
        fsRightPanel.setVisible(false);
        
        registerButton = new Button("Go to Step 2");
        registerButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                register();
            }
        });
        registerButton.addStyleName("jsc-button-right");
        
        outerVerticalPanel = new VerticalPanel();
        stepLabel = new Label(STEP_1);
        stepLabel.addStyleName("jsc-step");
        outerVerticalPanel.add(stepLabel);
        
        bsRadio = new RadioButton("BSorFSGroup", "Basic Skills Classes");
        bsRadio.addValueChangeHandler(this);
        bsRadio.setValue(true);
        fsRadio = new RadioButton("BSorFSGroup", "Figure Skating Classes");
        fsRadio.addValueChangeHandler(this);
        outerVerticalPanel.add(bsRadio);
        outerVerticalPanel.add(fsRadio);
        
        outerHorizPanel = new HorizontalPanel();
        outerHorizPanel.add(bsLeftPanel);
        outerHorizPanel.add(fsRightPanel);
        outerVerticalPanel.add(outerHorizPanel);
        outerVerticalPanel.add(registerButton);
        outerVerticalPanel.addStyleName("jsc-rightpanel");
        screen.add(outerVerticalPanel);
    }
    
    /**
     * Add the given widget to the grid table along with a label.  The Label is
     * placed in column 1 of the grid, and the widget in column 2.
     * @param label the label to display in column 1
     * @param widget the widget to display in column 2
     */
    private void addToBSGrid(String label, Widget widget) {
        int newRow = basicSkillsGrid.insertRow(basicSkillsGrid.getRowCount());
        basicSkillsGrid.setWidget(newRow, 0, new Label(label));
        basicSkillsGrid.setWidget(newRow, 1, widget);
        HTMLTable.CellFormatter fmt = basicSkillsGrid.getCellFormatter();
        fmt.addStyleName(newRow, 0,  "jsc-fieldlabel");
        fmt.addStyleName(newRow, 1,  "jsc-field");
    }
    
    /**
     * Add the given widget to the Figure Skating grid table along with a label.  
     * The Label is placed in column 2 of the grid, and the widget in column 1.
     * @param label the label to display in column 2
     * @param widget the widget to display in column 1
     */
    private void addToFSGrid(String label, Widget widget) {
        int newRow = figureSkatingGrid.insertRow(figureSkatingGrid.getRowCount());
        figureSkatingGrid.setWidget(newRow, 0, widget);
        figureSkatingGrid.setWidget(newRow, 1, new Label(label));
        HTMLTable.CellFormatter fmt = figureSkatingGrid.getCellFormatter();
        fmt.addStyleName(newRow, 0,  "jsc-fieldlabel");
        fmt.addStyleName(newRow, 1,  "jsc-field");
    }
    
    /**
     * Remove the current list of classes from the box and replace with the 
     * classes that are currently present in sessionClassList. Reset the 
     * registration form to the initial state of the application.
     */
    protected void updateRegistrationScreenDetails() {
        // Reset the form fields to begin registration
        bsPaymentPanel.setVisible(false);
        stepLabel.setText(STEP_1);
        bsRadio.setVisible(true);
        fsRadio.setVisible(true);
        bsClassChoicePanel.setVisible(true);
        registerButton.setVisible(true);
        
        // Clear the list of basic skills classes to reflect the new data
        classField.clear();
        sessionClassLabels = new TreeMap<String, String>();
        
        // Remove all of the rows from the table, except the header row
        int rows = figureSkatingGrid.getRowCount();
        for (int i = rows-1; i >= 0; i--) {
            GWT.log("Removing FS table row: " + i, null);
            figureSkatingGrid.removeRow(i);
        }

        // Iterate over the new list of classes, populating the right widgets
        ArrayList<SessionSkatingClass> list = sessionClassList.getClassList();
        if (list != null) {
            for (SessionSkatingClass curClass : list) {
                GWT.log("SessionClass: " + (new Long(curClass.getClassId()).toString()) + " " + curClass.getClassType(), null);
                String classLabel = curClass.formatClassLabel();
                
                // If it starts with "FS " it is a figure skating class
                if (curClass.getClassType().startsWith("FS ")) {
                    CheckBox checkbox = new CheckBox();
                    checkbox.setValue(false, false);
                    addToFSGrid(classLabel, checkbox);
                    
                // Otherwise it is a Basic Skills class
                } else {
                    classField.addItem(classLabel, new Long(curClass.getClassId()).toString());
                    sessionClassLabels.put(new Long(curClass.getClassId()).toString(), classLabel.toString());    
                }
            }
        }
        
        
        // Update the cost and paypal forms
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
                        registerButton.setVisible(false);
                        stepLabel.setText(STEP_2);
                        bsRadio.setVisible(false);
                        fsRadio.setVisible(false);
                        bsClassChoicePanel.setVisible(false);
                        bsPaymentPanel.clear();
                        bsPaymentPanel.setVisible(true);
                        bsPaymentPanel.add(new HTMLPanel(PAYPAL_EXPLANATION));
                        bsPaymentPanel.add(new HTMLPanel(testForm));
//                        bsHTMLPanel.addAndReplaceElement(new HTMLPanel(PAYPAL_EXPLANATION), "explainstep");
//                        bsHTMLPanel.addAndReplaceElement(new HTMLPanel(testForm), "wizard");
                    }
                }
            };

            // Make the call to the registration service.
            regService.register(loginSession.getPerson(), entry, callback);
            
        } else {
            GWT.log("Error: Can not register without first signing in.", null);
        }
    }

    public void onValueChange(ValueChangeEvent event) {
        Widget sender = (Widget) event.getSource();

        if (sender == bsRadio) {
            GWT.log("bsRadio clicked", null);
            bsLeftPanel.setVisible(true);
            fsRightPanel.setVisible(false);
        } else if (sender == fsRadio) {
            GWT.log("fsRadio clicked", null);
            bsLeftPanel.setVisible(false);
            fsRightPanel.setVisible(true);
        }

    }
}
