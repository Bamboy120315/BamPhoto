package com.bamboy.bimage.page.photo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bamboy.bimage.R;
import com.bamboy.bimage.page.media.bean.FileBean;
import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLf;
    private Map<Integer, View> mViewMap;
    private List<FileBean> mDataList;

    public PhotoAdapter(Context context, LayoutInflater lf, List<FileBean> list) {
        mContext = context;
        mLf = lf;
        mDataList = list;//构造方法，参数是我们的页卡，这样比较方便。
        mViewMap = new HashMap<>();
    }

    //直接继承PagerAdapter，至少必须重写下面的四个方法，否则会报错
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = mViewMap.get(position);
        if (view != null)
            container.removeView(mViewMap.get(position));//删除页卡
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = mViewMap.get(position);
        if (view == null) {
            view = mLf.inflate(R.layout.item_photo, null);
            mViewMap.put(position, view);
        }

        PhotoView pv_photo = view.findViewById(R.id.pv_photo);

        // 允许缩放
        pv_photo.enable();
        pv_photo.setMaxScale(5);
        Glide.with(mContext).load(mDataList.get(position).getPath()).into(pv_photo);

        //这个方法用来实例化页卡
        container.addView(view, 0);//添加页卡

        return view;
    }

    @Override
    public int getCount() {
        return mDataList.size();//返回页卡的数量
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;//官方提示这样写
    }

}
