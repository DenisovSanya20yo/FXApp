import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ListDBTest {

    private ListDB listDB;
    private DataBase db1;
    private DataBase db2;

    @Before
    public void setUp() {
        listDB = new ListDB();
        db1 = new DataBase("Database1");
        db2 = new DataBase("Database2");
    }

    @Test
    public void testAddDB() {
        listDB.addDB(db1);
        assertTrue(listDB.getDataBases().contains(db1));
    }

    @Test
    public void testRemoveDB() {
        listDB.addDB(db1);
        listDB.addDB(db2);

        listDB.removeDB(db1);
        assertFalse(listDB.getDataBases().contains(db1));
        assertTrue(listDB.getDataBases().contains(db2));
    }

    @Test
    public void testGetDataBases() {
        listDB.addDB(db1);
        listDB.addDB(db2);

        assertEquals(2, listDB.getDataBases().size());
        assertTrue(listDB.getDataBases().contains(db1));
        assertTrue(listDB.getDataBases().contains(db2));
    }
}