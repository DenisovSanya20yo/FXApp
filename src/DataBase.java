import java.io.Serializable;

public class DataBase implements Serializable
{
    private String NameDB;

    public DataBase (String NameDB)
    {
        this.NameDB = NameDB;
    }

    public String getNameDB ()
    {
        return this.NameDB;
    }
    @Override
    public String toString() {
        return getNameDB();
    }
}
