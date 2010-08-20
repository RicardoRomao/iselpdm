package storage.recordKey;

public class RecordKey {

    private String _idItem;
    private String _idRecord;
    private String _idImageRecord;
    private boolean _own;
    
    public RecordKey(String idItem, String idRecord, String idImageRecord, boolean own){
        _idItem = idItem;
        _idRecord = idRecord;
        _idImageRecord = idImageRecord;
        _own = own;
    }

    public String getIdItem(){ return _idItem;}
    public String getIdRecord(){ return _idRecord;}
    public String getIdImage(){ return _idImageRecord;}
    public void setIdImage(String idImageRecord){ _idImageRecord = idImageRecord;}
    public boolean getOwn(){ return _own; }
    
}
