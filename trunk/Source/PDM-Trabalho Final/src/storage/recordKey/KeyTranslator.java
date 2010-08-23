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

        int lastIndex = 0;
        int delimiterindex = key.indexOf(delimiter);

        String idItem = key.substring(lastIndex, delimiterindex);

        lastIndex = delimiterindex + 1;
        delimiterindex = key.indexOf(delimiter, lastIndex);

        String idRecord = key.substring(lastIndex, delimiterindex);

        lastIndex = delimiterindex + 1;
        delimiterindex = key.indexOf(delimiter, lastIndex);
        
        String idImage = key.substring(lastIndex, delimiterindex);

        boolean ownValue = key.substring(delimiterindex + 1).equalsIgnoreCase("true");

        return new RecordKey(idItem, idRecord, idImage, ownValue);
    }
    public static byte[] RecordKey2byte(RecordKey key){
        String keyStr = key.getIdItem() + "|" + key.getIdRecord() + "|" + key.getIdImage() + "|" + key.getOwn();
        return keyStr.getBytes();
    }
}
