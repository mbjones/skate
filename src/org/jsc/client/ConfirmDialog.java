package org.jsc.client;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A simple dialog to display a confirmation dialog to the user.
 * @author Matt Jones
 */
public class ConfirmDialog extends DialogBox {
    private long identifier;
    
    public ConfirmDialog(String question, ClickHandler handler) {
        // Set the dialog box's caption.
        setText("Are you sure?");
        VerticalPanel content = new VerticalPanel();
        content.addStyleName("jsc-button-right");

        Label qLabel = new Label(question);
        qLabel.addStyleName("jsc-dialog");
        
        Button yes = new Button("Yes");
        yes.addClickHandler(handler);
        yes.addStyleName("jsc-dialog");
        
        Button no = new Button("No");
        no.addClickHandler(handler);
        no.addStyleName("jsc-dialog");

        
        content.add(qLabel);
        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.addStyleName("jsc-button-right");
        buttonPanel.add(no);
        buttonPanel.add(new Label(" "));
        buttonPanel.add(yes);
        content.add(buttonPanel);
        content.addStyleName("jsc-dialog");
        this.setWidget(content);
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(long identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the identifier
     */
    public long getIdentifier() {
        return identifier;
    }
}
