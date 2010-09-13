package storage.filter;

import domainObjects.translator.ItemTranslator;
import javax.microedition.rms.RecordFilter;

public class ItemByIdFilter implements RecordFilter {

    private final int _id;

    public ItemByIdFilter(int id){
        _id = id;
    }
    public boolean matches(byte[] candidate) {
        return ItemTranslator.byte2Item(candidate).getId() == _id;
    }
}
