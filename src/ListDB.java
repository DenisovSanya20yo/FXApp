import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class ListDB implements Serializable
{
    private List<DataBase> dataBases = new ArrayList<>();

    public void addDB (DataBase dataBase)
    {
        dataBases.add(dataBase);
    }
    public void removeDB (DataBase dataBase)
    {
        dataBases.remove(dataBase);
    }

    public List<DataBase> getDataBases()
    {
        return this.dataBases;
    }
}
