package org.jettcompany.customermanagement.model;

import java.util.Objects;

public class City {
    private static City anyCity = new City("beliebig", State.ANY);
    private String name;
    private State state;

    public City(String name, State state) {
        this.name = name;
        this.state = state;
    }

    public static City copyOf(City other) {
        return new City(other.name, other.state);
    }

    public static City any() {
        return anyCity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return Objects.equals(this.name, city.name) &&
                this.state == city.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.state);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
