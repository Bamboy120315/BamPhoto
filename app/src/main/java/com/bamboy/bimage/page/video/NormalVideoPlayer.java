package com.bamboy.bimage.page.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.shuyu.gsyvideoplayer.video.NormalGSYVideoPlayer;

public class NormalVideoPlayer extends NormalGSYVideoPlayer {

    public NormalVideoPlayer(Context context) {
        super(context);
    }

    public NormalVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NormalVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    @Override
    protected void init(Context context) {
        super.init(context);

        View fullscreen = findViewById(com.shuyu.gsyvideoplayer.R.id.fullscreen);
        fullscreen.setVisibility(GONE);
    }
}
