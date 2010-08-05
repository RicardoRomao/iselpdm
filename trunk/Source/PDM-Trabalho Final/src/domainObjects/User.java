package domainObjects;

public class User {

    private int _id;
    private String _userName;
    private String _password;

    public User() { }

    public User(int id, String userName, String password) {
        _id = id;
        _userName = userName;
        _password = password;
    }

    public int getId() { return _id; }
    public void setId(int id) { _id = id; }

    public String getName() { return _userName; }
    public void setName(String userName) { _userName = userName; }

    public String getPassword() { return _password; }
    public void setPassword(String password) { _password = password; }

}
