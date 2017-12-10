package de.codemakers.util;

import java.util.ArrayList;
import java.util.List;

/**
 * StringUtil
 *
 * @author Paul Hagedorn
 */
public class StringUtil {

    public static final String[] split(String toSplit, String delimiter) {
        if (toSplit == null) {
            return null;
        } else if (delimiter == null || !toSplit.contains(delimiter)) {
            return new String[]{toSplit};
        }
        final List<String> temp_list = new ArrayList<>();
        String temp = "";
        for (int i = 0; i < toSplit.length(); i++) {
            temp += toSplit.charAt(i);
            if (temp.endsWith(delimiter)) {
                temp_list.add(temp.substring(0, temp.length() - delimiter.length()));
                temp = "";
            }
        }
        temp_list.add(temp);
        return temp_list.toArray(new String[temp_list.size()]);
    }

}
