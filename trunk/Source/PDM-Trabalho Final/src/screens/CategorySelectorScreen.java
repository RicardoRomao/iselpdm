package screens;

import constants.Constants;
import entryPoint.PenPAL;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

public class CategorySelectorScreen extends FormScreen{

    private int ownerRef;
    private final ChoiceGroup catSelector =
        new ChoiceGroup("Category", Choice.POPUP, Constants.CATEGORIES, null);

    public CategorySelectorScreen(PenPAL owner) {
        super(owner,Constants.APP_TITLE,null);
        append(catSelector);
    }

    public CategorySelectorScreen setOwnerRef(int ownerRef) {
        this.ownerRef = ownerRef;
        return this;
    }

    public void cls() { }

    public void commandAction(Command cmd, Displayable d) {
        if (cmd == cmdOk) {
            owner.showItemListScreen(catSelector.getSelectedIndex(), ownerRef);
        } else if (cmd == cmdBack) {
            owner.showMainScreen();
        }
    }

}
