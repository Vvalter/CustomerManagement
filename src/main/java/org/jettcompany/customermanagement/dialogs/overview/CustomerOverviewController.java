package org.jettcompany.customermanagement.dialogs.overview;

import com.google.common.collect.ImmutableMap;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jettcompany.customermanagement.Database;
import org.jettcompany.customermanagement.dialogs.creation.CustomerFilterDialog;
import org.jettcompany.customermanagement.dialogs.creation.CustomerViewDialog;
import org.jettcompany.customermanagement.model.City;
import org.jettcompany.customermanagement.model.CompanyDivision;
import org.jettcompany.customermanagement.model.Country;
import org.jettcompany.customermanagement.model.Customer;
import org.jettcompany.customermanagement.model.FilterProperties;
import org.jettcompany.customermanagement.model.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CustomerOverviewController {
    private static Logger logger = LogManager.getLogger(CustomerOverviewController.class);

    @FXML
    private TableView<Customer> customerTableView;

    @FXML
    private TableColumn<Customer, Country> countryColumn;
    @FXML
    private TableColumn<Customer, CompanyDivision> divisionColumn;
    @FXML
    private TableColumn<Customer, State> stateColumn;
    @FXML
    private TableColumn<Customer, String> nameColumn;
    @FXML
    private TableColumn<Customer, String> personNameColumn;
    @FXML
    private TableColumn<Customer, String> notesColumn;
    @FXML
    private TableColumn<Customer, String> telephoneColumn;
    @FXML
    private TableColumn<Customer, String> addressColumn;
    @FXML
    private TableColumn<Customer, City> cityColumn;

    @FXML
    private Button removeCustomerButton;

    @FXML
    private Button editCustomerButton;

    @FXML
    private TextField searchField;

    private Predicate<Customer> filterPropertiesFilter = customer -> true;
    private Predicate<Customer> searchFilter = customer -> true;

    private ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private FilteredList<Customer> filteredCustomers;

    private SimpleObjectProperty<FilterProperties> currentFilterProperties =
            new SimpleObjectProperty<>(FilterProperties.any());

    @FXML
    public void initialize() {
        setupCustomerList();
        setupColumns();
        setupDoubleClick();
        setupFilterBindings();

        openFilterDialog(null);
        updateButtonsEnabled();
    }

    @FXML
    public void openFilterDialog(@SuppressWarnings("unused") ActionEvent unused) {
        CustomerFilterDialog dialog = new CustomerFilterDialog(this.currentFilterProperties.get(), this.allCustomers);
        Optional<FilterProperties> result = dialog.showAndWait();
        logger.info(String.format("FilterDialog returned filter: %s", result));
        result.ifPresent(filterProperties -> this.currentFilterProperties.set(filterProperties));
    }

    @FXML
    public void openCustomerCreationDialog(@SuppressWarnings("unused") ActionEvent unused) {
        logger.info("Opening customer creation dialog.");
        CustomerViewDialog dialog = new CustomerViewDialog(Customer.empty(this.currentFilterProperties.get()));
        Optional<Customer> result = dialog.showAndWait();
        logger.info(String.format("Returned customer: %s", result.toString()));
        result.ifPresent(customer -> {
            logger.info(String.format("Created new customer: %s", customer));
            this.allCustomers.add(customer);
            this.customerTableView.getSelectionModel().select(customer);
        });
    }

    @SuppressWarnings("unused")
    @FXML
    public void removeCustomer(ActionEvent e) {
        Customer customer = this.customerTableView.getSelectionModel().getSelectedItem();
        logger.info(String.format("Asking user to delete customer: %s", customer));
        if (customer != null && askUserForDeleteConfirmation(customer)) {
            logger.info("User confirmed.");
            this.allCustomers.remove(customer);
        } else {
            logger.info("User declined.");
        }
    }

    @SuppressWarnings("unused")
    @FXML
    public void editCustomer(ActionEvent e) {
        Customer customer = this.customerTableView.getSelectionModel().getSelectedItem();
        if (customer != null) {
            this.openCustomerEditingDialog(customer);
        }
    }

    private void openCustomerEditingDialog(Customer customer) {
        logger.info(String.format("Started editing customer: %s", customer));
        CustomerViewDialog dialog = new CustomerViewDialog(customer);
        Optional<Customer> result = dialog.showAndWait();
        result.ifPresent(newCustomer -> {
            logger.info(String.format("Finished editing customer: %s", newCustomer));
            int index = this.allCustomers.indexOf(customer);
            if (index != -1) {
                this.allCustomers.set(index, newCustomer);
            } else {
                this.allCustomers.add(newCustomer);
            }
        });
    }

    private void setupDoubleClick() {
        this.customerTableView.setRowFactory(tableView -> {
            TableRow<Customer> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openCustomerEditingDialog(row.getItem());
                }
            });
            return row;
        });
    }

    private void setupFilterBindings() {
        this.searchField.textProperty().addListener(((observable1, oldValue1, newValue1) -> {
            if (newValue1 == null || newValue1.equals("")) {
                this.searchFilter = customer -> true;
            } else {
                this.searchFilter = customer -> customer.getSearchableText().toLowerCase().contains
                        (newValue1.toLowerCase());
            }
            updateFilter();
        }));
        this.currentFilterProperties.addListener(((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.filterPropertiesFilter = customer -> true;
            } else {
                this.filterPropertiesFilter = customer -> customer.getFilterProperties().matches(newValue);
            }
            updateFilter();
        }));
    }

    private void updateFilter() {
        this.filteredCustomers.setPredicate(this.filterPropertiesFilter.and(this.searchFilter));
    }

    private void setupCustomerList() {
        this.allCustomers.addAll(Database.fetchAllCustomers());
        this.allCustomers.addListener(
                (ListChangeListener<Customer>) c -> {
                    while (c.next()) {
                        List<Customer> deletedCustomers = new ArrayList<>();
                        List<Customer> changedCustomers = new ArrayList<>();
                        if (c.wasRemoved()) {
                            deletedCustomers.addAll(c.getRemoved());
                        }
                        if (c.wasUpdated()) {
                            for (int i = c.getFrom(); i < c.getTo(); i++) {
                                changedCustomers.add(this.allCustomers.get(i));
                            }
                        } else if (c.wasAdded()) {
                            changedCustomers.addAll(c.getAddedSubList());
                        }
                        Database.deleteCustomers(deletedCustomers);
                        Database.persistCustomers(changedCustomers);
                    }
                }
        );
        this.filteredCustomers = new FilteredList<>(this.allCustomers);
        SortedList<Customer> sortedCustomers = new SortedList<>(this.filteredCustomers);
        sortedCustomers.comparatorProperty().bind(this.customerTableView.comparatorProperty());
        this.customerTableView.setItems(sortedCustomers);
        this.customerTableView.getSelectionModel().selectedItemProperty().addListener((
                (observable, oldValue, newValue) -> updateButtonsEnabled()));
        this.customerTableView.getSelectionModel().selectFirst();
    }

    private void updateButtonsEnabled() {
        boolean isNoCustomerSelected = this.customerTableView.getSelectionModel().getSelectedItem() == null;
        this.removeCustomerButton.setDisable(isNoCustomerSelected);
        this.editCustomerButton.setDisable(isNoCustomerSelected);
    }

    private void setupColumns() {
        // Filter Properties Columns
        this.countryColumn.setCellValueFactory(cellData -> cellData.getValue().getFilterProperties().countryProperty());
        this.countryColumn.setCellFactory(col -> new TableCell<Customer, Country>() {
                    @Override
                    public void updateItem(Country item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setText("");
                        } else if (!empty) {
                            setText(item.getName());
                        }
                    }
                }
        );
        this.divisionColumn.setCellValueFactory(cellData -> cellData.getValue().getFilterProperties()
                .divisionProperty());
        this.divisionColumn.setCellFactory(col -> new TableCell<Customer, CompanyDivision>() {
                    @Override
                    public void updateItem(CompanyDivision item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setText("");
                        } else if (!empty) {
                            setText(item.getName());
                        }
                    }
                }
        );

        this.stateColumn.setCellValueFactory(cellData -> cellData.getValue().getFilterProperties().stateProperty());
        this.stateColumn.setCellFactory(col -> new TableCell<Customer, State>() {
                    @Override
                    public void updateItem(State item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setText("");
                        } else if (!empty) {
                            setText(item.getName());
                        }
                    }
                }
        );

        this.cityColumn.setCellValueFactory(cellData -> cellData.getValue().getFilterProperties().cityProperty());
        this.cityColumn.setCellFactory(col -> new TableCell<Customer, City>() {
            @Override
            public void updateItem(City item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setText("");
                } else if (!empty) {
                    setText(item.getName());
                }
            }
        });

        // String columns
        Map<TableColumn<Customer, String>, String> columnToKeyMap = ImmutableMap.of(
                this.nameColumn, Customer.COMPANY_NAME,
                this.personNameColumn, Customer.NAME,
                this.telephoneColumn, Customer.TELEPHONE_COMPANY,
                this.addressColumn, Customer.ADDRESS_COMPANY,
                this.notesColumn, Customer.NOTES
        );

        for (Map.Entry<TableColumn<Customer, String>, String> entry : columnToKeyMap.entrySet()) {
            TableColumn<Customer, String> column = entry.getKey();
            String key = entry.getValue();
            column.setCellValueFactory(cellData -> cellData.getValue().getColumnProperty(key));
            column.setCellFactory(TextFieldTableCell.forTableColumn());
//            column.setText(key);
        }
    }

    private boolean askUserForDeleteConfirmation(Customer customer) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Sicherheitsabfrage");
        confirmationDialog.setHeaderText("Soll \"" + customer.getColumn(Customer.COMPANY_NAME) + "\" wirklich " +
                "gel\u00f6scht werden?");
        Stage stage = (Stage) confirmationDialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("CustomerIcon.png"));
        Optional<ButtonType> result = confirmationDialog.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
