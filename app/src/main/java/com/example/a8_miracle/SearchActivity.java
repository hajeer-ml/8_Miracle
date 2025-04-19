package com.example.a8_miracle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
public class SearchActivity extends AppCompatActivity {
    private EditText searchEditText;
    private RecyclerView searchRecyclerView;
    private BookAdapter bookAdapter;
    private RequestQueue requestQueue;
    private List<Book> bookList = new ArrayList<>();
    private String selectedCategory = "", selectedLanguage = "";
    private double minPrice = 0, maxPrice = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEditText = findViewById(R.id.searchEditText);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        ImageView filterButton = findViewById(R.id.filterButton);

        requestQueue = Volley.newRequestQueue(this);
        bookAdapter = new BookAdapter(this, new ArrayList<>());
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(bookAdapter);


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchBooks(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });


        filterButton.setOnClickListener(v -> {
            FilterBottomSheet filterBottomSheet = new FilterBottomSheet();
            filterBottomSheet.show(getSupportFragmentManager(), filterBottomSheet.getTag());
        });


        bookAdapter.setOnItemClickListener(book -> {
            Intent intent = new Intent(SearchActivity.this, BookDetails.class);
            intent.putExtra("book", book);
            startActivity(intent);
        });
    }

    public void applyFilters(String category, double min, double max) {
        selectedCategory = category;
        minPrice = min;
        maxPrice = max;
        fetchBooks(searchEditText.getText().toString());
    }

    private void fetchBooks(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String url = "https://8miracle.serv00.net/Home/search_books.php?query=" + encodedQuery
                    + "&category=" + (selectedCategory.isEmpty() ? "0" : selectedCategory)
                    + "&minPrice=" + minPrice + "&maxPrice=" + maxPrice;

            Log.d("SearchActivity", "Fetching books with URL: " + url);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    response -> {
                        List<Book> fetchedBooks = new ArrayList<>();

                        if (response.length() == 0) {
                            Toast.makeText(this, "No matching results found", Toast.LENGTH_SHORT).show();
                        }

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Book book = new Book(
                                        jsonObject.getInt("BookID"),
                                        jsonObject.getString("Title"),
                                        jsonObject.getString("Author"),
                                        jsonObject.getDouble("Price"),
                                        (float) jsonObject.getDouble("Rating"),
                                        jsonObject.getInt("StockQuantity"),
                                        jsonObject.getString("Description"),
                                        jsonObject.getString("CoverImage"),
                                        jsonObject.getString("PublishedDate"),
                                        jsonObject.getInt("CategoryID")
                                );
                                fetchedBooks.add(book);
                            } catch (JSONException e) {
                                Log.e("SearchActivity", "Error parsing book data: " + e.getMessage());
                            }
                        }


                        bookAdapter.setBookList(fetchedBooks);

                    }, error -> {
                Log.e("SearchActivity", "Error fetching books: " + error.getMessage());
                Toast.makeText(this, "Failed to fetch books", Toast.LENGTH_SHORT).show();
            });

            requestQueue.add(jsonArrayRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
