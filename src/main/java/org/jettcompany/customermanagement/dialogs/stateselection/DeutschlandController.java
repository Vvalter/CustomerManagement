package org.jettcompany.customermanagement.dialogs.stateselection;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.SVGPath;
import org.jettcompany.customermanagement.model.Country;
import org.jettcompany.customermanagement.model.State;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class DeutschlandController extends Group {

    @SuppressWarnings("unused")
    @FXML
    private Group stateGroup;

    private ObjectProperty<State> selectedState;
    private ObjectProperty<Country> selectedCountry;

    private Map<State, SVGPath> stateToSVGPath = new EnumMap<>(State.class);
    private Map<SVGPath, State> svgPathToState = new HashMap<>();

    public DeutschlandController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Deutschland.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void bindStateProperty(ObjectProperty<State> property) {
        this.selectedState = property;
        this.selectedState.addListener((observable, oldValue, newValue) -> {
                    deselectState(oldValue);
                    selectState(newValue);
                }
        );
    }

    public void bindCountryProperty(ObjectProperty<Country> property) {
        this.selectedCountry = property;
    }

    private void selectState(State newState) {
        if (newState == null || !this.stateToSVGPath.containsKey(newState)) return;
        SVGPath svgPath = this.stateToSVGPath.get(newState);
        svgPath.setOpacity(1.0);
    }

    private void deselectState(State newState) {
        if (newState == null || !this.stateToSVGPath.containsKey(newState)) return;
        SVGPath svgPath = this.stateToSVGPath.get(newState);
        svgPath.setOpacity(0.5);
    }

    @SuppressWarnings("unused")
    @FXML
    public void initialize() {
        double size = 0.5;
        this.stateGroup.setScaleX(size);
        this.stateGroup.setScaleY(size);

        for (State state : State.values()) {
            for (Node path : this.stateGroup.getChildren()) {
                SVGPath svgPath = (SVGPath) path;
                if (state.getName().equals(svgPath.getId())) {
                    this.stateToSVGPath.put(state, svgPath);
                    this.svgPathToState.put(svgPath, state);
                    break;
                }
            }
        }
        for (Node path : this.stateGroup.getChildren()) {
            SVGPath svgPath = (SVGPath) path;
            svgPath.setOpacity(0.5);
        }
    }

    @SuppressWarnings("unused")
    @FXML
    public void onGroupClicked(MouseEvent e) {
        if (!(e.getTarget() instanceof SVGPath)) return;
        SVGPath target = (SVGPath) e.getTarget();
        if (this.svgPathToState.containsKey(target)) {
            this.selectedCountry.setValue(Country.GERMANY);
            this.selectedState.setValue(this.svgPathToState.get(target));
        }
    }
}
