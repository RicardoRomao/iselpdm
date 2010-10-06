package connector;

import constants.Constants;
import domainObjects.Item;
import domainObjects.User;
import entryPoint.PenPAL;
import javax.microedition.io.HttpConnection;

public class ConnectionMediator implements IConnectionListener {
    private static IConnectionDecorator con;
    private User user;
    private int action;
    private Object obj;
    private final PenPAL owner;
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

            con.init();
            con.send();
        }
    }

    public void showSentItems() {
        if (inUse == false) {
            inUse = true;

            action = LST_OWN;

            con.setMethod(HttpConnection.GET);

            con.addParameter("user", new Integer(user.getId()));

            con.init();
            con.send();
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
                str = new String(con.getResponse());
                //String[] resp = str.split();
            default:
                break;
        }
        inUse = false;
    }
}