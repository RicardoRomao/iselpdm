package screens;

import constants.Constants;
import domainObjects.Item;
import entryPoint.PenPAL;
import java.util.Date;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import storage.IRepository;
import storage.RecStoreRepository;

public class MainScreen extends ListScreen {

    protected final Command cmdExit = new Command("Exit", Command.EXIT, 1);

    private static final int IDX_PROFILE = 0;
    private static final int IDX_SETTINGS = 1;
    private static final int IDX_GETITEMS = 2;
    private static final int IDX_SENDITEM = 3;
    private static final int IDX_SAVEDITEMS = 4;
    private static final int IDX_SENTITEMS = 5;

    private static final String[] itemLabels = {"Profile", "Settings", "Get Items", "Send Item", "Saved Items", "Sent Items"};
    private static final Image[] itemImages = {null, null, null, null, null, null};

    public MainScreen(PenPAL owner) {
        super(owner, Constants.APP_TITLE, itemLabels, itemImages);
        addCommand(cmdExit);
        removeCommand(cmdBack);
    }

    public void commandAction(Command cmd, Displayable d) {
        if (cmd == cmdOk) {

            switch(this.getSelectedIndex()) {
                case (IDX_PROFILE) :
                    owner.showProfileScreen();
                    break;
                case (IDX_SETTINGS) :
                    owner.showSettingsScreen();
                    break;
                case (IDX_GETITEMS) :
                    owner.showQueryScreen();
                    break;
                case (IDX_SENDITEM) :
                    owner.showSendItemScreen();
                    break;
                case (IDX_SAVEDITEMS) :
                    owner.showSavedItemsScreen();
                    break;
                case (IDX_SENTITEMS) :
                    owner.showSentItemsScreen();
                    break;
            }
        } else if (cmd == cmdExit) {
            owner.notifyDestroyed();
        }
    }

    
}
