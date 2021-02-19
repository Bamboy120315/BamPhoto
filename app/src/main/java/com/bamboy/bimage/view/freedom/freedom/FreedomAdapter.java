package com.bamboy.bimage.view.freedom.freedom;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.bamboy.bimage.util.NullUtil;

import java.util.List;

/**
 * RecyclerView的Adapter
 * <p/>
 * 由于使用插件式列表，
 * 在开发过程中，
 * 本类基本不需要改动
 * <p/>
 * Created by Bamboy on 2017/4/11.
 */
public class FreedomAdapter extends RecyclerView.Adapter<ViewHolderManager.ViewHolder> {

    private List<FreedomBean> mList;
    private Context mContext;

    /**
     * Adapter 构造
     *
     * @param list
     */
    public FreedomAdapter(Context context, List list) {
        mContext = context;
        mList = list;
    }

    @Override
    public ViewHolderManager.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return ViewHolderManager.createViewHolder(viewGroup, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolderManager.ViewHolder viewHolder, int position) {
        if (NullUtil.isNull(mList)) {
            return;
        }

        if (position >= mList.size()) {
            return;
        }

        FreedomBean bean = mList.get(position);
        if (bean.getViewHolderBindListener() == null) {
            bean.initBindView(mList);
        }
        bean.bindViewHolder(mContext, viewHolder, position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (NullUtil.isNull(mList)) {
            return 0;
        }

        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int defaultItem = 0;
        try {
            if (NullUtil.isNull(mList)) {
                return defaultItem;
            }

            if (position >= mList.size()) {
                return defaultItem;
            }

            return mList.get(position).getItemType();
        } catch (Exception e) {
            return defaultItem;
        } catch (Error e) {
            return defaultItem;
        }
    }

    public int getSpanSize(int spanCount, int position) {
        try {
            if (NullUtil.isNull(mList)) {
                return 1;
            }

            if (position >= mList.size()) {
                return 1;
            }

            return mList.get(position).getSpanSize(spanCount);
        } catch (Exception e) {
            return 1;
        } catch (Error e) {
            return 1;
        }
    }

}
