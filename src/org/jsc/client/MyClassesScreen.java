package org.jsc.client;

/**
 * A specialization of BaseScreen to represent the "My Classes" screen in the 
 * application.  This user interface class provides a way to browse through the
 * classes for a student.
 * 
 * @author Matthew Jones
 *
 */
public class MyClassesScreen extends BaseScreen {

    public MyClassesScreen(LoginSession loginSession) {
        super(loginSession);
        this.setScreenTitle("My Classes");
    }
}
