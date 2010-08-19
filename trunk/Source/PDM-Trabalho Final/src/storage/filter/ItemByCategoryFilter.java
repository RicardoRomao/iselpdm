/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package storage.filter;

import domainObjects.translator.Translator;
import javax.microedition.rms.RecordFilter;

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
        return Translator.byte2Item(candidate).getCategory() == _catId;
    }
}
