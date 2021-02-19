package com.bamboy.bimage.view.freedom.freedom;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bamboy.bimage.util.NullUtil;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * ViewHolder的管理类
 * 用于存放ViewHolder的基类
 * 和 条目类型与相对应的Bean类型
 * <p/>
 * Created by Bamboy on 2017/4/13.
 */
public class ViewHolderManager {

    /**
     * 条目类型 和 对应的条目XML
     */
    private static Map<String, Class> itemMap = new HashMap<>();
    /**
     * 条目类型 和 对应的条目XML
     */
    private static Map<String, Integer> itemTypeMap = new HashMap<>();

    /**
     * 添加单个条目
     *
     * @param key
     * @param value
     */
    public static void addItem(String key, Class value) {
        if (NullUtil.isNull(key) || value == null || itemMap.get(key) != null) {
            return;
        }
        itemMap.put(key, value);
        itemTypeMap.put(key, itemMap.size());
    }

    /**
     * 获取条目类型
     *
     * @param key
     * @return
     */
    public static int getItemType(String key) {
        if (NullUtil.isNull(key) || !itemTypeMap.containsKey(key)) {
            return 0;
        }
        return itemTypeMap.get(key);
    }

    /**
     * 获取条目Class
     *
     * @param key
     * @return
     */
    public static Class getItem(String key) {
        if (NullUtil.isNull(key) || itemMap.get(key) == null) {
            return null;
        }
        return itemMap.get(key);
    }

    /**
     * 获取条目Class
     *
     * @param itemType
     * @return
     */
    public static Class getItem(int itemType) {
        String key = "";
        for (String item : itemTypeMap.keySet()) {
            if (itemTypeMap.get(item) == itemType) {
                key = item;
                break;
            }
        }
        return itemMap.get(key);
    }

    /**
     * ViewHolder基类
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements Serializable {

        public ViewHolder(ViewGroup viewGroup, int layoutId) {
            super(findView(viewGroup, layoutId));
        }
    }

    /**
     * 创建ViewHolder
     *
     * @param viewGroup
     * @param viewType  当前item的类型
     * @return
     */
    public static ViewHolder createViewHolder(ViewGroup viewGroup, int viewType) {
        try {
            // 获取Class对象
            Class itemClass = getItem(viewType);
            // 开始反射，获取构造方法
            Constructor<?> cons[] = itemClass.getConstructors();
            if (cons == null || cons.length == 0) {
                return null;
            }
            // 运行方法，获取ViewHolder
            Object obj = cons[0].newInstance(viewGroup);

            return (ViewHolder) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取View
     * <p/>
     * Ps：简化createViewHolder里的代码
     *
     * @param viewGroup
     * @param itemId
     * @return
     */
    private static View findView(ViewGroup viewGroup, int itemId) {
        return LayoutInflater.from(viewGroup.getContext())
                .inflate(itemId, viewGroup, false);
    }
}
