package org.jsc.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A screen to pay membership dues to become a member of the club.  Collects the information
 * needed for membership, and then redirects the user to PayPal for payment.
 * @author Matt Jones
 */
public class MemberScreen extends BaseScreen implements ValueChangeHandler<Boolean>, ChangeHandler {

    private static final String PAYPAL_EXPLANATION = "<div id=\"explainstep\"><p class=\"jsc-text\">You must make your payment using PayPal by clicking on the button below.  <b>Your membership is <em>not complete</em></b> until after you have completed payment.</p><p class=\"jsc-text\">When you click \"Pay Now\" below, you will be taken to the PayPal site to make payment.  PayPal will allow you to pay by credit card or using your bank account, among other options.  Once the payment has been made, you will be returned to this site and your registration will be complete.</p></div>";
    private static final String MMB_EXPLANATION = "Club membership provides many benefits, including reduced prices when registering for multiple figure skating classes, membership in the US Figure Skating Association (includes a subscription to 'Skating' magazine), and the ability to participate as a club member in testing and competitions.";
    private static final String PENDING = "Pending";
    
    private static final String STEP_1 = "Step 1: Select membership options";
    private static final String STEP_2 = "Step 2: Process payment";
    
    private HorizontalPanel screen;
    private VerticalPanel outerVerticalPanel;
    private HorizontalPanel outerHorizPanel;
    private VerticalPanel ppPaymentPanel;
    private VerticalPanel memberPanel;  
    private Label stepLabel;
    private Grid memberGrid;
    private Button registerButton;
    private SkaterRegistrationServiceAsync regService;
    private Label memberDues;
    private double totalCost;
    private Label totalCostLabel;
    private Label memberPaidLabel;
    private NumberFormat numfmt;
    private RadioButton singleMemberRadio;
    private RadioButton familyMemberRadio;
    
    /**
     * Construct the Member view and controller used to display a form for
     * registering for skating classes.
     * @param loginSession the authenticated login session for submissions to the remote service
     * @param sessionClassList the model of skating classes
     */
    public MemberScreen(LoginSession loginSession, HandlerManager eventBus) {
        super(loginSession, eventBus);
        numfmt = NumberFormat.getFormat("$#,##0.00");
        totalCost = 0;
        layoutScreen();
        this.setContentPanel(screen);
        regService = GWT.create(SkaterRegistrationService.class);        
    }
    
    /**
     * Lay out the user interface widgets on the screen.
     */
    private void layoutScreen() {
        this.setScreenTitle("Membership");
        this.setStyleName("jsc-onepanel-screen");
        
        screen = new HorizontalPanel();
                        
        layoutMemberPanel();
        
        ppPaymentPanel = new VerticalPanel();
        ppPaymentPanel.setVisible(false);
        
        registerButton = new Button("Continue");
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
                
        outerHorizPanel = new HorizontalPanel();
        outerHorizPanel.add(memberPanel);
        outerHorizPanel.add(ppPaymentPanel);
        outerVerticalPanel.add(outerHorizPanel);
        outerVerticalPanel.add(registerButton);
        outerVerticalPanel.addStyleName("jsc-rightpanel");
        screen.add(outerVerticalPanel);
    }
    
