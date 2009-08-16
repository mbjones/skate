package org.jsc.client;

import com.google.gwt.core.client.GWT;
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
    
    /**
     * Construct the BaseScreen
     * @param loginSession to be saved for reference by subclass screens
     */
    public BaseScreen(LoginSession loginSession) {
        super();
        
        // Record the LoginSession for later use
        this.loginSession = loginSession;
        
        this.addStyleName("jsc-screen");
        this.screenTitle = "Base Screen";
        title = new Label();
        title.setText(screenTitle);
        title.addStyleName("jsc-screentitle");
        this.add(title);
        message = new Label();
        setMessage("Hello");
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
    public String getMessage() {
        return currentMessage;
    }

    /**
     * @param currentMessage the currentMessage to display on the screen
     */
    public void setMessage(String currentMessage) {
        this.currentMessage = currentMessage;
        message.setText(currentMessage);
        message.removeStyleName("jsc-message-clear");
        GWT.log(currentMessage, null);
    }
    
    /**
     * Clear the current message and change the style so it does not display
     */
    public void clearMessage() {
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
