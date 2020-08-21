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


public class AdapterComments extends RecyclerView.Adapter<AdapterComments.MyViewHolder> {

    private ArrayList<ItemComment> arrayList;
    private Context context;
    private Methods methods;
    private ClickListener clickListener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView iv_dp;
        ImageView imageView_more;
        TextView textView_name, textView_comment, textView_date;

        MyViewHolder(View view) {
            super(view);
            iv_dp = view.findViewById(R.id.iv_comment_pro);
            textView_name = view.findViewById(R.id.tv_comment_name);
            textView_comment = view.findViewById(R.id.tv_comment);
            textView_date = view.findViewById(R.id.tv_comment_date);
            imageView_more = view.findViewById(R.id.iv_comment_more);
            textView_comment.setMaxLines(2);
        }
    }

    public AdapterComments(Context context, ArrayList<ItemComment> arrayList, ClickListener clickListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.clickListener = clickListener;
        methods = new Methods(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        Picasso.get().load(Constant.URL_IMAGE + arrayList.get(position).getDp()).placeholder(R.drawable.comment2).into(holder.iv_dp);
        holder.textView_name.setTypeface(methods.getFontMedium());
        holder.textView_name.setText(arrayList.get(position).getUserName());
        holder.textView_comment.setText(arrayList.get(position).getCommentText());
        holder.textView_date.setText(arrayList.get(position).getDate());

        if(Constant.itemUser.getId().equals(arrayList.get(holder.getAdapterPosition()).getUserId())) {
            holder.imageView_more.setVisibility(View.VISIBLE);
        } else {
            holder.imageView_more.setVisibility(View.GONE);
        }

        holder.imageView_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOptionPopUp(holder.imageView_more, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
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