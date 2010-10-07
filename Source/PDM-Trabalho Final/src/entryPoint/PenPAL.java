package entryPoint;

import connector.ConnectionMediator;
import constants.Constants;
import domainObjects.Config;
import domainObjects.Item;
import domainObjects.Query;
import domainObjects.User;
import java.util.Vector;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
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

public class PenPAL extends MIDlet {

    private boolean isInited;
    private Display display;
    private Config config;
    private ConnectionMediator con;
    private Command waitCmd;
    private static IRepository rep = new RecStoreRepository();
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
        con = new ConnectionMediator(this);
        isInited = true;
    }

    protected void startApp() throws MIDletStateChangeException {
        if (!isInited) {
            init();
        }
        showMainScreen();
    }

    protected void pauseApp() {    }

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

    public void updateUserProfile(User user) {
        rep.updateUserProfile(user);
        con.setUser(user);
    }

    public User getUserProfile() { return rep.getUserProfile(); }

    public boolean hasUserProfile() { return rep.getUserProfile() != null; }

    public void updateConfig(Config c) {
        this.config = c;
        rep.updateConfig(config);
    }

    // SEND ITEM ---
    public void sendItem(Item item) {
        showCommunicatingScreen();
        con.addItem(item);
    }

    public void sendItemComplete(Item item) {
        if (config.getSaveOwnItems()) {
            addOwnItem(item);
        }
        showMainScreen();
        showWaitScreen("Item successfully sent!", AlertType.CONFIRMATION);
    }
    // --- SEND ITEM

    // GET SAVED ITEMS ---
    public void savedItems(int category) {
        showCommunicatingScreen();
        Vector items = rep.getItemsByCat(category);
        if (items.size() == 0) {
            showCategorySelectorScreen(IDX_SENTITEMS_SCREEN);
            showWaitScreen(
                "No saved items in category '" + getCategoryName(category) + "'!",
                AlertType.CONFIRMATION
            );
        } else {
            showItemListScreen(category, IDX_SENTITEMS_SCREEN, items);
            showWaitScreen(
               "Successfully loaded " + items.size() + " saved items!",
               AlertType.CONFIRMATION
            );
        }
    }
    // --- GET SAVED ITEMS

    // GET SENT ITEMS ---
    public void sentItems(int category) {
        showCommunicatingScreen();
        con.showSentItems(category);
    }

    public void sentItemsComplete(int category, Vector items) {
        if (items.size() == 0) {
            showCategorySelectorScreen(IDX_SENTITEMS_SCREEN);
            showWaitScreen(
                "No sent items in category '" + getCategoryName(category) + "'!",
                AlertType.CONFIRMATION
            );
        } else {
            showItemListScreen(category, IDX_SENTITEMS_SCREEN, items);
            showWaitScreen(
               "Successfully received " + items.size() + " sent items!",
               AlertType.CONFIRMATION
            );
        }
    }
    // --- GET SENT ITEMS

    // GET QUERY RESULT ---
    public void sendQuery(Query q) {
        showMainScreen();
        showWaitScreen("Not implemented!", AlertType.WARNING);
    }
    // --- GET QUERY RESULT
    public void showCommunicatingScreen() {
        showWaitScreen("Communicating....", AlertType.INFO);
        waitCmd = new Command("Cancel",Command.CANCEL,1);
        addCommandToWaitScreen(new CommandListener() {
            public void commandAction(Command c, Displayable d) {
                con.cancel();
                showWaitScreen("Communication canceled by user!", AlertType.ERROR);
            }
        });
    }

    public void showWaitScreen() { showWaitScreen(null, AlertType.INFO); }

    public void showWaitScreen(String msg, AlertType type) {
        if (screens[IDX_WAIT_SCREEN] == null) {
            screens[IDX_WAIT_SCREEN] = new Alert(Constants.APP_TITLE, null, null, type);
            ((Alert)screens[IDX_WAIT_SCREEN]).setTimeout(Alert.FOREVER);
        }
        if (waitCmd != null) {
            ((Alert)screens[IDX_WAIT_SCREEN]).removeCommand(waitCmd);
            ((Alert)screens[IDX_WAIT_SCREEN]).setCommandListener(null);
        }
        ((Alert)screens[IDX_WAIT_SCREEN]).setString(msg);
        display.setCurrent(screens[IDX_WAIT_SCREEN]);
    }

    private void addCommandToWaitScreen(CommandListener listener) {
        if (waitCmd != null) {
            ((Alert)screens[IDX_WAIT_SCREEN]).addCommand(waitCmd);
            ((Alert)screens[IDX_WAIT_SCREEN]).setCommandListener(listener);
        }
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
        showCategorySelectorScreen(IDX_SAVEDITEMS_SCREEN);
    }
    
    public void showSentItemsScreen() {
        showCategorySelectorScreen(IDX_SENTITEMS_SCREEN);
    }

    public void showCategorySelectorScreen(int targetScreen) {
        if (screens[IDX_CATSELECTOR_SCREEN] == null) {
            screens[IDX_CATSELECTOR_SCREEN] = new CategorySelectorScreen(this);
        }
        screens[targetScreen] = screens[IDX_CATSELECTOR_SCREEN];
        if (targetScreen == IDX_QUERY_SCREEN) {
            showQueryScreen();
        }
        ((CategorySelectorScreen) screens[targetScreen]).setTargetScreen(targetScreen);
        display.setCurrent(screens[targetScreen]);
    }

    public void resolveCategorySelection(int category, int targetScreen) {
        //Ver qual é o target e direccionar para 'Sent Items' ou 'Saved Items'
        switch (targetScreen) 
        {
            case IDX_SAVEDITEMS_SCREEN:

                break;
            case IDX_SENTITEMS_SCREEN:
                sentItems(category);
                break;
            case -1:

                break;
        }
    }

    public void showItemListScreen(int category, int targetScreen, Vector items) {
        if (screens[IDX_ITEMLIST_SCREEN] == null) {
            screens[IDX_ITEMLIST_SCREEN] = new ItemListScreen(this);
        }
        String sufix = null;
        switch (targetScreen) {
            case IDX_QUERY_SCREEN:
                sufix = "Query Result";
                break;
            case IDX_SAVEDITEMS_SCREEN:
                sufix = "Saved Items";
                break;
            case IDX_SENTITEMS_SCREEN:
                sufix = "Sent Items";
                break;
        }
        ((ItemListScreen) screens[IDX_ITEMLIST_SCREEN]).initList(items, category);
        screens[IDX_ITEMLIST_SCREEN].setTitle(
                Constants.APP_TITLE + " - " + sufix
                + "(" + Constants.CATEGORIES[category] + ")");
        display.setCurrent(screens[IDX_ITEMLIST_SCREEN]);
    }

    public void showItemListScreen(Vector items) {
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

    private String getCategoryName(int id) {
        return Constants.CATEGORIES[Constants.CATEGORIES_NAMES[id]];
    }
}
