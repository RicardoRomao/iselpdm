package connector;

import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import storage.Exception.NotExpectedException;

public class HttpConnector implements IConnectionDecorator, IConnectionListener {
    private String _url;
    private String _method;
    private Hashtable _params;
    private byte[] _res;
    private IConnectionListener _failListener;
    private IConnectionListener _completeListener;
    private Thread _worker;
    private HttpConnection _con;
    private int _responseCode;
    private volatile boolean bUse = false;

    private HttpConnector() {
        _params = new Hashtable();
        _method = HttpConnection.POST;
    }

    public static IConnectionDecorator getInstance() {
        return new HttpConnector();
    }

    public void addParameter(String key, Object value) {
        if (bUse) return;
        _params.put(key, value);
    }

    public Object removeParameter(String key) {
        if (bUse) return null;
        return _params.remove(key);
    }

    public void setMethod(String method) {
        if (bUse) return;
        _method = method;
    }

    public void setUrl(String url) {
        if (bUse) return;
        _url = url;
    }

    public void init() {
        if (bUse) return;
        bUse = true;
        try {
            String rawData = buildQueryString();
            String endPoint = _url + ((_method.compareTo(HttpConnection.POST) == 0)?"":rawData);
            _con = (HttpConnection) Connector.open(endPoint);
            if (_method.compareTo(HttpConnection.POST) == 0) {
                _con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                _con.setRequestProperty("Content-Length", String.valueOf(rawData.length()));

                OutputStream os = _con.openOutputStream();
                os.write(rawData.getBytes());
                os.flush();
                os.close();
            }
        } catch (IOException ex) {
            bUse = false;
            throw new NotExpectedException(ex.getMessage());
        }
    }

    private String buildQueryString() {
        StringBuffer query = new StringBuffer();
        Enumeration keys = _params.keys();
        String key;

        while(keys.hasMoreElements()) {
            key = keys.nextElement().toString();
            if (query.length() > 0) {
                query.append("&");
            }
            query.append(key);
            query.append("=");
            query.append(_params.get(key).toString());
        }

        return query.toString();
    }

    public void send() {
        //init();
        final IConnectionDecorator obj = this;
        _worker = new Thread() {
            public void run() {
                boolean failed = false;

                try {
                    _responseCode = _con.getResponseCode();
                    if (isResponseOk()) {
                        InputStream is = _con.openInputStream();
                        long len = _con.getLength();
                        if (len == -1) {
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            int bite;
                            while ((bite = is.read()) != -1) {
                                bos.write(bite);
                            }
                            _res = bos.toByteArray();
                            bos.close();
                        }
                        else {
                            _res = new byte[(int)len];
                            is.read(_res, 0, (int)len);
                        }
                        is.close();
                    }
                    else {
                        failed = true;
                    }
                } catch (IOException ex) {
                    failed = true;
                }
                finally {
                    if (_con != null) {
                        try {
                            _con.close();
                        } catch (IOException ex) {
                            throw new NotExpectedException(ex.getMessage());
                        }
                    }
                }

                if (failed) {
                    onFail(obj);
                }
                else {
                    onComplete(obj);
                }
            }
        };
        _worker.start();
    }

    public void cancelRequest() {
        if (_worker != null && _worker.isAlive())
            _worker.interrupt();
    }

    public boolean isResponseOk() {
        return _responseCode == HttpConnection.HTTP_OK;
    }

    public byte[] getResponse() {
        return _res;
    }

    public void setCompleteListener(IConnectionListener listener) {
        _completeListener = listener;
    }

    public void setFailListener(IConnectionListener listener) {
        _failListener = listener;
    }

    public void onFail(IConnectionDecorator con) {
        bUse = false;
        if (_failListener != null) {
            _failListener.onFail(this);
        }
    }

    public void onComplete(IConnectionDecorator con) {
        bUse = false;
        if (_completeListener != null) {
            _completeListener.onComplete(this);
        }
    }
}
