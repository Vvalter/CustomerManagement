package org.jettcompany.customermanagement.dialogs;

import javafx.beans.NamedArg;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import org.apache.logging.log4j.LogManager;

public class LimitedTextArea extends TextArea {
    public LimitedTextArea(@NamedArg(value = "limit", defaultValue = "5000") int limit) {
        this.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= limit ? change : null));
    }
}
