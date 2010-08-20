/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package domainObjects.translator;

import domainObjects.Item;
import java.util.Date;

/**
 *
 * @author nuno.sousa
 */
public class ItemTranslator{// implements IITemTranslator {

    public static byte[] item2Byte(Item item) {
        String itemStr = item.getId() + "|"
                + item.getCategory() + "|"
                + item.getTitle() + "|"
                + item.getDesc() + "|"
                + item.getExpiryDate().getTime();
        return itemStr.getBytes();
    }

    public static Item byte2Item(byte[] item) {
        String itemStr = new String(item);
        int index, lastIndex = 0;
        
        index = itemStr.indexOf("|", 0);

        //Getting id
        int id = Integer.parseInt(itemStr.substring(lastIndex, index));

        lastIndex = index + 1;
        index = itemStr.indexOf("|", lastIndex);

        //Getting categoryId
        int categoryId = Integer.parseInt(itemStr.substring(lastIndex, index));

        lastIndex = index + 1;
        index = itemStr.indexOf("|", lastIndex);

        //Getting Tittle
        String tittle = itemStr.substring(lastIndex, index);

        lastIndex = index + 1;
        index = itemStr.indexOf("|", lastIndex);

        //Getting Description
        String desc = itemStr.substring(lastIndex, index);

        lastIndex = index + 1;

        //Getting ExpiryDate
        Date expiryDate = new Date(Long.parseLong(itemStr.substring(lastIndex)));

        return new Item(id, categoryId, tittle, desc, null, expiryDate);
    }

}
