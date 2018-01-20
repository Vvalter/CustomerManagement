package org.jettcompany.customermanagement.model;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomerCSVParser {

    private CustomerCSVParser() {
    }

    public static List<City> parseCities(InputStream inputStream) throws IOException {
        List<City> result = new ArrayList<>();
        CSVFormat format = CSVFormat.DEFAULT.withDelimiter(',').withFirstRecordAsHeader();
        CSVParser parser = CSVParser.parse(inputStream, Charset.forName("UTF-8"), format);

        for (CSVRecord csvRecord : parser) {
            State state = State.getFromName(csvRecord.get("bundesland"));
            if (state.equals(State.ANY)) {
                throw new RuntimeException();
            }
            result.add(new City(csvRecord.get("ort"), state));
        }
        return result;
    }

    public static List<Customer> parse(File file, State state) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.withDelimiter('\t').withFirstRecordAsHeader();
        CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), format);

        List<Customer> result = new ArrayList<>();

        int i = 0;
        for (CSVRecord csvRecord : parser) {
            Country country;
            switch (csvRecord.get("addr:country")) {
                case "":
                case "DE":
                    country = Country.GERMANY;
                    break;
                case "CH":
                case "Switzerland":
                    country = Country.SWITZERLAND;
                    break;
                case "AT":
                    country = Country.AUSTRIA;
                    break;
                default:
                    throw new IllegalArgumentException(csvRecord.get("addr:country"));
            }
            if (i % CompanyDivision.values().length == 0) i = 1;
            FilterProperties filterProperties = new FilterProperties(CompanyDivision.values()[i % CompanyDivision
                    .values().length], country, state, new City(csvRecord.get("addr:city"), state));
            i++;
            Map<String, String> columnNames = ImmutableMap.of(
                    Customer.COMPANY_NAME, csvRecord.get("name"),
                    Customer.ADDRESS_COMPANY, csvRecord.get("addr:street") + " " + csvRecord .get("addr:housenumber") + " " + csvRecord.get("addr:city"),
                    Customer.TELEPHONE_COMPANY, csvRecord.get("phone"),
                    Customer.CITY, csvRecord.get("addr:city")
            );
            Customer customer = Customer.newCustomer(filterProperties, columnNames);
            if (!customer.getColumn(Customer.COMPANY_NAME).equals("")) result.add(customer);
        }
        return result;
    }
}
