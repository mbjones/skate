package org.jsc.client;

import org.jsc.client.event.LoginSessionChangeEvent;
import org.jsc.client.event.LoginSessionChangeHandler;
import org.jsc.client.event.NotificationEvent;
import org.jsc.client.event.NotificationHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HeaderPanel extends VerticalPanel {

    private HorizontalPanel toolbar;
    private Label title;
    private Label statusLabel;
    private LoginSession loginSession;
    private Hyperlink regLink;
    private Hyperlink classesLink;
    private Label manageSeparator;
    private Hyperlink manageLink;
    private Hyperlink settingsLink;
    private Hyperlink signoutLink;
    private String currentMessage;
    private Label message;

    
    /**
     * Construct the header, passing it the login session to be used in tracking
     * the login status.
     * @param loginSession the session information for status updates
     */
    public HeaderPanel(LoginSession loginSession, HandlerManager eventBus) {
        super();
        this.loginSession = loginSession;
        layoutHeaderPanel();
        
        // Register as a handler for LoginSession changes, and handle those changes
        // by updating the header appropriately
        eventBus.addHandler(LoginSessionChangeEvent.TYPE, new LoginSessionChangeHandler() {
            public void onLoginSessionChange(LoginSessionChangeEvent event) {
                updateStatus();
            }
        });
        
        // Register as a handler for Notification events, and handle those changes
        // by updating the message field appropriately
        eventBus.addHandler(NotificationEvent.TYPE, new NotificationHandler() {
            public void onNotification(NotificationEvent event) {
                if (event.shouldClearMessage()) {
                    clearMessage();
                } else {
                    setMessage(event.getMessage());
                }
            }
        });
    }
    
    /**
     * Create and lay out widgets for the top header panel
     */
    private void layoutHeaderPanel() {
        this.setWidth("100%");
        this.addStyleName("jsc-header");
        
        HorizontalPanel leftLinks = new HorizontalPanel(); 
        classesLink = new Hyperlink("My Classes", "myclasses");
        leftLinks.add(classesLink);
        leftLinks.add(createSeparatorLabel());
        regLink = new Hyperlink("Register for a Class", "register");
        leftLinks.add(regLink);
        
        manageSeparator = createSeparatorLabel();
        leftLinks.add(manageSeparator);
        manageSeparator.setVisible(false);
        manageLink = new Hyperlink("Manage Classes", "manage");
        leftLinks.add(manageLink);
        leftLinks.addStyleName("jsc-toolbar");
        manageLink.setVisible(false);
        
        HorizontalPanel rightLinks = new HorizontalPanel();
        rightLinks.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        statusLabel = new Label(" ");
        rightLinks.add(statusLabel);
        rightLinks.add(createSeparatorLabel());
        settingsLink = new Hyperlink("Settings", "settings");
        rightLinks.add(settingsLink);
        rightLinks.add(createSeparatorLabel());
        signoutLink = new Hyperlink("Sign Out", "signout");
        rightLinks.add(signoutLink); 
        rightLinks.addStyleName("jsc-toolbar");
        
        toolbar = new HorizontalPanel();
        //toolbar.setWidth("100%");
        toolbar.add(leftLinks);
        toolbar.setCellHorizontalAlignment(leftLinks, HasHorizontalAlignment.ALIGN_LEFT);
        Label spacer = new Label();
        toolbar.add(spacer);
        spacer.addStyleName("jsc-toolbar-spacer");
        //spacer.setWidth("100%");
        //toolbar.setCellWidth(spacer, "50%");
        toolbar.add(rightLinks);
        toolbar.setCellHorizontalAlignment(rightLinks, HasHorizontalAlignment.ALIGN_RIGHT);
        
        message = new Label();
        message.addStyleName("jsc-message");
        //this.setMessage("Account created, please log in.");
        //this.clearMessage();
        
        this.add(toolbar);
        title = new Label("Skate!");
        title.addStyleName("jsc-header-title");
        this.add(title);
        this.add(message);
        
        updateStatus();
    }
    
    /**
     * Set the title of the header.
     */
    public void setTitle(String headerTitle) {
        title.setText(headerTitle);
    }
    
    /**
     * When called, change the text of the status label in the header to
     * reflect the current login status.
     */
    protected void updateStatus() {
        if (loginSession.isAuthenticated() && loginSession.getPerson() != null) {
            statusLabel.setText(loginSession.getPerson().getFname() + " " + 
                    loginSession.getPerson().getLname());
            signoutLink.setText("Sign Out");
            GWT.log("LoginSession Person Role is: " + loginSession.getPerson().getRole(), null);
            if (loginSession.getPerson().getRole() >= Person.COACH) {
                manageSeparator.setVisible(true);
                manageLink.setVisible(true);
            }
            //regLink.setVisible(true);
            //classesLink.setVisible(true);
        } else {
            statusLabel.setText(" ");
            signoutLink.setText("Sign In");
            manageSeparator.setVisible(false);
            manageLink.setVisible(false);
            //regLink.setVisible(false);
            //classesLink.setVisible(false);
        }
    }
    
    /**
     * Create a Label to be used to separate items in the toolbar.
     * @return Label to be used as separator
     */
    private Label createSeparatorLabel() {
        Label l = new Label(" | ");
        l.addStyleName("jsc-toolbar-separator");
        return l;
    }
    
    /**
     * @return the currentMessage
     */
    protected String getMessage() {
        return currentMessage;
    }

    /**
     * @param currentMessage the currentMessage to display on the screen
     */
    protected void setMessage(String currentMessage) {
        this.currentMessage = currentMessage;
        message.setText(currentMessage);
        message.removeStyleName("jsc-message-clear");
        GWT.log(currentMessage, null);
    }
    
    /**
     * Update the message Label when the screen has been switched from another.
     */
    protected void updateMessage() {
        message.setText(currentMessage);
    }
    
    /**
     * Clear the current message and change the style so it does not display
     */
    protected void clearMessage() {
        message.setText("");
        message.addStyleName("jsc-message-clear");
    }
}
