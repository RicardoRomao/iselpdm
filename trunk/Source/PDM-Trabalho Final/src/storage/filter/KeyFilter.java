/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package storage.filter;

import javax.microedition.rms.RecordFilter;
import storage.recordKey.KeyTranslator;

/**
 *
 * @author nuno.sousa
 */
public class KeyFilter implements RecordFilter {

    public String _idItem;

    public KeyFilter(String idItem){
        _idItem = idItem;
    }

    public boolean matches(byte[] candidate) {
        return KeyTranslator.byte2RecordKey(candidate).getIdItem().equalsIgnoreCase(_idItem);
    }

}
