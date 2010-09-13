package storage;

import domainObjects.Config;
import domainObjects.Item;
import domainObjects.User;
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
    
    public User getUserProfile();
    public void updateUserProfile(User u);
    public void deleteUserProfile();

    public Config getConfig();
    public void updateConfig(Config c);
}