    /**
     * Create the widgets needed to populate the figure skating registration panel.
     */
    private void layoutMemberPanel() {
        
        // Create the panel, its labels, and the contained grid for layout
        memberPanel = new VerticalPanel();
        Label mmbTitle = new Label(" ");
        mmbTitle.addStyleName("jsc-fieldlabel-left");
        memberPanel.add(mmbTitle);
        Label mmbDescription = new Label(MMB_EXPLANATION);
        mmbDescription.addStyleName("jsc-text");
        memberPanel.add(mmbDescription);
        memberGrid = new Grid(0, 6);
        HTMLTable.CellFormatter fmt = memberGrid.getCellFormatter();

        // Insert the first row containing an indicator of membership or not.        
        int newRow = memberGrid.insertRow(memberGrid.getRowCount());
        memberPaidLabel = new Label(" ");
        memberGrid.setWidget(newRow, 3, memberPaidLabel);
        fmt.addStyleName(newRow, 2,  "jsc-fieldlabel");
        fmt.addStyleName(newRow, 3,  "jsc-field");
        fmt.addStyleName(newRow, 4,  "jsc-fieldlabel");
        fmt.addStyleName(newRow, 5,  "jsc-currencyfield");
        
        // Row containing the Single/Family radio buttons and price amount
        singleMemberRadio = new RadioButton("MemberTypeGroup", "Single Membership");
        singleMemberRadio.addValueChangeHandler(this);
        singleMemberRadio.setValue(true);
        familyMemberRadio = new RadioButton("MemberTypeGroup", "Family Membership");
        familyMemberRadio.addValueChangeHandler(this);
        memberDues = new Label();
        memberDues.setText(numfmt.format(AppConstants.MEMBERSHIP_SINGLE_PRICE));
        totalCost = AppConstants.MEMBERSHIP_SINGLE_PRICE;
        newRow = memberGrid.insertRow(memberGrid.getRowCount());
        memberGrid.setWidget(newRow, 2, singleMemberRadio);
        memberGrid.setWidget(newRow, 3, familyMemberRadio);
        memberGrid.setWidget(newRow, 4, new Label("Dues"));
        memberGrid.setWidget(newRow, 5, memberDues);
        fmt.addStyleName(newRow, 2,  "jsc-field");
        fmt.addStyleName(newRow, 3,  "jsc-field");
        fmt.addStyleName(newRow, 4,  "jsc-fieldlabel");
        fmt.addStyleName(newRow, 5,  "jsc-currencyfield");

        // Insert a spacer row before the row for totals
        newRow = memberGrid.insertRow(memberGrid.getRowCount());
        memberGrid.setWidget(newRow, 4, new Label(" "));
        memberGrid.setWidget(newRow, 5, new Label(" "));
        fmt.addStyleName(newRow, 4,  "jsc-fieldlabel");
        fmt.addStyleName(newRow, 5,  "jsc-currencyfield");
        
        // Insert the last row containing the payment total
        newRow = memberGrid.insertRow(memberGrid.getRowCount());
        totalCostLabel = new Label();
        totalCostLabel.setText(numfmt.format(totalCost));
        memberGrid.setWidget(newRow, 4, new Label("Total"));
        memberGrid.setWidget(newRow, 5, totalCostLabel);
        fmt.addStyleName(newRow, 4,  "jsc-fieldlabel");
        fmt.addStyleName(newRow, 5,  "jsc-currencyfield");
        
        memberPanel.add(memberGrid);
        memberPanel.setVisible(true);
    }
    
    /**
     * Register but only send a membership entry to be paid.
     */
    private void register() {
        GWT.log("Registering for a class...", null);
        
        if (loginSession.isAuthenticated()) {
            // This is the array of roster entries that should be created in the db
            ArrayList<RosterEntry> entryList = new ArrayList<RosterEntry>();
            
            boolean createMembership = false;
            if ((singleMemberRadio.getValue() == true || familyMemberRadio.getValue() == true) &! loginSession.getPerson().isMember()) {
                createMembership = true;
            }
                        
            // Initialize the service proxy.
            if (regService == null) {
                regService = GWT.create(SkaterRegistrationService.class);
            }

            // Set up the callback object.
            AsyncCallback<RegistrationResults> callback = new AsyncCallback<RegistrationResults>() {

                public void onFailure(Throwable caught) {
                    // TODO: Do something with errors.
                    GWT.log("Failed to register the RosterEntry array.", caught);
                }

                public void onSuccess(RegistrationResults results) {
                    
                    ArrayList<RosterEntry> newEntryList = results.getEntriesCreated();
                    
                    if ((newEntryList == null || newEntryList.size() == 0) && !results.isMembershipAttempted()) {
                        // Failure on the remote end.
                        setMessage("Error registering... Have you are already registered for these classes? Check 'My Classes'.");
                        return;
                    } else {
                        if (results.isMembershipCreated()) {
                            loginSession.getPerson().setMember(true);
                            loginSession.getPerson().setMembershipId(results.getMembershipId());
                            loginSession.getPerson().setMembershipStatus(results.getMembershipStatus());
                        }
                        
                        createAndShowPaypalForm(results);
                    }
                }
            };

            // Make the call to the registration service.
            if (createMembership) {
                GWT.log("Sending membership request.", null);
                MembershipInfo memInfo = new MembershipInfo();
                if (singleMemberRadio.getValue() == true) {
                    memInfo.setMembershipType(AppConstants.MEMBER_SINGLE);
                } else if (familyMemberRadio.getValue() == true) {
                    memInfo.setMembershipType(AppConstants.MEMBER_FAMILY);
                }
                Long firstMemberID = loginSession.getPerson().getMembershipId();
                memInfo.addMemberID(firstMemberID);
                regService.register(loginSession, loginSession.getPerson(), entryList, createMembership, memInfo, callback);
            } else {
                setMessage("You must select a membership option before clicking 'Continue'.");
            }
            
        } else {
            GWT.log("Error: Can not create a membership without first signing in.", null);
        }
    }

