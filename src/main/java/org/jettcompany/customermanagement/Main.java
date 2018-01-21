package org.jettcompany.customermanagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jettcompany.customermanagement.dialogs.overview.CustomerOverviewController;
import org.jettcompany.customermanagement.model.State;

import java.io.IOException;
import java.util.Locale;

public class Main extends Application {
    private static final String ROOT_FOLDER_KEY = "customerManagementRootFolder";
    public static String ROOT_FOLDER;

    static {
        ROOT_FOLDER = System.getProperty(ROOT_FOLDER_KEY);
        if (ROOT_FOLDER == null) {
            System.err.println("No system property customerManagementRootFolder found. Looking at environment " +
                    "variables.");
            ROOT_FOLDER = System.getenv(ROOT_FOLDER_KEY);
            if (ROOT_FOLDER == null) {
                System.err.println("No environment variable was found either. Using default value.");
                ROOT_FOLDER = System.getProperty("user.home") + "/Kundenverwaltung";
            }
            System.setProperty(ROOT_FOLDER_KEY, ROOT_FOLDER);
        }

    }
    private static Logger logger = LogManager.getLogger();
    private Stage primaryStage;

    public static void main(String[] args) {
        logger.info(String.format("Root folder: %s", ROOT_FOLDER));

        boolean databaseExists = Database.doesCustomerTableExist();
        if (!databaseExists) {
            logger.info("No customer table found. Creating a new one.");
            Database.createTable();
        }

        Locale.setDefault(Locale.GERMANY);
        logger.info("Starting JavaFX");
        launch(args);
    }

    @SuppressWarnings("unused")
    private static void loadCustomersFromCSVFiles() {
        logger.info("Persisting customers from csv file.");
        Database.persistCustomersFromCSV("res/austria.csv", State.Tirol);
        Database.persistCustomersFromCSV("res/mittelfranken.csv", State.BAVARIA);
        Database.persistCustomersFromCSV("res/freiburg.csv", State.BADEN);
        logger.info("Done.");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.getIcons().add(new Image("file:res/CustomerIcon.png"));
        this.primaryStage.setMaximized(true);
        this.primaryStage.setTitle("Kundenverwaltung");
        showCustomerOverview();
    }

    private void showCustomerOverview() throws IOException {
        Parent root = FXMLLoader.load(CustomerOverviewController.class.getResource("CustomerOverview.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("style.css");
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }
}
