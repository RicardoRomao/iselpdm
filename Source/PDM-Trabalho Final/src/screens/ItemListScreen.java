package screens;

import constants.Constants;
import domainObjects.Item;
import entryPoint.PenPAL;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

public class ItemListScreen extends ListScreen{

    private Vector items = null;
    private int parentRef;

    public ItemListScreen(PenPAL owner) {
        super(owner, Constants.APP_TITLE);
    }

    public void initList(Vector items, int parentRef) {
        cls();
        this.items = items;
        this.parentRef = parentRef;
        for (int i=0; i<items.size(); i++)
            addListItem(((Item)items.elementAt(i)).getCategory() + " - "
                    + ((Item)items.elementAt(i)).getTitle(), null);
    }

    public void cls() {
        this.deleteAll();
    }

    public void commandAction(Command cmd, Displayable d) {
        if (cmd == cmdOk) {
            owner.showItemViewScreen((domainObjects.Item)(items.elementAt(this.getSelectedIndex())));
        } else if (cmd == cmdBack) {
            owner.showCategorySelectorScreen(parentRef);
        }
    }

}
