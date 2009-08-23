package org.jsc.client.event;

import java.util.List;

import org.jsc.client.SessionSkatingClass;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An event that is created when the list of Skating Classes has been updated.  It
 * is used to post events that allow the user interface to reflect new class changes.
 * 
 * @author Matt Jones
 *
 */
public class SkatingClassChangeEvent extends GwtEvent<SkatingClassChangeHandler> {
    private final List<SessionSkatingClass> sessionClassList;

    public SkatingClassChangeEvent(List<SessionSkatingClass> sessionClassList) {
        super();
        this.sessionClassList = sessionClassList;
    }

    public static final GwtEvent.Type<SkatingClassChangeHandler> TYPE = new GwtEvent.Type<SkatingClassChangeHandler>();
    //public static final GwtEvent.Type<TableRowEventHandler> TYPE = new GwtEvent.Type<TableRowEventHandler>();
    
    @Override
    protected void dispatch(SkatingClassChangeHandler handler) {
        handler.onClassChange(this);
    }

    @Override
    public GwtEvent.Type<SkatingClassChangeHandler> getAssociatedType() {
        return TYPE;
    }

    public List<SessionSkatingClass> getClassList(){
        return sessionClassList;
    }

}
