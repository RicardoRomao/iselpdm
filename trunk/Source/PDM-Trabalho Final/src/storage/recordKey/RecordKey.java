/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package storage.recordKey;

/**
 *
 * @author nuno.sousa
 */
public class RecordKey {

    private String _idItem;
    private String _idRecord;
    private String _idImageRecord;
    
    public RecordKey(String idItem, String idRecord, String idImageRecord){
        _idItem = idItem;
        _idRecord = idRecord;
        _idImageRecord = idImageRecord;
    }

    public String getIdItem(){ return _idItem;}
    public String getIdRecord(){ return _idRecord;}
    public String getIdImage(){ return _idImageRecord;}
    public void setIdImage(String idImageRecord){ _idImageRecord = idImageRecord;}

    public byte[] getBytes(){
        String s = _idItem + "|" + _idRecord + "|" + _idImageRecord;
        return s.getBytes();
    }
}
