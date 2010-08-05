package dataMappers;

import dataMappers.query.IItemQuery;
import domainObjects.Item;
import java.util.Vector;

public interface IItemMapper {

    /*
     * TODO: Define listener interface
     */

    public void insert(Item item);
    public void remove(Item item);
    public Vector getItems(IItemQuery query);
    public Vector getOwnItems();
    public void cancel();
    public void setCompleteListener(Object listener);
    public void setFailListener(Object listener);


}
