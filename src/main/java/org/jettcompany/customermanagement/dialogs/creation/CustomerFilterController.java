package org.jettcompany.customermanagement.dialogs.creation;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.jettcompany.customermanagement.dialogs.stateselection.DeutschlandController;
import org.jettcompany.customermanagement.model.*;

import java.util.Comparator;
import java.util.function.Predicate;

public class CustomerFilterController {

    @FXML
    private TextField citySearchBar;
    @FXML
    private ListView<City> citiesTableView;
    @FXML
    private ComboBox<Country> countryComboBox;
    @FXML
    private ComboBox<State> stateComboBox;
    @FXML
    private ComboBox<CompanyDivision> divisionComboBox;
    @FXML
    private DeutschlandController deutschlandController;

    private ObjectProperty<State> selectedState = new SimpleObjectProperty<>(State.ANY);
    private ObjectProperty<Country> selectedCountry = new SimpleObjectProperty<>(Country.ANY);
    private ObjectProperty<CompanyDivision> selectedDivision = new SimpleObjectProperty<>(CompanyDivision.ANY);
    private ObjectProperty<City> selectedCity = new SimpleObjectProperty<>(City.any());

    private FilteredList<City> filteredCities = new FilteredList<>(FXCollections.observableArrayList());

    private Predicate<City> searchFilter = city -> true;
    private Predicate<City> stateFilter = city -> true;
    private Predicate<City> countryFilter = city -> true;

    public void setCities(ObservableList<City> cities) {
        this.filteredCities = new FilteredList<>(cities);
        updateFilter();
        this.citiesTableView.setItems(new SortedList<>(this.filteredCities, Comparator.comparing(City::getName)));
    }

    public void setFilterProperties(FilterProperties filterProperties) {
        this.selectedState.setValue(filterProperties.getState());
        this.selectedCountry.setValue(filterProperties.getCountry());
        this.selectedDivision.setValue(filterProperties.getDivision());
        this.citiesTableView.getSelectionModel().select(filterProperties.getCity());
    }

    public FilterProperties createFilterPropertiesFromFields() {
        City city = this.selectedCity.get();
        if (city == null) {
            city = City.any();
        }
        return new FilterProperties(this.divisionComboBox.getValue(), this.countryComboBox.getValue(),
                this.stateComboBox.getValue(), city);
    }

    @FXML
    public void initialize() {
        State.setupComboBox(this.stateComboBox);
        Country.setupComboBoxes(this.countryComboBox, this.stateComboBox);
        CompanyDivision.setupComboBox(this.divisionComboBox);

        setupBindings();
        setupFilterBindings();
    }

    private void setupBindings() {
        this.stateComboBox.valueProperty().bindBidirectional(this.selectedState);
        this.countryComboBox.valueProperty().bindBidirectional(this.selectedCountry);
        this.deutschlandController.bindStateProperty(this.selectedState);
        this.deutschlandController.bindCountryProperty(this.selectedCountry);
        this.divisionComboBox.valueProperty().bindBidirectional(this.selectedDivision);
        this.selectedCity.bind(this.citiesTableView.getSelectionModel().selectedItemProperty());
    }

    private void setupFilterBindings() {
        this.stateComboBox.valueProperty().addListener((observable, oldValue, newValue) -> setStateFilter(newValue));
        this.countryComboBox.valueProperty().addListener(((observable, oldValue, newValue) -> setCountryFilter(newValue)));
        this.citySearchBar.textProperty().addListener(((observable, oldValue, newValue) -> setSearchFilter(newValue)));
    }

    private void setCountryFilter(Country country) {
        if (country == null || country == Country.ANY) {
            this.countryFilter = city -> true;
        } else {
            this.countryFilter = city -> city.getState().getCountry() == country;
        }
        updateFilter();
    }

    private void setStateFilter(State state) {
        if (state == null || state == State.ANY) {
            this.stateFilter = city -> true;
        } else {
            this.stateFilter = city -> city.getState() == state;
        }
        updateFilter();
    }

    private void setSearchFilter(String newValue) {
        if (newValue == null || newValue.equals("")) {
            this.searchFilter = city -> true;
        } else {
            this.searchFilter = city -> city.getName().toLowerCase().contains(newValue.toLowerCase());
        }
        updateFilter();
    }

    private void updateFilter() {
        this.filteredCities.setPredicate(this.countryFilter.and(this.stateFilter.and(this.searchFilter)));
    }

}
