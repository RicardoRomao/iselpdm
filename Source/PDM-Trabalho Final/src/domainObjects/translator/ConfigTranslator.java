package domainObjects.translator;

import constants.Constants;
import domainObjects.Config;

public class ConfigTranslator {

    public static byte[] config2Byte(Config config) {
        String configStr = config.getRecordsPerPage() + Constants.TRANSLATOR_SEP
                + (config.getSaveOwnItems() ? 1 : 0) + Constants.TRANSLATOR_SEP;
        return configStr.getBytes();
    }

    public static Config byte2Config(byte[] config) {
        if (config == null)
            return new Config(
                Constants.CONFIG_DEFAULT_RECORDSPERPAGE,
                Constants.CONFIG_DEFAULT_SAVESENTITEMS
            );

        String configStr = new String(config);
        int index, lastIndex = 0;
        int sepLen = Constants.TRANSLATOR_SEP.length();

        index = configStr.indexOf(Constants.TRANSLATOR_SEP, 0);

        //Getting records per page
        int recsPerPage = Integer.parseInt(configStr.substring(lastIndex, index));

        lastIndex = index + sepLen;
        index = configStr.indexOf(Constants.TRANSLATOR_SEP, lastIndex);

        //Getting save own items flag
        boolean saveOwnItems =
                configStr.substring(lastIndex, index).equals("1") ? true : false;

        return new Config(recsPerPage, saveOwnItems);
    }
}
