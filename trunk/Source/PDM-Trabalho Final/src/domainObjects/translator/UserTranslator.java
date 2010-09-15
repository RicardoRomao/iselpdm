package domainObjects.translator;

import constants.Constants;
import domainObjects.User;

public class UserTranslator {

    public static byte[] user2Byte(User user) {
        String userStr = user.getId() + Constants.TRANSLATOR_SEP
                    + user.getName() + Constants.TRANSLATOR_SEP
                    + user.getPassword();
        return userStr.getBytes();
    }

    public static User byte2User(byte[] user) {
        if (user == null)
            return null;
        
        String userStr = new String(user);
        int index, lastIndex = 0;
        int sepLen = Constants.TRANSLATOR_SEP.length();

        index = userStr.indexOf(Constants.TRANSLATOR_SEP, 0);

        //Getting id
        int id = Integer.parseInt(userStr.substring(lastIndex, index));

        lastIndex = index + sepLen;
        index = userStr.indexOf(Constants.TRANSLATOR_SEP, lastIndex);

        //Getting username
        String username = userStr.substring(lastIndex, index);

        lastIndex = index + sepLen;
        index = userStr.indexOf(Constants.TRANSLATOR_SEP, lastIndex);

        //Getting password
        String password = userStr.substring(lastIndex);

        return new User(id, username, password);
    }
}
