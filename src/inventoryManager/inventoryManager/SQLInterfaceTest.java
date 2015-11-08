package inventoryManager;

/**
 * Created by Jarrah on 8/11/2015.
 */
public class SQLInterfaceTest {

    @org.junit.Before
    public void setUp() throws Exception {

    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @org.junit.Test
    public void testDeleteEntry() throws Exception {

    }

    @org.junit.Test
    public void testAddEntry() throws Exception {

    }

    @org.junit.Test
    public void testAddEntry1() throws Exception {

    }

    @org.junit.Test
    public void testAddEntry2() throws Exception {

    }

    @org.junit.Test
    public void testAddEntry3() throws Exception {

    }

    @org.junit.Test
    public void testAddEntry4() throws Exception {

    }

    @org.junit.Test
    public void testAddLog() throws Exception {

    }

    @org.junit.Test
    public void testAddLog1() throws Exception {

    }

    @org.junit.Test
    public void testReturnItem() throws Exception {

    }

    @org.junit.Test
    public void testGetLog() throws Exception {

    }

    @org.junit.Test
    public void testGetLog1() throws Exception {

    }

    @org.junit.Test
    public void testGetLog2() throws Exception {

    }

    @org.junit.Test
    public void testGetOutItemsLog() throws Exception {

    }

    @org.junit.Test
    public void testGetDatabase() throws Exception {

    }

    @org.junit.Test
    public void testGetDatabase1() throws Exception {

    }

    @org.junit.Test
    public void testGetDatabase2() throws Exception {

    }

    @org.junit.Test
    public void testLowerQuantity() throws Exception {

    }

    @org.junit.Test
    public void testGetName() throws Exception {
        assert !SQLInterface.getName(SQLInterface.TABPERSON).isEmpty();
        assert !SQLInterface.getName(SQLInterface.TABITEM).isEmpty();
    }

    @org.junit.Test
    public void testGetName1() throws Exception {
        assert SQLInterface.getName(SQLInterface.TABITEM, "1").get().equals("razor");
        assert SQLInterface.getName(SQLInterface.TABPERSON, "7000000").get().equals("nameb");
        assert SQLInterface.getName(SQLInterface.TABPERSON, "123").get().equals("name");

    }

    @org.junit.Test
    public void testGetID() throws Exception {

        assert SQLInterface.getID(SQLInterface.TABPERSON, "nameb").get().equals("7000000");
        assert !SQLInterface.getID(SQLInterface.TABPERSON, "").isPresent();
        assert !SQLInterface.getID(SQLInterface.TABPERSON, null).isPresent();
        assert !SQLInterface.getID(SQLInterface.TABPERSON, "123").isPresent();

        assert SQLInterface.getID(SQLInterface.TABITEM, "razor").get().equals("1");
        assert !SQLInterface.getID(SQLInterface.TABITEM, "1").isPresent();
    }

    @org.junit.Test
    public void testGetPassword() throws Exception {
        String[] psswd = SQLInterface.getPassword("123");
        assert psswd[0].equals("�U��m��ڍ��5��<xHpD�e����K��ݫ�ƫ4*�r�4��U[lE���N~�");
        assert psswd[1].equals("[B@66713fde"); //TODO: Fix this to use the correct salt when salting is fixed.
        //See https://github.com/jarrah-95/inventoryManager/issues/4

        psswd = SQLInterface.getPassword("aseoun");
        assert psswd[0] == null;
        assert psswd[1] == null;


    }

    @org.junit.Test
    public void testSetPassword() throws Exception {

    }

    @org.junit.Test
    public void testGetRole() throws Exception {
        assert SQLInterface.getRole("7000000") == PersonDatabase.USER;
        assert SQLInterface.getRole("123") == PersonDatabase.USER;
        assert SQLInterface.getRole("1234") == PersonDatabase.ADMIN;
        assert SQLInterface.getRole("12345") == PersonDatabase.ROOT;
        assert SQLInterface.getRole("asonetuh") == PersonDatabase.USER;
        assert SQLInterface.getRole("2342342") == PersonDatabase.USER;
        assert SQLInterface.getRole("0") == PersonDatabase.USER;
    }

    @org.junit.Test
    public void testEntryExists() throws Exception {

    }

    @org.junit.Test
    public void testExport() throws Exception {

    }

    @org.junit.Test
    public void testGetQuantity() throws Exception {

    }

    @org.junit.Test
    public void testSetQuantity() throws Exception {

    }

    @org.junit.Test
    public void testUpdateEntry() throws Exception {

    }

    @org.junit.Test
    public void testIsItemControlled() throws Exception {

    }
}