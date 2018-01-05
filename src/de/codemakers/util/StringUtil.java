package de.codemakers.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * StringUtil
 *
 * @author Paul Hagedorn
 */
public class StringUtil {

    public static final int count(String text, String toCount) {
        return count(text, toCount, 0);
    }

    public static final int count(String text, String toCount, int start_index) {
        if (!text.contains(toCount)) {
            return 0;
        } else if (start_index > 0 && !text.substring(start_index).contains(toCount)) {
            return 0;
        }
        int count = 0;
        int index = start_index;
        while ((index = text.indexOf(toCount, index)) != -1) {
            count++;
            index += toCount.length();
        }
        return count;
    }

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

    public static final boolean stringEquals(String text, String[] toTest) {
        for (String g : toTest) {
            if (text.equals(g)) {
                return true;
            }
        }
        return false;
    }

    public static final boolean stringEqualsIgnoreCase(String text, String[] toTest) {
        for (String g : toTest) {
            if (text.equalsIgnoreCase(g)) {
                return true;
            }
        }
        return false;
    }

    public static final boolean stringContains(String text, String[] toTest) {
        for (String g : toTest) {
            if (text.contains(g)) {
                return true;
            }
        }
        return false;
    }

    public static final <T> T[] concatArrays(T[]... arrays) {
        if (arrays == null || arrays.length == 0) {
            return null;
        }
        return (T[]) Arrays.asList(arrays).toArray();
    }

    public static final String stringToLettersAndDigitsOnly(String text) {
        return stringToLettersAndDigitsOnly(text, false);
    }

    public static final String stringToLettersAndDigitsOnly(String text, boolean invert, Character... exceptions) {
        if (text == null) {
            return null;
        }
        try {
            String out = "";
            for (int i = 0; i < text.length(); i++) {
                if (Character.isLetterOrDigit(text.charAt(i)) == !invert || ArrayUtil.contains(exceptions, text.charAt(i))) {
                    out += text.charAt(i);
                }
            }
            return out;
        } catch (Exception ex) {
            return null;
        }
    }

    public static final boolean isStringLettersAndDigitsOnly(String text) {
        if (text == null) {
            return false;
        }
        if (text.isEmpty()) {
            return true;
        }
        return text.equals(stringToLettersAndDigitsOnly(text));
    }

    public static final String stringToLettersOnly(String text) {
        return stringToLettersOnly(text, false);
    }

    public static final String stringToLettersOnly(String text, boolean invert, Character... exceptions) {
        if (text == null) {
            return null;
        }
        try {
            String out = "";
            for (int i = 0; i < text.length(); i++) {
                if (Character.isLetter(text.charAt(i)) == !invert || ArrayUtil.contains(exceptions, text.charAt(i))) {
                    out += text.charAt(i);
                }
            }
            return out;
        } catch (Exception ex) {
            return null;
        }
    }

    public static final boolean isStringLettersOnly(String text) {
        if (text == null) {
            return false;
        }
        if (text.isEmpty()) {
            return true;
        }
        return text.equals(stringToLettersOnly(text));
    }

    public static final String stringToDigitsOnly(String text) {
        return stringToDigitsOnly(text, false);
    }

    public static final String stringToDigitsOnly(String text, boolean invert, Character... exceptions) {
        if (text == null) {
            return null;
        }
        try {
            String out = "";
            for (int i = 0; i < text.length(); i++) {
                if (Character.isDigit(text.charAt(i)) == !invert || ArrayUtil.contains(exceptions, text.charAt(i))) {
                    out += text.charAt(i);
                }
            }
            return out;
        } catch (Exception ex) {
            return null;
        }
    }

    public static final boolean isStringDigitsOnly(String text) {
        if (text == null) {
            return false;
        }
        if (text.isEmpty()) {
            return true;
        }
        return text.equals(stringToDigitsOnly(text));
    }

    public static final boolean isEmpty(String text) {
        return text.isEmpty();
    }

    public static final boolean notEmpty(String text) {
        return !text.isEmpty();
    }

}
