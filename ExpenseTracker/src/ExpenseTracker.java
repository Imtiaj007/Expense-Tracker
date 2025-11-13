import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ExpenseTracker extends Application {

    private final ObservableList<Expense> expenses = FXCollections.observableArrayList();
    private final Label totalLabel = new Label("Total: ৳0.0");
    private final String FILE_NAME = "expenses.txt";

    public void start(Stage stage) {
        // Input fields
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Food", "Transport", "Shopping", "Other");
        categoryBox.setValue("Food");

        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");

        Button addButton = new Button("Add");
        Button deleteButton = new Button("Delete Selected");

        // TableView
        TableView<Expense> table = new TableView<>(expenses);
        TableColumn<Expense, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(e -> new javafx.beans.property.SimpleStringProperty(e.getValue().getDate()));

        TableColumn<Expense, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(e -> new javafx.beans.property.SimpleStringProperty(e.getValue().getCategory()));

        TableColumn<Expense, String> amountCol = new TableColumn<>("Amount (৳)");
        amountCol.setCellValueFactory(e -> new javafx.beans.property.SimpleStringProperty(String.valueOf(e.getValue().getAmount())));

        table.getColumns().addAll(dateCol, categoryCol, amountCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        amountField.setStyle("-fx-background-color: white; -fx-border-color: #0288d1; -fx-border-radius: 5;");
        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #0097a7;" +
                        "-fx-border-radius: 5;" +
                        "-fx-font-size: 14px;"
        );
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #004d40;");
        categoryBox.setStyle("-fx-background-color: white; -fx-border-color: #0288d1; -fx-border-radius: 5;");

        // Load saved data
        loadExpenses();

        // Add button
        addButton.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                String category = categoryBox.getValue();
                String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yy"));

                Expense expense = new Expense(date, category, amount);
                expenses.add(expense);
                saveExpenses();
                updateTotal();

                amountField.clear();
            } catch (NumberFormatException ex) {
                showAlert("Invalid amount! Please enter a number.");
            }
        });
        // Delete button
        deleteButton.setOnAction(e -> {
            Expense selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                expenses.remove(selected);
                saveExpenses();
                updateTotal();
            } else {
                showAlert("Please select an expense to delete.");
            }
        });

        addButton.setStyle("-fx-background-color: #0288d1; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        deleteButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");

        // Layout
        HBox inputBox = new HBox(10, new Label("Category:"), categoryBox, new Label("Amount:"), amountField, addButton, deleteButton);
        inputBox.setPadding(new Insets(10));

        VBox root = new VBox(10, inputBox, table, totalLabel);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #e0f7fa, #80deea);");
        root.setPadding(new Insets(10));

        updateTotal();

        Scene scene = new Scene(root, 650, 450);
        stage.setTitle("💰 Simple Expense Tracker");
        stage.setScene(scene);
        stage.show();
    }

    private void updateTotal() {
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        totalLabel.setText("Total: ৳" + total);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }

    private void saveExpenses() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Expense exp : expenses) {
                writer.write(exp.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            showAlert("Error saving expenses!");
        }
    }

    private void loadExpenses() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    expenses.add(new Expense(parts[0], parts[1], Double.parseDouble(parts[2])));
                }
            }
            updateTotal();
        } catch (IOException e) {
            showAlert("Error loading expenses!");
        }
    }

    /*public void start(Stage stage) {

        amountField.setStyle("-fx-background-color: white; -fx-border-color: #0288d1; -fx-border-radius: 5;");
        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #0097a7;" +
                        "-fx-border-radius: 5;" +
                        "-fx-font-size: 14px;"
        );
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #004d40;");
        categoryBox.setStyle("-fx-background-color: white; -fx-border-color: #0288d1; -fx-border-radius: 5;");
    }
*/

    public static void main(String[] args) {
        launch(args);
    }
}