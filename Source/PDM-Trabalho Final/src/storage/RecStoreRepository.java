package storage;

import domainObjects.Item;
import domainObjects.translator.ItemTranslator;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import storage.Exception.NotExpectedException;
import storage.filter.ItemByCategoryFilter;
import storage.filter.KeyFilter;
import storage.recordKey.KeyTranslator;
import storage.recordKey.RecordKey;

public class RecStoreRepository implements IRepository
{    
    private static final String ITEM_RS_NAME = "PenPal_Item";
    private static final String KEY_RS_NAME = "PenPal_ItemKey";
    private static final String IMAGE_RS_NAME = "PenPal_Image";

    //IRepository

    //Public Items
    public Item getItemByid(int id) {

        RecordKey key = getItemRecordKey(id, false);
        Item i = getItemRecord(key);
        i.setImage(getImageRecord(key));
        return i;

    }
    public Vector getItemsByCat(int cat) {

        RecordStore rs = null;
        Item item = null;
        int recId = 0;
        Vector v = new Vector();
        Vector ownKeys = getAllOwnKeys();
        Enumeration en = ownKeys.elements();
        Vector ownId = new Vector();
        while(en.hasMoreElements())
            ownId.addElement(((RecordKey)en.nextElement()).getIdItem());

        try{
            rs = RecordStore.openRecordStore(ITEM_RS_NAME, true);
            RecordEnumeration re = rs.enumerateRecords(
                    new ItemByCategoryFilter(cat, ownId),
                    null,
                    false);

            while(re.hasNextElement()){
                recId = re.nextRecordId();
                item = ItemTranslator.byte2Item(rs.getRecord(recId));
                RecordKey key = getItemRecordKey(item.getId(), false);
                item.setImage(getImageRecord(key));
                v.addElement(item);
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
        int idRecord;
        int idImage;
        RecordKey key = getItemRecordKey(i.getId(), false);
        if(key == null){
            idRecord = addItemRecord(i);
            idImage = saveImage(i.getImage());
            key = new RecordKey(String.valueOf(i.getId()), String.valueOf(idRecord), String.valueOf(idImage), false);
            addKey(key);
        }else{
            updateImage(i.getImage(), key);
            updateItemRecord(i, key);
        }
    }
    public void deleteItem(Item i) {
        RecordKey key = deleteKey(i.getId(), false);
        if(key == null)
            return;
        deleteImage(key);
        deleteItemRecord(key);
    }

    //Own Items
    public Vector getOwnItems() {

        Enumeration keys = getAllOwnKeys().elements();
        Item item = null;
        RecordKey key = null;
        Vector v = new Vector();

        while(keys.hasMoreElements()){
            key = (RecordKey)keys.nextElement();
            item = getItemRecord(key);
            item.setImage(getImageRecord(key));
            v.addElement(item);
        }

        return v;
    }
    public void addOwnItem(Item i) {
        int idRecord;
        int idImage;
        RecordKey key = getItemRecordKey(i.getId(), true);
        if(key == null){
            idRecord = addItemRecord(i);
            idImage = saveImage(i.getImage());
            key = new RecordKey(String.valueOf(i.getId()), String.valueOf(idRecord), String.valueOf(idImage), true);
            addKey(key);
        }else{
            updateImage(i.getImage(), key);
            updateItemRecord(i, key);
        }
    }
    public void deleteOwnItem(Item i) {
        RecordKey key = deleteKey(i.getId(), true);
        if(key == null)
            return;
        deleteImage(key);
        deleteItemRecord(key);
    }

    //IRepository End

    private RecordKey getItemRecordKey(int id, boolean own){
        RecordStore rs = null;
        byte[] itemByteArr = null;
        try{
            rs = RecordStore.openRecordStore(KEY_RS_NAME, true);
            RecordEnumeration idRe = rs.enumerateRecords(
                    new KeyFilter(String.valueOf(id), own)
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
    private RecordKey deleteKey(int idItem, boolean own){
        RecordStore rs = null;
        byte[] keyByteArr = null;
        try{
            rs = RecordStore.openRecordStore(KEY_RS_NAME, true);
            RecordEnumeration re = rs.enumerateRecords(
                    new KeyFilter(String.valueOf(idItem), own),
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
    private Vector getAllOwnKeys(){
        RecordStore rs = null;
        Vector v = new Vector();
        try{
            rs = RecordStore.openRecordStore(KEY_RS_NAME, true);
            RecordEnumeration re = rs.enumerateRecords(
                    new KeyFilter(null, true),
                    null,
                    true);
            while(re.hasNextElement())
                v.addElement(KeyTranslator.byte2RecordKey(re.nextRecord()));
        }catch(RecordStoreException ex){
            throw new NotExpectedException("getAllOwnKeys: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
               throw new NotExpectedException("getAllOwnKeys: " + ex.getMessage());
            }
        }
        return v;
    }

    private byte[] getImageRecord(RecordKey key){
        RecordStore rs = null;
        byte[] imageByteArr = null;
        try{
            rs = RecordStore.openRecordStore(IMAGE_RS_NAME, true);
            imageByteArr = rs.getRecord(Integer.parseInt(key.getIdImage()));
        }catch(RecordStoreException ex){
            throw new NotExpectedException("getImage: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
               throw new NotExpectedException("getImage: " + ex.getMessage());
            }
        }
        return imageByteArr;
    }
    private int saveImage(byte[] imgByteArr){
        RecordStore rs = null;
        int idImage;
        try{
            rs = RecordStore.openRecordStore(IMAGE_RS_NAME, true);
            idImage = rs.addRecord(imgByteArr, 0, imgByteArr.length);
        }catch(RecordStoreException ex){
            throw new NotExpectedException("saveImage: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
               throw new NotExpectedException("saveImage: " + ex.getMessage());
            }
        }
        return idImage;
    }
    private void updateImage(byte[] imgByteArr, RecordKey key){
        RecordStore rs = null;
        int idImage = Integer.parseInt(key.getIdImage());
        try{
            rs = RecordStore.openRecordStore(IMAGE_RS_NAME, true);
            rs.setRecord(idImage, imgByteArr, 0, imgByteArr.length);
        }catch(RecordStoreException ex){
            throw new NotExpectedException("saveImage: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
               throw new NotExpectedException("saveImage: " + ex.getMessage());
            }
        }
    }
    private void deleteImage(RecordKey key){
        RecordStore rs = null;
        int idRecord = Integer.parseInt(key.getIdImage());
        if(key == null)
            return;
        try{
            rs = RecordStore.openRecordStore(IMAGE_RS_NAME, true);
            rs.deleteRecord(idRecord);
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

    private Item getItemRecord(RecordKey key){

        if(key == null)
            return null;

        RecordStore rs = null;
        byte[] itemByteArr = null;
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
        if(itemByteArr == null)
            return null;
        return ItemTranslator.byte2Item(itemByteArr);
    }
    private int addItemRecord(Item item){
        RecordStore rs = null;
        int recordId;
        byte[] itemByteArr = ItemTranslator.item2Byte(item);
        try{
            rs = RecordStore.openRecordStore(ITEM_RS_NAME, true);
            recordId = rs.addRecord(itemByteArr, 0, itemByteArr.length);
        }catch(RecordStoreException ex){
            throw new NotExpectedException("addItemRecord: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
               throw new NotExpectedException("addItemRecord: " + ex.getMessage());
            }
        }
        return recordId;
    }
    private void updateItemRecord(Item item, RecordKey key){
        RecordStore rs = null;
        int recordId = Integer.parseInt(key.getIdRecord());
        byte[] itemByteArr = ItemTranslator.item2Byte(item);
        try{
            rs = RecordStore.openRecordStore(ITEM_RS_NAME, true);
            rs.setRecord(recordId, itemByteArr, 0, itemByteArr.length);
        }catch(RecordStoreException ex){
            throw new NotExpectedException("addItemRecord: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
               throw new NotExpectedException("addItemRecord: " + ex.getMessage());
            }
        }
    }
    private void deleteItemRecord(RecordKey key){
        RecordStore rs = null;
        int recordId = Integer.parseInt(key.getIdRecord());
        try{
            rs = RecordStore.openRecordStore(ITEM_RS_NAME, true);
            rs.deleteRecord(recordId);
        }catch(RecordStoreException ex){
            throw new NotExpectedException("addItemRecord: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
               throw new NotExpectedException("addItemRecord: " + ex.getMessage());
            }
        }
    }
   
    //Just for test purpose
    public int getItemsCount(){
        RecordStore rs = null;
        int count = -1;
        try{
            rs = RecordStore.openRecordStore(KEY_RS_NAME, true);
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
        Vector v = getAllOwnKeys();
        return v.size();
    }
    public static void clearRecStores(){
        try{
            String[] recStoreList = RecordStore.listRecordStores();
            for(int i = 0; i < recStoreList.length; i++){
                if(recStoreList[i].equals(KEY_RS_NAME))
                    RecordStore.deleteRecordStore(KEY_RS_NAME);
                if(recStoreList[i].equals(ITEM_RS_NAME))
                    RecordStore.deleteRecordStore(ITEM_RS_NAME);
            }
        }catch(RecordStoreException ex){
            throw new NotExpectedException("Clearing RecordStores: " + ex.getMessage());
        }
    }
}