package com.bamboy.bimage.page.photo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bamboy.bimage.R;
import com.bamboy.bimage.page.media.bean.FileBean;
import com.bm.library.PhotoView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class Photo2Adapter extends RecyclerView.Adapter<Photo2Adapter.PhotoViewHolder> {
    private Context mContext;
    private List<FileBean> mList;

    public Photo2Adapter(Context context, List<FileBean> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PhotoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        // 允许缩放
        holder.pv_photo.enable();
        holder.pv_photo.setImageDrawable(getDrawable(mList.get(position).getPath()));
    }

    /**
     * 将本地文件转换为 Drawable
     */
    private Drawable getDrawable(String file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        Drawable drawable = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            drawable = new BitmapDrawable(mContext.getResources(), bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return drawable;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {
        PhotoView pv_photo;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            pv_photo = itemView.findViewById(R.id.pv_photo);
        }
    }
}
