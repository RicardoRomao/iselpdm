/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package storage.filter;

import domainObjects.Item;
import domainObjects.translator.ItemTranslator;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.rms.RecordFilter;
import storage.recordKey.RecordKey;

/**
 *
 * @author nuno.sousa
 */
public class ItemByCategoryFilter implements RecordFilter {

    private final int _catId;
    private final Vector _excluded;

    public ItemByCategoryFilter(int categoryId, Vector excluded){
        _catId = categoryId;
        Enumeration en = excluded.elements();
        _excluded = new Vector();
        while(en.hasMoreElements())
            _excluded.addElement(((RecordKey)en.nextElement()).getIdItem());
    }
    public boolean matches(byte[] candidate) {
        Item i = ItemTranslator.byte2Item(candidate);
        return i.getCategory() == _catId && !_excluded.contains(String.valueOf(i.getId()));
    }
}
