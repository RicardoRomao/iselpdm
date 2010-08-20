/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package storage.filter;

import domainObjects.translator.ItemTranslator;
import javax.microedition.rms.RecordFilter;

/**
 *
 * @author nuno.sousa
 */
public class ItemByIdFilter implements RecordFilter {

    private final int _id;

    public ItemByIdFilter(int id){
        _id = id;
    }
    public boolean matches(byte[] candidate) {
        return ItemTranslator.byte2Item(candidate).getId() == _id;
    }
}
