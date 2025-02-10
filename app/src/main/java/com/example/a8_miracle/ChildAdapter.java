package com.example.a8_miracle;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder> {
    List<ChildMC> childMCList;
    Context context;
    private OnItemClickListener listener;

    public ChildAdapter(Context context, List<ChildMC> childMCList) {
        this.context = context;
        this.childMCList = childMCList;
    }

    public interface OnItemClickListener {
        void onItemClick(ChildMC childMC);
    }

    // Set click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.child_rv, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChildMC book = childMCList.get(position);
        // Ensure the URL is an absolute URL
        Glide.with(context).load(book.getCoverImage()).into(holder.iv_child);
        holder.Title.setText(book.getTitle());
        holder.Price.setText(String.format("%.2f", book.getPrice())); // Ensure price is a double or float
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(book);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return childMCList.size();
    }

    public void childMCList(List<ChildMC> books) {
        this.childMCList.clear();
        this.childMCList.addAll(books);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_child;
        TextView Title;
        TextView Price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_child = itemView.findViewById(R.id.iv_ch);
            Title = itemView.findViewById(R.id.Title);
            Price = itemView.findViewById(R.id.Price);
        }
    }
}