package org.jsc.client;

import org.jsc.client.event.NotificationEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Base class from which all JSC user interface screens derive.
 * 
 * @author Matthew Jones
 */
public class BaseScreen extends VerticalPanel {
    private String screenTitle;
    private Label title;
    private static String currentMessage;
    private Label message;
    private Panel contentPanel;
    protected LoginSession loginSession;
    protected HandlerManager eventBus;
    
    /**
     * Construct the BaseScreen
     * @param loginSession to be saved for reference by subclass screens
     */
    public BaseScreen(LoginSession loginSession, HandlerManager eventBus) {
        super();
        
        // Record the LoginSession and eventBus for later use
        this.loginSession = loginSession;
        this.eventBus = eventBus;
        
        this.addStyleName("jsc-screen");
        this.screenTitle = "Base Screen";
        title = new Label();
        title.setText(screenTitle);
        title.addStyleName("jsc-screentitle");
        this.add(title);
        message = new Label();
        setMessage("BaseScreen created.");
        message.addStyleName("jsc-message");
        clearMessage();
        this.add(message);
        contentPanel = new HorizontalPanel();
        contentPanel.setWidth("100%");
        this.add(contentPanel);
    }

    /**
     * @return the screenTitle
     */
    public String getScreenTitle() {
        return screenTitle;
    }

    /**
     * @param screenTitle the screenTitle to set
     */
    public void setScreenTitle(String screenTitle) {
        this.screenTitle = screenTitle;
        title.setText(screenTitle);
    }

    /**
     * @return the contentPanel
     */
    public Panel getContentPanel() {
        return contentPanel;
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
        /*
        this.currentMessage = currentMessage;
        message.setText(currentMessage);
        message.removeStyleName("jsc-message-clear");
        */
        eventBus.fireEvent(new NotificationEvent(currentMessage));
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
    
    /**
     * @param contentPanel the contentPanel to set
     */
    public void setContentPanel(Panel contentPanel) {
        this.contentPanel.removeFromParent();
        this.contentPanel = contentPanel;
        this.add(contentPanel);
    }
    
}
