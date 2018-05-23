package com.example.hafiz_saad.custome;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.viewpagerindicator.CirclePageIndicator;

public class ViewPagerImages extends AppCompatActivity {

    private ViewPager viewPager;
    private ShowImagesAdapter showImagesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_images);
        viewPager = (ViewPager) findViewById(R.id.view_images);
        Intent intent = getIntent();
        if(intent != null){
            showImagesAdapter = new ShowImagesAdapter(getApplicationContext(),intent.getStringArrayListExtra("Images"));
        }
        viewPager.setAdapter(showImagesAdapter);
        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.circle);
        circlePageIndicator.setViewPager(viewPager);



    }
}
