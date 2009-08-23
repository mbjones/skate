package org.jsc.client;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The asynchronous version of the remote RPC service that is used to store application data.
 * @see SkaterRegistrationServiceImpl
 * 
 * @author Matt Jones
 */
public interface SkaterRegistrationServiceAsync {

  void createAccount(Person person, AsyncCallback<Person> callback);
  void authenticate(String username, String password, AsyncCallback<Person> callback);
  void getPerson(long pid, AsyncCallback<Person> callback);
  void getSessionClassList(Person person, AsyncCallback<ArrayList<SessionSkatingClass>> callback);
  void register(Person person, RosterEntry newEntry, AsyncCallback<RosterEntry> callback);
  void getStudentRoster(Person person, AsyncCallback<ArrayList<RosterEntry>> callback);
  void getClassRoster(Person person, long classid, AsyncCallback<ArrayList<RosterEntry>> callback);
}