/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package storage;

import domainObjects.Item;
import domainObjects.translator.ItemTranslator;
import java.util.Vector;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import storage.Exception.NotExpectedException;
import storage.filter.ItemByCategoryFilter;
import storage.filter.ItemByIdFilter;
import storage.filter.KeyFilter;
import storage.recordKey.KeyTranslator;
import storage.recordKey.RecordKey;

public class RecStoreRepository implements IRepository
{    
    private static final String ITEM_RS_NAME = "PenPal_Item";
    private static final String KEY_RS_NAME = "PenPal_ItemKey";
    private static final String OWN_ITEM_RS_NAME = "PenPal_OwnItem";

    private RecordKey getItemRecordKey(int id){
        RecordStore rs = null;
        byte[] itemByteArr = null;
        try{
            rs = RecordStore.openRecordStore(KEY_RS_NAME, true);
            RecordEnumeration idRe = rs.enumerateRecords(
                    new KeyFilter(String.valueOf(id))
                    , null
                    , true);
            if(idRe.numRecords() > 0)
                itemByteArr = idRe.nextRecord();
        }catch(RecordStoreException ex){
            throw new NotExpectedException("GetItemRecordKey: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
                throw new NotExpectedException("GetItemRecordKey: " + ex.getMessage());
            }
        }
        if(itemByteArr != null)
            return KeyTranslator.byte2RecordKey(itemByteArr);
        return null;
    }
    private void addKey(RecordKey key){
        RecordStore rs = null;
        byte[] keyByteArr = KeyTranslator.RecordKey2byte(key);
        try{
            rs = RecordStore.openRecordStore(KEY_RS_NAME, true);
            rs.addRecord(keyByteArr, 0, keyByteArr.length);
        }catch(RecordStoreException ex){
            throw new NotExpectedException("addKey: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
               throw new NotExpectedException("addKey: " + ex.getMessage());
            }
        }
    }
    private RecordKey deleteKey(int idItem){
        RecordStore rs = null;
        byte[] keyByteArr = null;
        try{
            rs = RecordStore.openRecordStore(KEY_RS_NAME, true);
            RecordEnumeration re = rs.enumerateRecords(
                    new KeyFilter(String.valueOf(idItem)),
                    null,
                    true);
            if(!re.hasNextElement())
                return null;
            int recId = re.nextRecordId();
            re.reset();
            keyByteArr = rs.getRecord(recId);
            rs.deleteRecord(recId);
        }catch(RecordStoreException ex){
            throw new NotExpectedException("deleteKey: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
                throw new NotExpectedException("deleteKey: " + ex.getMessage());
            }
        }
        if(keyByteArr != null)
            return KeyTranslator.byte2RecordKey(keyByteArr);
        return null;
    }

    //Public Items
    public Item getItemByid(int id) {
        RecordStore rs = null;
        byte[] itemByteArr = null;

        RecordKey key = getItemRecordKey(id);
        if(key == null)
            return null;
        int recPos = Integer.parseInt(key.getIdRecord());
        try{
            rs = RecordStore.openRecordStore(ITEM_RS_NAME, true);
            itemByteArr = rs.getRecord(recPos);
        }catch(RecordStoreException ex){
            throw new NotExpectedException("GetItemById - Record Store: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
                throw new NotExpectedException("GetItemById - Record Store: " + ex.getMessage());
            }
        }
        if(itemByteArr != null)
            return ItemTranslator.byte2Item(itemByteArr);
        return null;
    }
    public Vector getItemsByCat(int cat) {
        RecordStore rs = null;
        Vector v = new Vector();
        try{
            rs = RecordStore.openRecordStore(ITEM_RS_NAME, true);
            RecordEnumeration re = rs.enumerateRecords(
                    new ItemByCategoryFilter(cat),
                    null,
                    false);
            while(re.hasNextElement()){
                v.addElement(ItemTranslator.byte2Item(re.nextRecord()));
            }
        }catch(RecordStoreException ex){
            throw new NotExpectedException("GetItemsByCat - Record Store: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
                throw new NotExpectedException("GetItemsByCat - Record Store: " + ex.getMessage());
            }
        }
        return v;
    }    
    public void addItem(Item i) {
        RecordStore rs = null;
        int idRecord;
        RecordKey key = getItemRecordKey(i.getId());
        byte[] itemByteArr = ItemTranslator.item2Byte(i);
        try{
            rs = RecordStore.openRecordStore(ITEM_RS_NAME, true);
            if(key == null){
                idRecord = rs.addRecord(
                        itemByteArr,
                        0,
                        itemByteArr.length);
                key = new RecordKey(String.valueOf(i.getId()), String.valueOf(idRecord));
                addKey(key);
            }else{
                idRecord = Integer.parseInt(key.getIdRecord());
                rs.setRecord(idRecord, itemByteArr, 0, itemByteArr.length);
            }            
        }catch(RecordStoreException ex){
            throw new NotExpectedException("AddItem - RecordStore: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
                throw new NotExpectedException("deleteKey: " + ex.getMessage());
            }
        }
    }    
    public void deleteItem(Item i) {        
        RecordStore rs = null;
        RecordKey key = deleteKey(i.getId());
        if(key == null)
            return;
        try{
            rs = RecordStore.openRecordStore(ITEM_RS_NAME, true);
            rs.deleteRecord(Integer.parseInt(key.getIdRecord()));
        }catch(RecordStoreException ex){
            throw new NotExpectedException("DeleteItem - RecordStore: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
                throw new NotExpectedException("deleteKey: " + ex.getMessage());
            }
        }
    }    

