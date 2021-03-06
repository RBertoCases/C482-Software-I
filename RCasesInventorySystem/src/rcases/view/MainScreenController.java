package rcases.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import rcases.InvMgmt;
import rcases.model.InhousePart;
import rcases.model.Inventory;
import static rcases.model.Inventory.deletePart;
import rcases.model.Part;
import static rcases.model.Inventory.getAllParts;
import static rcases.model.Inventory.getPartIDCount;
import static rcases.model.Inventory.getProducts;
import static rcases.model.Inventory.lookupPart;
import rcases.model.OutsourcedPart;
import rcases.model.Product;
import static rcases.model.Inventory.removeProduct;

public class MainScreenController {

    @FXML 
    private TextField searchPartsField;
    @FXML 
    private TextField searchProductsField;       
    @FXML
    private TableView<Part> partsTableView;  
    @FXML
    private TableColumn<Part, Integer> partsIDColumn;  
    @FXML
    private TableColumn<Part, String> partsNameColumn;  
    @FXML
    private TableColumn<Part, Integer> partsInStockColumn;  
    @FXML
    private TableColumn<Part, Double> partsPriceColumn;
    @FXML
    private TableView<Product> productsTableView;  
    @FXML
    private TableColumn<Product, Integer> productsIDColumn;  
    @FXML
    private TableColumn<Product, String> productsNameColumn;  
    @FXML
    private TableColumn<Product, Integer> productsInStockColumn;  
    @FXML
    private TableColumn<Product, Double> productsPriceColumn;
    @FXML
    public ObservableList<Part> tempPart=FXCollections.observableArrayList();
    public ObservableList<Product> tempProduct=FXCollections.observableArrayList();
    private Part newPart;
    private Product newProduct;
    private InvMgmt invMgmt;
    private static int index;

    public MainScreenController() {
        
    }
    
    //method to be able to grab selected part or product index from other controllers
    public static int modifyIndex() {
        return index;
    }
           
    @FXML
    void exitHandler(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Confirm Exit");
        alert.setHeaderText("Please confirm you want to exit.");
        alert.setContentText("Any unsaved changes will be lost.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            System.exit(0);
        }
        else {
            System.out.println("You clicked cancel.");
        }
    }

    @FXML
    void partsAddHandler(ActionEvent event) throws IOException{
        boolean okClicked = invMgmt.showPartScreen();
    }

    

