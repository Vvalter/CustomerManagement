package org.jettcompany.customermanagement.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public enum CompanyDivision {
    ANY("beliebig"),
    COSMETIC("Kosmetik"),
    SPORT_AND_FITNESS("Sport & Fitness"),
    WELLNESS_AND_SPA("Wellness & Spa"),
    MEDICINE("Medizin"),
    ;

    private final String name;

    CompanyDivision(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static ObservableList<CompanyDivision> getDivisions(boolean withAny) {
        ObservableList<CompanyDivision> divisions = FXCollections.observableArrayList(values());
        if (!withAny) {
            divisions.removeAll(ANY);
        }
        return divisions;
    }

    public static void setupComboBox(ComboBox<CompanyDivision> comboBox) {
        comboBox.getItems().setAll(CompanyDivision.getDivisions(true));
        comboBox.getSelectionModel().selectFirst();
        comboBox.setConverter(new StringConverter<CompanyDivision>() {
            @Override
            public String toString(CompanyDivision object) {
                if (object == null) {
                    return "";
                }
                return object.getName();
            }

            @Override
            public CompanyDivision fromString(String string) {
                return null;
            }
        });
    }
}
