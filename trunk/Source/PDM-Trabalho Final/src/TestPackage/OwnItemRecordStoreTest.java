package TestPackage;

import domainObjects.Item;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import jmunit.framework.cldc10.Assertion;
import jmunit.framework.cldc10.TestCase;
import storage.IRepository;
import storage.RecStoreRepository;

public class OwnItemRecordStoreTest extends TestCase {

    public OwnItemRecordStoreTest(){
        //The first parameter of inherited constructor is the number of test cases
        super(5,"ItemRecordStoreTest");
    }
    public void test(int i) throws Throwable {
        switch(i){
            case 0:
                canAddItem();
                break;
            case 1:
                canGetAllOwnItems();
                break;
            case 2:
                canDeleteOwnItem();
                break;
            case 3:
                canInsertTwicetheSameOwnItem();
                break;
            case 4:
                canUpdateOwnItem();
                break;
            default:
                break;
        }
    }

    //0
    private void canAddItem(){
        IRepository rep = new RecStoreRepository();
        Item i = new Item(1, 1, "tittle", "description", null, new Date());
        try{
            rep.addOwnItem(i);
            assertTrue(true);
        }catch(Exception ex){
            Assertion.fail("Unable to add own item");
        }
    }
    //1
    private void canGetAllOwnItems(){
        Item i1 = new Item(1, 1, "tittle1", "description1", null, new Date());
        Item i2 = new Item(2, 1, "tittle2", "description2", null, new Date());
        Item i3 = new Item(3, 2, "tittle3", "description3", null, new Date());
        Item i4 = new Item(4, 2, "tittle4", "description4", null, new Date());
        Item i5 = new Item(5, 2, "tittle5", "description5", null, new Date());
        Item i6 = new Item(6, 2, "tittle6", "description6", null, new Date());
        Item i7 = new Item(7, 3, "tittle7", "description7", null, new Date());
        Item i8 = new Item(8, 1, "tittle8", "description8", null, new Date());

        IRepository rep = new RecStoreRepository();
        rep.addOwnItem(i1);
        rep.addOwnItem(i2);
        rep.addOwnItem(i3);
        rep.addOwnItem(i4);
        rep.addOwnItem(i5);
        rep.addOwnItem(i6);
        rep.addOwnItem(i7);
        rep.addOwnItem(i8);

        Vector v = rep.getOwnItems();
        assertEquals(v.size(), 8);
    }
    //2
    private void canDeleteOwnItem(){
        IRepository rep = new RecStoreRepository();
        int count = rep.getOwnItemsCount();

        Item i3 = new Item(3, 2, "tittle3", "description3", null, new Date());
        rep.deleteOwnItem(i3);

        assertTrue(rep.getOwnItemsCount() == --count);
    }
    //3
    private void canInsertTwicetheSameOwnItem(){
        IRepository rep = new RecStoreRepository();
        Item i9 = new Item(9, 1, "tittle9", "description9", null, new Date());

        int count = rep.getOwnItemsCount();

        rep.addOwnItem(i9);
        assertTrue(rep.getOwnItemsCount() == ++count);

        rep.addOwnItem(i9);
        assertFalse(rep.getOwnItemsCount() == ++count);
    }
    //4
    private void canUpdateOwnItem(){
        IRepository rep = new RecStoreRepository();
        Item i10 = new Item(10, 1, "tittle10", "description10", null, new Date());

        rep.addOwnItem(i10);

        i10.setDesc("description10_new");

        rep.addOwnItem(i10);

        Vector v = rep.getOwnItems();
        Enumeration en = v.elements();
        while(en.hasMoreElements()){
            Item i = (Item)en.nextElement();
            if(i.getId() == 10)
                assertTrue(i.getDesc().equals("description10_new"));
        }
    }
}
