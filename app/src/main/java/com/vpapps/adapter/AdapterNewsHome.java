package com.vpapps.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.vpapps.allinonenewsapp.NewsDetailsActivity;
import com.vpapps.allinonenewsapp.R;
import com.vpapps.interfaces.InterAdListener;
import com.vpapps.item.ItemNews;
import com.vpapps.utils.Constant;
import com.vpapps.utils.DBHelper;
import com.vpapps.utils.Methods;
import com.vpapps.utils.MyBounceInterpolator;

import java.util.ArrayList;


public class AdapterNewsHome extends RecyclerView.Adapter<AdapterNewsHome.MyViewHolder> {

    private ArrayList<ItemNews> arrayList;
    private Context context;
    private ArrayList<ItemNews> filteredArrayList;
    private NameFilter filter;
    private DBHelper dbHelper;
    private Methods methods;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_heading, textView_date, textView_cat;
        ImageView imageView_fav, imageView_play;
        RelativeLayout rl_main;
        RoundedImageView imageView, iv_share;
        Boolean isFav;

        MyViewHolder(View view) {
            super(view);
            rl_main = view.findViewById(R.id.rl_news);
            textView_heading = view.findViewById(R.id.tv_home_news_title);
            textView_date = view.findViewById(R.id.tv_home_news_date);
            textView_cat = view.findViewById(R.id.tv_home_news_cat);
            imageView = view.findViewById(R.id.imageView_home_latest);
            imageView_fav = view.findViewById(R.id.iv_home_news_fav);
            imageView_play = view.findViewById(R.id.iv_home_news_play);
            iv_share = view.findViewById(R.id.iv_home_news_share);
            dbHelper = new DBHelper(context);
        }
    }

    public AdapterNewsHome(Context context, ArrayList<ItemNews> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        filteredArrayList = arrayList;
        methods = new Methods(context, interAdListener);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_news_home, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.textView_heading.setTypeface(methods.getFontMedium());

        setPlay(holder.imageView_play, position);
        holder.isFav = dbHelper.isFav(arrayList.get(position).getId());
        methods.setFavImage(holder.isFav, holder.imageView_fav);

        holder.textView_cat.setText(arrayList.get(position).getCatName());
        holder.textView_heading.setText(arrayList.get(position).getHeading());
        holder.textView_date.setText(arrayList.get(position).getDate());

        Picasso.get()
                .load(methods.getImageThumbSize(arrayList.get(holder.getAdapterPosition()).getImageThumb(), "home"))
                .placeholder(R.drawable.placeholder_news)
                .into(holder.imageView);


        holder.imageView_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                didTapFav(holder.imageView_fav);
                if (holder.isFav) {
                    holder.isFav = false;
                    dbHelper.removeFav(arrayList.get(holder.getAdapterPosition()).getId());
                } else {
                    holder.isFav = true;
                    dbHelper.addFav(arrayList.get(holder.getAdapterPosition()));
                }
                methods.setFavImage(holder.isFav, holder.imageView_fav);
            }
        });

        holder.rl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.showInterAd(holder.getAdapterPosition(), "");
            }
        });

        holder.imageView_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.startVideoPlay(arrayList.get(holder.getAdapterPosition()).getVideoId());
            }
        });

        holder.iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.shareNews(arrayList.get(holder.getAdapterPosition()));
            }
        });
    }

    private void didTapFav(ImageView imageView) {
        final Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.bubble);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.12, 40);
        myAnim.setInterpolator(interpolator);
        imageView.startAnimation(myAnim);
    }

    private void setPlay(ImageView imageView, int pos) {
        if (arrayList.get(pos).getType().equals("video")) {
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    public String getId(int pos) {
        return arrayList.get(pos).getId();
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public boolean isHeader(int position) {
        return (position + 3) % 3 == 0;
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new NameFilter();
        }
        return filter;
    }

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<ItemNews> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getHeading();
                    if (nameList.toLowerCase().contains(constraint))
                        filteredItems.add(filteredArrayList.get(i));
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = filteredArrayList;
                    result.count = filteredArrayList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            arrayList = (ArrayList<ItemNews>) results.values;
            notifyDataSetChanged();
        }
    }

    private InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            Constant.selected_news_pos = position;
            Constant.itemNewsCurrent = arrayList.get(position);
            Intent intent = new Intent(context, NewsDetailsActivity.class);
            context.startActivity(intent);
        }
    };
}