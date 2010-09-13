package domainObjects.translator;

import constants.Constants;
import domainObjects.Item;
import java.util.Date;

public class ItemTranslator {// implements IITemTranslator {

    public static byte[] item2Byte(Item item) {
        String itemStr = item.getId() + Constants.TRANSLATOR_SEP
                + item.getCategory() + Constants.TRANSLATOR_SEP
                + item.getTitle() + Constants.TRANSLATOR_SEP
                + item.getDesc() + Constants.TRANSLATOR_SEP
                + item.getExpiryDate().getTime();
        return itemStr.getBytes();
    }

    public static Item byte2Item(byte[] item) {
        String itemStr = new String(item);
        int index, lastIndex = 0;
        int sepLen = Constants.TRANSLATOR_SEP.length();
        
        index = itemStr.indexOf(Constants.TRANSLATOR_SEP, 0);

        //Getting id
        int id = Integer.parseInt(itemStr.substring(lastIndex, index));

        lastIndex = index + sepLen;
        index = itemStr.indexOf(Constants.TRANSLATOR_SEP, lastIndex);

        //Getting categoryId
        int categoryId = Integer.parseInt(itemStr.substring(lastIndex, index));

        lastIndex = index + sepLen;
        index = itemStr.indexOf(Constants.TRANSLATOR_SEP, lastIndex);

        //Getting Title
        String title = itemStr.substring(lastIndex, index);

        lastIndex = index + sepLen;
        index = itemStr.indexOf(Constants.TRANSLATOR_SEP, lastIndex);

        //Getting Description
        String desc = itemStr.substring(lastIndex, index);

        lastIndex = index + sepLen;

        //Getting ExpiryDate
        Date expiryDate = new Date(Long.parseLong(itemStr.substring(lastIndex)));

        return new Item(id, categoryId, title, desc, null, expiryDate);
    }

}
