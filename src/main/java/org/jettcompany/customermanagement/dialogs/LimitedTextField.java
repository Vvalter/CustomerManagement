package org.jettcompany.customermanagement.dialogs;

import javafx.beans.NamedArg;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class LimitedTextField extends TextField {

    public LimitedTextField(@NamedArg("limit") int limit) {
        this.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= limit ? change : null));
    }
}
