package org.jettcompany.customermanagement.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public enum Country {
    ANY("beliebig"), GERMANY("Deutschland"), SWITZERLAND("Schweiz"), AUSTRIA("Österreich");
    private final String name;

    Country(String name) {
        this.name = name;
    }

    public static void setupComboBoxes(ComboBox<Country> countryComboBox, ComboBox<State> stateComboBox) {
        countryComboBox.getItems().setAll(FXCollections.observableArrayList(values()));
        countryComboBox.getSelectionModel().selectFirst();
        countryComboBox.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == Country.ANY) {
                        stateComboBox.getItems().setAll(State.ANY);
                        stateComboBox.getSelectionModel().selectFirst();
                    } else if (newValue != null && newValue != oldValue) {
                        stateComboBox.getItems().setAll(newValue.getStates());
                        stateComboBox.getSelectionModel().selectFirst();
                    }
                }
        );

        countryComboBox.setConverter(new StringConverter<Country>() {
            @Override
            public String toString(Country object) {
                if (object == null) {
                    return "";
                }
                return object.getName();
            }

            @Override
            public Country fromString(String string) {
                return null;
            }
        });
    }

    public String getName() {
        return this.name;
    }

    private ObservableList<State> getStates() {
        ObservableList<State> result = FXCollections.observableArrayList();
        for (State state : State.values()) {
            if (state == State.ANY || this == ANY || state.getCountry().equals(this)) {
                result.add(state);
            }
        }
        return result;
    }
}
