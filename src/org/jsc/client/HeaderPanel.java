package org.jsc.client;

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
    
    /**
     * Construct the header, passing it the login session to be used in tracking
     * the login status.
     * @param loginSession the session information for status updates
     */
    public HeaderPanel(LoginSession loginSession) {
        super();
        this.loginSession = loginSession;
        layoutHeaderPanel();
    }
    
    /**
     * Create and lay out widgets for the top header panel
     */
    private void layoutHeaderPanel() {
        this.setWidth("100%");
        this.addStyleName("jsc-header");
        
        HorizontalPanel leftLinks = new HorizontalPanel(); 
        leftLinks.add(new Hyperlink("Register for a Class", "register"));
        // TODO: add css style for setting width of pipe separator label
        leftLinks.add(createSeparatorLabel());
        leftLinks.add(new Hyperlink("My Classes", "myclasses"));
        leftLinks.add(createSeparatorLabel());
        leftLinks.add(new Hyperlink("Manage Classes", "manage"));
        leftLinks.addStyleName("jsc-toolbar");
        
        HorizontalPanel rightLinks = new HorizontalPanel();
        rightLinks.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        statusLabel = new Label(" ");
        rightLinks.add(statusLabel);
        updateStatus();
        rightLinks.add(createSeparatorLabel());
        Hyperlink link0 = new Hyperlink("Settings", "settings");
        rightLinks.add(link0);
        rightLinks.add(createSeparatorLabel());
        Hyperlink link1 = new Hyperlink("Sign Out", "signout");
        rightLinks.add(link1); 
        rightLinks.addStyleName("jsc-toolbar");
        
        toolbar = new HorizontalPanel();
        //toolbar.setWidth("100%");
        toolbar.add(leftLinks);
        toolbar.setCellHorizontalAlignment(leftLinks, HasHorizontalAlignment.ALIGN_LEFT);
        Label spacer = new Label();
        toolbar.add(spacer);
        spacer.addStyleName("jsc-toolbar-spacer");
        //toolbar.setCellWidth(spacer, "50%");
        toolbar.add(rightLinks);
        toolbar.setCellHorizontalAlignment(rightLinks, HasHorizontalAlignment.ALIGN_RIGHT);
        
        this.add(toolbar);
        title = new Label("Skater Data");
        title.addStyleName("jsc-header-title");
        this.add(title);
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
        if (loginSession.isAuthenticated()) {
            statusLabel.setText(loginSession.getPerson().getFname() + " " + 
                    loginSession.getPerson().getLname());
        } else {
            statusLabel.setText(" ");
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
}
