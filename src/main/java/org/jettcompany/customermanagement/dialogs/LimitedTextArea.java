package org.jettcompany.customermanagement.dialogs;

import javafx.beans.NamedArg;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;

public class LimitedTextArea extends TextArea {
    public LimitedTextArea(@NamedArg("limit") int limit) {
        this.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= limit ? change : null));
    }
}
