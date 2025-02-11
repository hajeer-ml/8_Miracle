package com.example.a8_miracle;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends Fragment implements BookAdapter.OnItemClickListener {
    private RequestQueue requestQueue;
    private LinearLayout parentLayout;  // Parent layout for dynamic views
    private Map<Integer, BookAdapter> adapterMap = new HashMap<>();
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();  // Initialize context
        parentLayout = view.findViewById(R.id.parent_layout);  // Parent layout in XML
        requestQueue = Volley.newRequestQueue(context);
        fetchCategories();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.stop(); // Stop the request queue to release resources
        }
    }

    private void fetchCategories() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "https://8miracle.serv00.net/Home/get_categories.php", null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("MainActivity", "Categories Response: " + response.toString());

                        if (response.length() == 0) {
                            Log.w("MainActivity", "No categories found.");
                            return;
                        }

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                int categoryID = jsonObject.getInt("CategoryID");
                                String catName = jsonObject.getString("CatName");

                                // Create a TextView for the category name
                                TextView categoryTitle = new TextView(context);
                                categoryTitle.setText(catName);
                                categoryTitle.setTextSize(18);
                                categoryTitle.setPadding(20, 40, 20, 10);

                                // Create a RecyclerView for books under this category
                                RecyclerView recyclerView = new RecyclerView(context);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                recyclerView.setLayoutParams(layoutParams);

                                // Set horizontal scrolling
                                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

                                // Add TextView and RecyclerView to the parent layout
                                parentLayout.addView(categoryTitle);
                                parentLayout.addView(recyclerView);

                                // Set up adapter and fetch books
                                BookAdapter adapter = new BookAdapter(context, new ArrayList<>());
                                adapter.setOnItemClickListener(Home.this);
                                adapterMap.put(categoryID, adapter);
                                recyclerView.setAdapter(adapter);

                                fetchBooks(categoryID);
                            } catch (Exception e) {
                                Log.e("MainActivity", "Error fetching categories: " + e.getMessage());
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MainActivity", "Error fetching categories: " + error.getMessage());
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private void fetchBooks(int categoryID) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "https://8miracle.serv00.net/Home/get_books.php?category_id=" + categoryID, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("MainActivity", "Books Response: " + response.toString());

                        List<Book> books = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                int bookID = jsonObject.getInt("BookID");
                                String title = jsonObject.getString("Title");
                                String author = jsonObject.getString("Author");
                                double price = jsonObject.getDouble("Price");
                                float rating = (float) jsonObject.getDouble("Rating");
                                int stockQuantity = jsonObject.getInt("StockQuantity");
                                String description = jsonObject.getString("Description");
                                String coverImage = jsonObject.getString("CoverImage");
                                String publishedDate = jsonObject.getString("PublishedDate");
                                int categoryID = jsonObject.getInt("CategoryID");

                                Book book = new Book(bookID, title, author, price, rating, stockQuantity, description, coverImage, publishedDate, categoryID);
                                books.add(book);
                            } catch (Exception e) {
                                Log.e("MainActivity", "Error parsing book: " + e.getMessage());
                            }
                        }

                        if (adapterMap.containsKey(categoryID)) {
                            adapterMap.get(categoryID).setBookList(books);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MainActivity", "Error fetching books: " + error.getMessage());
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public void onItemClick(Book book) {
        Intent intent = new Intent(context, BookDetails.class);
        intent.putExtra("book", book);
        startActivity(intent);
    }
}