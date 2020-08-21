package com.vpapps.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.vpapps.allinonenewsapp.R;
import com.vpapps.item.ItemNews;
import com.vpapps.utils.Methods;

import java.util.ArrayList;


public class AdapterTop extends RecyclerView.Adapter<AdapterTop.MyViewHolder> {

    private ArrayList<ItemNews> arrayList;
    public Methods methods;
    private ArrayList<ItemNews> filteredArrayList;
    private NameFilter filter;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_date, tv_cat;
        RoundedImageView imageView;

        MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.iv_top);
            tv_title = view.findViewById(R.id.tv_top_title);
            tv_date = view.findViewById(R.id.tv_top_date);
            tv_cat = view.findViewById(R.id.tv_top_cat);
        }
    }

    public AdapterTop(Context context, ArrayList<ItemNews> arrayList) {
        this.arrayList = arrayList;
        filteredArrayList = arrayList;
        methods = new Methods(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_topstories, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.tv_cat.setText(arrayList.get(position).getCatName());
        holder.tv_title.setText(arrayList.get(position).getHeading());
        holder.tv_date.setText(arrayList.get(position).getDate());
        Picasso.get()
                .load(methods.getImageThumbSize(arrayList.get(holder.getAdapterPosition()).getImageThumb(), "top"))
                .placeholder(R.drawable.placeholder_news)
                .into(holder.imageView);
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
        return lastPos();
    }

    private int lastPos() {
        return arrayList.size();
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
}