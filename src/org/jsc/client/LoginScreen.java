package org.jsc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A specialized extension of BaseScreen intended to handle input for logging
 * into the application.
 * 
 * @author jones
 *
 */
public class LoginScreen extends BaseScreen {

    private HorizontalPanel screen;
    private VerticalPanel loginPanel;
    private VerticalPanel introPanel;
    private TextBox username;
    private TextBox password;
    private Button signinButton;
    private SkaterRegistrationServiceAsync regService;
    
    public LoginScreen(LoginSession loginSession) {
        super(loginSession);
        layoutScreen();
        this.setContentPanel(screen);
    }

    /**
     * Lay out the user interface widgets on the screen.
     */
    private void layoutScreen() {
        this.setScreenTitle("Login");
        this.setStyleName("jsc-twopanel-screen");
        
        screen = new HorizontalPanel();
        //screen.setHeight("30em");
        //screen.setWidth("50em");
        
        createLoginPanel();
        Label spacer = new Label("");
        spacer.addStyleName("jsc-spacer");
        createIntroPanel();
        
        screen.add(loginPanel);
        screen.add(spacer);
        screen.add(introPanel);
    }

    private void createLoginPanel() {
        loginPanel = new VerticalPanel();
        loginPanel.addStyleName("jsc-leftpanel");
        Label signin = new Label("Sign In");
        signin.addStyleName("jsc-screentitle");
        loginPanel.add(signin);
        Label emailLabel = new Label("Email:");
        loginPanel.add(emailLabel);
        emailLabel.addStyleName("jsc-fieldlabel-left");
        username = new TextBox();
        loginPanel.add(username);
        Label pwLabel = new Label("Password:");
        loginPanel.add(pwLabel);
        pwLabel.addStyleName("jsc-fieldlabel-left");
        password = new PasswordTextBox();
        loginPanel.add(password);
        loginPanel.add(new Label(" "));
        signinButton = new Button("Sign In");
        signinButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                authenticate();
            }
        });
        loginPanel.add(signinButton);
    }
    
    private void authenticate() {
        // Initialize the service proxy.
        if (regService == null) {
            regService = GWT.create(SkaterRegistrationService.class);
        }

        // Set up the callback object.
        AsyncCallback<Person> callback = new AsyncCallback<Person>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
                loginSession.setAuthenticated(false);
                GWT.log("Authentication failed to complete.", null);
                password.setText("");
            }

            public void onSuccess(Person person) {
                // Clear the password box
                password.setText("");
                
                if (person != null) {
                    // Login succeeded
                    long pid = person.getPid();
                    GWT.log("Login succeeded: " + pid, null);
                    GWT.log(person.toString(), null);

                    // TODO: Record the authenticated person in the LoginSession
                    loginSession.setPerson(person);
                    loginSession.setAuthenticated(true);

                    // Change our application state to the classes screen
                    History.newItem("myclasses");
                } else {
                    loginSession.setAuthenticated(false);
                    // TODO: Post an error message but stay on the login screen

                    GWT.log("Login unsuccessful, try again.", null);
                }
            }
        };

        // Make the call to the registration service.
        regService.authenticate(username.getText(), password.getText(), callback);
    }
    
    private void createIntroPanel() {
        introPanel = new VerticalPanel();
        introPanel.addStyleName("jsc-rightpanel");
        //introPanel.setHeight("20em");
        //introPanel.setWidth("27em");
        Label intro1 = new Label("Welcome!");
        intro1.setWordWrap(true);
        Label intro2 = new Label("You can register for new classes after you have Signed In.");
        intro2.setWordWrap(true);
        Label intro3 = new Label("If you do not have an account, you can create a New Account.");
        intro3.setWordWrap(true);
        introPanel.add(intro1);
        introPanel.add(intro2);
        introPanel.add(intro3);
    }
}
