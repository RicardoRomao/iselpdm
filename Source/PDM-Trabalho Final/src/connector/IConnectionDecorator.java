package connector;

public interface IConnectionDecorator {

    public void addParameter(String key, Object value);
    public Object removeParameter(String key);
    public void setMethod(String method);
    public void setUrl(String url);
    public void init();
    public void send();
    public void cancelRequest();
    public boolean isResponseOk();
    public byte[] getResponse();
    public void setCompleteListener(IConnectionListener listener);
    public void setFailListener(IConnectionListener listener);

}
