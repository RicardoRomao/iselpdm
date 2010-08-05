package domainObjects.translator;

import domainObjects.Item;

public interface IITemTranslator {

    public byte[] item2Byte(Item item);
    public Item byte2Item(byte[] item);
}
