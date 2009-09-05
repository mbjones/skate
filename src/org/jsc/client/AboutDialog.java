package org.jsc.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A simple dialog to display information about the application.
 * @author Matt Jones
 */
public class AboutDialog extends DialogBox {
    private static final String VERSION = "1.0.0alpha1";
    
    public AboutDialog() {
        // Set the dialog box's caption.
        setText("About Skate...");
        VerticalPanel content = new VerticalPanel();
        Label title = new Label("Skate");
        title.addStyleName("jsc-dialog");
        Label version = new Label("Version: " + VERSION);
        version.addStyleName("jsc-dialog");
        Label copyright = new Label("Copyright 2009 Matthew B. Jones. All rights reserved.");
        copyright.addStyleName("jsc-dialog");

        Button ok = new Button("OK");
        ok.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                AboutDialog.this.hide();
                History.newItem("aboutClosed");
            }
        });
        ok.addStyleName("jsc-dialog");

        content.add(title);
        content.add(copyright);
        content.add(version);
        content.add(ok);
        content.addStyleName("jsc-dialog");
        this.setWidget(content);
    }
}
