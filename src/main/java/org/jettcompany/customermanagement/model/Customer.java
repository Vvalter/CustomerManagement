package org.jettcompany.customermanagement.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.*;
import java.util.stream.Collectors;

public class Customer {

    public static final String COMPANY_NAME = "Firma";
    public static final String NAME = "Name";
    public static final String POSITION = "Position";
    public static final String NOTES = "Notizen";
    public static final String PRIMARY_EMAIL = "Email";
    public static final String EMAIL2 = "Email_2";
    public static final String EMAIL3 = "Email_3";
    public static final String TELEPHONE_COMPANY = "Telefon_Geschäftlich";
    public static final String TELEPHONE_PRIVATE = "Telefon_Privat";
    public static final String TELEPHONE_MOBILE = "Telefon_Mobil";
    public static final String PRIMARY_WEBSITE = "Webseite_1";
    public static final String WEBSITE2 = "Webseite_2";
    public static final String WEBSITE3 = "Webseite_3";
    public static final String ADDRESS_COMPANY = "Adresse_Geschäftlich";
    public static final String ADDRESS_SHIPPING = "Lieferadresse";
    public static final String ADDRESS_BILLING = "Rechnungsadresse";
    public static final String ADDRESS_PRIVATE = "Adresse_Privat";

    public static final String PLACE_HOLDER = "Additional1";
    public static final String PLACE_HOLDER1 = "Additional2";
    public static final String PLACE_HOLDER2 = "Additional3";
    public static final String PLACE_HOLDER3 = "Additional4";
    public static final String PLACE_HOLDER4 = "Additional5";

    public static final String COMPANY_DIVISION = "Bereich";
    public static final String STATE = "Bundesland";
    public static final String COUNTRY = "Land";
    public static final String CITY = "Stadt";

    public static final String UUID_COLUMN = "id";

    public static final List<String> stringColumnNames = ImmutableList.of(COMPANY_NAME, NAME, POSITION, NOTES,
            PRIMARY_EMAIL, EMAIL2, EMAIL3, TELEPHONE_COMPANY, TELEPHONE_PRIVATE, TELEPHONE_MOBILE, PRIMARY_WEBSITE,
            WEBSITE2, WEBSITE3, ADDRESS_COMPANY, ADDRESS_SHIPPING, ADDRESS_BILLING, ADDRESS_PRIVATE, PLACE_HOLDER,
            PLACE_HOLDER1, PLACE_HOLDER2, PLACE_HOLDER3, PLACE_HOLDER4);

    public static final List<String> filterPropertiesColumnNames = ImmutableList.of(COMPANY_DIVISION, STATE, COUNTRY,
            CITY);


    private final UUID id;
    private final FilterProperties filterProperties;
    private final Map<String, StringProperty> stringColumns;

    private Customer(UUID id, FilterProperties filterProperties, Map<String, String> columnValues) {
        this.id = id;
        this.filterProperties = filterProperties;
        Map<String, StringProperty> temp = new HashMap<>();
        for (String key : columnValues.keySet()) {
            assert (stringColumnNames.contains(key));
        }
        for (String key : stringColumnNames) {
            temp.put(key, new SimpleStringProperty(columnValues.getOrDefault(key, "")));
        }
        this.stringColumns = Collections.unmodifiableMap(temp);
    }

    private Customer(FilterProperties filterProperties, Map<String, String> columnValues) {
        this(UUID.randomUUID(), filterProperties, columnValues);
    }

    public static Customer empty(FilterProperties filterProperties) {
        return new Customer(null, FilterProperties.copyOf(filterProperties), ImmutableMap.of());
    }

    public static Customer empty() {
        return empty(FilterProperties.any());
    }

    public static Customer copyOf(UUID id, Customer other) {
        Map<String, String> columnValues = other.getStringColumns().entrySet().stream().collect(Collectors.toMap(Map
                .Entry::getKey, entry -> entry.getValue().get()));
        return new Customer(id, FilterProperties.copyOf(other.getFilterProperties()), columnValues);
    }

    public static Customer copyOf(Customer other) {
        return copyOf(UUID.randomUUID(), other);
    }

    public static Customer existingCustomer(UUID id, FilterProperties filterProperties, Map<String, String>
            columnNames) {
        return new Customer(id, filterProperties, columnNames);
    }

    public static Customer newCustomer(FilterProperties filterProperties, Map<String, String> columnNames) {
        return new Customer(FilterProperties.copyOf(filterProperties), new HashMap<>(columnNames));
    }

    public static int getNumberOfColumns() {
        return stringColumnNames.size() + filterPropertiesColumnNames.size() + 1; // +1, because of UUID
    }

    private Map<String, StringProperty> getStringColumns() {
        return this.stringColumns;
    }

    public String getColumn(String key) {
        return this.getStringColumns().get(key).getValue();
    }

    public StringProperty getColumnProperty(String key) {
        return this.getStringColumns().get(key);
    }

    public String getSearchableText() {
        String stringColumns = this.stringColumns.values().stream().map(StringExpression::getValue)
                .collect(Collectors.joining());
        return this.filterProperties.getDivision().getName() + this.filterProperties.getCountry() + this
                .filterProperties.getState().getName() + this.filterProperties.getCountry().getName() + stringColumns;
    }

    public FilterProperties getFilterProperties() {
        return this.filterProperties;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + this.id +
                ", filterProperties=" + this.filterProperties +
                ", stringColumns=" + this.stringColumns +
                '}';
    }

    public UUID getId() {
        return this.id;
    }
}
