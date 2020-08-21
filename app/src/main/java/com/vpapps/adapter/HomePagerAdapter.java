package com.vpapps.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.tiagosantos.enchantedviewpager.EnchantedViewPager;
import com.tiagosantos.enchantedviewpager.EnchantedViewPagerAdapter;
import com.vpapps.allinonenewsapp.NewsDetailsActivity;
import com.vpapps.allinonenewsapp.R;
import com.vpapps.interfaces.InterAdListener;
import com.vpapps.item.ItemNews;
import com.vpapps.utils.Constant;
import com.vpapps.utils.Methods;

import java.util.ArrayList;

public class HomePagerAdapter extends EnchantedViewPagerAdapter{

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<ItemNews> arrayList;
    private Methods methods;

    public HomePagerAdapter(Context context, ArrayList<ItemNews> arrayList) {
        super(arrayList);
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.arrayList = arrayList;
        methods = new Methods(context, interAdListener);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View mCurrentView = inflater.inflate(R.layout.layout_home_pager, container, false);

        RoundedImageView imageView = mCurrentView.findViewById(R.id.imageView_home_vp);
        TextView textView_title = mCurrentView.findViewById(R.id.textView_home_title);
        TextView textView_desc = mCurrentView.findViewById(R.id.textView_home_desc);

        textView_title.setTypeface(methods.getFontMedium());

        textView_title.setText(arrayList.get(position).getHeading());
        textView_desc.setText(Html.fromHtml(arrayList.get(position).getDesc()));
        Picasso.get()
                .load(methods.getImageThumbSize(arrayList.get(position).getImageThumb(), "banner"))
                .placeholder(R.drawable.placeholder_news)
                .into(imageView);

        mCurrentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showInterAd(position, "");
            }
        });

        mCurrentView.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);
        container.addView(mCurrentView);

        return mCurrentView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            Constant.selected_news_pos = position;
            Constant.itemNewsCurrent = arrayList.get(position);
            Intent intent = new Intent(mContext, NewsDetailsActivity.class);
            mContext.startActivity(intent);
        }
    };
}