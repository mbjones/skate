package org.jsc.client;

import java.util.ArrayList;
import java.util.TreeMap;

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

    Person createAccount(Person person);
    Person authenticate(String username, String password);
    Person getPerson(long pid);
    ArrayList<SessionSkatingClass> getSessionClassList(Person person);
    RosterEntry register(Person person, RosterEntry newEntry);
    ArrayList<RosterEntry> getStudentRoster(Person person);
    ArrayList<RosterEntry> getClassRoster(Person person, long classId);
}