package screens;

import entryPoint.PenPAL;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;

public abstract class FormScreen extends Form implements CommandListener{

    protected final PenPAL owner;
    protected final Command cmdBack, cmdOk;

    public FormScreen(PenPAL owner, String title, Item[] items) {
        super(title, items);
        this.owner = owner;
        
        cmdBack = new Command("Back", Command.BACK, 1);
        cmdOk = new Command("Ok", Command.OK, 1);

        this.addCommand(cmdBack);
        this.addCommand(cmdOk);

        this.setCommandListener(this);
    }

    public abstract void cls();
}
