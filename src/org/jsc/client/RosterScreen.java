package org.jsc.client;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * A screen used to display the roster for a class in a format that is suitable
 * for printing. Extends ManageScreen and depends on it for layout.
 * 
 * @author Matt Jones
 */
public class RosterScreen extends ManageScreen {

    /**
     * Construct the roster screen and layout its contents.
     * @param loginSession the session that is logged in
     * @param eventBus the eventBus for posting events
     */
    public RosterScreen(LoginSession loginSession, HandlerManager eventBus) {
        super(loginSession, eventBus);
        
        this.setScreenTitle("Class Roster");
        layoutScreen();
        this.setContentPanel(screen);
    }

    /**
     * Lay out the user interface widgets on the screen, only including the 
     * class roster.
     */
    protected void layoutScreen() {
        this.setScreenTitle("Class Roster");
        this.setStyleName("jsc-twopanel-screen");
        
        screen = new HorizontalPanel();
        createRosterPanel();
        screen.add(rosterPanel);
    }
}