    //Own Items
    public Vector getOwnItems() {
        RecordStore rs = null;
        Vector v = new Vector();
        try{
            rs = RecordStore.openRecordStore(OWN_ITEM_RS_NAME, true);
            RecordEnumeration re = rs.enumerateRecords(null, null, true);            
            while(re.hasNextElement())
                v.addElement(ItemTranslator.byte2Item(re.nextRecord()));
        }catch(RecordStoreException ex){
            throw new NotExpectedException("GetOwnItem - RecordStore: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
                throw new NotExpectedException("GetOwnItem - RecordStore: " + ex.getMessage());
            }
        }
        return v;
    }
    public void addOwnItem(Item i) {
        RecordStore rs = null;
        byte[] itemByteArr = ItemTranslator.item2Byte(i);
        try{
            rs = RecordStore.openRecordStore(OWN_ITEM_RS_NAME, true);
            RecordEnumeration re = rs.enumerateRecords(
                    new ItemByIdFilter(i.getId())
                    , null
                    , true);            
            if(re.hasNextElement()){
                int idRecord = re.nextRecordId();
                rs.setRecord(idRecord, itemByteArr, 0, itemByteArr.length);
            }else
                rs.addRecord(itemByteArr, 0, itemByteArr.length);
        }catch(RecordStoreException ex){
            throw new NotExpectedException("AddOwnItem - RecordStore: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
                throw new NotExpectedException("addOwnItem: " + ex.getMessage());
            }
        }
    }
    public void deleteOwnItem(Item i) {
        RecordStore rs = null;
        int idRecord;
        try{
            rs = RecordStore.openRecordStore(OWN_ITEM_RS_NAME, true);
            RecordEnumeration re = rs.enumerateRecords(
                    new ItemByIdFilter(i.getId())
                    , null
                    , true);
            if(re.hasNextElement()){
                idRecord = re.nextRecordId();
                rs.deleteRecord(idRecord);
            }
        }catch(RecordStoreException ex){
            throw new NotExpectedException("DeleteOwnItem - RecordStore: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
                throw new NotExpectedException("addOwnItem: " + ex.getMessage());
            }
        }
    }

    //Just for test purpose
    public int getItemsCount(){
        RecordStore rs = null;
        int count = -1;
        try{
            rs = RecordStore.openRecordStore(ITEM_RS_NAME, true);
            count =  rs.getNumRecords();
        }catch(RecordStoreException ex){
            throw new NotExpectedException("GetItemsCount - RecordStore: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
                throw new NotExpectedException("GetItemsCount: " + ex.getMessage());
            }
        }
        return count;
    }
    public int getOwnItemsCount(){
        RecordStore rs = null;
        int count = -1;
        try{
            rs = RecordStore.openRecordStore(OWN_ITEM_RS_NAME, true);
            count =  rs.getNumRecords();
        }catch(RecordStoreException ex){
            throw new NotExpectedException("GetOwnItemsCount - RecordStore: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
                throw new NotExpectedException("GetOwnItemsCount: " + ex.getMessage());
            }
        }
        return count;
    }
    public static void clearRecStores(){
        try{
            String[] recStoreList = RecordStore.listRecordStores();
            for(int i = 0; i < recStoreList.length; i++){
                if(recStoreList[i].equals(KEY_RS_NAME))
                    RecordStore.deleteRecordStore(KEY_RS_NAME);
                if(recStoreList[i].equals(ITEM_RS_NAME))
                    RecordStore.deleteRecordStore(ITEM_RS_NAME);
                if(recStoreList[i].equals(OWN_ITEM_RS_NAME))
                    RecordStore.deleteRecordStore(OWN_ITEM_RS_NAME);
            }
        }catch(RecordStoreException ex){
            throw new NotExpectedException("Clearing RecordStores: " + ex.getMessage());
        }
    }

}