import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Table implements Serializable
{
    private String tableName;
    private TableView<List<String>> tableView = new TableView<>();

    public Table(String tableName) {
        this.tableName = tableName;

        // Создайте столбцы
        TableColumn<List<String>, String> indexColumn = new TableColumn<>("Indx");
        TableColumn<List<String>, String> nameColumn = new TableColumn<>("Name");
        TableColumn<List<String>, String> dataTypeColumn = new TableColumn<>("DataType");
        TableColumn<List<String>, String> pkFkColumn = new TableColumn<>("PK/FK");

        tableView.getColumns().addAll(indexColumn, nameColumn, dataTypeColumn, pkFkColumn);

        tableView.setEditable(true);

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // Добавьте обработчик события редактирования столбца "Name"
        nameColumn.setOnEditCommit(event -> {
            // Получите новое значение из события редактирования
            String newValue = event.getNewValue();


            int rowIndex = event.getTablePosition().getRow();


            List<String> rowData = tableView.getItems().get(rowIndex);
            rowData.set(1, newValue);


            tableView.refresh();
        });


        indexColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        indexColumn.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            int rowIndex = event.getTablePosition().getRow();
            List<String> rowData = tableView.getItems().get(rowIndex);
            rowData.set(0, newValue);
            tableView.refresh();
        });

        dataTypeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        dataTypeColumn.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            int rowIndex = event.getTablePosition().getRow();
            List<String> rowData = tableView.getItems().get(rowIndex);
            rowData.set(2, newValue);
            tableView.refresh();
        });


        nameColumn.setCellValueFactory(cellData -> {
            List<String> rowData = cellData.getValue();
            String nameValue = rowData.get(1);
            return new SimpleStringProperty(nameValue);
        });

        indexColumn.setCellValueFactory(cellData -> {
            List<String> rowData = cellData.getValue();
            String indexValue = rowData.get(0);
            return new SimpleStringProperty(indexValue);
        });

        dataTypeColumn.setCellValueFactory(cellData -> {
            List<String> rowData = cellData.getValue();
            String dataTypeValue = rowData.get(2);
            return new SimpleStringProperty(dataTypeValue);
        });

        pkFkColumn.setCellValueFactory(cellData -> {
            List<String> rowData = cellData.getValue();
            String pkFkValue = rowData.get(3);
            return new SimpleStringProperty(pkFkValue);
        });

        pkFkColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        pkFkColumn.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            int rowIndex = event.getTablePosition().getRow();
            List<String> rowData = tableView.getItems().get(rowIndex);
            rowData.set(3, newValue);
            tableView.refresh();
        });
    }

    public String getTableName() {
        return tableName;
    }

    public TableView<List<String>> getTableView() {
        return tableView;
    }

    public void addColumn(String columnName) {
        TableColumn<List<String>, String> newColumn = new TableColumn<>(columnName);

        tableView.getColumns().add(newColumn);
    }
}