    /**
     * Create the paypal form by iterating across the registration roster entries and
     * membership information and serializing an appropriate HTML form to request payment
     * for the items.
     * @param results the results of the registration step listing registration items
     * @return a StringBuffer containing the HTML form to be displayed
     */
    protected StringBuffer createPayPalForm(RegistrationResults results, ClassListModel sessionClassList, double discount) {
        
        long paymentId;
        String membershipType;
        long membershipId;
        if (results == null) {
            paymentId = loginSession.getPerson().getMembershipPaymentId();
            membershipType = loginSession.getPerson().getMembershipType();
            membershipId = loginSession.getPerson().getMembershipId();
        } else {
            paymentId = results.getPaymentId();
            membershipType = results.getMembershipType();
            membershipId = results.getMembershipId();
        }
        
        StringBuffer ppCart = new StringBuffer();
        ppCart.append("<form id=\"wizard\" action=\""+ ClientConstants.getString("CLIENT_PAYPAL_URL") + "\" method=\"post\">");
        ppCart.append("<input type=\"hidden\" name=\"cmd\" value=\"_cart\">");
        ppCart.append("<input type=\"hidden\" name=\"upload\" value=\"1\">");
        ppCart.append("<input type=\"hidden\" name=\"business\" value=\"" + ClientConstants.getString("CLIENT_MERCHANT_ID") + "\">");
        ppCart.append("<input type=\"hidden\" name=\"currency_code\" value=\"USD\">");
        ppCart.append("<input type=\"hidden\" name=\"no_note\" value=\"1\">");
        ppCart.append("<input type=\"hidden\" name=\"no_shipping\" value=\"1\">");
        ppCart.append("<input type=\"hidden\" name=\"cpp_header_image\" value=\""+ ClientConstants.getString("CLIENT_PAYPAL_HEADER_IMAGE") + "\">");
        ppCart.append("<input type=\"hidden\" name=\"item_name\" value=\""+ "Registration Invoice" + "\">");
        ppCart.append("<input type=\"hidden\" name=\"invoice\" value=\""+ paymentId + "\">");
        
        int i = 0;
        // Handle membership payment by creating form items as needed
        if (results == null || results.isMembershipCreated()) {
            double dues = 0;
            if (membershipType.equals(AppConstants.MEMBER_SINGLE)) {
                dues = AppConstants.MEMBERSHIP_SINGLE_PRICE;
            } else if (membershipType.equals(AppConstants.MEMBER_FAMILY)) {
                dues = AppConstants.MEMBERSHIP_FAMILY_PRICE;
            }
            i++;
            String season = SessionSkatingClass.calculateSeason();
            ppCart.append("<input type=\"hidden\" name=\"item_name_" + i + "\" value=\"Membership dues for " + season + " season (" + membershipType + ")\">");
            ppCart.append("<input type=\"hidden\" name=\"item_number_" + i + "\" value=\"" + membershipId +"\">");
            ppCart.append("<input type=\"hidden\" name=\"amount_" + i + "\" value=\"" + dues + "\">");
        }
        ppCart.append("<input type=\"hidden\" name=\"discount_amount_cart\" value=\"" + discount + "\">");
        
        ppCart.append("<input type=\"hidden\" name=\"return\" value=\"" + ClientConstants.getString("CLIENT_PAYPAL_RETURN_URL") + "\">");
        ppCart.append("<input type=\"hidden\" name=\"cancel_return\" value=\"" + ClientConstants.getString("CLIENT_PAYPAL_CANCEL_URL") + "\">");
        ppCart.append("<input type=\"submit\" name=\"Pay Now\" value=\"Complete Payment Now\">");

        //ppCart.append("<input type=\"image\" src=\"https://www.sandbox.paypal.com/en_US/i/btn/btn_paynow_LG.gif\" border=\"0\" name=\"submit\" alt=\"PayPal - The safer, easier way to pay online!\">");
        //ppCart.append("<img alt=\"\" border=\"0\" src=\"https://www.sandbox.paypal.com/en_US/i/scr/pixel.gif\" width=\"1\" height=\"1\">");
        ppCart.append("</form>");
        return ppCart;
    }
    
