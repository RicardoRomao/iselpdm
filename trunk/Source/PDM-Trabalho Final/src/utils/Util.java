package utils;

import java.util.Vector;

public class Util {

    public final static String[] splitString(String str, char sep) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return null;
        }
        Vector list = new Vector();
        int i = 0;
        int start = 0;
        boolean match = false;
        while (i < len) {
            if (str.charAt(i) == sep) {
                if (match) {
                    list.addElement(str.substring(start, i).trim());
                    match = false;
                }
                start = ++i;
                continue;
            }
            match = true;
            i++;
        }
        if (match) {
            list.addElement(str.substring(start, i).trim());
        }
        String[] arr = new String[list.size()];
        list.copyInto(arr);
        return arr;
    }
}
