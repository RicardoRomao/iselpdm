package screens;

import constants.Constants;
import domainObjects.User;
import entryPoint.PenPAL;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.TextField;

public class ProfileScreen extends FormScreen {

    private static final int IDX_USERNAME = 0;
    private static final int IDX_PASSWORD = 1;
    private final Item[] items = {
        new TextField("Username","",40,TextField.EMAILADDR),
        new TextField("Password","",40,TextField.ANY)
    };

    public ProfileScreen(PenPAL owner) {
        super(owner, Constants.APP_TITLE + " - User Profile", null);
        for (int i = 0; i < items.length ; i++)
            this.append(items[i]);
    }
    
    public ProfileScreen setUser(User user) {
         if (user != null) {
            ((TextField)get(IDX_USERNAME)).setString(user.getName());
            ((TextField)get(IDX_PASSWORD)).setString(user.getPassword());
        }
         return this;
    }

    public void commandAction(Command cmd, Displayable d) {
        if (cmd == cmdOk) {
            owner.updateUserProfile(
                new User(
                    0,
                    ((TextField) get(IDX_USERNAME)).getString(),
                    ((TextField) get(IDX_PASSWORD)).getString()
                )
            );
            owner.showMainScreen();
        } else if (cmd == cmdBack) {
            owner.showMainScreen();
        }
    }

    public void cls() {
        ((TextField)get(IDX_USERNAME)).setString("");
        ((TextField)get(IDX_PASSWORD)).setString("");
    }
}
