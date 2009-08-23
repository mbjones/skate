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
    private Hyperlink regLink;
    private Hyperlink classesLink;
    private Hyperlink manageLink;
    private Hyperlink settingsLink;
    private Hyperlink signoutLink;
    
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
        regLink = new Hyperlink("Register for a Class", "register");
        leftLinks.add(regLink);
        leftLinks.add(createSeparatorLabel());
        classesLink = new Hyperlink("My Classes", "myclasses");
        leftLinks.add(classesLink);
        leftLinks.add(createSeparatorLabel());
        manageLink = new Hyperlink("Manage Classes", "manage");
        leftLinks.add(manageLink);
        leftLinks.addStyleName("jsc-toolbar");
        
        HorizontalPanel rightLinks = new HorizontalPanel();
        rightLinks.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        statusLabel = new Label(" ");
        rightLinks.add(statusLabel);
        updateStatus();
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
            //signoutLink.setText("Sign Out");
            //regLink.setVisible(false);
        } else {
            statusLabel.setText(" ");
            //signoutLink.setText("Sign In");
            //regLink.setVisible(true);
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
