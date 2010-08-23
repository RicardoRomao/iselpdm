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
import storage.filter.KeyByItemIdFilter;
import storage.filter.KeyByOwnFilter;
import storage.recordKey.KeyTranslator;
import storage.recordKey.RecordKey;

public final class RecStoreRepository implements IRepository
{    
    private static final String ITEM_RS_NAME = "PenPal_Item";
    private static final String OWN_ITEM_RS_NAME = "PenPal_OwnItem";
    private static final String KEY_RS_NAME = "PenPal_ItemKey";
    private static final String IMAGE_RS_NAME = "PenPal_Image";

    //IRepository

    //Public Items
    public Item getItemByid(int id) {
        RecordKey key = getRecordKey(id);
        Item i = getItemRecord(key);
        i.setImage(getImageRecord(key));
        return i;
    }
    public Vector getItemsByCat(int cat) {
        RecordStore rs = null;
        Item item = null;
        int recId = 0;
        Vector v = new Vector();

        try{
            rs = RecordStore.openRecordStore(ITEM_RS_NAME, true);
            RecordEnumeration re = rs.enumerateRecords(
                    new ItemByCategoryFilter(cat),
                    null,
                    false);

            while(re.hasNextElement()){
                recId = re.nextRecordId();
                item = ItemTranslator.byte2Item(rs.getRecord(recId));//A ver melhor
                RecordKey key = getRecordKey(item.getId());
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
        saveItem(i, false);
    }
    public void deleteItem(Item i) {
        RecordKey key = deleteKey(i.getId());
        if(key == null)
            return;
        deleteImageRecord(key);
        deleteItemRecord(key);
    }

    //Own Items
    public Vector getOwnItems() {
        Enumeration keys = getAllOwnKeys().elements();
        return loadAllItems(keys);
    }
    public void addOwnItem(Item i) {
        saveItem(i, true);
    }
    public void deleteOwnItem(Item i) {
        deleteItem(i);
    }

    //IRepository End

    private void saveItem(Item item, boolean own){
        int idRecord;
        int idImage;
        RecordKey key = getRecordKey(item.getId());
        if(key == null){
            idRecord = addItemRecord(item, own);
            idImage = addImageRecord(item.getImage());
            key = new RecordKey(String.valueOf(item.getId()), String.valueOf(idRecord), String.valueOf(idImage), own);
            addKey(key);
        }else{
            updateImageRecord(item.getImage(), key);
            updateItemRecord(item, key);
        }
    }
    private Vector loadAllItems(Enumeration keys){
        RecordStore rsItem = null;
        RecordStore rsImage = null;
        RecordKey key = null;
        Item item = null;
        int recId;
        int imageId;
        Vector v = new Vector();
        try{
            rsItem = RecordStore.openRecordStore(OWN_ITEM_RS_NAME, true);
            rsImage = RecordStore.openRecordStore(IMAGE_RS_NAME, true);
            
            while(keys.hasMoreElements()){
                key = (RecordKey)keys.nextElement();
                recId = Integer.parseInt(key.getIdRecord());
                imageId = Integer.parseInt(key.getIdImage());
                item = ItemTranslator.byte2Item(rsItem.getRecord(recId));
                item.setImage(rsImage.getRecord(imageId));
                v.addElement(item);
            }
        }catch(RecordStoreException ex){
            throw new NotExpectedException("loadAll");
        }finally{
            try{
                rsItem.closeRecordStore();
                rsImage.closeRecordStore();
            }catch(RecordStoreException ex){
               throw new NotExpectedException("loadAll");
            }
        }
        return v;
    }
    private Vector getAllOwnKeys(){
        RecordStore rs = null;
        Vector v = new Vector();
        try{
            rs = RecordStore.openRecordStore(KEY_RS_NAME, true);
            RecordEnumeration re = rs.enumerateRecords(
                    new KeyByOwnFilter(true),
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

    //CRUD Key
    private RecordKey getRecordKey(int id){
        RecordStore rs = null;
        byte[] itemByteArr = null;
        try{
            rs = RecordStore.openRecordStore(KEY_RS_NAME, true);
            RecordEnumeration re = rs.enumerateRecords(
                    new KeyByItemIdFilter(String.valueOf(id))
                    , null
                    , true);
            if(re.hasNextElement())
                itemByteArr = re.nextRecord();
        }catch(RecordStoreException ex){
            throw new NotExpectedException("GetItemRecordKey: " + ex.getMessage());
        }finally{
            try{
                rs.closeRecordStore();
            }catch(RecordStoreException ex){
                throw new NotExpectedException("GetItemRecordKey: " + ex.getMessage());
            }
        }
        return (itemByteArr == null) ? null : KeyTranslator.byte2RecordKey(itemByteArr);
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
                    new KeyByItemIdFilter(String.valueOf(idItem)),
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

    //CRUD Image
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
    private int addImageRecord(byte[] imgByteArr){
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
    private void updateImageRecord(byte[] imgByteArr, RecordKey key){
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
    private void deleteImageRecord(RecordKey key){
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

    //CRUD Item Info
    private Item getItemRecord(RecordKey key){

        if(key == null)
            return null;

        RecordStore rs = null;
        byte[] itemByteArr = null;
        int recPos = Integer.parseInt(key.getIdRecord());

        try{
            rs = RecordStore.openRecordStore(key.getOwn() ? OWN_ITEM_RS_NAME : ITEM_RS_NAME, true);
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
        return (itemByteArr == null) ? null : ItemTranslator.byte2Item(itemByteArr);
    }
    private int addItemRecord(Item item, boolean own){
        RecordStore rs = null;
        int recordId;
        byte[] itemByteArr = ItemTranslator.item2Byte(item);
        try{
            rs = RecordStore.openRecordStore(own ? OWN_ITEM_RS_NAME : ITEM_RS_NAME, true);
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
            rs = RecordStore.openRecordStore(key.getOwn() ? OWN_ITEM_RS_NAME : ITEM_RS_NAME, true);
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
            rs = RecordStore.openRecordStore(key.getOwn() ? OWN_ITEM_RS_NAME : ITEM_RS_NAME, true);
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
            RecordEnumeration re = rs.enumerateRecords(
                    new KeyByOwnFilter(false),
                    null,
                    true);
            count =  re.numRecords();
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
                if(recStoreList[i].equals(OWN_ITEM_RS_NAME))
                    RecordStore.deleteRecordStore(OWN_ITEM_RS_NAME);
            }
        }catch(RecordStoreException ex){
            throw new NotExpectedException("Clearing RecordStores: " + ex.getMessage());
        }
    }
}