package com.dongliang.verticalbannerdemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;


public class VerticalBannerActivity extends Activity {

    VerticalBanner banner;
    private List<String> bannerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_banner);
        banner=findViewById(R.id.vertical_banner);
        for (int i = 0; i < 10; i++) {
            bannerList.add("第" + i + "个广告");
        }

           banner.setOnItemClickListener(new VerticalBanner.ItemClickListener(){

               @Override
               public void onItemClick(int pos) {
                   Toast.makeText(VerticalBannerActivity.this,bannerList.get(pos),Toast.LENGTH_LONG);
               }
           });
            banner.setBannerAdapter(new VerticalBanner.BannerAdapter() {
                @Override
                public int getCount() {
                    return bannerList.size();
                }

                @NonNull
                @Override
                public View getView(int pos) {
                    TextView textView = new TextView(VerticalBannerActivity.this);
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER_VERTICAL);
                    textView.setLayoutParams(lp);
                    textView.setBackgroundColor(Color.YELLOW);
                    textView.setTextSize(30);
                    textView.setGravity(Gravity.CENTER);
                     textView.setText(bannerList.get(pos));
                    return textView;

                }
            });


    }

}
