package app;

import connector.*;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class TestMIDP extends MIDlet implements CommandListener, IConnectionListener {
    private Form frm;
    private Command cmd;
    private Command cmd2;
    private HttpConnector httpCon;

    private void init() {
        if (frm == null) {
            frm = new Form("Form");
            httpCon = (HttpConnector)HttpConnector.getInstance();
            httpCon.setUrl("http://localhost/j2me/index.html");
            httpCon.setMethod(HttpConnection.GET);
            httpCon.setCompleteListener(this);
            httpCon.setFailListener(this);
        }
    }

    public void startApp() {
        init();
        frm.append("Hello");
        cmd = new Command("Sair", Command.EXIT, 1);
        cmd2 = new Command("GET", Command.OK, 1);
        frm.addCommand(cmd);
        frm.addCommand(cmd2);
        frm.setCommandListener(this);
        Display.getDisplay(this).setCurrent(frm);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command c, Displayable d) {
        if (c == cmd) {
            notifyDestroyed();
        }
        else if (c == cmd2) {
            httpCon.init();
            httpCon.send();
        }
    }

    public void onFail(IConnectionDecorator con) {
        frm.append("Failed");
    }

    public void onComplete(IConnectionDecorator con) {
        String str = new String(httpCon.getResponse());
        frm.append(str);
    }
}
