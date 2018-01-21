package org.jettcompany.customermanagement.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class City {
    private static final Map<String, City> instances = new HashMap<>();
    private String name;

    private City(String name) {
        this.name = name;
    }

    public static City get(String cityName) {
        instances.putIfAbsent(cityName, new City(cityName));
        return instances.get(cityName);
    }

    public static City any() {
        return get("beliebig");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return Objects.equals(this.name, city.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
