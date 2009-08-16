package org.jsc.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SkaterRegistrationServiceAsync {

  void createAccount(Person person, AsyncCallback<Person> callback);
  void authenticate(String username, String password, AsyncCallback<Person> callback);
  void getPerson(long pid, AsyncCallback<Person> callback);
}