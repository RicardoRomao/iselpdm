package connector;

public interface IConnectionListener {
    void onFail(IConnectionDecorator con);
    void onComplete(IConnectionDecorator con);
}