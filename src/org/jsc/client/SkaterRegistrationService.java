package org.jsc.client;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("registration")
public interface SkaterRegistrationService extends RemoteService {

    Person createAccount(Person person);
    Person authenticate(String username, String password);
    Person getPerson(long pid);
    TreeMap<String,String> getClassList(Person person);
    ArrayList<SessionSkatingClass> getSessionClassList(Person person);
    RosterEntry register(Person person, RosterEntry newEntry);
}