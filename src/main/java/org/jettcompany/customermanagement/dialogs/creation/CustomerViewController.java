package org.jettcompany.customermanagement.dialogs.creation;

import com.google.common.collect.ImmutableMap;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.util.StringConverter;
import org.jettcompany.customermanagement.model.*;

import java.util.HashMap;
import java.util.Map;

public class CustomerViewController {
    private final Customer selectionCustomer = Customer.empty();
    @FXML
    private TextField companyNameField;
    @FXML
    private TextField personNameField;
    @FXML
    private TextField positionField;
    @FXML
    private ComboBox<CompanyDivision> divisionComboBox;
    @FXML
    private ComboBox<Country> countryComboBox;
    @FXML
    private ComboBox<State> stateComboBox;
    @FXML
    private TextField cityField;
    @FXML
    private TextField email1Field;
    @FXML
    private TextField email2Field;
    @FXML
    private TextField email3Field;
    @FXML
    private TextField website1Field;
    @FXML
    private TextField website2Field;
    @FXML
    private TextField website3Field;
    @FXML
    private TextField phoneCompanyField;
    @FXML
    private TextField phonePrivateField;
    @FXML
    private TextField phoneMobileField;
    @FXML
    private TextField phoneFaxField;
    @FXML
    private TextArea addressCompanyField;
    @FXML
    private TextArea addressShippingField;
    @FXML
    private TextArea addressBillingField;
    @FXML
    private TextArea addressPrivateField;
    @FXML
    private TextArea notesArea;

    @FXML
    public void initialize() {
        State.setupComboBox(this.stateComboBox);
        Country.setupComboBoxes(this.countryComboBox, this.stateComboBox);
        CompanyDivision.setupComboBox(this.divisionComboBox);

        setupBindings();
    }

    private void setupBindings() {
        FilterProperties selectionFilterProperties = this.selectionCustomer.getFilterProperties();
        this.countryComboBox.valueProperty().bindBidirectional(selectionFilterProperties.countryProperty());
        this.stateComboBox.valueProperty().bindBidirectional(selectionFilterProperties.stateProperty());
        this.divisionComboBox.valueProperty().bindBidirectional(selectionFilterProperties.divisionProperty());
        this.cityField.textProperty().bindBidirectional(selectionFilterProperties.cityProperty(), new
                StringConverter<City>() {
                    @Override
                    public String toString(City city) {
                        return city.getName();
                    }

                    @Override
                    public City fromString(String string) {
                        return new City(string, selectionFilterProperties.getState());
                    }
                });

        // TODO
        Map<String, TextInputControl> customerKeysToTextFields = new HashMap<>();
        customerKeysToTextFields.putAll(ImmutableMap.of(
                Customer.COMPANY_NAME, this.companyNameField,
                Customer.NAME, this.personNameField,
                Customer.POSITION, this.positionField,
                Customer.PRIMARY_EMAIL, this.email1Field));


        customerKeysToTextFields.putAll(ImmutableMap.of(
                Customer.EMAIL2, this.email2Field,
                Customer.EMAIL3, this.email3Field,
                Customer.PRIMARY_WEBSITE, this.website1Field,
                Customer.WEBSITE2, this.website2Field,
                Customer.WEBSITE3, this.website3Field));

        customerKeysToTextFields.putAll(ImmutableMap.of(
                Customer.TELEPHONE_COMPANY, this.phoneCompanyField,
                Customer.TELEPHONE_MOBILE, this.phoneMobileField,
                Customer.TELEPHONE_PRIVATE, this.phonePrivateField));

        customerKeysToTextFields.putAll(ImmutableMap.of(
                Customer.ADDRESS_COMPANY, this.addressCompanyField,
                Customer.ADDRESS_BILLING, this.addressBillingField,
                Customer.ADDRESS_SHIPPING, this.addressShippingField,
                Customer.ADDRESS_PRIVATE, this.addressPrivateField,
                Customer.NOTES, this.notesArea
        ));

        for (Map.Entry<String, TextInputControl> entry : customerKeysToTextFields.entrySet()) {
            String key = entry.getKey();
            TextInputControl textField = entry.getValue();
            textField.textProperty().bindBidirectional(this.selectionCustomer.getColumnProperty(key));
        }
    }

    public Customer createCustomerFromFields() {
        return Customer.copyOf(this.selectionCustomer);
    }

    public void setInitialValues(Customer customer) {
        for (String key : Customer.stringColumnNames) {
            this.selectionCustomer.getColumnProperty(key).setValue(customer.getColumn(key));
        }
        FilterProperties filterProperties = this.selectionCustomer.getFilterProperties();
        filterProperties.countryProperty().set(customer.getFilterProperties().getCountry());
        filterProperties.stateProperty().set(customer.getFilterProperties().getState());
        filterProperties.divisionProperty().set(customer.getFilterProperties().getDivision());
        filterProperties.cityProperty().set(City.copyOf(customer.getFilterProperties().getCity()));
    }
}
