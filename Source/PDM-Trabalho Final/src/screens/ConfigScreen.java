package screens;

import constants.Constants;
import domainObjects.Config;
import entryPoint.PenPAL;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Item;

public class ConfigScreen extends FormScreen {

    private static final int IDX_SENT_ITEMS = 0;
    private static final int IDX_PAGE_ITEMS = 1;
    private final Item[] items = {
        new ChoiceGroup("Sent items", ChoiceGroup.MULTIPLE, new String[] { "Save ?" }, null),
        new Gauge("Records per page",true,4,20)
    };

    public ConfigScreen(PenPAL owner) {
        super(owner, Constants.APP_TITLE + " - Settings", null);
        for (int i = 0; i < items.length ; i++)
            this.append(items[i]);
    }

    public ConfigScreen setConfig(Config config) {
         if (config != null) {
            ((ChoiceGroup)get(IDX_SENT_ITEMS)).setSelectedIndex(0,config.getSaveOwnItems());
            ((Gauge)get(IDX_PAGE_ITEMS)).setValue(config.getRecordsPerPage());
        }
         return this;
    }

    public void commandAction(Command cmd, Displayable d) {
        if (cmd == cmdOk) {
            owner.updateConfig(
                new Config(
                    ((Gauge) get(IDX_PAGE_ITEMS)).getValue(),
                    ((ChoiceGroup) get(IDX_SENT_ITEMS)).isSelected(0)
                )
            );
            owner.showMainScreen();
        } else if (cmd == cmdBack) {
            owner.showMainScreen();
        }
    }

    public void cls() {
        ((ChoiceGroup)get(IDX_SENT_ITEMS)).setSelectedIndex(0, Constants.CONFIG_DEFAULT_SAVESENTITEMS);
        ((Gauge)get(IDX_PAGE_ITEMS)).setValue(Constants.CONFIG_DEFAULT_RECORDSPERPAGE);
    }
}
