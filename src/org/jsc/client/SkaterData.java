package org.jsc.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jsc.client.event.LoginSessionChangeEvent;
import org.jsc.client.event.NotificationEvent;
import org.jsc.client.event.SkatingClassChangeEvent;
import org.jsc.client.event.SkatingClassChangeHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The SkaterData web application supports registration and payment for 
 * figure skating classes.  Developed for the Juneau Skating Club, but made
 * open source in the hope that it is useful to other non-profit skating clubs.
 * 
 * Entry point classes define <code>onModuleLoad()</code>.
 * @author Matt Jones
 */
public class SkaterData implements EntryPoint, ValueChangeHandler<String> {

    private LoginSession loginSession;
    private Map<java.lang.String,java.util.List<java.lang.String>> params;
    private HeaderPanel header;
    private ContentPanel content;
    private LoginScreen login;
    private ResetPasswordScreen reset;
    private SettingsScreen settings;
    private MyClassesScreen myclasses;
    private RegisterScreen register;
    private ManageScreen manage;
    private RosterScreen roster;
    private ConfirmScreen confirm;

    private ClassListModel sessionClassList;
    private RosterModel rosterModel;
    private HandlerManager eventBus;
    private SkaterRegistrationServiceAsync regService;
    private AboutDialog about;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

        // Create our event bus for handling application events
        eventBus = new HandlerManager(null);
        
        // Initialize our About dialog
        about = new AboutDialog();

        // Create a login session, which initially is not logged in
        loginSession = new LoginSession();
        
        // Check for a cookie to see if we have a previously valid session
        String sessionId = Cookies.getCookie("jscSession");
        String savedPid = Cookies.getCookie("jscPid");
        if ( sessionId != null && savedPid != null) {
            GWT.log("SessionID in onModuleLoad is: " + sessionId, null);
            loginSession.setAuthenticated(true);
            loginSession.setSessionId(sessionId);
            Person person = new Person();
            person.setPid(new Long(savedPid).longValue());
            loginSession.setPerson(person);
            refreshLoginSessionPerson(person.getPid());
            // TODO: need to get hold of the Person and set it too (maybe look it up on the server?
        } else {
            GWT.log("SessionId not found in onModuleLoad.", null);
        }
        
        sessionClassList = new ClassListModel(eventBus, loginSession);
        rosterModel = new RosterModel(eventBus, loginSession);

        // Create our header with internal toolbar
        header = new HeaderPanel(loginSession, eventBus);
        header.setTitle("Juneau Skating Club");
   
        // Create the screens to be used in the application
        content = new ContentPanel();
        content.setStyleName("jsc-content");
        
        // Store the parameters passed into the page
        params = Window.Location.getParameterMap();
        for (Map.Entry<String, List<String>> param : params.entrySet()) {
            GWT.log(param.getKey() + " " + param.getValue().toString(), null);
        }
        
        // Initialize application screens
        login = new LoginScreen(loginSession, eventBus);
        reset = new ResetPasswordScreen(loginSession, eventBus);
        settings = new SettingsScreen(loginSession, eventBus);
        myclasses = new MyClassesScreen(loginSession, eventBus, sessionClassList, rosterModel);
        register = new RegisterScreen(loginSession, eventBus, sessionClassList, rosterModel);
        roster = new RosterScreen(loginSession, eventBus);
        manage = new ManageScreen(loginSession, eventBus, sessionClassList, roster);
        confirm = new ConfirmScreen(loginSession, eventBus);
        
        // Set the default view screen to be login
        content.setScreen(login);

        // Clear out the window's built-in margin,
        // because we want to take advantage of the entire client area.
        Window.setMargin("0px");

        // Add our header and content containers to the root panel of the window
        RootPanel root = RootPanel.get();
        root.add(header);
        root.add(content);
        root.setStylePrimaryName("jsc-root-window");
        
        // Register as a handler for Skating class changes, and handle those changes
        eventBus.addHandler(SkatingClassChangeEvent.TYPE, new SkatingClassChangeHandler(){
            public void onClassChange(SkatingClassChangeEvent event) {
                register.updateRegistrationScreenDetails();
            }
        });
        
        clearMessage();

        // Add history listener
        History.addValueChangeHandler(this);
        
