package com.bamboy.bimage.page.base;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bamboy.bimage.page.main.MainActivity;
import com.bamboy.bimage.util.NullUtil;
import com.bamboy.bimage.view.toast.BamToast;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!(this instanceof MainActivity)){
            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null){
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示Toast
     *
     * @param text
     */
    protected void showToast(String text) {
        if (NullUtil.isNull(text)) {
            return;
        }
        BamToast.show(this, text);
    }
}
