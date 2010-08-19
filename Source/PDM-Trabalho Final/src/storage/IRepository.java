package storage;

import domainObjects.Item;
import java.util.Vector;

public interface IRepository {

    public Item getItemByid(int id);
    public Vector getItemsByCat(int cat);
    public Vector getOwnItems();

    public void addItem(Item i);
    public void addOwnItem(Item i);

    public void deleteItem(Item i);
    public void deleteOwnItem(Item i);

    public int getItemsCount();
    public int getOwnItemsCount();
}
