package com.example.a8_miracle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> cartItems;
    private OnCartItemListener listener;

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartItemListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bookName.setText(item.getBookName());
        holder.bookPrice.setText(item.getPrice() + "dz");
        holder.quantity.setText(String.valueOf(item.getQuantity()));

        Glide.with(context).load(item.getImageUrl()).into(holder.bookImage);


        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClicked(item));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage, btnDelete;
        TextView bookName, bookPrice, quantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.book_image);
            bookName = itemView.findViewById(R.id.book_name);
            bookPrice = itemView.findViewById(R.id.book_price);
            quantity = itemView.findViewById(R.id.book_quant);
            btnDelete = itemView.findViewById(R.id.delete_item);
        }
    }

    public interface OnCartItemListener {
        void onDeleteClicked(CartItem item);
    }
}


