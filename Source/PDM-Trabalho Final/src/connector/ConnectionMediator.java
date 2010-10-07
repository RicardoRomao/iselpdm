package connector;

import com.sun.j2me.global.DateTimeFormat;
import constants.Constants;
import domainObjects.Item;
import domainObjects.User;
import entryPoint.PenPAL;
import java.util.Date;
import java.util.Vector;
import javax.microedition.io.HttpConnection;
import utils.Util;

public class ConnectionMediator implements IConnectionListener {
    private static IConnectionDecorator con;
    private User user;
    private int action;
    private Object obj;
    private final PenPAL owner;
    private Thread _worker;
    private volatile boolean inUse = false;
    private static final int ADD = 0;
    private static final int LST_OWN = 1;

    public ConnectionMediator(PenPAL owner) {
        this.owner = owner;
        user = this.owner.getUserProfile();
        con = HttpConnector.getInstance();
        con.setCompleteListener(this);
        con.setFailListener(this);
        con.setUrl(Constants.SVC_URL);
    }

    public void setUser(User usr) {
        user = usr;
    }

    public void addItem(Item item) {
        if (inUse == false) {

            inUse = true;
            obj = item;
            action = ADD;
            con.setMethod(HttpConnection.POST);
            con.addParameter("action", "add");
            con.addParameter("title", item.getTitle());
            con.addParameter("description", item.getDesc());
            con.addParameter("expirydate", new Integer((int)item.getExpiryDate().getTime()));
            con.addParameter("user", new Integer(user.getId()));
            con.addParameter("category", new Integer(item.getCategory()));

            doConn();
        }
    }

    public void doConn() {
        _worker = new Thread() {
            public void run() {
                con.init();
                con.send();
            }
        };
        _worker.start();
    }

    public void cancel() {
        if (_worker != null && _worker.isAlive())
            _worker.interrupt();
    }

    public void showSentItems(int category) {
        if (inUse == false) {
            inUse = true;
            obj = new Integer(category);
            action = LST_OWN;
            con.setMethod(HttpConnection.GET);
            con.addParameter("user", new Integer(user.getId()));
            doConn();
        }
    }

    public void onFail(IConnectionDecorator con) {
        inUse = false;
    }

    public void onComplete(IConnectionDecorator con) {
        String str;
        switch (action) {
            case ADD:
                str = new String(con.getResponse());
                Item i = (Item)obj;
                i.setId(Integer.parseInt(str));
                owner.sendItemComplete(i);
                break;
            case LST_OWN:
                Vector returnedItems = new Vector();
                str = new String(con.getResponse());
                Integer cat = (Integer)obj;
                if (str.length() > 0) {
                    String[] items = Util.splitString(str, '#');
                    String[] tempItem;
                    Item newItem;
                    for (int j=0 ; j<items.length ; j++) {
                        tempItem = Util.splitString(items[j], '|');
                        newItem = new Item(
                                Integer.parseInt(tempItem[0]),
                                Integer.parseInt(tempItem[5]),
                                tempItem[1],
                                tempItem[2],
                                null,
                                new Date(Long.parseLong(tempItem[3]))
                        );
                    }
                }
                owner.sentItemsComplete(cat.intValue(),returnedItems);
                break;
            default:
                break;
        }
        inUse = false;
    }
}