package org.jsc.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The interface definition for the remote service that is used to handle RPC
 * communication on the remote server.  These methods can be used to retrieve and
 * modify data from the remote data store.
 * @author Matt Jones
 */
@RemoteServiceRelativePath("registration")
public interface SkaterRegistrationService extends RemoteService {

    Person createAccount(LoginSession loginSession, Person person) throws SQLRecordException;
    LoginSession authenticate(String username, String password);
    boolean logout();
    Person getPerson(long pid);
    ArrayList<SessionSkatingClass> getSessionClassList(LoginSession loginSession, Person person);
    RegistrationResults register(LoginSession loginSession, Person person, ArrayList<RosterEntry> newEntryList, boolean createMembership);
    boolean cancelInvoice(LoginSession loginSession, long paymentid);
    ArrayList<RosterEntry> getStudentRoster(LoginSession loginSession, Person person);
    ArrayList<RosterEntry> getClassRoster(LoginSession loginSession, Person person, long classId);
}