package screens;

import entryPoint.PenPAL;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

public abstract class ListScreen extends List implements CommandListener {

    protected final PenPAL owner;
    protected final Command cmdBack, cmdOk;

    public ListScreen(PenPAL owner, String title) {
        super(title, List.IMPLICIT);
        this.owner = owner;

        cmdBack = new Command("Back", Command.BACK, 1);
        cmdOk = new Command("Ok", Command.OK, 1);
        
        this.addCommand(cmdBack);
        this.addCommand(cmdOk);
        
        this.setCommandListener(this);
    }

    public void addListItem(String label, Image img) {
        this.append(label, img);
    }
}
