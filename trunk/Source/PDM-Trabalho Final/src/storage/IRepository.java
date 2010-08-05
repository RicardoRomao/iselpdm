package storage;

import domainObjects.Item;
import java.util.Vector;

public interface IRepository {

    public Item getItemByid(int id);
    public Vector getItemsByCat(int cat);
    public Vector getOwnItems();
}
