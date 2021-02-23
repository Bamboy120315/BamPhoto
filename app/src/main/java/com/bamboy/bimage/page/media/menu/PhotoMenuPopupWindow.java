package com.bamboy.bimage.page.media.menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bamboy.bimage.R;
import com.bamboy.bimage.util.AnimationUtil;
import com.bamboy.bimage.util.BlurUtil;
import com.bamboy.bimage.util.UIUtil;

import java.util.HashMap;
import java.util.Map;

import static com.bamboy.bimage.page.media.menu.MenuLocationUtil.PARAM_BTN_HEIGHT;
import static com.bamboy.bimage.page.media.menu.MenuLocationUtil.PARAM_BTN_MARGIN;
import static com.bamboy.bimage.page.media.menu.MenuLocationUtil.PARAM_BTN_WIDTH;
import static com.bamboy.bimage.page.media.menu.MenuLocationUtil.PARAM_ITEMVIEW_HEIGHT;
import static com.bamboy.bimage.page.media.menu.MenuLocationUtil.PARAM_ITEMVIEW_WIDTH;
import static com.bamboy.bimage.page.media.menu.MenuLocationUtil.PARAM_ITEMVIEW_X;
import static com.bamboy.bimage.page.media.menu.MenuLocationUtil.PARAM_ITEMVIEW_Y;
import static com.bamboy.bimage.page.media.menu.MenuLocationUtil.PARAM_MARGIN_BOTTOM;
import static com.bamboy.bimage.page.media.menu.MenuLocationUtil.PARAM_MARGIN_TOP;
import static com.bamboy.bimage.view.clickanimview.BamAnim.ANIM_SPEED;

public class PhotoMenuPopupWindow extends PopupWindow {

    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 条目View
     */
    private View mItemView;
    /**
     * 是否是隐私条目
     */
    private boolean mIsPrivate;
    /**
     * 容器
     */
    private View rl_root;
    /**
     * 条目View展示图
     */
    private View cv_item_view;
    /**
     * 条目View展示图
     */
    private View v_item_view;
    /**
     * 重命名图标
     */
    private ImageView iv_rename;
    /**
     * 隐私图标
     */
    private ImageView iv_private;
    /**
     * 删除图标
     */
    private ImageView iv_delete;

    /**
     * 构造
     *
     * @param context   上下文
     * @param itemView  条目View
     * @param isPrivate 是否是隐私条目
     */
    public PhotoMenuPopupWindow(Context context, View itemView, boolean isPrivate) {
        mContext = context;
        mItemView = itemView;
        mIsPrivate = isPrivate;

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_menu_photo, null);
        rl_root = contentView.findViewById(R.id.rl_root);
        cv_item_view = contentView.findViewById(R.id.cv_item_view);
        v_item_view = contentView.findViewById(R.id.v_item_view);
        iv_rename = contentView.findViewById(R.id.iv_rename);
        iv_private = contentView.findViewById(R.id.iv_private);
        iv_delete = contentView.findViewById(R.id.iv_delete);

        setClippingEnabled(false);

        setContentView(contentView);

        rl_root.animate()
                .alpha(1)
                .setDuration(300);

        // 延迟设置坐标
        cv_item_view.animate()
                .alpha(0)
                .setDuration(ANIM_SPEED)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        // 设置坐标
                        cv_item_view.post(() -> initLocation());

