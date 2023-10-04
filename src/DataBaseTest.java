import org.junit.Test;
import static org.junit.Assert.*;

public class DataBaseTest {

    @Test
    public void testGetNameDB() {
        String dbName = "TestDB";
        DataBase db = new DataBase(dbName);
        assertEquals(dbName, db.getNameDB());
    }

    @Test
    public void testToString() {
        String dbName = "TestDB";
        DataBase db = new DataBase(dbName);
        assertEquals(dbName, db.toString());
    }
}