    @FXML
    void partsDeleteHandler(ActionEvent event) {
        Part part = partsTableView.getSelectionModel().getSelectedItem();
        if (part != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete " + part.getName() + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                deletePart(part);
            } else {
                alert.close();
            }
        }
    }


    @FXML
    void partsModifyHandler(ActionEvent event) {
        Part selectedPart = partsTableView.getSelectionModel().getSelectedItem();
        index = getAllParts().indexOf(selectedPart);
        if (selectedPart != null) {
            boolean saveClicked = invMgmt.showModifyPartScreen(selectedPart);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No part selected");
            alert.setContentText("Please select a part in the Table");
            alert.showAndWait();
        }
    }
    
    @FXML
    void partsSearchHandler(ActionEvent event) {
        String searchItem=searchPartsField.getText();
        if (searchItem.equals("")){
                partsTableView.setItems(getAllParts());
        } else{
            boolean found=false;
            try{
                int itemNumber=Integer.parseInt(searchItem);
                Part p = Inventory.lookupPart(itemNumber);
                if(p != null){
                    found=true;
                    tempPart.clear();
                    tempPart.add(p);
                    partsTableView.setItems(tempPart);
                } 
                if (found==false){
                partsTableView.setItems(getAllParts());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Part Not Found");
                alert.setContentText("Please search by exact name or ID");

                alert.showAndWait();
            }
        }
        catch(NumberFormatException e){
            for(Part p: Inventory.getAllParts()){
                if(p.getName().equals(searchItem)){
                    System.out.println("This is part "+p.getPartID());
                    found=true;
                    tempPart.clear();
                    tempPart.add(p);
                    partsTableView.setItems(tempPart);
                }
            
            }
            if (found==false){
            partsTableView.setItems(getAllParts());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Part Not Found");
            alert.setContentText("Please search by exact name or ID #");
            
            alert.showAndWait();
            }
        }
        }
    
    }

    @FXML
    void productsAddHandler(ActionEvent event) {
        boolean okClicked = invMgmt.showProductScreen();
    }

    @FXML
    void productsDeleteHandler(ActionEvent event) {
        Product product = productsTableView.getSelectionModel().getSelectedItem();
        if (product != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion of Product");
            alert.setHeaderText("Are you sure you want to delete " + product.getName() + "?");
            alert.setContentText("Product has associated Parts");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                System.out.println("Product Deleted");
                removeProduct(product);
            } else {
                alert.close();
            }
        }
        
    }

    @FXML
    void productsModifyHandler(ActionEvent event) {
        Product selectedProduct = productsTableView.getSelectionModel().getSelectedItem();
        index = getProducts().indexOf(selectedProduct);
        if (selectedProduct != null) {
            boolean okClicked = invMgmt.showModifyProductScreen(selectedProduct);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No product selected");
            alert.setContentText("Please select a product in the Table");
            alert.showAndWait();
        }
    }

    @FXML
    void productsSearchHandler(ActionEvent event) {
        String searchItem=searchProductsField.getText();
        if (searchItem.equals("")){
                productsTableView.setItems(getProducts());
        } else {
            boolean found=false;
            try{
                int itemNumber=Integer.parseInt(searchItem);
                Product p = Inventory.lookupProduct(itemNumber);
                if(p != null){
                    found=true;
                    tempProduct.clear();
                    tempProduct.add(p);
                    productsTableView.setItems(tempProduct);
                }
                if (found==false){
                    productsTableView.setItems(getProducts());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Product not found");
                    alert.setContentText("Please search by exact Product Name or ID #");
                    alert.showAndWait();
                }
                }
            catch(NumberFormatException e){
                for(Product p: Inventory.getProducts()){
                    if(p.getName().equals(searchItem)){
                        found=true;
                        tempProduct.clear();
                        tempProduct.add(p);
                        productsTableView.setItems(tempProduct);
                    }
            
                }   
                if (found==false){
                    productsTableView.setItems(getProducts());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Product not found");
                    alert.setContentText("Please search by ProductID or exact Product Name");
                    alert.showAndWait();
                }
            }
        }   
    }
    
    //default data methods for program
    void existingParts() {
        int partID = Inventory.getPartIDCount();
        InhousePart camPart1 = new InhousePart(partID, "RC8021", 79.99, 42, 0, 999, 8025);
            Inventory.addPart(camPart1);
        OutsourcedPart camPart2 = new OutsourcedPart(getPartIDCount(), "RC8025", 149.99, 35, 0, 999, "ADT");
            Inventory.addPart(camPart2);
        OutsourcedPart camPart3 = new OutsourcedPart(getPartIDCount(), "OC835", 199.99, 26, 0, 999, "ADT");
            Inventory.addPart(camPart3);
    }
    
    void existingProducts() {
        int productID = Inventory.getProductIDCount();
        ArrayList<Part> sysParts1 = new ArrayList<>();
        sysParts1.add(lookupPart(1));
        Product sysProduct1 = new Product(productID, "SafeWatch", 299.99, 45, 1, 999, sysParts1);
        Inventory.addProduct(sysProduct1);
        ArrayList<Part> sysParts2 = new ArrayList<>();
        sysParts2.add(lookupPart(3));
        Product sysProduct2 = new Product(Inventory.getProductIDCount(), "SafeWatch Pro", 499.99, 20, 1, 999, sysParts2);
        Inventory.addProduct(sysProduct2);
                                                   
    }
        
    
    @FXML
    private void initialize() {
        partsIDColumn.setCellValueFactory(
                cellData -> cellData.getValue().partIDProperty().asObject());
        partsNameColumn.setCellValueFactory(
                cellData -> cellData.getValue().nameProperty());
        partsInStockColumn.setCellValueFactory(
                cellData -> cellData.getValue().inStockProperty().asObject());
        partsPriceColumn.setCellValueFactory(
                cellData -> cellData.getValue().priceProperty().asObject());
        productsIDColumn.setCellValueFactory(
                cellData -> cellData.getValue().productIDProperty().asObject());
        productsNameColumn.setCellValueFactory(
                cellData -> cellData.getValue().productNameProperty());
        productsInStockColumn.setCellValueFactory(
                cellData -> cellData.getValue().productInStockProperty().asObject());
        productsPriceColumn.setCellValueFactory(
                cellData -> cellData.getValue().productPriceProperty().asObject());
    }
    
    public void setMainApp(InvMgmt invMgmt) {
        this.invMgmt = invMgmt;
        existingParts();
        existingProducts();
        partsTableView.setItems(getAllParts());
        productsTableView.setItems(getProducts());
        
    }

}
