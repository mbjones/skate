package org.jsc.client;

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
    private Panel contentPanel;
    protected LoginSession loginSession;
    
    public BaseScreen(LoginSession loginSession) {
        super();
        
        // Record the LoginSession for later use
        this.loginSession = loginSession;
        
        //this.setWidth("100%");
        this.addStyleName("jsc-screen");
        this.screenTitle = "Base Screen";
        title = new Label();
        title.setText(screenTitle);
        title.addStyleName("jsc-screentitle");
        this.add(title);
        contentPanel = new HorizontalPanel();
        //contentPanel.setHeight("20em");
        //contentPanel.setWidth("63em");
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
        //this.add(new HTML("<p class=\"label\">" + screenTitle + "</p>"));
    }

    /**
     * @return the contentPanel
     */
    public Panel getContentPanel() {
        return contentPanel;
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
