package com.example.util;

/**
 * Unicode
 */
public class UnicodeUtils {

    /*
     * web类型的unicode转string
     */
    public static String webUnicodeToString(String input) {
        String str = "";
        String[] array = input.split(";");
        for (String st : array) {
            int idx = st.lastIndexOf("&#");
            if (idx > -1) {
                String str1 = st.substring(0, idx);
                String str2 = st.substring(idx + 2);
                if (st.length() > 2) {
                    str = str + str1;
                    str = str + (char) Integer.parseInt(str2);
                }
            } else {
                str = str + st;
            }
        }
        return str;
    }
}

