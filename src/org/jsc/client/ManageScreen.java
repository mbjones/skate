package org.jsc.client;

import com.google.gwt.event.shared.HandlerManager;

public class ManageScreen extends BaseScreen {

    public ManageScreen(LoginSession loginSession, HandlerManager eventBus) {
        super(loginSession, eventBus);
        this.setScreenTitle("Manage Classes");
    }
}
