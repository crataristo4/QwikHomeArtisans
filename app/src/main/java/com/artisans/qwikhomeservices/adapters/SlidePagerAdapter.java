package com.artisans.qwikhomeservices.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.databinding.LayoutSlidePageItemsBinding;

public class SlidePagerAdapter extends PagerAdapter {

    //Array of type String to display the descriptions that moves on the screen
    public final String[] slideDescriptions = {"Dummy Txt1", "Dummy Txt2", "Dummy Txt3", "Dummy Txt4"};
    //Array of type String to display the heading that moves on the screen
    private final String[] slideHeadings = {"Heading 1", "Heading 2", "Heading 3", "Heading 4"};
    //object of the Context class to allow views to be passed into another activity
    private final Context context;
    //Array of type int to display the descriptions that moves on the screen
    private final int[] slideImages = {R.drawable.a, R.drawable.aa, R.drawable.aaa, R.drawable.aaaa};

    //Constructor to initialize objects
    public SlidePagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return slideImages.length;
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.layout_slide_page_items, container, false);

        LayoutSlidePageItemsBinding slidePageItemsBinding = DataBindingUtil.bind(view);

        assert slidePageItemsBinding != null;
        slidePageItemsBinding.slideImage.setImageResource(slideImages[position]);
        slidePageItemsBinding.txtHeading.setText(slideHeadings[position]);
        slidePageItemsBinding.txtDescription.setText(slideDescriptions[position]);

        container.addView(slidePageItemsBinding.getRoot());


        return slidePageItemsBinding.getRoot();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);

    }

}