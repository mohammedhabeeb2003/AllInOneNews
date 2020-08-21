package com.vpapps.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.davidecirillo.multichoicerecyclerview.MultiChoiceAdapter;
import com.vpapps.allinonenewsapp.R;
import com.vpapps.item.ItemCat;
import com.vpapps.utils.Methods;

import java.util.ArrayList;


public class AdapterSelectCategories extends MultiChoiceAdapter<AdapterSelectCategories.MyViewHolder> {

    private ArrayList<ItemCat> arrayList;
    private ArrayList<ItemCat> filteredArrayList;
    private NameFilter filter;
    private Methods methods;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_catname;

        MyViewHolder(View view) {
            super(view);
            textView_catname = view.findViewById(R.id.textView_select_cat);
        }
    }

    public AdapterSelectCategories(Context context, ArrayList<ItemCat> arrayList) {
        this.arrayList = arrayList;
        filteredArrayList = arrayList;
        methods = new Methods(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_select_cat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        holder.textView_catname.setTypeface(methods.getFontMedium());
        holder.textView_catname.setText(arrayList.get(position).getName());
    }

    @Override
    protected View.OnClickListener defaultItemViewClickListener(final MyViewHolder holder, int position) {

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getSelectedItemList().contains(holder.getAdapterPosition())) {
                    deselect(holder.getAdapterPosition());
                } else {
                    select(holder.getAdapterPosition());
                }
            }
        };
    }

    @Override
    public void setActive(@NonNull View view, boolean state) {

        TextView textView = view.findViewById(R.id.textView_select_cat);

        if(state) {
            textView.setBackgroundResource(R.drawable.bg_cat_select);
        } else {
            textView.setBackgroundResource(R.drawable.bg_cat_unselect);
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