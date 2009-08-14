package org.jsc.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The main application window for JSCDB.  This class creates a panel which is 
 * placed at the root of the GWT application, and contains a screen which can be
 * switched with other screens as the state of the changes with user input.
 * 
 * @author Matthew Jones
 */
public class ContentPanel extends HorizontalPanel {

    /**
     * Construct the main content pane for the application .
     *
     */
    public ContentPanel() {
        super();
    }

    /**
     * Change the currently displayed screen in the application to the provided
     * screen, which should be an extension of the BaseScreen class.
     * @param screen the new screen to make active
     */
    public void setScreen(BaseScreen screen) {
        int widgetCount = this.getWidgetCount();
        if (widgetCount > 0) {
            Widget currentScreen = this.getWidget(0);
            if (currentScreen != null) {
                currentScreen.removeFromParent();
            }
        }
        super.add(screen);
    }
}