        // Now that we've setup our listener, fire the initial history state.
        History.fireCurrentHistoryState();
    }
    
    /**
     * This method is called whenever the application's history changes, and 
     * we use it to track application state. 
     */
    public void onValueChange(ValueChangeEvent<String> event) {
        Object historyToken = event.getValue();
        
        if (historyToken.equals("about")) {
            about.center();
            about.show();
        }
        
        //Check if this is an redirect from PayPal, and if so send to the confirm page
        List<String> txList = params.get("tx");
        if (historyToken.equals("") && txList != null && txList.size() > 0) {
            GWT.log("Transaction id: " + txList.get(0), null);
            confirm.setTransactionId(params.get("tx").get(0));
            confirm.setStatus(params.get("st").get(0));
            confirm.setAmountPaid(params.get("amt").get(0));
            //confirm.setRosterId(params.get("item_number").get(0));
            historyToken = "confirm";
        }
        
        if (!loginSession.isAuthenticated() &! historyToken.equals("settings")
                &! historyToken.equals("confirm") &! historyToken.equals("cancel")
                &! historyToken.equals("reset")) {
            historyToken = "signin";
        }
        
        if (historyToken.equals("settings")) {
            settings.updateScreen();
            content.setScreen(settings);
            clearMessage();
        } else if (historyToken.equals("signout")) {
            logout();
            content.setScreen(login);
            clearMessage();
        } else if (historyToken.equals("reset")) {
            content.setScreen(reset);
            clearMessage();
        } else if (historyToken.equals("signin")) {
            content.setScreen(login);
        } else if (historyToken.equals("myclasses")) {
            rosterModel.refreshRoster();
            content.setScreen(myclasses);
            clearMessage();
        } else if (historyToken.equals("register")) {
            sessionClassList.refreshClassList();
            rosterModel.refreshRoster();
            content.setScreen(register);
            clearMessage();
        } else if (historyToken.equals("manage")) {
            content.setScreen(manage);
            clearMessage();
        } else if (historyToken.equals("roster")) {
            content.setScreen(roster);
            clearMessage();
        } else if (historyToken.equals("confirm")) {
            content.setScreen(confirm);
            clearMessage();
        } else if (historyToken.equals("cancel")) {
            content.setScreen(manage);
            clearMessage();
        }
        header.updateStatus();
    }

    /**
     * When the screen message should be cleared, generate a NotificationEvent.
     */
    private void clearMessage() {
        eventBus.fireEvent(new NotificationEvent(true));
    }
    
    /**
     * Look up the current details for this Person from the remote service. This
     * method is called when we enter the application without authenticating and
     * instead use the previous session to validate.  
     */
    private void refreshLoginSessionPerson(long pid) {
        // Initialize the service proxy.
        if (regService == null) {
            regService = GWT.create(SkaterRegistrationService.class);
        }

        // Set up the callback object.
        AsyncCallback<Person> callback = new AsyncCallback<Person>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
                GWT.log("Failed to refresh the person record.", caught);
            }

            public void onSuccess(Person newPerson) {
                if (newPerson == null) {
                    // Failure on the remote end.
                    GWT.log("Error refreshing the person's attributes.", null);
                    return;
                } else {
                    // Update the loginSession to contain the newly looked up Person
                    loginSession.setPerson(newPerson);
                    //sessionClassList.refreshClassList();
                    rosterModel.refreshRoster();
                    LoginSessionChangeEvent event = new LoginSessionChangeEvent();
                    eventBus.fireEvent(event);
                }
            }
        };

        // Make the call to the registration service.
        regService.getPerson(pid, callback);
    }
    
    /**
     * Look up the current details for this Person from the remote service. This
     * method is called when we enter the application without authenticating and
     * instead use the previous session to validate.  
     */
    private void logout() {
        
        Date expires = new Date(System.currentTimeMillis());
        Cookies.setCookie("jscSession", "invalid", expires, null, "/", false);
        Cookies.setCookie("jscPid", "0", expires, null, "/", false);
        loginSession.setAuthenticated(false);
        
        // Initialize the service proxy.
        if (regService == null) {
            regService = GWT.create(SkaterRegistrationService.class);
        }

        // Set up the callback object.
        AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
                GWT.log("Failed to logout.", caught);
            }

            public void onSuccess(Boolean isLoggedOut) {
                GWT.log("Logout succeeded. HttpSession should be invalid.", null);
            }
        };

        // Make the call to the registration service.
        //regService.logout(null);
    }
}
