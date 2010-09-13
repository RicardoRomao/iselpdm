package screens;

import constants.Constants;
import entryPoint.PenPAL;
import java.util.TimeZone;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

public class ItemCreateScreen extends FormScreen {

    private domainObjects.Item newItem;

    private static final int IDX_CATEGORY = 0;
    private static final int IDX_TITLE = 1;
    private static final int IDX_DESC = 2;
    private static final int IDX_DATE = 3;
    private static final int IDX_IMAGE = 4;
    private final Item[] items = {
        new ChoiceGroup("Category", Choice.POPUP, Constants.CATEGORIES, null)
        , new TextField("Title", "", 400, TextField.ANY)
        , new TextField("Description", "", 2500, TextField.ANY)
        , new DateField("Expiry Date", DateField.DATE_TIME, TimeZone.getTimeZone("GMT"))
    };

    public ItemCreateScreen(PenPAL owner) {
        super (owner, Constants.APP_TITLE, null);
        for (int i=0; i<items.length; i++)
            append(items[i]);
    }

    public void cls() { }

    private int getSelectedCategory() {
        return ((ChoiceGroup)items[IDX_CATEGORY]).getSelectedIndex();
    }

    public void commandAction(Command cmd, Displayable d) {
        if (cmd == cmdOk) {
            if (newItem == null)
                newItem = new domainObjects.Item();
            newItem.setCategory(Constants.CATEGORIES_IDX[getSelectedCategory()]);
            newItem.setTitle(((TextField)items[IDX_TITLE]).getString());
            newItem.setDesc(((TextField)items[IDX_DESC]).getString());
            newItem.setExpiryDate(((DateField)items[IDX_DATE]).getDate());
            //newItem.setImage(null);
            owner.sendItem(newItem);
            owner.showMainScreen();

        } else if (cmd == cmdBack) {
            owner.showMainScreen();
        }
    }


}
