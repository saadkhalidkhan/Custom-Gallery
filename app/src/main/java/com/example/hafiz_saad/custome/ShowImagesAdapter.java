package com.example.hafiz_saad.custome;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class ShowImagesAdapter extends PagerAdapter{
    private Context context;
    private ArrayList<String> imageUrls;
    private LayoutInflater layoutInflater;
    private ImageView imageView;
    public ShowImagesAdapter(Context applicationContext, ArrayList<String> images) {
        this.context = applicationContext;
        this.imageUrls = images;
        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View images = layoutInflater.inflate(R.layout.showimages,container,false);
        imageView = (ImageView) images.findViewById(R.id.images);
        Glide.with(context)
                .load("file://"+imageUrls.get(position))
                .centerCrop()
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_launcher)
                .into(imageView);
        container.addView(images);
        return images;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
}
