package domainObjects;

import java.util.Date;

public class Item {

    private int _id;
    private int _category;
    private String _title;
    private String _desc;
    private byte[] _image;
    private Date _expiryDate;

    public Item() { }

    public Item(int id, int category, String title, String desc,
            byte[] image, Date expiryDate) {
        _id = id;
        _category = category;
        _title = title;
        _desc = desc;
        _image = image == null ? new byte[0] : image;
        _expiryDate = expiryDate == null ? new Date(0) : expiryDate;
    }

    public int getId() { return _id; }
    public void setId(int id) { _id = id; }

    public int getCategory() { return _category; }
    public void setCategory(int cat) { _category = cat; }

    public String getTitle() { return _title; }
    public void setTitle(String title) { _title = title; }

    public String getDesc() { return _desc; }
    public void setDesc(String desc) { _desc = desc; }

    public byte[] getImage() { return _image; }
    public void setImage(byte[] image) { _image = image; }

    public Date getExpiryDate() { return _expiryDate; }
    public void setExpiryDate(Date expiryDate){ _expiryDate = expiryDate; }
}
