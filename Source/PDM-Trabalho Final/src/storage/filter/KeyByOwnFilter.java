package storage.filter;

import javax.microedition.rms.RecordFilter;
import storage.recordKey.KeyTranslator;
import storage.recordKey.RecordKey;

public class KeyByOwnFilter implements RecordFilter {

    private final boolean _own;

    public KeyByOwnFilter(boolean own){
        _own = own;
    }

    public boolean matches(byte[] candidate) {
        RecordKey key = KeyTranslator.byte2RecordKey(candidate);
        return key.getOwn() == _own;
    }
}
