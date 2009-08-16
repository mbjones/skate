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
        
        String testForm = 
            "<form action=\"https://www.sandbox.paypal.com/cgi-bin/webscr\" method=\"post\">" +
            "<input type=\"hidden\" name=\"cmd\" value=\"_s-xclick\">" +
            "<input type=\"hidden\" name=\"hosted_button_id\" value=\"1059849\">" +
            "<input type=\"image\" src=\"https://www.sandbox.paypal.com/en_US/i/btn/btn_paynowCC_LG.gif\" border=\"0\" name=\"submit\" alt=\"PayPal - The safer, easier way to pay online!\">" +
            "<img alt=\"\" border=\"0\" src=\"https://www.sandbox.paypal.com/en_US/i/scr/pixel.gif\" width=\"1\" height=\"1\">" +
            "</form>";
        String realForm =
            "<form action=\"https://www.sandbox.paypal.com/cgi-bin/webscr\" method=\"post\">" +
            "<input type=\"hidden\" name=\"cmd\" value=\"_s-xclick\"> <table>" +
            "<tr><td><input type=\"hidden\" name=\"on0\" value=\"Registration Date\">Registration Date</td></tr><tr><td>" +
            "<select name=\"os0\">" +
            "    <option value=\"Early (until Aug 31)\">Early (until Aug 31) $50.00" +
            "    <option value=\"Normal (until Sep 15)\">Normal (until Sep 15) $60.00" +
            "    <option value=\"On-site (after Sep 15)\">On-site (after Sep 15) $75.00" +
            "</select> </td></tr> </table>" +
            "<input type=\"hidden\" name=\"currency_code\" value=\"USD\">" +
            "<input type=\"hidden\" name=\"encrypted\" value=\"-----BEGIN PKCS7-----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-----END PKCS7-----\">" +
            "<input type=\"image\" src=\"https://www.paypal.com/en_US/i/btn/btn_paynowCC_LG.gif\" border=\"0\" name=\"submit\" alt=\"PayPal - The safer, easier way to pay online!\">" +
            "<img alt=\"\" border=\"0\" src=\"https://www.paypal.com/en_US/i/scr/pixel.gif\" width=\"1\" height=\"1\">" +
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

}
