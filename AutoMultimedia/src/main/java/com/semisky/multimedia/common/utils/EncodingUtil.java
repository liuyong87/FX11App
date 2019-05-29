package com.semisky.multimedia.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 乱码解决工具类
 * @author liuyong
 *
 */
public class EncodingUtil {
    private static final String TAG = EncodingUtil.class.getSimpleName();
    private static boolean debug = false;

    public static String getEncoding(String str) {
        String[] encodes = {"GB2312", "ISO-8859-1", "GB18030", "UTF-8", "GBK", "UTF-16BE", "UTF-16LE"};
        for (String encode : encodes) {
            try {
                if (str.equals(new String(str.getBytes(encode), encode))) {
                    return encode;
                }
            } catch (Exception exception) {
            }
        }
        return null;
    }

    public static String getEncodeString(String source, String encode) {
        if (encode == null) {
            encode = "GB2312";
        }

        String old_encode = getEncoding(source);
        if (debug)
            Logutil.d(TAG, "old_encode: " + old_encode);
        if (encode.equals(old_encode) || "GB18030".equals(old_encode)) {
            if (debug)
            	Logutil.d(TAG, "return source: " + source);
            return source;
        }
        try {
            return new String(source.getBytes(old_encode), encode);
        } catch (Exception e) {
            return source;
        }
    }

    /**
     *
     * 是否是中文
     * @param txt
     * @return
     */
    public static boolean isChinese(String txt) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        for (int i = 0; i < txt.length(); i++) {
            String ts = txt.charAt(i) + "";
            Matcher m = p.matcher(ts);
            if (!m.matches()) {
                return false;
            }
        }
        return true;
    }

    /**
     *字符只能包含是字母，数字
     */
    public static boolean isLetterAndNumber(String txt) {
        final String REGEX = "^[0-9a-zA-Z]$";
        if (Pattern.matches(REGEX, txt)) {
            return true;
        }
        return false;
    }
    public static boolean isNormalText(String string){
        final String REGEX = "^[\\u4e00-\\u9fa5_().a-zA-Z0-9]+$";
        if (Pattern.matches(REGEX, string)) {
            return true;
        }
        return false;
    }


}
