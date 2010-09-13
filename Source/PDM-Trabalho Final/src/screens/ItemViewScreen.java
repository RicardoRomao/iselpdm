package screens;

import constants.Constants;
import entryPoint.PenPAL;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

public class ItemViewScreen extends FormScreen {

    private Image itemImage;

    private static final int IDX_ID = 0;
    private static final int IDX_CATEGORY = 1;
    private static final int IDX_TITLE = 2;
    private static final int IDX_DESC = 3;
    private static final int IDX_DATE = 4;
    private static final int IDX_IMAGE = 5;
    private final Item[] items = {
        new StringItem("ID", "")
        , new StringItem("Category", "")
        , new StringItem("Title", "")
        , new StringItem("Description", "")
        , new StringItem("Expiry Date", "")
        , new ImageItem("Item Image", null, ImageItem.LAYOUT_LEFT, "")
    };

    public ItemViewScreen(PenPAL owner) {
        super(owner, Constants.APP_TITLE, null);
        removeCommand(cmdOk);
        for (int i=0; i<items.length; i++)
            append(items[i]);
    }

    public void setModel(domainObjects.Item item) {
        cls();
        ((StringItem)items[IDX_ID]).setText(String.valueOf(item.getId()));
        ((StringItem)items[IDX_CATEGORY]).setText(
            Constants.CATEGORIES[Constants.CATEGORIES_NAMES[item.getCategory()]]
        );
        ((StringItem)items[IDX_TITLE]).setText(item.getTitle());
        ((StringItem)items[IDX_DESC]).setText(item.getDesc());
        ((StringItem)items[IDX_DATE]).setText(item.getExpiryDate().toString());
        if (item.getImage() != null) {
            itemImage = Image.createImage(item.getImage(), 0, item.getImage().length);
            ((ImageItem)items[IDX_IMAGE]).setImage(itemImage);
        }
    }

    public void cls() {
        ((StringItem)items[IDX_ID]).setText(null);
        ((StringItem)items[IDX_CATEGORY]).setText(null);
        ((StringItem)items[IDX_TITLE]).setText(null);
        ((StringItem)items[IDX_DESC]).setText(null);
        ((StringItem)items[IDX_DATE]).setText(null);
        ((ImageItem)items[IDX_IMAGE]).setImage(null);
    }

    public void commandAction(Command cmd, Displayable d) {
        if (cmd == cmdBack) {
            owner.backToItemListScreen();
        }
    }

}
