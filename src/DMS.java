import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DMS extends Application implements Serializable {
    private ListDB listDB = new ListDB();
    private TreeView<DataBase> treeView = new TreeView<>();
    private List<Table> tables = new ArrayList<>();
    private List<NodeTablePair> nodeTablePairs = new ArrayList<>(); // Збереження зв'язків вузлів та таблиць
    private VBox tablesVBox = new VBox();
    private BorderPane borderPane = new BorderPane();
    private Button addTableButton = new Button("Створити таблицю");
    private Button deleteTableButton = new Button("Видалити таблицю");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {

        saveData("data.ser"); // Збереження
        super.stop();
    }

    @Override
    public void start(Stage stage) {
        loadData("data.ser");

        stage.setTitle("Database management system");

        treeView.setShowRoot(true);

        Button addButton = new Button("Створити вузол");
        addButton.setOnAction(event -> addNode());

        Button deleteButton = new Button("Видалити вузол");
        deleteButton.setOnAction(event -> deleteNode());

        addTableButton.setOnAction(event -> addTable());
        deleteTableButton.setOnAction(event -> deleteTable());

        Button addRowButton = new Button("Додати запис");
        addRowButton.setOnAction(event -> addRow());
        Button deleteRowButton = new Button("Видалити запис");
        deleteRowButton.setOnAction(event -> deleteRow());

        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Перевірка чи є вибраний елемент вузлом а не таблицею
                if (!isTable(newValue)) {
                    addTableButton.setDisable(false); // Якщо вузол активуємо "Створити вузол"
                    deleteTableButton.setDisable(true);
                } else {
                    addTableButton.setDisable(true);
                    deleteTableButton.setDisable(false);
                }
            } else {
                addTableButton.setDisable(true);
                deleteTableButton.setDisable(true);
            }
        });

        VBox buttonsHBox = new VBox(addButton, deleteButton, addTableButton, deleteTableButton, addRowButton, deleteRowButton); // Добавляем кнопку "Удалить таблицу" в интерфейс
        buttonsHBox.setSpacing(10);

        VBox treeViewVBox = new VBox(treeView, buttonsHBox);
        treeViewVBox.setSpacing(10);

        VBox tablesBox = new VBox(tablesVBox);
        tablesBox.setSpacing(10);

        HBox contentHBox = new HBox(treeViewVBox, tablesBox);

        borderPane.setCenter(contentHBox);

        Scene scene = new Scene(borderPane, 800, 600);

        stage.setScene(scene);
        stage.show();

        TreeItem<DataBase> rootNodeItem = new TreeItem<>(new DataBase("Catalog"));
        treeView.setRoot(rootNodeItem);
        updateTreeView();
    }

    public void saveData(String filename) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename))) {
            outputStream.writeObject(listDB);
            System.out.println("Дані збережено " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadData(String filename) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename))) {
            listDB = (ListDB) inputStream.readObject();
            System.out.println("Дані завантажено " + filename);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addRow() {
        TreeItem<DataBase> selectedTableItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedTableItem != null && isTable(selectedTableItem)) {
            Table selectedTable = getTableFromNode(selectedTableItem);
            TableView<List<String>> tableView = selectedTable.getTableView();

            Dialog<List<String>> dialog = new Dialog<>();
            dialog.setTitle("Додати запис");
            dialog.setHeaderText(null);

            ButtonType addButton = new ButtonType("Додати", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

            TextField indxField = new TextField();
            indxField.setPromptText("Indx");
            TextField nameField = new TextField();
            nameField.setPromptText("Name");
            ComboBox<String> dataTypeComboBox = new ComboBox<>();
            dataTypeComboBox.setPromptText("DataType");
            dataTypeComboBox.getItems().addAll("String", "Integer", "Real", "Datetime", "Datetimelnl", "Char");
            TextField pkFkField = new TextField();
            pkFkField.setPromptText("PK/FK");

            // Добавьте текстовые поля и ComboBox в диалоговое окно
            GridPane grid = new GridPane();
            grid.add(new Label("Indx:"), 0, 0);
            grid.add(indxField, 1, 0);
            grid.add(new Label("Name:"), 0, 1);
            grid.add(nameField, 1, 1);
            grid.add(new Label("DataType:"), 0, 2);
            grid.add(dataTypeComboBox, 1, 2);
            grid.add(new Label("PK/FK:"), 0, 3);
            grid.add(pkFkField, 1, 3);
            dialog.getDialogPane().setContent(grid);

            Platform.runLater(() -> indxField.requestFocus());

            // Преобразуйте результат диалогового окна в объект List<String> и добавьте его в таблицу
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButton) {
                    String indx = indxField.getText();
                    String name = nameField.getText();
                    String dataType = dataTypeComboBox.getValue();
                    String pkFk = pkFkField.getText();

                    if (indx != null && !indx.isEmpty() && name != null && !name.isEmpty() && dataType != null && pkFk != null) {
                        List<String> rowData = new ArrayList<>();
                        rowData.add(indx);
                        rowData.add(name);
                        rowData.add(dataType);
                        rowData.add(pkFk);
                        return rowData;
                    }
                }
                return null;
            });

            Optional<List<String>> result = dialog.showAndWait();
            result.ifPresent(rowData -> {
                tableView.getItems().add(rowData);
            });
        }
    }

    public void deleteRow() {
        TreeItem<DataBase> selectedTableItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedTableItem != null && isTable(selectedTableItem)) {
            Table selectedTable = getTableFromNode(selectedTableItem);
            TableView<List<String>> tableView = selectedTable.getTableView();


            List<String> selectedRow = tableView.getSelectionModel().getSelectedItem();
            if (selectedRow != null) {
                tableView.getItems().remove(selectedRow);
            }
        }
    }

    public TableColumn<String, String> findTableColumnByName(TableView<String> tableView, String columnName) {
        for (TableColumn<String, ?> column : tableView.getColumns()) {
            if (column.getText().equals(columnName)) {
                return (TableColumn<String, String>) column;
            }
        }
        return null;
    }

    public void addNode() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Створення бд");
        dialog.setHeaderText(null);
        dialog.setContentText("Назва:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String nodeName = result.get();
            DataBase newNode = new DataBase(nodeName);
            listDB.addDB(newNode);

            TreeItem<DataBase> catalogItem = treeView.getRoot();
            TreeItem<DataBase> newNodeItem = new TreeItem<>(newNode);
            catalogItem.getChildren().add(newNodeItem);

            updateTreeView();
        }
    }

    public void deleteNode() {
        TreeItem<DataBase> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getParent() != null) {
            DataBase nodeToDelete = selectedItem.getValue();

            List<Table> tablesToRemove = new ArrayList<>();
            for (NodeTablePair pair : nodeTablePairs) {
                if (pair.getNode().getParent() == selectedItem) {
                    tablesToRemove.add(pair.getTable());
                }
            }
            tables.removeAll(tablesToRemove);

            listDB.removeDB(nodeToDelete);
            selectedItem.getParent().getChildren().remove(selectedItem);
            updateTreeView();
            updateTables();
        }
    }

    public void addTable ()
    {
        TreeItem<DataBase> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getParent() != null) {
            // Создайте таблицу только если выбран вложенный узел
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Створення таблиці");
            dialog.setHeaderText(null);
            dialog.setContentText("Назва:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String tableName = result.get();
                Table newTable = new Table(tableName); // Создайте пустую таблицу без записей
                tables.add(newTable);
                updateTables();

                TreeItem<DataBase> tableNode = new TreeItem<>(new DataBase(tableName));
                selectedItem.getChildren().add(tableNode);

                NodeTablePair pair = new NodeTablePair(tableNode, newTable);
                nodeTablePairs.add(pair);
                treeView.getSelectionModel().select(tableNode);
            }
        }
    }

    public void deleteTable() {
        TreeItem<DataBase> selectedTableItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedTableItem != null) {
            if (isTable(selectedTableItem)) {
                // Получаем связанный объект Table
                Table tableToRemove = getTableFromNode(selectedTableItem);

                // Удаляем таблицу из списка
                tables.remove(tableToRemove);

                // Удаляем узел из дерева
                TreeItem<DataBase> parentItem = selectedTableItem.getParent();
                parentItem.getChildren().remove(selectedTableItem);

                updateTables();
            }
        }
        addTableButton.setDisable(false);
    }

    public boolean isTable(TreeItem<DataBase> item) {
        return getTableFromNode(item) != null;
    }

    public Table getTableFromNode(TreeItem<DataBase> item) {
        // Пошук таблиця для зв'язаного вузла
        for (NodeTablePair pair : nodeTablePairs) {
            if (pair.getNode() == item) {
                return pair.getTable();
            }
        }
        return null;
    }

    public void updateTreeView() {
        TreeItem<DataBase> rootNode = new TreeItem<>(new DataBase("Catalog"));
        for (DataBase db : listDB.getDataBases()) {
            TreeItem<DataBase> nodeItem = new TreeItem<>(db);
            rootNode.getChildren().add(nodeItem);
        }
        treeView.setRoot(rootNode);
    }

    public void updateTables() {
        tablesVBox.getChildren().clear();
        for (Table table : tables) {
            tablesVBox.getChildren().add(table.getTableView());
        }
    }
}