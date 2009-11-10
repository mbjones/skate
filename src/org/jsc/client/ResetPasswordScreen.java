package org.jsc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A specialized extension of BaseScreen intended to handle input for resetting
 * a user's password or looking up their username.
 * 
 * @author Matt Jones
 */
public class ResetPasswordScreen extends BaseScreen {
    private HorizontalPanel screen;
    private VerticalPanel resetPanel;
    private VerticalPanel introPanel;
    private TextBox username;
    private TextBox password;
    private Button resetButton;
    private SkaterRegistrationServiceAsync regService;
    
    /**
     * Construct the login screen, recording the loginSession for later reference.
     * @param loginSession to be used for user information in later steps
     */
    public ResetPasswordScreen(LoginSession loginSession, HandlerManager eventBus) {
        super(loginSession, eventBus);
        layoutScreen();
        this.setContentPanel(screen);
    }

    /**
     * Lay out the user interface widgets on the screen.
     */
    private void layoutScreen() {
        this.setScreenTitle("Reset Password");
        this.setStyleName("jsc-twopanel-screen");
        
        screen = new HorizontalPanel();
        
        createResetPanel();
        Label spacer = new Label("");
        spacer.addStyleName("jsc-spacer");
        createIntroPanel();
        screen.add(resetPanel);
        screen.add(spacer);
        screen.add(introPanel);
    }
    
    /**
     * Fill in the GUI for the Login screen
     */
    private void createResetPanel() {
        resetPanel = new VerticalPanel();
        resetPanel.addStyleName("jsc-leftpanel");
        Label signin = new Label("Reset Password");
        signin.addStyleName("jsc-screentitle");
        Label usernameLabel = new Label("Username:");
        resetPanel.add(usernameLabel);
        usernameLabel.addStyleName("jsc-fieldlabel-left");
        username = new TextBox();
        username.addStyleName("jsc-field");
        resetPanel.add(username);
        resetButton = new Button("Reset Password");
        resetButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                resetPassword();
            }
        });
        resetButton.addStyleName("jsc-button-right");
        resetPanel.add(resetButton);
        
        Label pwLabel = new Label("Password:");
        resetPanel.add(pwLabel);
        pwLabel.addStyleName("jsc-fieldlabel-left");
        password = new PasswordTextBox();
        password.addStyleName("jsc-field");
        resetPanel.add(password);
        resetPanel.add(new Label(" "));

        
        Hyperlink newAccountLink = new Hyperlink("Need a New Account?", "settings");
        newAccountLink.addStyleName("jsc-link-right");
        resetPanel.add(newAccountLink);
    }
    
    /**
     * Check the user credentials and set up the loginSession if valid.
     */
    private void resetPassword() {
        // Initialize the service proxy.
        if (regService == null) {
            regService = GWT.create(SkaterRegistrationService.class);
        }

        // Set up the callback object.
        AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
                GWT.log("Reset failed to complete.", caught);
            }

            public void onSuccess(Boolean successFlag) {                
                if (successFlag) {
                    // The reset succeeded
                    GWT.log("Reset succeeded.", null);

                    // Change our application state to the classes screen
                    History.newItem("login");
                } else {
                    setMessage("Resetting password failed.");
                }
            }
        };

        // Make the call to the registration service.
        regService.resetPassword(username.getText(), callback);
    }
    
    /**
     * Set up the Introductory dialog.
     */
    private void createIntroPanel() {
        introPanel = new VerticalPanel();
        introPanel.addStyleName("jsc-rightpanel");
        StringBuffer intro = new StringBuffer();
        intro.append("<p class=\"jsc-text\">Use this screen to reset a forgotten password, or to look up a forgotten username. When you submit the form to the left, we will email you with the requested information at the email address used to originally register your account.</p>");
        intro.append("<p class=\"jsc-text\"></p>");
        intro.append("<p class=\"jsc-text\">Need help? Contact <b>'registrar@juneauskatingclub.org'</b></p>");

        HTMLPanel introHTML = new HTMLPanel(intro.toString());
        introPanel.add(introHTML);
    }
}
