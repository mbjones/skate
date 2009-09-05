package org.jsc.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * An exception that is thrown from the remote service when a SQLException occurs
 * during data processing.  
 * @author Matt Jones
 *
 */
public class SQLRecordException extends Throwable implements IsSerializable {

    private String message;

    public SQLRecordException(Exception e) {
        this.message = e.getMessage();
    }

    public SQLRecordException() {
        this.message = "";
    }

    public SQLRecordException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
