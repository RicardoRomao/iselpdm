package domainObjects;

public class Category {

    private int _id;
    private String _desc;

    public Category() { }
    
    public Category(int id, String desc) {
        _id = id;
        _desc = desc;
    }

    public int getId() { return _id; }
    public void setId(int id) { _id = id; }

    public String getDesc() { return _desc; }
    public void setDesc(String desc) { _desc = desc; }

}
