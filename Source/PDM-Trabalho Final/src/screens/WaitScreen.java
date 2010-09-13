package screens;

import entryPoint.PenPAL;
import javax.microedition.lcdui.Alert;

public class WaitScreen extends Alert {

    private final PenPAL owner;

    public WaitScreen(PenPAL owner) {
        super(null);
        this.owner = owner;
    }



}