    /**
     * Listen for change events when the radio buttons on the membership form
     * are selected and deselected.
     */
    @Override
    public void onValueChange(ValueChangeEvent<Boolean> event) {
        GWT.log("Called: onValueChange()");
        Widget sender = (Widget) event.getSource();
        updateMembershipScreenDetails(sender);
        
    }


    @Override
    public void onChange(ChangeEvent event) {
        GWT.log("Called: onChange()");
        Widget sender = (Widget) event.getSource();
        updateMembershipScreenDetails(sender);
    }
    
    /**
     * Check the current membership status, and based on that set the
     * form as either membership paid or enable the user to sign up
     * for a single or family membership. 
     */
    protected void updateMembershipScreenDetails(Widget sender) {
        // Reset the form fields to begin registration
        GWT.log("Called: updateMembershipScreenDetails()");
        boolean isMember = loginSession.getPerson().isMember();
        String membershipStatus = loginSession.getPerson().getMembershipStatus();
        boolean isPending = membershipStatus.equals(PENDING);
        String membershipType = loginSession.getPerson().getMembershipType();
        if (sender==null) {
            sender = singleMemberRadio;
        }
                
//        memberPanel.setVisible(true);
//        ppPaymentPanel.setVisible(false);
//        stepLabel.setText(STEP_1);
//        registerButton.setVisible(true);
        
        totalCost = 0;
        double dues = 0;
    
        // All paid up: isMember &! isPending
        // Disable membership controls, show as paid
        if (isMember &! isPending ) {
            stepLabel.setText("Membership dues paid");
            memberPanel.setVisible(true);
            ppPaymentPanel.setVisible(false);
            memberPaidLabel.setText(membershipType + " membership dues already paid. Discounts will be applied to class registrations.");
            singleMemberRadio.setEnabled(false);
            familyMemberRadio.setEnabled(false);
            registerButton.setEnabled(false);
           
        // Membership payment incomplete: isMember && isPending
        // Show the Paypal form
        } else if (isMember && isPending) {
            memberPanel.setVisible(false);
            memberPaidLabel.setText(" ");
            singleMemberRadio.setValue(false);
            familyMemberRadio.setValue(false);
//            registerButton.setEnabled(true);
            createAndShowPaypalForm(null);

        // Not started at all: !isMember and all other cases
        // Show the signup form
        } else {
            memberPanel.setVisible(true);
            ppPaymentPanel.setVisible(false);
            stepLabel.setText(STEP_1);
            memberPaidLabel.setText(" ");
            singleMemberRadio.setValue(true);
            familyMemberRadio.setValue(false);
            singleMemberRadio.setEnabled(true);
            familyMemberRadio.setEnabled(true);
            registerButton.setEnabled(true);
        }

        // Now determine how to set radio buttons and totals
        if (sender == singleMemberRadio) {
            GWT.log("singleMemberRadio clicked");
            singleMemberRadio.setValue(true);
            familyMemberRadio.setValue(false);

            //singleMemberRadio.getValue() == true &! 
            if (!isMember || isPending) {
                GWT.log("Person is not member");
                dues = AppConstants.MEMBERSHIP_SINGLE_PRICE;
                totalCost = dues;
            } else {
                GWT.log("Person is member");
                GWT.log("isMember: " + isMember);
                dues = 0;
                totalCost = 0;
            }
        } else if (sender == familyMemberRadio) {
            GWT.log("familyMemberRadio clicked");
            singleMemberRadio.setValue(false);
            familyMemberRadio.setValue(true);

            //familyMemberRadio.getValue() == true &! 
            if (!isMember || isPending) {
                GWT.log("Person is not member");
                dues = AppConstants.MEMBERSHIP_FAMILY_PRICE;
                totalCost = dues;
            } else {
                GWT.log("Person is member");
                GWT.log("isMember: " + isMember);
                dues = 0;
                totalCost = 0;
            }
        }
        memberDues.setText(numfmt.format(dues));
        totalCostLabel.setText(numfmt.format(totalCost));
    }

    private void createAndShowPaypalForm(RegistrationResults results) {
        double discount = 0;
        StringBuffer ppCart = createPayPalForm(results, null, discount);
        registerButton.setVisible(false);
        stepLabel.setText(STEP_2);
        memberPanel.setVisible(false);
        
        ppPaymentPanel.clear();
        ppPaymentPanel.add(new HTMLPanel(PAYPAL_EXPLANATION));
        ppPaymentPanel.add(new HTMLPanel(ppCart.toString()));
        ppPaymentPanel.setVisible(true);
    }
}
