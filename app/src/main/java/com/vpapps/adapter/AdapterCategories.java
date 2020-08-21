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
import com.vpapps.item.ItemCat;
import com.vpapps.utils.Methods;

import java.util.ArrayList;


public class AdapterCategories extends RecyclerView.Adapter<AdapterCategories.MyViewHolder> {

    private ArrayList<ItemCat> arrayList;
    private Context context;
    private ArrayList<ItemCat> filteredArrayList;
    private NameFilter filter;
    private Methods methods;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_cat;
        RoundedImageView iv_cat;

        MyViewHolder(View view) {
            super(view);
            tv_cat = view.findViewById(R.id.tv_cat);
            iv_cat = view.findViewById(R.id.iv_cat);
        }
    }

    public AdapterCategories(Context context, ArrayList<ItemCat> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        filteredArrayList = arrayList;
        methods = new Methods(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.tv_cat.setTypeface(methods.getFontMedium());
        holder.tv_cat.setText(arrayList.get(position).getName());
        Picasso.get()
                .load(methods.getImageThumbSize(arrayList.get(holder.getAdapterPosition()).getImageThumb(), context.getString(R.string.categories)))
                .placeholder(R.drawable.placeholder_news)
                .into(holder.iv_cat);
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
                ArrayList<ItemCat> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getName();
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

            arrayList = (ArrayList<ItemCat>) results.values;
            notifyDataSetChanged();
        }
    }
}