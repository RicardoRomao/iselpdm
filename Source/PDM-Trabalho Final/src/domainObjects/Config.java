package domainObjects;

public class Config {

    private boolean _saveSentItems;
    private int _recordsPerPage;
    
    public Config() { }
    
    public Config(int recordsPerPage, boolean saveOwnItems) {
        this._saveSentItems = saveOwnItems;
        this._recordsPerPage = recordsPerPage;
    }

    public boolean getSaveOwnItems() { return _saveSentItems; }
    public void setSaveOwnItems(boolean flag) { _saveSentItems = flag; }

    public int getRecordsPerPage() { return _recordsPerPage; }
    public void setRecordsPerPage(int num) { _recordsPerPage = num; }

}
