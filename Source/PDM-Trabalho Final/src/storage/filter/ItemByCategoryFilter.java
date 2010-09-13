package storage.filter;

import domainObjects.Item;
import domainObjects.translator.ItemTranslator;
import javax.microedition.rms.RecordFilter;

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
