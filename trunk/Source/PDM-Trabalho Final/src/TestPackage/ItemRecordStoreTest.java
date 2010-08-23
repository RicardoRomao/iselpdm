/*
 * RecordStoreTest.java
 * JMUnit based test
 *
 * Created on 18/Ago/2010, 17:32:56
 */

package TestPackage;

import domainObjects.Item;
import java.util.Date;
import java.util.Vector;
import jmunit.framework.cldc10.*;
import storage.IRepository;
import storage.RecStoreRepository;

public class ItemRecordStoreTest extends TestCase {
    
    public ItemRecordStoreTest() {
        //The first parameter of inherited constructor is the number of test cases
        super(7,"ItemRecordStoreTest");
    }
    public void test(int testNumber) throws Throwable {
        switch(testNumber){
            case 0:
                isRecordStoreNotNull();
                break;
            case 1:
                canAddItem();
                break;
            case 2:
                canGetItem();
                break;
            case 3:
                canGetItemsByCat();
                break;
            case 4:
                canDeleteItem();
                break;
            case 5:
                canInsertTwicetheSameItem();
                break;
            case 6:
                canUpdateItem();
                break;
            default:
                break;
        }
    }

    //0
    private void isRecordStoreNotNull(){
        IRepository rep = new RecStoreRepository();
        assertNotNull(rep);
    }
    //1
    private void canAddItem(){
        IRepository rep = new RecStoreRepository();
        Item i = new Item(1, 1, "tittle", "description", null, new Date());
        rep.addItem(i);
        assertTrue(rep.getItemsCount() == 1);
    }
    //2
    private void canGetItem(){
        IRepository rep = new RecStoreRepository();
        Item i = new Item(1, 1, "tittle", "description", null, new Date());
        rep.addItem(i);

        Item newItem = rep.getItemByid(i.getId());

        assertEquals(i.getCategory(), newItem.getCategory());
    }
    //3
    private void canGetItemsByCat(){
        Item i1 = new Item(1, 1, "tittle1", "description1", null, new Date());
        Item i2 = new Item(2, 1, "tittle2", "description2", null, new Date());
        Item i3 = new Item(3, 2, "tittle3", "description3", null, new Date());
        Item i4 = new Item(4, 2, "tittle4", "description4", null, new Date());
        Item i5 = new Item(5, 2, "tittle5", "description5", null, new Date());
        Item i6 = new Item(6, 2, "tittle6", "description6", null, new Date());
        Item i7 = new Item(7, 3, "tittle7", "description7", null, new Date());
        Item i8 = new Item(8, 1, "tittle8", "description8", null, new Date());

        IRepository rep = new RecStoreRepository();
        rep.addItem(i1);
        rep.addItem(i2);
        rep.addItem(i3);
        rep.addItem(i4);
        rep.addItem(i5);
        rep.addItem(i6);
        rep.addItem(i7);
        rep.addItem(i8);

        Vector v = rep.getItemsByCat(2);
        assertEquals(v.size(), 4);

        v = rep.getItemsByCat(1);
        assertEquals(v.size(), 3);

        v = rep.getItemsByCat(3);
        assertEquals(v.size(), 1);
    }
    //4
    private void canDeleteItem(){
        IRepository rep = new RecStoreRepository();
        int count = rep.getItemsCount();
        
        Item i3 = new Item(3, 2, "tittle3", "description3", null, new Date());
        rep.deleteItem(i3);

        assertTrue(rep.getItemsCount() == --count);
    }
    //5
    private void canInsertTwicetheSameItem(){
        IRepository rep = new RecStoreRepository();
        Item i9 = new Item(9, 1, "tittle9", "description9", null, new Date());

        int count = rep.getItemsCount();

        rep.addItem(i9);
        assertTrue(rep.getItemsCount() == ++count);

        rep.addItem(i9);
        assertFalse(rep.getItemsCount() == ++count);
    }
    //6
    private void canUpdateItem(){
        IRepository rep = new RecStoreRepository();
        Item i10 = new Item(10, 1, "tittle10", "description10", null, new Date());

        rep.addItem(i10);

        i10.setDesc("description10_new");

        rep.addItem(i10);

        Item newItem = rep.getItemByid(10);

        assertTrue(newItem.getDesc().equals("description10_new"));
    }
}
