package com.vpapps.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.vpapps.allinonenewsapp.R;
import com.vpapps.interfaces.ClickListener;
import com.vpapps.item.ItemComment;
import com.vpapps.utils.Constant;
import com.vpapps.utils.Methods;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class AdapterCommentEndless extends RecyclerView.Adapter {

    private ArrayList<ItemComment> arrayList;
    public Context context;
    private Methods methods;
    private ClickListener clickListener;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    public AdapterCommentEndless(Context context, ArrayList<ItemComment> arrayList, ClickListener clickListener) {
        this.arrayList = arrayList;
        this.clickListener = clickListener;
        this.context = context;
        methods = new Methods(context);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView iv_dp;
        ImageView imageView_more;
        TextView textView_name, textView_comment, textView_date;
        View vieww;

        MyViewHolder(View view) {
            super(view);
            iv_dp = view.findViewById(R.id.iv_comment_pro);
            textView_name = view.findViewById(R.id.tv_comment_name);
            textView_comment = view.findViewById(R.id.tv_comment);
            textView_date = view.findViewById(R.id.tv_comment_date);
            imageView_more = view.findViewById(R.id.iv_comment_more);
            vieww = view.findViewById(R.id.view);
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
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment, parent, false);
            return new MyViewHolder(itemView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progressbar, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyViewHolder) {
            if(Constant.itemUser.getId().equals(arrayList.get(holder.getAdapterPosition()).getUserId())) {
                ((MyViewHolder) holder).imageView_more.setVisibility(View.VISIBLE);
            } else {
                ((MyViewHolder) holder).imageView_more.setVisibility(View.GONE);
            }

            Picasso.get().load(Constant.URL_IMAGE + arrayList.get(position).getDp()).placeholder(R.drawable.comment2).into(((MyViewHolder) holder).iv_dp);
            ((MyViewHolder) holder).textView_name.setTypeface(methods.getFontMedium());
            ((MyViewHolder) holder).textView_name.setText(arrayList.get(position).getUserName());
            ((MyViewHolder) holder).textView_comment.setText(arrayList.get(position).getCommentText());
            ((MyViewHolder) holder).textView_date.setText(arrayList.get(position).getDate());
            if (position == arrayList.size() - 1) {
                ((MyViewHolder) holder).vieww.setVisibility(View.GONE);
            } else {
                ((MyViewHolder) holder).vieww.setVisibility(View.VISIBLE);
            }

            ((MyViewHolder) holder).imageView_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openOptionPopUp(((MyViewHolder) holder).imageView_more, holder.getAdapterPosition());
                }
            });

        } else {
            if (getItemCount() == 1) {
                ProgressViewHolder.progressBar.setVisibility(View.GONE);
            }
        }
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

    public boolean isHeader(int position) {
        return isProgressPos(position);
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }



    private void openOptionPopUp(ImageView imageView, final int pos) {
        PopupMenu popup = new PopupMenu(context, imageView);
        popup.getMenuInflater().inflate(R.menu.popup_delete, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.popup_delete:
                        clickListener.onClick(pos);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }
}