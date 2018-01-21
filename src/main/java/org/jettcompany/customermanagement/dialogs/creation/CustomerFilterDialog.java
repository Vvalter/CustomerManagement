package org.jettcompany.customermanagement.dialogs.creation;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jettcompany.customermanagement.model.Customer;
import org.jettcompany.customermanagement.model.FilterProperties;

import java.io.IOException;
import java.util.List;

public class CustomerFilterDialog extends Dialog<FilterProperties> {

    public CustomerFilterDialog(FilterProperties init, List<Customer> allCustomers) {
        DialogPane dialogPane;
        CustomerFilterController controller;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerFilter.fxml"));
            dialogPane = loader.load();
            controller = loader.getController();
        } catch (IOException e) {
            return;
        }

        controller.setCustomers(allCustomers);
        controller.setFilterProperties(init);

        setResultConverter(buttonType -> {
            ButtonBar.ButtonData buttonData = buttonType == null ? null : buttonType.getButtonData();
            if (buttonData == ButtonBar.ButtonData.OK_DONE) {
                return controller.createFilterPropertiesFromFields();
            } else {
                return null;
            }
        });

        dialogPane.getButtonTypes().addAll(ButtonType.OK);
        setDialogPane(dialogPane);
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.setTitle("Kundenverwaltung");
        stage.getIcons().add(new Image("file:res/CustomerIcon.png"));
    }
}
