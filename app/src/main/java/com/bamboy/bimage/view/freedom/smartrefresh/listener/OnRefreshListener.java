package com.bamboy.bimage.view.freedom.smartrefresh.listener;

import androidx.annotation.NonNull;

import com.bamboy.bimage.view.freedom.smartrefresh.api.RefreshLayout;

/**
 * 刷新监听器
 * Created by SCWANG on 2017/5/26.
 */

public interface OnRefreshListener {
    void onRefresh(@NonNull RefreshLayout refreshLayout);
}
