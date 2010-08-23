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
