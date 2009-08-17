package org.jsc.client;

import java.util.TreeMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SkaterRegistrationServiceAsync {

  void createAccount(Person person, AsyncCallback<Person> callback);
  void authenticate(String username, String password, AsyncCallback<Person> callback);
  void getPerson(long pid, AsyncCallback<Person> callback);
  void getClassList(Person person, AsyncCallback<TreeMap<String,String>> callback);
  void register(Person person, RosterEntry newEntry, AsyncCallback<RosterEntry> callback);
}