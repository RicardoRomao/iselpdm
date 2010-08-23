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

    public ItemByCategoryFilter(int categoryId){
        _catId = categoryId;        
    }
    public boolean matches(byte[] candidate) {
        Item i = ItemTranslator.byte2Item(candidate);
        return i.getCategory() == _catId;
    }
}
