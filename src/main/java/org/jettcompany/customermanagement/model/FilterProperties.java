package org.jettcompany.customermanagement.model;

import javafx.beans.property.SimpleObjectProperty;

public final class FilterProperties {

    private final SimpleObjectProperty<CompanyDivision> division;
    private final SimpleObjectProperty<Country> country;
    private final SimpleObjectProperty<State> state;
    private final SimpleObjectProperty<City> city;

    public FilterProperties(CompanyDivision division, Country country,
                            State state, City city) {
        this.division = new SimpleObjectProperty<>(division);
        this.country = new SimpleObjectProperty<>(country);
        this.state = new SimpleObjectProperty<>(state);
        this.city = new SimpleObjectProperty<>(city);
    }

    public static FilterProperties any() {
        return new FilterProperties(CompanyDivision.ANY, Country.ANY, State.ANY, City.any());
    }

    public static FilterProperties copyOf(FilterProperties other) {
        return new FilterProperties(other.getDivision(), other.getCountry(), other.getState(), other.getCity());
    }

    public SimpleObjectProperty<City> cityProperty() {
        return this.city;
    }

    @Override
    public String toString() {
        return "FilterProperties{" + "division=" + this.division.get() + ", country=" + this.country.get() + ", " +
                "state=" + this.state.get() + ", city=" + this.city.get() + '}';
    }

    public State getState() {
        return this.state.get();
    }

    public void setState(State state) {
        this.state.set(state);
    }

    public SimpleObjectProperty<State> stateProperty() {
        return this.state;
    }

    public Country getCountry() {
        return this.country.get();
    }

    public void setCountry(Country country) {
        this.country.set(country);
    }

    public SimpleObjectProperty<Country> countryProperty() {
        return this.country;
    }

    public CompanyDivision getDivision() {
        return this.division.get();
    }

    public SimpleObjectProperty<CompanyDivision> divisionProperty() {
        return this.division;
    }

    public boolean matches(FilterProperties other) {
        Country country1 = this.country.get();
        Country country2 = other.country.get();
        if (country1 != Country.ANY && country2 != Country.ANY && country1 != country2) return false;

        State state1 = this.state.get();
        State state2 = other.state.get();
        if (state1 != State.ANY && state2 != State.ANY && state1 != state2) return false;

        CompanyDivision division1 = this.division.get();
        CompanyDivision division2 = other.division.get();
        if (division1 != CompanyDivision.ANY && division2 != CompanyDivision.ANY && division1 != division2)
            return false;

        City city1 = this.city.get();
        City city2 = other.city.get();
        return city1.equals(city2) ||  city2.equals(City.any());
    }

    public City getCity() {
        return this.city.get();
    }

    public void setCity(City city) {
        this.city.set(city);
    }
}

