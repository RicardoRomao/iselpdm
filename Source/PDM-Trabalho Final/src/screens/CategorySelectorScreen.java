package screens;

import constants.Constants;
import entryPoint.PenPAL;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

public class CategorySelectorScreen extends FormScreen{

    private int _targetScreen;
    private final ChoiceGroup catSelector =
        new ChoiceGroup("Category", Choice.POPUP, Constants.CATEGORIES, null);

    public CategorySelectorScreen(PenPAL owner) {
        super(owner,Constants.APP_TITLE,null);
        append(catSelector);
    }

    public CategorySelectorScreen setTargetScreen(int targetScreen) {
        this._targetScreen = targetScreen;
        return this;
    }

    public void cls() { }

    public void commandAction(Command cmd, Displayable d) {
        if (cmd == cmdOk) {
            owner.resolveCategorySelection(
                catSelector.getSelectedIndex(),
                _targetScreen
            );
            //owner.showItemListScreen(catSelector.getSelectedIndex(), _targetScreen);
        } else if (cmd == cmdBack) {
            owner.showMainScreen();
        }
    }

}
