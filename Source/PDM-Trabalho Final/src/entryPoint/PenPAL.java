package entryPoint;

import asyncDispatch.AsyncDispatcher;
import connector.HttpConnector;
import connector.IConnectionDecorator;
import connector.IConnectionListener;
import constants.Constants;
import domainObjects.Config;
import domainObjects.Item;
import domainObjects.User;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import screens.CategorySelectorScreen;
import screens.ConfigScreen;
import screens.ItemCreateScreen;
import screens.ItemListScreen;
import screens.ItemViewScreen;
import screens.MainScreen;
import screens.ProfileScreen;
import screens.QueryScreen;
import storage.IRepository;
import storage.RecStoreRepository;

public class PenPAL extends MIDlet implements IConnectionListener {

    private boolean isInited;
    private Display display;
    private Config config;
    private static IRepository rep = new RecStoreRepository();
    private static IConnectionDecorator con;
    private static Displayable[] screens = new Displayable[Constants.MAX_SCREENS];
    private static final int IDX_MAIN_SCREEN = 0;
    private static final int IDX_PROFILE_SCREEN = 1;
    private static final int IDX_SETTINGS_SCREEN = 2;
    private static final int IDX_QUERY_SCREEN = 3;
    private static final int IDX_SENDITEM_SCREEN = 4;
    private static final int IDX_SAVEDITEMS_SCREEN = 5;
    private static final int IDX_SENTITEMS_SCREEN = 6;
    private static final int IDX_CATSELECTOR_SCREEN = 7;
    private static final int IDX_ITEMLIST_SCREEN = 8;
    private static final int IDX_ITEMVIEW_SCREEN = 9;
    private static final int IDX_WAIT_SCREEN = 10;

    public PenPAL() {
        isInited = false;
    }

    private void init() {
        display = Display.getDisplay(this);
        config = rep.getConfig();
    }

    protected void startApp() throws MIDletStateChangeException {
        if (!isInited) {
            init();
        }
        showMainScreen();
    }

