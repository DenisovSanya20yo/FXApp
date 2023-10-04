import javafx.scene.control.TreeItem;

import java.io.Serializable;

public class NodeTablePair implements Serializable
{
    private TreeItem<DataBase> node;
    private Table table;

    public NodeTablePair(TreeItem<DataBase> node, Table table) {
        this.node = node;
        this.table = table;
    }

    public TreeItem<DataBase> getNode() {
        return node;
    }

    public Table getTable() {
        return table;
    }
}
