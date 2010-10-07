package domainObjects;

import java.util.Date;


public class Query {

    private int _category;
    private String[] _keywords;
    private String _location;
    private Date _to;
    private Date _from;

    public int getCategory() {
        return _category;
    }

    public void setCategory(int category) {
        this._category = category;
    }

    public String[] getKeywords() {
        return _keywords;
    }

    public void setKeywords(String[] keywords) {
        this._keywords = keywords;
    }

    public String getLocation() {
        return _location;
    }

    public void setLocation(String location) {
        this._location = location;
    }

    public Date getTo() {
        return _to;
    }

    public void setTo(Date to) {
        this._to = to;
    }

    public Date getFrom() {
        return _from;
    }

    public void setFrom(Date from) {
        this._from = from;
    }

}
