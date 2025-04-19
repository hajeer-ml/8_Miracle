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

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private Context context;
    private List<Book> bookList;
    private OnItemClickListener listener;

    // Constructor
    public BookAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = new ArrayList<>(bookList);
    }

    // Interface for click listener
    public interface OnItemClickListener {
        void onItemClick(Book book);
    }

    // Set click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Set book list
    public void setBookList(List<Book> books) {
        this.bookList.clear();
        this.bookList.addAll(books);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.child_rv, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.bookTitle.setText(book.getTitle());
//        holder.bookAuthor.setText(book.getAuthor());
        holder.bookPrice.setText(String.format("%.2f DZD", book.getPrice()));

        // Ensure the URL is an absolute URL
        Glide.with(context).load(book.getCoverImage()).into(holder.bookCover);

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
        return bookList.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView bookCover;
        TextView bookTitle;
        TextView bookAuthor;
        TextView bookPrice;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCover = itemView.findViewById(R.id.iv_ch);
            bookTitle = itemView.findViewById(R.id.Title);
//            bookAuthor = itemView.findViewById(R.id.book_author);
            bookPrice = itemView.findViewById(R.id.Price);
        }
    }
}