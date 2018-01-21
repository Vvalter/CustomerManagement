package org.jettcompany.customermanagement.dialogs;

import javafx.beans.NamedArg;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class LimitedTextField extends TextField {

    public LimitedTextField(@NamedArg(value = "limit", defaultValue = "5000") int limit) {
        this.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= limit ? change : null));
    }
}
