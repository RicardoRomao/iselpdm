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

    public RecordKey(String idItem, String idRecord){
        _idItem = idItem;
        _idRecord = idRecord;
    }

    public String getIdItem(){ return _idItem;}
    public String getIdRecord(){ return _idRecord;}

    public byte[] getBytes(){
        String s = _idItem + "|" + _idRecord;
        return s.getBytes();
    }
}
