package com.example.a8_miracle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder> {
    private Context context;
    private List<Book> favoriteBooks;
    private OnFavoriteRemovedListener listener;

    public FavAdapter(Context context, List<Book> favoriteBooks, OnFavoriteRemovedListener listener) {
        this.context = context;
        this.favoriteBooks = favoriteBooks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fav, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = favoriteBooks.get(position);
        holder.bookTitle.setText(book.getTitle());
        holder.bookAuthor.setText(book.getAuthor());


        Glide.with(context).load(book.getCoverImage()).into(holder.bookImage);

        //  الإزالة
        holder.removeFavoriteBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFavoriteRemoved(book.getBookID(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteBooks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage, removeFavoriteBtn;
        TextView bookTitle , bookAuthor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.bookImage);
            bookTitle = itemView.findViewById(R.id.bookTitle);
            bookAuthor = itemView.findViewById(R.id.bookAuthor);
            removeFavoriteBtn = itemView.findViewById(R.id.removeFavoriteBtn);
        }
    }

    public interface OnFavoriteRemovedListener {
        void onFavoriteRemoved(int bookId, int position);
    }
}

