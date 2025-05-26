package com.example.a8_miracle;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BookDetails extends AppCompatActivity {

    private BroadcastReceiver favoriteUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int updatedBookId = intent.getIntExtra("BookID", -1);
            boolean isFavorite = intent.getBooleanExtra("isFavorite", false);
            Log.d("BookDetails", "Broadcast Received - UpdatedBookID: " + updatedBookId + ", isFavorite: " + isFavorite);

            if (updatedBookId == BookID) {
                sharedPreferences.edit().putBoolean("isLiked_" + BookID, isFavorite).commit(); // استخدام commit() بدلاً من apply()
                Log.d("BookDetails", "SharedPreferences updated for BookID: " + BookID + ", isFavorite: " + isFavorite);
                runOnUiThread(() -> updateLikeButton(isFavorite));
            } else {
                Log.d("BookDetails", "Broadcast ignored - BookID mismatch");
            }
        }
    };

    private ImageView bookCover, favButton, backButton;
    private TextView btnIncrease, btnDecrease, bookName, bookAuthor, bookPrice, bookDescription, tvQuantity, ratingCountText;
    private Button btnAddToCart;
    private RatingBar bookRatingBar;
    private RequestQueue requestQueue;
    private int quantity = 1;
    private boolean isFavorite;
    private int BookID;
    private String userID;
    private String bookImageUrl;
    private SharedPreferences sharedPreferences;
    private TextView ratingPercentageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);


        bookCover = findViewById(R.id.bookCover);
        bookName = findViewById(R.id.bookName);
        bookAuthor = findViewById(R.id.bookAuthor);
        bookPrice = findViewById(R.id.bookPrice);
        bookDescription = findViewById(R.id.bookDescription);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        bookRatingBar = findViewById(R.id.bookRatingBar);
        ratingCountText = findViewById(R.id.ratingCountText);
        favButton = findViewById(R.id.favButton);
        backButton = findViewById(R.id.backbutton);

        ratingPercentageText = findViewById(R.id.ratingPercentageText);

        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", "");


        Book book = (Book) getIntent().getSerializableExtra("book");
        if (book != null) {
            BookID = book.getBookID();
            Log.d("BookDetails", "BookID set to: " + BookID);
            displayBookDetails(book);
            fetchBookDetails(BookID);

            isFavorite = sharedPreferences.getBoolean("isLiked_" + BookID, false);
            updateLikeButton(isFavorite);
        } else {
            Toast.makeText(this, "Book data not found.", Toast.LENGTH_SHORT).show();
            finish();
        }


        loadBookRating(BookID);


        bookRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                int userId = Integer.parseInt(userID);
                submitRating(userId, BookID, (int) rating);
            }
        });


        backButton.setOnClickListener(v -> finish());


        btnIncrease.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });


        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });


        favButton.setOnClickListener(v -> toggleLike());


        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("UPDATE_FAVORITE_STATUS");
        LocalBroadcastManager.getInstance(this).registerReceiver(favoriteUpdateReceiver, filter);
        Log.d("BookDetails", "BroadcastReceiver registered");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(favoriteUpdateReceiver);
        Log.d("BookDetails", "BroadcastReceiver unregistered");
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFavorite = sharedPreferences.getBoolean("isLiked_" + BookID, false);
        updateLikeButton(isFavorite);
        Log.d("BookDetails", "onResume: Like button updated to: " + isFavorite);
    }


    private void displayBookDetails(Book book) {
        bookName.setText(book.getTitle());
        bookAuthor.setText(" " + book.getAuthor());
        bookPrice.setText(" " + book.getPrice() + " DZ");

        bookImageUrl = book.getCoverImage();
        Glide.with(this).load(bookImageUrl).into(bookCover);
    }

    private void fetchBookDetails(int BookID) {
        String url = "https://8miracle.serv00.net/Home/get_book_details.php?BookID=" + BookID;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("Description")) {
                            bookDescription.setText(response.getString("Description"));
                        } else {
                            bookDescription.setText("No description available.");
                        }
                    } catch (JSONException e) {
                        Log.e("BookDetails", "JSON Error: " + e.getMessage());
                        bookDescription.setText("Error loading description.");
                    }
                },
                error -> {
                    Log.e("BookDetails", "Volley Error: " + error.toString());
                    bookDescription.setText("Server connection error.");
                }
        );

        requestQueue.add(request);
    }

    private void toggleLike() {
        isFavorite = sharedPreferences.getBoolean("isLiked_" + BookID, false);
        String url = isFavorite ? "https://8miracle.serv00.net/Home/unlike_book.php"
                : "https://8miracle.serv00.net/Home/like_book.php";
        String successMessage = isFavorite ? "The book has been removed from favorites!"
                : "The book has been added to your favorites!";
        String errorMessage = isFavorite ? "Failed to remove book from favourites"
                : "Failed to add book to favourites";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            isFavorite = !isFavorite;
                            sharedPreferences.edit().putBoolean("isLiked_" + BookID, isFavorite).apply();
                            updateLikeButton(isFavorite);
                            Toast.makeText(BookDetails.this, successMessage, Toast.LENGTH_SHORT).show();


                            Intent intent = new Intent("UPDATE_FAVORITE_STATUS");
                            intent.putExtra("BookID", BookID);
                            intent.putExtra("isFavorite", isFavorite);
                            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        } else {
                            Toast.makeText(BookDetails.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(BookDetails.this, "Data processing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(BookDetails.this, "Connection error", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("UserID", userID);
                params.put("BookID", String.valueOf(BookID));
                return params;
            }
        };

        requestQueue.add(request);
    }

    private void updateLikeButton(boolean isLiked) {
        runOnUiThread(() -> {
            favButton.setImageResource(isLiked ? R.drawable.baseline_favorite_24 : R.drawable.favorite_border_24);
            Log.d("BookDetails", "Like button visually updated to: " + isLiked);
        });
    }

    private void loadBookRating(int bookId) {
        // Remove the space after book_id=
        String url = "https://8miracle.serv00.net/Home/get_book_rating.php?book_id=" + bookId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        // Check if success field exists and is true
                        if (jsonObject.optBoolean("success", false)) {
                            int totalRatings = jsonObject.getInt("total_ratings");
                            float avgRating = (float) jsonObject.getDouble("avg_rating");

                            // Calculate rating percentage
                            float ratingPercentage = (avgRating / 5) * 100;

                            // Update UI
                            runOnUiThread(() -> {
                                bookRatingBar.setRating(avgRating);
                                ratingCountText.setText(totalRatings + " ");

                                // Set rating description
                                String ratingDescription;
                                if (avgRating >= 4) {
                                    ratingDescription = "Great";
                                } else if (avgRating >= 3) {
                                    ratingDescription = "Good";
                                } else if (avgRating > 0) {
                                    ratingDescription = "Average";
                                } else {
                                    ratingDescription = "Not rated yet";
                                }

                                // Update rating percentage text
                                if (totalRatings > 0) {
                                    ratingPercentageText.setText(String.format("%.0f%% %s", ratingPercentage, ratingDescription));
                                } else {
                                    ratingPercentageText.setText("No ratings yet");
                                }
                            });
                        } else {
                            // Handle error response
                            String message = jsonObject.optString("message", "Error loading ratings");
                            Log.e("BookDetails", "Rating error: " + message);
                        }
                    } catch (JSONException e) {
                        Log.e("BookDetails", "JSON parsing error: " + e.getMessage());
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("BookDetails", "Volley error: " + error.getMessage());
                    error.printStackTrace();
                });

        // Set retry policy
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000, // Timeout in milliseconds
                1,    // Max retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }

    private void submitRating(int userId, int bookId, float rating) {
        String url = "https://8miracle.serv00.net/Home/submit_rating.php";

        Log.d("BookDetails", "Submitting rating - UserID: " + userId + ", BookID: " + bookId + ", Rating: " + rating);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            Toast.makeText(this, "Evaluation submitted successfully", Toast.LENGTH_SHORT).show();
                            // Reload book rating after submission
                            loadBookRating(bookId);
                        } else {
                            String message = jsonObject.optString("message", "Failed to submit rating");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            Log.e("BookDetails", "Rating submission error: " + message);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error processing response", Toast.LENGTH_SHORT).show();
                        Log.e("BookDetails", "JSON Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                    Log.e("BookDetails", "Volley Error: " + error.toString());
                    error.printStackTrace();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("UserID", String.valueOf(userId));
                params.put("BookID", String.valueOf(bookId));
                params.put("Rating", String.valueOf(rating));
                return params;
            }
        };

        // Set retry policy
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000, // Timeout in milliseconds
                1,    // Max retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }
    private void addToCart() {
        String url = "https://8miracle.serv00.net/Home/add_to_cart.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            Toast.makeText(BookDetails.this, "The book has been added to the cart.", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMessage = jsonResponse.optString("message", "Failed to add to cart");
                            Toast.makeText(BookDetails.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(BookDetails.this, "Error processing response", Toast.LENGTH_SHORT).show();
                        Log.e("BookDetails", "JSON Error: " + e.getMessage());
                    }
                },
                error -> {
                    Toast.makeText(BookDetails.this, "Network error", Toast.LENGTH_SHORT).show();
                    Log.e("BookDetails", "Volley Error: " + error.toString());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("UserID", userID);
                params.put("BookID", String.valueOf(BookID));
                params.put("Quantity", String.valueOf(quantity));
                return params;
            }
        };

        requestQueue.add(request);
    }
}