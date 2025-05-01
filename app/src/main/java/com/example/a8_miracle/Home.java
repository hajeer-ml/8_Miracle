package com.example.a8_miracle;


import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends Fragment implements BookAdapter.OnItemClickListener {
    private RequestQueue requestQueue;
    private ImageButton notificationButton;
    private View notificationBadge;
    private ImageView searchButton;
    private LinearLayout parentLayout;
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
        context = getContext();
        parentLayout = view.findViewById(R.id.parent_layout);
        searchButton = view.findViewById(R.id.searchButton);
        requestQueue = Volley.newRequestQueue(context);

        notificationButton = view.findViewById(R.id.notificationButton);
        notificationBadge = view.findViewById(R.id.notification_badge);


        checkUnreadNotifications();


        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, SearchActivity.class);
            startActivity(intent);
        });

        notificationButton.setOnClickListener(v -> fetchAndShowNotifications());

        fetchCategories();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.stop();
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


                                TextView categoryTitle = new TextView(context);
                                categoryTitle.setText(catName);
                                categoryTitle.setTextSize(25);
                                categoryTitle.setTypeface(null, Typeface.BOLD);
                                categoryTitle.setTextColor(ContextCompat.getColor(context, R.color.Brown3));
                                Typeface typeface = ResourcesCompat.getFont(context, R.font.pacifico);
                                categoryTitle.setTypeface(typeface);

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

    private void checkUnreadNotifications() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", "");

        if (!userID.isEmpty()) {
            String url = "https://8miracle.serv00.net/Home/check_unread_notifications.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");

                            if (status.equals("success")) {
                                boolean hasUnread = jsonObject.getBoolean("hasUnread");
                                notificationBadge.setVisibility(hasUnread ? View.VISIBLE : View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("userID", userID);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
            requestQueue.add(stringRequest);
        }
    }

    private void fetchAndShowNotifications() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", "");

        if (!userID.isEmpty()) {
            String url = "https://8miracle.serv00.net/Home/get_notifications.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");

                            if (status.equals("success")) {
                                JSONObject notificationObject = jsonObject.getJSONObject("notification");

                                if (notificationObject != null) {
                                    Intent intent = new Intent(requireActivity(), Notif.class);
                                    intent.putExtra("message", notificationObject.getString("message"));
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(requireActivity(), "No notifications found.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(requireActivity(), "Error fetching notifications.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireActivity(), "Error parsing response.", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(requireActivity(), "Server connection error.", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("userID", userID);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
            requestQueue.add(stringRequest);
        }

    }


    @Override
    public void onItemClick(Book book) {
        Intent intent = new Intent(context, BookDetails.class);
        intent.putExtra("book", book);
        startActivity(intent);
    }
}