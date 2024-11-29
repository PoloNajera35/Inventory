package upa.inventario;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewClientFormController implements Initializable {
    @FXML
    private Button clientes_Post;
    @FXML
    private TextField direccionClientes;
    @FXML
    private TextField emailCliente;
    @FXML
    private TextField enfermedadesClientes;
    @FXML
    private DatePicker fechaNacimientoClientes;
    @FXML
    private TextField nombreCliente;
    @FXML
    private TextField suscripCliente;
    @FXML
    private TextField telefonoCliente;
    @FXML
    private ComboBox<String> medicinasBox;

    private Map<String, Integer> productMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populateProductDropdown();
        medicinasBox.setOnAction(event -> {
            String selectedProductName = medicinasBox.getValue();
            Integer selectedProductId = productMap.get(selectedProductName);
            System.out.println("Selected Product ID: " + selectedProductId);
        });
    }

    private void populateProductDropdown() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://127.0.0.1:8000/products/Product/");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            InputStream inputStream = (responseCode >= 200 && responseCode < 300)
                                        ? connection.getInputStream()
                                        : connection.getErrorStream();

            StringBuilder response = new StringBuilder();
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }
            }

            String jsonResponse = response.toString();
            JSONArray jsonArray = new JSONArray(jsonResponse);

            ObservableList<String> productList = FXCollections.observableArrayList();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                int productId = obj.getInt("id");
                String productName = obj.getString("name");

                productList.add(productName);
                productMap.put(productName, productId);
            }

            medicinasBox.setItems(productList);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @FXML
    void PostClientServer(ActionEvent event) {
        StringBuilder errorMessages = new StringBuilder();

        // Input validations
        String valueNombre = nombreCliente.getText();
        if (valueNombre.isEmpty()) {
            errorMessages.append("El nombre no puede estar vacío.\n");
        }

        Long valueTelefono = null;
        try {
            valueTelefono = Long.parseLong(telefonoCliente.getText());
        } catch (NumberFormatException e) {
            errorMessages.append("El teléfono debe ser un número válido.\n");
        }

        Integer valueEnfermedades = null;
        try {
            valueEnfermedades = Integer.parseInt(enfermedadesClientes.getText());
        } catch (NumberFormatException e) {
            errorMessages.append("Las enfermedades deben ser un número válido.\n");
        }

        Integer valueMedicinas = null;
        if (medicinasBox.getValue() == null) {
            errorMessages.append("Seleccione una medicina.\n");
        } else {
            valueMedicinas = productMap.get(medicinasBox.getValue());
        }

        LocalDate valueFechaNacimiento = fechaNacimientoClientes.getValue();
        if (valueFechaNacimiento == null) {
            errorMessages.append("La fecha de nacimiento no puede estar vacía.\n");
        }

        String valueEmail = emailCliente.getText();
        if (valueEmail.isEmpty() || !valueEmail.contains("@")) {
            errorMessages.append("Ingrese un correo electrónico válido.\n");
        }

        String valueSus = suscripCliente.getText();
        if (valueSus.isEmpty()) {
            errorMessages.append("La suscripción no puede estar vacía.\n");
        }

        String valueDireccion = direccionClientes.getText();
        if (valueDireccion.isEmpty()) {
            errorMessages.append("La dirección no puede estar vacía.\n");
        }

        // If there are any errors, display them in an Alert and stop the POST request
        if (errorMessages.length() > 0) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Se encontraron errores en el formulario");
            alert.setContentText(errorMessages.toString());
            alert.showAndWait();
            return; // Stop the method here to prevent the POST request
        }

        // If no errors, proceed with the POST request
        HttpURLConnection connection = null;

        try {
            // Create connection
            URL url = new URL("http://127.0.0.1:8000/clients/Client/");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            // Prepare JSON data
            String jsonInputString = String.format(
                "{\"name\": \"%s\", \"phone\": \"%d\", \"birthdate\": \"%s\", \"email\": \"%s\", \"suscription\": \"%s\", \"address\": \"%s\", \"diseases\": \"%d\", \"medicines\": \"%d\"}",
                valueNombre, valueTelefono, valueFechaNacimiento, valueEmail, valueSus, valueDireccion, valueEnfermedades, valueMedicinas
            );

            // Send request
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(jsonInputString);
                wr.flush();
            }

            // Get response
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            InputStream inputStream;
            if (responseCode >= 200 && responseCode < 300) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            try (BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }
                System.out.println("Response: " + response.toString());
            }
        } catch (IOException e) {
            errorMessages.append("Ocurrió un error al procesar la solicitud.\n");
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error en el procesamiento");
            alert.setContentText(errorMessages.toString());
            alert.showAndWait();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        // If no error occurred, close the window
        if (errorMessages.length() == 0) {
            Stage currentStage = (Stage) clientes_Post.getScene().getWindow();
            currentStage.close();
        }
    }
}
