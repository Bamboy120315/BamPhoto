package com.bamboy.bimage.util;

import android.widget.TextView;

import java.util.List;
import java.util.Map;
import java.util.Queue;

public class NullUtil {

    /**
     * 判断字符串是否为空
     *
     * @param str 需要判断的字符串
     * @return true即为空；false不为空
     */
    public static boolean isNull(String str) {
        if (str == null
                || "".equals(str)
                || "null".equals(str)
                || "[null]".equals(str)
                || "{null}".equals(str)
                || "[]".equals(str)
                || "{}".equals(str)) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否为空
     *
     * @param sb 需要判断的字符串
     * @return true即为空；false不为空
     */
    public static boolean isNull(StringBuilder sb) {
        if (sb == null
                || sb.length() == 0
                || "".equals(sb)
                || "null".equals(sb)
                || "[null]".equals(sb)
                || "{null}".equals(sb)
                || "[]".equals(sb)
                || "{}".equals(sb)) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否为空
     *
     * @param charSequence 需要判断的字符串
     * @return true即为空；false不为空
     */
    public static boolean isNull(CharSequence charSequence) {
        if (charSequence == null
                || isNull(charSequence.toString())) {
            return true;
        }
        return false;
    }

    /**
     * 判断TextView或其内容是否为空
     *
     * @param tv 需要判断的TextView
     * @return true即为空；false不为空
     */
    public static boolean isNull(TextView tv) {
        if (tv == null
                || tv.getText() == null
                || isNull(tv.getText().toString())) {
            return true;
        }
        return false;
    }

    /**
     * 判断数组或其内容是否为空
     *
     * @param arr 需要判断的数组
     * @return true即为空；false不为空
     */
    public static boolean isNull(Object[] arr) {
        if (arr == null
                || arr.length == 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断list或其内容是否为空
     *
     * @param list 需要判断的list
     * @return true即为空；false不为空
     */
    public static boolean isNull(List list) {
        if (list == null
                || list.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断list或其内容是否为空
     *
     * @param list  需要判断的list
     * @param index 索引
     * @return true即为空；false不为空
     */
    public static boolean isNull(List list, int index) {
        if (isNull(list)
                || list.get(index) == null) {
            return true;
        }
        return false;
    }

    /**
     * 判断Map或其内容是否为空
     *
     * @param map 需要判断的Map
     * @return true即为空；false不为空
     */
    public static boolean isNull(Map map) {
        if (map == null
                || map.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断Map或其内容是否为空
     *
     * @param map 需要判断的Map
     * @param key Map的key
     * @return true即为空；false不为空
     */
    public static boolean isNull(Map map, Object key) {
        if (isNull(map)
                || key == null
                || map.get(key) == null) {
            return true;
        }
        return false;
    }

    /**
     * 判断Map或其内容是否为空
     *
     * @param queue 需要判断的Queue
     * @return true即为空；false不为空
     */
    public static boolean isNull(Queue queue) {
        if (queue == null
                || queue.size() == 0) {
            return true;
        }
        return false;
    }

}
