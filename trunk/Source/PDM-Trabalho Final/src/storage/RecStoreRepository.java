/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package storage;

import domainObjects.Item;
import domainObjects.translator.Translator;
import java.util.Vector;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
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
    
    private final RecordStore _itemRecStore;
    private final RecordStore _recStoreKey;
    private final RecordStore _ownItemRecStore;

    public RecStoreRepository(){
        try{
            _itemRecStore = RecordStore.openRecordStore(ITEM_RS_NAME, true
                    , RecordStore.AUTHMODE_PRIVATE, true);
            _ownItemRecStore = RecordStore.openRecordStore(OWN_ITEM_RS_NAME, true
                    , RecordStore.AUTHMODE_PRIVATE, true);
            _recStoreKey = RecordStore.openRecordStore(KEY_RS_NAME, true
                    , RecordStore.AUTHMODE_PRIVATE, true);

        }catch(RecordStoreNotOpenException ex){
            throw new NotExpectedException("RecStoreRepository - Record Store Not Open: "
                    + ex.getMessage());
        }catch(RecordStoreException ex){
            throw new NotExpectedException("RecStoreRepository - Record Store: " + ex.getMessage());
        }
    }

    private RecordKey getItemRecordKey(int id){
        try{
            RecordEnumeration idRe = _recStoreKey.enumerateRecords(
                    new KeyFilter(String.valueOf(id))
                    , null
                    , true);
            if(idRe.numRecords() > 0)
                return KeyTranslator.byte2RecordKey(idRe.nextRecord());
            else
                return null;
        }catch(RecordStoreNotOpenException ex){
            throw new NotExpectedException("GetItemRecordKey - Record Store Not Open: "
                    + ex.getMessage());
        }catch(RecordStoreException ex){
            throw new NotExpectedException("GetItemRecordKey - Record Store: " + ex.getMessage());
        }
    }

    protected void finalize() throws Throwable{
        try{
            _itemRecStore.closeRecordStore();
            _ownItemRecStore.closeRecordStore();
            _recStoreKey.closeRecordStore();
        }catch(RecordStoreNotOpenException ex){
            throw new NotExpectedException("finalize - RecordStore Not Open: " + ex.getMessage());
        }catch(RecordStoreException ex){
            throw new NotExpectedException("finalize - RecordStore: " + ex.getMessage());
        }
    }

    //Public Items
    public Item getItemByid(int id) {
        try{
            RecordKey key = getItemRecordKey(id);
            if(key == null)
                return null;
            else{
                int recPos = Integer.parseInt(key.getIdRecord());
                return Translator.byte2Item(_itemRecStore.getRecord(recPos));
            }
        }catch(RecordStoreNotOpenException ex){
            throw new NotExpectedException("GetItemsById - Record Store Not Open: "
                    + ex.getMessage());
        }catch(RecordStoreException ex){
            throw new NotExpectedException("GetItemsById - Record Store: " + ex.getMessage());
        }
    }
    public Vector getItemsByCat(int cat) {
        try{
            RecordEnumeration re = _itemRecStore.enumerateRecords(
                    new ItemByCategoryFilter(cat),
                    null,
                    false);

            Vector v = new Vector();
            while(re.hasNextElement()){
                v.addElement(Translator.byte2Item(re.nextRecord()));
            }
            return v;

        }catch(RecordStoreNotOpenException ex){
            throw new NotExpectedException("GetItemsByCat - Record Store Not Open: "
                    + ex.getMessage());
        }catch(RecordStoreException ex){
            throw new NotExpectedException("GetItemsByCat - Record Store: " + ex.getMessage());
        }
    }
    public void addItem(Item i) {
        int idRecord;
        RecordKey key;
        byte[] itemByteArr;
        try{
            itemByteArr = Translator.item2Byte(i);
            key = getItemRecordKey(i.getId());
            
            if(key == null){
                idRecord = _itemRecStore.addRecord(
                        itemByteArr,
                        0,
                        itemByteArr.length);
                key = new RecordKey(String.valueOf(i.getId()), String.valueOf(idRecord));
                byte[] keyByteArr = KeyTranslator.RecordKey2byte(key);
                _recStoreKey.addRecord(keyByteArr, 0, keyByteArr.length);
            }else{
                idRecord = Integer.parseInt(key.getIdRecord());
                _itemRecStore.setRecord(idRecord, itemByteArr, 0, itemByteArr.length);
            }
        }catch(RecordStoreNotOpenException ex){
            throw new NotExpectedException("AddItem - RecordStore Not Open: " + ex.getMessage());
        }catch(RecordStoreException ex){
            throw new NotExpectedException("AddItem - RecordStore: " + ex.getMessage());
        }
    }
    public void deleteItem(Item i) {        
        try{
            RecordEnumeration re = _recStoreKey.enumerateRecords(
                    new KeyFilter(String.valueOf(i.getId()))
                    , null
                    , true);
            if(re.hasNextElement()){
                int recId = re.nextRecordId();
                re.reset();
                RecordKey key = KeyTranslator.byte2RecordKey(re.nextRecord());
                _itemRecStore.deleteRecord(Integer.parseInt(key.getIdRecord()));
                _recStoreKey.deleteRecord(recId);
            }
        }catch(RecordStoreNotOpenException ex){
            throw new NotExpectedException("DeleteItem - RecordStore Not Open: " + ex.getMessage());
        }catch(RecordStoreException ex){
            throw new NotExpectedException("DeleteItem - RecordStore: " + ex.getMessage());
        }
    }    

    //Own Items
    public Vector getOwnItems() {
        try{
            RecordEnumeration re = _ownItemRecStore.enumerateRecords(null, null, true);

            Vector v = new Vector();
            while(re.hasNextElement())
                v.addElement(Translator.byte2Item(re.nextRecord()));
            return v;
        }catch(RecordStoreNotOpenException ex){
            throw new NotExpectedException("GetOwnItem - RecordStore not Open: " + ex.getMessage());
        }catch(RecordStoreException ex){
            throw new NotExpectedException("GetOwnItem - RecordStore: " + ex.getMessage());
        }
    }
    public void addOwnItem(Item i) {
        try{
            RecordEnumeration re = _ownItemRecStore.enumerateRecords(
                    new ItemByIdFilter(i.getId())
                    , null
                    , true);
            byte[] itemByteArr = Translator.item2Byte(i);
            if(re.hasNextElement()){
                int idRecord = re.nextRecordId();
                _ownItemRecStore.setRecord(idRecord, itemByteArr, 0, itemByteArr.length);
            }else
                _ownItemRecStore.addRecord(itemByteArr, 0, itemByteArr.length);

        }catch(RecordStoreNotOpenException ex){
            throw new NotExpectedException("AddOwnItem - RecordStore Not Open: " + ex.getMessage());
        }catch(RecordStoreException ex){
            throw new NotExpectedException("AddOwnItem - RecordStore: " + ex.getMessage());
        }
    }
    public void deleteOwnItem(Item i) {
        try{
            RecordEnumeration re = _ownItemRecStore.enumerateRecords(
                    new ItemByIdFilter(i.getId())
                    , null
                    , true);
            if(re.hasNextElement()){
                int idRecord = re.nextRecordId();
                _ownItemRecStore.deleteRecord(idRecord);
            }

        }catch(RecordStoreNotOpenException ex){
            throw new NotExpectedException("DeleteOwnItem - RecordStore Not Open: " + ex.getMessage());
        }catch(RecordStoreException ex){
            throw new NotExpectedException("DeleteOwnItem - RecordStore: " + ex.getMessage());
        }
    }

    //Just for test purpose
    public int getItemsCount(){
        try{
            return _itemRecStore.getNumRecords();
        }catch(RecordStoreNotOpenException ex){
            throw new NotExpectedException("GetItemsCount - RecordStore Not Open: " + ex.getMessage());
        }catch(RecordStoreException ex){
            throw new NotExpectedException("GetItemsCount - RecordStore: " + ex.getMessage());
        }
    }
    public int getOwnItemsCount(){
        try{
            return _ownItemRecStore.getNumRecords();
        }catch(RecordStoreNotOpenException ex){
            throw new NotExpectedException("GetOwnItemsCount - RecordStore Not Open: " + ex.getMessage());
        }catch(RecordStoreException ex){
            throw new NotExpectedException("GetOwnItemsCount - RecordStore: " + ex.getMessage());
        }
    }
    public static void clearRecStores(){
        try{
            RecordStore.deleteRecordStore(KEY_RS_NAME);
            RecordStore.deleteRecordStore(ITEM_RS_NAME);
            RecordStore.deleteRecordStore(OWN_ITEM_RS_NAME);
        }catch(RecordStoreException ex){
            throw new NotExpectedException("Clearing RecordStores: " + ex.getMessage());
        }
    }
}