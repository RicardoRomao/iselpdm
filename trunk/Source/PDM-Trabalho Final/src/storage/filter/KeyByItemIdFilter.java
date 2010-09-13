package storage.filter;

import javax.microedition.rms.RecordFilter;
import storage.recordKey.KeyTranslator;
import storage.recordKey.RecordKey;

public class KeyByItemIdFilter implements RecordFilter {

    private final String _idItem;

    public KeyByItemIdFilter(String idItem){
        _idItem = idItem;
    }

    public boolean matches(byte[] candidate) {
        RecordKey key = KeyTranslator.byte2RecordKey(candidate);
        return key.getIdItem().equalsIgnoreCase(_idItem);
    }
}