                        // 点击事件
                        initListener();
                    }
                });

        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * 初始化坐标
     */
    private void initLocation() {
        // 条目的宽
        float itemViewWidth = mItemView.getWidth();
        // 条目的高
        float itemViewHeight = mItemView.getHeight();

        // 条目的坐标
        int[] itemViewLocation = new int[2];
        mItemView.getLocationOnScreen(itemViewLocation);

        // 处理坐标 --> 占位布局
        initLocationToItemView(itemViewWidth, itemViewHeight, itemViewLocation);
        // 处理坐标 --> 菜单按钮
        initLocationToMenu(itemViewWidth, itemViewHeight, itemViewLocation);

        cv_item_view.post(() -> showView());
    }

    /**
     * 处理坐标 --> 占位布局
     *
     * @param itemViewWidth
     * @param itemViewHeight
     * @param itemViewLocation
     */
    private void initLocationToItemView(float itemViewWidth, float itemViewHeight, int[] itemViewLocation) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) cv_item_view.getLayoutParams();
        params.width = (int) itemViewWidth;
        params.height = (int) itemViewHeight;
        params.leftMargin = itemViewLocation[0];
        params.topMargin = itemViewLocation[1];

        v_item_view.setBackground(
                BlurUtil.coverColor(
                        mContext,
                        UIUtil.getDrawing(mItemView),
                        0x00000000));
    }

    /**
     * 处理坐标 --> 菜单按钮
     *
     * @param itemViewWidth
     * @param itemViewHeight
     * @param itemViewLocation
     */
    private void initLocationToMenu(float itemViewWidth, float itemViewHeight, int[] itemViewLocation) {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        // 屏幕宽度
        int displayWidth = dm.widthPixels;
        // 屏幕高度
        int displayHeight = dm.heightPixels;

        int marginLeft = itemViewLocation[0];
        int marginTop = itemViewLocation[1];
        int marginRight = (int) (displayWidth - (itemViewLocation[0] + itemViewWidth));
        int marginBottom = (int) (displayHeight - (itemViewLocation[1] + itemViewHeight));

        float btnWidth = iv_rename.getWidth();
        float btnHeight = iv_rename.getHeight();
        float btnMargin = btnWidth * 0.25f;

        Map<String, Integer> locationParam;
        Map<String, Float> param = new HashMap<>();
        param.put(PARAM_ITEMVIEW_X, (float) itemViewLocation[0]);
        param.put(PARAM_ITEMVIEW_Y, (float) itemViewLocation[1]);
        param.put(PARAM_ITEMVIEW_WIDTH, itemViewWidth);
        param.put(PARAM_ITEMVIEW_HEIGHT, itemViewHeight);
        param.put(PARAM_BTN_WIDTH, btnWidth);
        param.put(PARAM_BTN_HEIGHT, btnHeight);
        param.put(PARAM_BTN_MARGIN, btnMargin);
        param.put(PARAM_MARGIN_TOP, (float) marginTop);
        param.put(PARAM_MARGIN_BOTTOM, (float) marginBottom);

        if (marginLeft > btnWidth && marginRight > btnWidth && Math.abs(marginLeft - marginRight) < displayWidth * 0.5) {
            // 上下模式

            if (itemViewLocation[1] > (displayHeight - itemViewLocation[1])) {
                // 上边
                locationParam = MenuLocationUtil.getInfoToTop(param);
            } else {
                // 下边
                locationParam = MenuLocationUtil.getInfoToBottom(param);
            }
        } else if (marginTop > btnWidth && marginBottom > btnWidth && Math.abs(itemViewLocation[1] - (displayHeight - itemViewLocation[1])) < displayHeight * 0.58) {
            // 左右模式

            if (marginLeft > marginRight) {
                // 左边
                locationParam = MenuLocationUtil.getInfoToLeft(param);
            } else {
                // 右边
                locationParam = MenuLocationUtil.getInfoToRight(param);
            }
        } else if (itemViewHeight > btnHeight * 4 && marginTop > 0 - btnHeight * 3 && (displayHeight - itemViewLocation[1]) > btnHeight * 4) {
            // 左右模式

            if (marginLeft > marginRight) {
                // 左边
                locationParam = MenuLocationUtil.getInfoToLeft(param);
            } else {
                // 右边
                locationParam = MenuLocationUtil.getInfoToRight(param);
            }
        } else if (marginLeft > marginRight) {
            if (marginTop > marginBottom) {
                // 四角模式  左上方
                locationParam = MenuLocationUtil.getInfoToLeftTop(param);
            } else {
                // 四角模式  左下方
                locationParam = MenuLocationUtil.getInfoToLeftBottom(param);
            }
        } else {
            if (marginTop > marginBottom) {
                // 四角模式  右上方
                locationParam = MenuLocationUtil.getInfoToRightTop(param);
            } else {
                // 四角模式  右下方
                locationParam = MenuLocationUtil.getInfoToRightBottom(param);
            }
        }

        // 更新 重命名按钮 坐标
        FrameLayout.LayoutParams paramsRename = (FrameLayout.LayoutParams) iv_rename.getLayoutParams();
        paramsRename.leftMargin = locationParam.get("renameLeft");
        paramsRename.topMargin = locationParam.get("renameTop");
        iv_rename.setLayoutParams(paramsRename);

        // 更新 隐私按钮 坐标
        FrameLayout.LayoutParams paramsPrivate = (FrameLayout.LayoutParams) iv_private.getLayoutParams();
        paramsPrivate.leftMargin = locationParam.get("privateLeft");
        paramsPrivate.topMargin = locationParam.get("privateTop");
        iv_private.setLayoutParams(paramsPrivate);

        // 更新 删除按钮 坐标
        FrameLayout.LayoutParams paramsDelete = (FrameLayout.LayoutParams) iv_delete.getLayoutParams();
        paramsDelete.leftMargin = locationParam.get("deleteLeft");
        paramsDelete.topMargin = locationParam.get("deleteTop");
        iv_delete.setLayoutParams(paramsDelete);
    }

    /**
     * 显示View
     */
    private void showView() {
        // 显示占位容器
        cv_item_view.animate()
                .alpha(1)
                .setDuration(130)
                .setListener(null);

        // 显示占位图
        AnimationUtil.getInstance().revealContent(v_item_view, true, 200);

        // 显示按钮
        showBtn(iv_rename, true, 0);
        showBtn(iv_private, true, 70);
        showBtn(iv_delete, true, 140);
    }

    /**
     * 显示按钮
     *
     * @param view  按钮View
     * @param delay 延迟时间
     */
    private void showBtn(View view, boolean isShow, int delay) {
        view.animate()
                .alpha(isShow ? 1 : 0.3f)
                .scaleX(isShow ? 1 : 0)
                .scaleY(isShow ? 1 : 0)
                .rotation(isShow ? 0 : -180)
                .setDuration((isShow ? 300 : 40) + delay)
                .setStartDelay((isShow ? 100 : 0) + delay)
                .setInterpolator(new OvershootInterpolator(1.3f));
    }

    /**
     * 点击事件
     */
    private void initListener() {
        rl_root.setOnClickListener((View view) -> dismiss());
    }

    /**
     * dismiss状态
     * 【0：正常显示】
     * 【1：正在dismiss，动画未结束】
     * 【2：动画结束，可以dismiss】
     * 【3：dismiss结束】
     */
    private int mDismissType = 0;

    @Override
    public void dismiss() {
        switch (mDismissType) {
            case 0:         // 正常显示
                mDismissType = 1;
                hide();
                break;

            case 2:         // 动画结束，可以dismiss
                super.dismiss();
                break;

            case 1:         // 正在dismiss，动画未结束
            case 3:         // dismiss结束
                break;
        }
    }

    /**
     * 隐藏界面
     */
    private void hide() {
        // 隐藏按钮
        showBtn(iv_rename, false, 140);
        showBtn(iv_private, false, 70);
        showBtn(iv_delete, false, 0);

        // 界面隐藏
        rl_root.animate()
                .alpha(0)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mDismissType = 2;
                        dismiss();
                    }
                });
    }
}
