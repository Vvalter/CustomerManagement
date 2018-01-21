package org.jettcompany.customermanagement.model;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.jettcompany.customermanagement.model.Country.*;

public enum State {
    ANY("beliebig", Country.ANY),
    BADEN("Baden-W\u00fcrttemberg", GERMANY), BAVARIA("Bayern", GERMANY), BERLIN("Berlin", GERMANY), BRANDENBURG
            ("Brandenburg", GERMANY), BREMEN("Bremen", GERMANY), HAMBURG("Hamburg", GERMANY), HESSEN("Hessen",
            GERMANY), MECKPOM("Mecklenburg-Vorpommern", GERMANY), NIEDERSACHSEN("Niedersachsen", GERMANY), NRW
            ("Nordrhein-Westfalen", GERMANY), RHEINLAND("Rheinland-Pfalz", GERMANY), SAARLAND("Saarland", GERMANY),
    SACHSEN("Sachsen", GERMANY), SachsenAnhalt("Sachsen-Anhalt", GERMANY), SchleswigHolstein("Schleswig-Holstein",
            GERMANY), Thueringen("Th\u00fcringen", GERMANY),

    Aargau("Aargau", SWITZERLAND), AppenzellAusserrhoden("Appenzell Ausserrhoden", SWITZERLAND), AppenzellInnerrhoden
            ("Appenzell " +
                    "Innerrhoden", SWITZERLAND),
    BaselLandschaft("Basel-Landschaft", SWITZERLAND), BaselStadt("Basel-Stadt", SWITZERLAND), Bern("Bern",
            SWITZERLAND), Freiburg
            ("Freiburg", SWITZERLAND), Genf("Genf", SWITZERLAND), Glarus("Glarus", SWITZERLAND), Graubuenden
            ("Graub\u00fcnden", SWITZERLAND),
    Jura("Jura", SWITZERLAND), Luzern("Luzern", SWITZERLAND), Neuenburg("Neuenburg", SWITZERLAND), Nidwalden
            ("Nidwalden", SWITZERLAND),
    Obwalden("Obwalden", SWITZERLAND), Schaffhausen("Schaffhausen", SWITZERLAND), Schwyz("Schwyz", SWITZERLAND),
    Solothurn("Solothurn", SWITZERLAND), StGallen("St. Gallen", SWITZERLAND), Tessin("Tessin", SWITZERLAND), Thurgau
            ("Thurgau",

                    SWITZERLAND), Uri("Uri", SWITZERLAND), Waadt("Waadt", SWITZERLAND), Wallis("Wallis", SWITZERLAND)
    , Zurich
            ("Z\u00fcrich", SWITZERLAND), Zug
            ("Zug", SWITZERLAND),
    Burgenland("Burgenland", AUSTRIA), Kaernten("K\u00e4rnten", AUSTRIA), Niederoesterreich("Nieder\u00f6sterreich", AUSTRIA),
    Oberoesterreich("Ober\u00f6sterreich", AUSTRIA), Salzburg("Salzburg", AUSTRIA), Steiermark("Steiermark", AUSTRIA),
    Tirol("Tirol", AUSTRIA), Vorarlberg("Vorarlberg", AUSTRIA), Wien("Wien", AUSTRIA);

    private static Logger logger = LogManager.getLogger(State.class);
    private String name;
    private Country country;

    State(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    public static State getFromName(String name) {
        for (State state : values()) {
            if (state.getName().equals(name)) {
                return state;
            }
        }
        logger.error(String.format("Unable to get State from String %s.", name));
        return ANY;
    }

    public static void setupComboBox(ComboBox<State> comboBox) {
        comboBox.getItems().setAll(State.ANY);
        comboBox.getSelectionModel().selectFirst();
        comboBox.setConverter(new StringConverter<State>() {
            @Override
            public String toString(State object) {
                if (object == null) {
                    return "";
                }
                return object.getName();
            }

            @Override
            public State fromString(String string) {
                return null;
            }
        });
    }

    public Country getCountry() {
        return this.country;
    }

    public String getName() {
        return this.name;
    }
}
