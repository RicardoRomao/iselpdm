/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package storage.recordKey;

/**
 *
 * @author nuno.sousa
 */
public class KeyTranslator {

    public static RecordKey byte2RecordKey(byte[] byteArr){
        String key = new String(byteArr);
        String delimiter = "|";
        int delimiterindex = key.indexOf(delimiter);
        return new RecordKey(key.substring(0, delimiterindex), key.substring(delimiterindex + 1));
    }
    public static byte[] RecordKey2byte(RecordKey key){
        String keyStr = key.getIdItem() + "|" + key.getIdRecord();
        return keyStr.getBytes();
    }
}
