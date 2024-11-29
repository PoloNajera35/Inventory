/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author carlo
 */
public class InventoryFormController implements Initializable {
 
      @FXML
    private Button btnPost;

    @FXML
    private TextField categoriaProducto;

    @FXML
    private TextField codigoBarraProducto;

    @FXML
    private TextField descripcionProducto;

    @FXML
    private TextField descuentoProducto;

    @FXML
    private TextField exiActual;

    @FXML
    private TextField exiMinima;

    @FXML
    private TextField expiracionProducto;

    @FXML
    private TextField fechaRe;

    @FXML
    private DatePicker fechaRestablecimiento;

    @FXML
    private TextField marcaProducto;

    @FXML
    private TextField nombreProductoPro;

    @FXML
    private TextField provedorProducto;

    @FXML
    private TextField ubicacioProducto;
    
    @FXML
    private DatePicker fechaExpiracion;

    /**
     * Initializes the controller class.
     */
    @Override
    
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }  
 @FXML
void PostServer(ActionEvent event) {
    
    
    String valueNombre = nombreProductoPro.getText();
    String valueBarCode = "123456789";  // Example value; replace with actual input
    String valueBrand = marcaProducto.getText();;  // Example value; replace with actual input
    LocalDate valueExpirationDate = fechaExpiracion.getValue();//Example value; replace with actual input
    String valueDescription = descripcionProducto.getText();  // Example value; replace with actual input
    String valueMinStockText = exiMinima.getText();  // Example value; replace with actual input
    String valueStockText = exiActual.getText();  // Example value; replace with actual input
    String valueProvider = provedorProducto.getText();  // Example value; replace with actual input
    String valueLocation = ubicacioProducto.getText();  // Example value; replace with actual input
    String valueDiscountText = descuentoProducto.getText();
    String category = categoriaProducto.getText();
    LocalDate dateResupply = fechaRestablecimiento.getValue();
    
    
    if (valueNombre.isEmpty() || valueBrand.isEmpty() || valueDescription.isEmpty() || 
        valueMinStockText.isEmpty() || valueStockText.isEmpty() || valueProvider.isEmpty() || 
        valueLocation.isEmpty() || valueDiscountText.isEmpty() || category.isEmpty() || 
        valueExpirationDate == null || dateResupply == null) {
        
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Entrada inválida");
        alert.setContentText("Todos los campos deben estar llenos.");
    
        // Mostrar la alerta
        alert.showAndWait();
        
        return;
    }

    int valueMinStock, valueStock, discount;
    try {
        valueMinStock = Integer.parseInt(valueMinStockText);
        valueStock = Integer.parseInt(valueStockText);
        discount = Integer.parseInt(valueDiscountText);
    } catch (NumberFormatException e) {
         Alert alert = new Alert(AlertType.ERROR);
         alert.setTitle("Error");
         alert.setContentText("Entrada inválida");
         alert.setContentText("Los campos de existencias y descuento deben ser números.");
    
    // Mostrar la alerta
    alert.showAndWait();
        return;
    }

    // Validar longitud y formato de fechas (ya están controladas por DatePicker)
    if (valueExpirationDate.isAfter(dateResupply)) {
         Alert alert = new Alert(AlertType.ERROR);
         alert.setTitle("Error");
         alert.setContentText("Entrada inválida");
         alert.setContentText("La fecha de expiración no puede ser posterior a la fecha de reabastecimiento.");
    
    // Mostrar la alerta
    alert.showAndWait();
        return;
    }
    
   System.out.println(String.format("Nombre: %s, Codigo de barras: %s, Marca de producto: %s, Fecha Expiracion: %s, Descripcion: %s, MinStock: %s, Stock: %s, Provedor: %s, Ubicacion: %s,  Descuento: %s,  Categoria: %s,  FechaRest: %s",
        valueNombre, valueBarCode, valueBrand, valueExpirationDate, valueDescription, valueMinStock, valueStock, valueProvider,valueLocation,discount,category,dateResupply));
   
        Stage stage = (Stage) btnPost.getScene().getWindow();
        stage.close();
    
    
    HttpURLConnection connection = null;
    try {
        // Create connection
        URL url = new URL("http://127.0.0.1:8000/products/Product/");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setUseCaches(false);
        connection.setDoOutput(true);

        // Prepare JSON data
        String jsonInputString = String.format(
            "{\"bar_code\": \"%s\", \"name\": \"%s\", \"brand\": \"%s\", \"expiration_date\": \"%s\", \"description\": \"%s\", \"min_stock\": %d, \"stock\": %d, \"provider\": \"%s\", \"location\": \"%s\", \"discount\": \"%d\", \"category\": \"%s\", \"dateResupply\": \"%s\"}",
            valueBarCode, valueNombre, valueBrand, valueExpirationDate, valueDescription, valueMinStock, valueStock, valueProvider, valueLocation, discount, category, dateResupply
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
            inputStream = connection.getErrorStream(); // For non-2xx responses
        }

        try (BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            System.out.println("Response: " + response.toString());
            
        }
    } catch (IOException e) {
    } finally {
        if (connection != null) {
            connection.disconnect();
        }
    }
}

}
