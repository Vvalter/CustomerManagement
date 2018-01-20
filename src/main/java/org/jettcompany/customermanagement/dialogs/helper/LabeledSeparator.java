package org.jettcompany.customermanagement.dialogs.helper;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class LabeledSeparator extends StackPane {
    @FXML
    private Label label;
    public LabeledSeparator() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LabeledSeparator.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public StringProperty textProperty() {
        return this.label.textProperty();
    }
    public String getText() {
        return textProperty().get();
    }
    public void setText(String text) {
        textProperty().set(text);
    }
}
