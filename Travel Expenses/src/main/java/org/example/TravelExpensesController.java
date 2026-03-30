package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class TravelExpensesController {

    @FXML private Label lblTitle;
    @FXML private Label lblDistance;
    @FXML private Label lblConsumption;
    @FXML private Label lblPrice;
    @FXML private Label lblResult;

    @FXML private TextField txtDistance;
    @FXML private TextField txtConsumption;
    @FXML private TextField txtPrice;

    @FXML private Button btnCalculate;
    @FXML private Button btnEN;
    @FXML private Button btnFR;
    @FXML private Button btnJP;
    @FXML private Button btnIR;

    private ResourceBundle bundle;
    private Locale currentLocale;

    @FXML
    public void initialize() {
        setLanguage(Locale.US);
    }

    @FXML
    public void handleEnglish() {
        setLanguage(Locale.US);
    }

    @FXML
    public void handleFrench() {
        setLanguage(Locale.FRANCE);
    }

    @FXML
    public void handleJapanese() {
        setLanguage(Locale.JAPAN);
    }

    @FXML
    public void handlePersian() {
        setLanguage(new Locale.Builder().setLanguage("fa").setRegion("IR").build());
    }

    private void setLanguage(Locale locale) {
        this.currentLocale = locale;
        try {
            bundle = ResourceBundle.getBundle("messages", locale);
            updateUI();
        } catch (MissingResourceException e) {
            lblResult.setText("Error: Language resource file not found for " + locale.getDisplayLanguage());
            lblResult.setStyle("-fx-text-fill: #f44336; -fx-font-size: 14px;");
        }
    }

    private void updateUI() {
        lblTitle.setText(bundle.getString("title"));
        lblDistance.setText(bundle.getString("distance.label"));
        lblConsumption.setText(bundle.getString("consumption.label"));
        lblPrice.setText(bundle.getString("price.label"));
        btnCalculate.setText(bundle.getString("calculate.button"));
        
        txtDistance.setPromptText(bundle.getString("distance.prompt"));
        txtConsumption.setPromptText(bundle.getString("consumption.prompt"));
        txtPrice.setPromptText(bundle.getString("price.prompt"));
        
        lblResult.setText("");
        lblResult.setStyle("-fx-text-fill: #1976D2; -fx-font-size: 16px; -fx-font-weight: bold;");
    }

    @FXML
    public void handleCalculate() {
        try {
            double distance = Double.parseDouble(txtDistance.getText().trim());
            double consumption = Double.parseDouble(txtConsumption.getText().trim());
            double price = Double.parseDouble(txtPrice.getText().trim());

            if (distance < 0 || consumption < 0 || price < 0) {
                showError(bundle.getString("invalid.input"));
                return;
            }

            double totalFuel = (consumption / 100) * distance;
            double totalCost = totalFuel * price;

            String resultPattern = bundle.getString("result.label");
            String result = MessageFormat.format(resultPattern, 
                    String.format(currentLocale, "%.2f", totalFuel),
                    String.format(currentLocale, "%.2f", totalCost));

            lblResult.setText(result);
            lblResult.setStyle("-fx-text-fill: #1976D2; -fx-font-size: 16px; -fx-font-weight: bold;");

        } catch (NumberFormatException e) {
            showError(bundle.getString("invalid.input"));
        }
    }

    private void showError(String message) {
        lblResult.setText(message);
        lblResult.setStyle("-fx-text-fill: #f44336; -fx-font-size: 16px; -fx-font-weight: bold;");
    }
}
