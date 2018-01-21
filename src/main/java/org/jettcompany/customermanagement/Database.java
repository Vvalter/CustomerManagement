package org.jettcompany.customermanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jettcompany.customermanagement.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Database {
    private static final Logger logger = LogManager.getLogger(Database.class);
    private static final String DB_DRIVER = "org.h2.Driver";

    private static City resultSetToCityString(ResultSet res) throws SQLException {
        String city = res.getString(1);
        State state = State.valueOf(res.getString(2));
        return new City(city, state);
    }

    public static boolean doesCustomerTableExist() {
        try {
            Connection connection = getDBConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tableInfo = metaData.getTables(null, null, "CUSTOMERS", null);
            return tableInfo.next();
        } catch (SQLException e) {
            logger.error("Error while checking existing tables.", e);
            return false;
        }
    }

    private static Customer resultSetToCustomer(ResultSet res) throws SQLException {
        UUID uuid = (UUID) res.getObject(Customer.UUID_COLUMN);
        CompanyDivision division = CompanyDivision.valueOf(res.getString(Customer.COMPANY_DIVISION));
        Country country = Country.valueOf(res.getString(Customer.COUNTRY));
        State state = State.valueOf(res.getString(Customer.STATE));
        String cityName = res.getString(Customer.CITY);
        FilterProperties filterProperties = new FilterProperties(division, country, state, new City(cityName, state));

        Map<String, String> columnValues = new HashMap<>();
        for (String columnName : Customer.stringColumnNames) {
            columnValues.put(columnName, res.getString(columnName));
        }

        return Customer.existingCustomer(uuid, filterProperties, columnValues);
    }

    public static void persistCustomersFromCSV(String path, State state) {
        File file = Paths.get(path).toFile();
        List<Customer> customers;
        try {
            customers = CustomerCSVParser.parse(file, state);
        } catch (IOException e) {
            logger.error("Error while reading customers from csv file.", e);
            return;
        }
        logger.info(String.format("Read %d customers from csv file %s", customers.size(), path));
        Database.persistCustomers(customers);
    }

    public static List<Customer> fetchAllCustomers() {
        String sql = "SELECT * FROM customers";
        logger.info("Fetching all Customers");
        Connection con = null;
        Statement stmt = null;
        ResultSet res = null;
        List<Customer> result = null;
        try {
            con = getDBConnection();
            stmt = con.createStatement();
            res = stmt.executeQuery(sql);
            result = new ArrayList<>();
            while (res.next()) {
                result.add(resultSetToCustomer(res));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all customers", e);
        } finally {
            closeResultSetSilently(res);
            closeStatementSilently(stmt);
            closeConnectionSilently(con);
        }
        logger.info(String.format("Fetched %d customers.", result.size()));
        return result;
    }

    private static Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            logger.error(String.format("Error while registering DB_DRIVER (%s).", DB_DRIVER), e);
        }
        try {
            String dbConnectionString = "jdbc:h2:" + Main.ROOT_FOLDER + "/db/customers";
            dbConnection = DriverManager.getConnection(dbConnectionString, "", "");
        } catch (SQLException e) {
            logger.error("Error while acquiring db connection.", e);
        }
        return dbConnection;
    }

    public static List<City> fetchAllCities() {
        logger.info("Fetching all cities from database.");
        String sql = String.format("SELECT %s,%s FROM customers", Customer.CITY, Customer.STATE);
        Connection con = null;
        Statement stmt = null;
        ResultSet res = null;
        List<City> result = null;
        try {
            con = getDBConnection();
            stmt = con.createStatement();
            res = stmt.executeQuery(sql);
            result = new ArrayList<>();
            while (res.next()) {
                result.add(resultSetToCityString(res));
            }
        } catch (SQLException e) {
            logger.error("Error fetching cities from database.", e);
        } finally {
            closeResultSetSilently(res);
            closeStatementSilently(stmt);
            closeConnectionSilently(con);
        }
        logger.info(String.format("Fetched %d cities from database.", result.size()));
        return result;
    }

    public static void deleteCustomers(List<Customer> customers) {
        String sql = "DELETE FROM customers WHERE id = ?";
        Connection con = null;
        PreparedStatement stmt = null;
        Customer current = null;
        try {
            con = getDBConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(sql);

            for (Customer customer : customers) {
                current = customer;
                stmt.setObject(1, customer.getId());
                stmt.addBatch();
            }

            stmt.executeBatch();
            con.commit();
        } catch (SQLException e) {
            logger.error(String.format("Error while deleting customer (%s): ", current), e);
        } finally {
            closeStatementSilently(stmt);
            closeConnectionSilently(con);
        }
    }

    private static void prepareStatement(PreparedStatement stmt, Customer customer) throws SQLException {
        int index = 1;
        stmt.setObject(index++, customer.getId());

        stmt.setString(index++, customer.getFilterProperties().getDivision().toString());
        stmt.setString(index++, customer.getFilterProperties().getCountry().toString());
        stmt.setString(index++, customer.getFilterProperties().getState().toString());
        stmt.setString(index++, customer.getFilterProperties().getCity().getName());

        for (String columnName : Customer.stringColumnNames) {
            stmt.setString(index++, customer.getColumn(columnName));
        }
    }

    public static void persistCustomers(List<Customer> customers) {
        String questionMarks = IntStream.range(0, Customer.getNumberOfColumns()).mapToObj(i -> "?").collect
                (Collectors.joining(","));
        String sql = String.format("INSERT INTO customers  VALUES(%s)", questionMarks);

        logger.info(String.format("Persisting %d customers. (%s)", customers.size(), customers.size() == 1 ?
                customers.get(0) : " too many"));
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = Database.getDBConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(sql);

            for (Customer customer : customers) {
                prepareStatement(stmt, customer);
                stmt.addBatch();
            }
            stmt.executeBatch();
            con.commit();
        } catch (SQLException e) {
            logger.error("Error while persisting Customers: ", e);
            return;
        } finally {
            closeStatementSilently(stmt);
            closeConnectionSilently(con);
        }
        logger.info("Finished persisting customers.");
    }

    @SuppressWarnings("unused")
    public static void dropTable() {
        logger.info("Dropping table.");
        String sql = "DROP TABLE customers";
        executeUpdateStatement(Database.getDBConnection(), sql);
    }

    private static void addVarchar(StringBuilder builder, String name, int maxLength) {
        builder.append(String.format(name + " varchar(%d),", maxLength));
    }

    public static void createTable() {
        final int DEFAULT_LENGTH = 5000;
        StringBuilder createQuery = new StringBuilder(
                "CREATE TABLE customers ( " +
                        "id uuid NOT NULL PRIMARY KEY, ");
        for (String filterProperty : Customer.filterPropertiesColumnNames) {
            addVarchar(createQuery, filterProperty, DEFAULT_LENGTH);
        }

        for (String columnName : Customer.stringColumnNames) {
            if (columnName.equals(Customer.NOTES)) {
                addVarchar(createQuery, columnName, 5000);
            } else {
                addVarchar(createQuery, columnName, DEFAULT_LENGTH);
            }
        }
        createQuery.deleteCharAt(createQuery.length()-1);
        createQuery.append(")");
        logger.debug(createQuery.toString());
        executeUpdateStatement(getDBConnection(), createQuery.toString());
    }

    private static void executeUpdateStatement(Connection con, String sql) {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            logger.error(String.format("Error while executing SQL statement: %s", sql), e);
        } finally {
            closeStatementSilently(stmt);
            closeConnectionSilently(con);
        }
    }

    private static void closeConnectionSilently(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                logger.error("Unable to close Connection.", e);
            }
        }
    }

    private static void closeStatementSilently(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                logger.error("Unable to close Statement.", e);
            }
        }
    }

    private static void closeResultSetSilently(ResultSet res) {
        if (res != null) {
            try {
                res.close();
            } catch (SQLException e) {
                logger.error("Unable to close ResultSet.", e);
            }
        }
    }
}
