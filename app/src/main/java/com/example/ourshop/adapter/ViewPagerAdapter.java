package com.example.ourshop.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ourshop.R;

import java.util.ArrayList;
import java.util.Objects;

public class ViewPagerAdapter extends PagerAdapter {

    //Context object
    Context context;

    //Array of images
    ArrayList<String> images;

    //Layout Inflater
    LayoutInflater mLayoutInflater;


    //Viewpager Constructor
    public ViewPagerAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        //return the number of images
        if(images != null){
            return images.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        //inflating the item.xml
        View itemView = mLayoutInflater.inflate(R.layout.item, container, false);
        Log.e("TAG IS ANYTHING","sampe disini");
        //referencing the image view from the item.xml file
        ImageView imageView = itemView.findViewById(R.id.imageViewMain);

        //get Image
        Glide.with(context)
                .load(images.get(position))
                .placeholder(R.drawable.image_uploading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        //Adding the View
        Objects.requireNonNull(container).addView(itemView);



        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout) object);
    }
}
