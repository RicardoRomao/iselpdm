/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package storage.filter;

import javax.microedition.rms.RecordFilter;
import storage.recordKey.KeyTranslator;
import storage.recordKey.RecordKey;

/**
 *
 * @author nuno.sousa
 */
public class KeyFilter implements RecordFilter {

    private final String _idItem;
    private final boolean _own;

    public KeyFilter(String idItem, boolean own){
        _idItem = idItem;
        _own = own;
    }

    public boolean matches(byte[] candidate) {
        RecordKey key = KeyTranslator.byte2RecordKey(candidate);
        return ((_idItem != null) ? key.getIdItem().equalsIgnoreCase(_idItem) : true ) && key.getOwn() == _own;
    }
}
