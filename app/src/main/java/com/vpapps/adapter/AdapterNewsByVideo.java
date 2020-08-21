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

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class AdapterNewsByVideo extends RecyclerView.Adapter {

    private ArrayList<ItemNews> arrayList;
    private Context context;
    private ArrayList<ItemNews> filteredArrayList;
    private NameFilter filter;
    private DBHelper dbHelper;
    private Methods methods;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    public AdapterNewsByVideo(Context context, ArrayList<ItemNews> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        filteredArrayList = arrayList;
        methods = new Methods(context, interAdListener);
    }

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

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private static CircularProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_newsbyvideo, parent, false);
            return new MyViewHolder(itemView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progressbar, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).textView_heading.setTypeface(methods.getFontMedium());

            setPlay(((MyViewHolder) holder).imageView_play, position);
            ((MyViewHolder) holder).isFav = dbHelper.isFav(arrayList.get(position).getId());
            methods.setFavImage(((MyViewHolder) holder).isFav, ((MyViewHolder) holder).imageView_fav);

            ((MyViewHolder) holder).textView_cat.setText(arrayList.get(position).getCatName());
            ((MyViewHolder) holder).textView_heading.setText(arrayList.get(position).getHeading());


            ((MyViewHolder) holder).textView_date.setText(arrayList.get(position).getDate());

            Picasso.get()
                    .load(methods.getImageThumbSize(arrayList.get(holder.getAdapterPosition()).getImageThumb(), "header"))
                    .placeholder(R.drawable.placeholder_news)
                    .into(((MyViewHolder) holder).imageView);


            ((MyViewHolder) holder).imageView_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    didTapFav(((MyViewHolder) holder).imageView_fav);
                    if (((MyViewHolder) holder).isFav) {
                        ((MyViewHolder) holder).isFav = false;
                        dbHelper.removeFav(arrayList.get(holder.getAdapterPosition()).getId());
                    } else {
                        ((MyViewHolder) holder).isFav = true;
                        dbHelper.addFav(arrayList.get(holder.getAdapterPosition()));
                    }
                    methods.setFavImage(((MyViewHolder) holder).isFav, ((MyViewHolder) holder).imageView_fav);
                }
            });

            ((MyViewHolder) holder).rl_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    methods.showInterAd(holder.getAdapterPosition(), "");
                }
            });

            ((MyViewHolder) holder).imageView_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    methods.startVideoPlay(arrayList.get(holder.getAdapterPosition()).getVideoId());
                }
            });

            ((MyViewHolder) holder).iv_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    methods.shareNews(arrayList.get(holder.getAdapterPosition()));
                }
            });
        } else {
            if (getItemCount() == 1) {
                ProgressViewHolder.progressBar.setVisibility(View.GONE);
            }
        }
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
        return arrayList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return isProgressPos(position) ? VIEW_PROG : VIEW_ITEM;
    }

    private boolean isProgressPos(int position) {
        return position == arrayList.size();
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
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