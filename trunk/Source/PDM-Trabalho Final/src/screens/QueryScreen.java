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
import javax.microedition.lcdui.TextField;

public class QueryScreen extends FormScreen {

    private static final int IDX_CATEGORY = 0;
    private static final int IDX_KEYWORDS = 1;
    private static final int IDX_LOCATION = 2;
    private static final int IDX_DATE_FROM = 3;
    private static final int IDX_DATE_TO = 4;
    private final Item[] items = {
        new ChoiceGroup("Category", Choice.POPUP, Constants.CATEGORIES, null)
        , new TextField("Keywords", "", 400, TextField.ANY)
        , new TextField("Location", "", 30, TextField.ANY)
        , new DateField("From", DateField.DATE_TIME, TimeZone.getTimeZone("GMT"))
        , new DateField("To", DateField.DATE_TIME, TimeZone.getTimeZone("GMT"))
    };

    public QueryScreen(PenPAL owner) {
        super(owner,Constants.APP_TITLE,null);
        for (int i=0; i < items.length ; i++)
            append(items[i]);
    }

    public void commandAction(Command cmd, Displayable d) {

        if (cmd == cmdOk) {

        } else if (cmd == cmdBack) {
            owner.showMainScreen();
        }
    }

    public void cls() {
        ((ChoiceGroup)get(IDX_CATEGORY)).setSelectedIndex(0, true);
        ((TextField)get(IDX_KEYWORDS)).setString(null);
        ((TextField)get(IDX_LOCATION)).setString(null);
        ((DateField)get(IDX_DATE_FROM)).setDate(null);
        ((DateField)get(IDX_DATE_TO)).setDate(null);
    }
}
