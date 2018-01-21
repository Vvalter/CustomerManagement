package org.jettcompany.customermanagement.dialogs.creation;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jettcompany.customermanagement.model.Customer;

import java.io.IOException;

public class CustomerViewDialog extends Dialog<Customer> {
    private static final Logger logger = LogManager.getLogger(CustomerViewDialog.class);

    public CustomerViewDialog(Customer customer) {
        DialogPane dialogPane;
        CustomerViewController controller;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NewCustomerView.fxml"));
            dialogPane = loader.load();
            controller = loader.getController();
        } catch (IOException e) {
            logger.info("Error while loading Resource: ", e);
            throw new RuntimeException(e);
        }

        controller.setInitialValues(customer);

        dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        setResultConverter(buttonType -> {
            ButtonBar.ButtonData buttonData = buttonType == null ? null : buttonType.getButtonData();
            if (buttonData == ButtonBar.ButtonData.OK_DONE) {
                Customer newCustomer = controller.createCustomerFromFields();
                if (customer.getId() != null) {
                    newCustomer = Customer.copyOf(customer.getId(), newCustomer);
                }
                return newCustomer;
            } else {
                return null;
            }
        });

        setDialogPane(dialogPane);
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.setTitle("Visitenkarte");
        stage.getIcons().add(new Image("CustomerIcon.png"));
        getDialogPane().getScene().getStylesheets().add("style.css");
    }
}