    protected void pauseApp() {
    }

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        if (screens != null) {
            for (int i = 0; i < screens.length; i++) {
                if (screens[i] != null) {
                    screens[i] = null;
                }
            }
            screens = null;
        }
        rep = null;
    }

    private void addOwnItem(Item item) {
        rep.addOwnItem(item);
    }

    public void addItem(Item item) {
        rep.addOwnItem(item);
    }

    public void updateUserProfile(User user) {
        rep.updateUserProfile(user);
    }

    public boolean hasUserProfile() { return rep.getUserProfile() != null; }

    public void updateConfig(Config c) {
        this.config = c;
        rep.updateConfig(config);
    }

    public void sendItem(Item item) {
        //TODO: CALL TO CONNECTOR SENDING AN ITEM
        con = HttpConnector.getInstance();
        con.setCompleteListener(this);
        con.setFailListener(this);
        con.setUrl(Constants.SVC_URL);
        con.setMethod(HttpConnection.POST);

        con.addParameter("action", "add");
        con.addParameter("title", item.getTitle());
        con.addParameter("description", item.getDesc());
        con.addParameter("expirydate", new Integer((int)item.getExpiryDate().getTime()));
        con.addParameter("user", new Integer(rep.getUserProfile().getId()));
        con.addParameter("category", new Integer(item.getCategory()));

        con.init();
        con.send();

        showWaitScreen("A comunicar....", AlertType.INFO);

        if (config.getSaveOwnItems()) {
            addOwnItem(item);
        }
    }

    public void showWaitScreen() { showWaitScreen(null, AlertType.INFO); }

    public void showWaitScreen(String msg, AlertType type) {
        if (screens[IDX_WAIT_SCREEN] == null) {
            screens[IDX_WAIT_SCREEN] = new Alert(Constants.APP_TITLE, null, null, type);
            ((Alert)screens[IDX_WAIT_SCREEN]).setTimeout(Alert.FOREVER);
        }
        ((Alert)screens[IDX_WAIT_SCREEN]).setString(msg);
        display.setCurrent(screens[IDX_WAIT_SCREEN]);
    }

    public void showMainScreen() {
        if (screens[IDX_MAIN_SCREEN] == null) {
            screens[IDX_MAIN_SCREEN] = new MainScreen(this);
        }
        display.setCurrent(screens[IDX_MAIN_SCREEN]);
    }

    public void showProfileScreen() {
        if (screens[IDX_PROFILE_SCREEN] == null) {
            screens[IDX_PROFILE_SCREEN] = new ProfileScreen(this);
        }
        ((ProfileScreen) screens[IDX_PROFILE_SCREEN]).setUser(rep.getUserProfile());
        display.setCurrent(screens[IDX_PROFILE_SCREEN]);
    }

    public void showSettingsScreen() {
        if (screens[IDX_SETTINGS_SCREEN] == null) {
            screens[IDX_SETTINGS_SCREEN] = new ConfigScreen(this);
        }
        ((ConfigScreen) screens[IDX_SETTINGS_SCREEN]).setConfig(config);
        display.setCurrent(screens[IDX_SETTINGS_SCREEN]);
    }

    public void showQueryScreen() {
        if (screens[IDX_QUERY_SCREEN] == null) {
            screens[IDX_QUERY_SCREEN] = new QueryScreen(this);
        }
        ((QueryScreen) screens[IDX_QUERY_SCREEN]).cls();
        display.setCurrent(screens[IDX_QUERY_SCREEN]);
    }

    public void showSavedItemsScreen() {
        if (screens[IDX_CATSELECTOR_SCREEN] == null) {
            screens[IDX_CATSELECTOR_SCREEN] = new CategorySelectorScreen(this);
        }
        screens[IDX_SAVEDITEMS_SCREEN] = screens[IDX_CATSELECTOR_SCREEN];
        ((CategorySelectorScreen) screens[IDX_SAVEDITEMS_SCREEN]).setOwnerRef(IDX_SAVEDITEMS_SCREEN);
        display.setCurrent(screens[IDX_CATSELECTOR_SCREEN]);
    }

    public void showSentItemsScreen() {
        if (screens[IDX_CATSELECTOR_SCREEN] == null) {
            screens[IDX_CATSELECTOR_SCREEN] = new CategorySelectorScreen(this);
        }
        screens[IDX_SENTITEMS_SCREEN] = screens[IDX_CATSELECTOR_SCREEN];
        ((CategorySelectorScreen) screens[IDX_SENTITEMS_SCREEN]).setOwnerRef(IDX_SENTITEMS_SCREEN);
        display.setCurrent(screens[IDX_CATSELECTOR_SCREEN]);
    }

    public void showCategorySelectorScreen(int parentRef) {
        if (screens[IDX_CATSELECTOR_SCREEN] == null) {
            screens[IDX_CATSELECTOR_SCREEN] = new CategorySelectorScreen(this);
        }
        if (parentRef == -1) {
            showQueryScreen();
        }
        ((CategorySelectorScreen) screens[parentRef]).setOwnerRef(parentRef);
        display.setCurrent(screens[parentRef]);
    }

    public void showItemListScreen(int category, int parentRef) {

        //Called after category selection in 'Saved Items'
        // ans 'Sent Items' screen

        if (screens[IDX_ITEMLIST_SCREEN] == null) {
            screens[IDX_ITEMLIST_SCREEN] = new ItemListScreen(this);
        }

        String sufix = null;
        switch (parentRef) {
            case IDX_SAVEDITEMS_SCREEN:
                sufix = "Saved Items";
                ((ItemListScreen) screens[IDX_ITEMLIST_SCREEN]).initList(
                        rep.getItemsByCat(Constants.CATEGORIES_IDX[category]),
                        parentRef);
            case IDX_SENTITEMS_SCREEN:
                //TODO: CALL TO CONNECTOR ASKING FOR OWN ITEMS
                sufix = "Sent Items";
        }
        //((ItemListScreen)screens[IDX_ITEMLIST_SCREEN])
        screens[IDX_ITEMLIST_SCREEN].setTitle(
                Constants.APP_TITLE + " - " + sufix
                + "(" + Constants.CATEGORIES[category] + ")");
        display.setCurrent(screens[IDX_ITEMLIST_SCREEN]);
    }

    public void showItemListScreen() {
        //Called after returning from 'Get Items'
        if (screens[IDX_ITEMLIST_SCREEN] == null) {
            screens[IDX_ITEMLIST_SCREEN] = new ItemListScreen(this);
        }
        ((ItemListScreen) screens[IDX_ITEMLIST_SCREEN]).initList(rep.getItemsByCat(1), -1);
        screens[IDX_ITEMLIST_SCREEN].setTitle(Constants.APP_TITLE + " - " + "Received items");
    }

    public void backToItemListScreen() {
        display.setCurrent(screens[IDX_ITEMLIST_SCREEN]);
    }

    public void showItemViewScreen(Item item) {
        if (screens[IDX_ITEMVIEW_SCREEN] == null) {
            screens[IDX_ITEMVIEW_SCREEN] = new ItemViewScreen(this);
        }
        ((ItemViewScreen) screens[IDX_ITEMVIEW_SCREEN]).setModel(item);
        display.setCurrent(screens[IDX_ITEMVIEW_SCREEN]);
    }

    public void showSendItemScreen() {
        if (screens[IDX_SENDITEM_SCREEN] == null) {
            screens[IDX_SENDITEM_SCREEN] = new ItemCreateScreen(this);
        }
        ((ItemCreateScreen) screens[IDX_SENDITEM_SCREEN]).cls();
        display.setCurrent(screens[IDX_SENDITEM_SCREEN]);
    }

    public void onFail(IConnectionDecorator con) {
        showWaitScreen();
        ((Alert)screens[IDX_WAIT_SCREEN]).setString("FALHOU");
        display.setCurrent((Alert)screens[IDX_WAIT_SCREEN], display.getCurrent());
    }

    public void onComplete(IConnectionDecorator con) {
        ((Alert)screens[IDX_WAIT_SCREEN]).setString(new String(con.getResponse()));
        display.setCurrent((Alert)screens[IDX_WAIT_SCREEN], display.getCurrent());
        display.setCurrent(screens[IDX_MAIN_SCREEN]);
    }
}
