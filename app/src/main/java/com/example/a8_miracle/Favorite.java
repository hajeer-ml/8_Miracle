package com.example.a8_miracle;

import static androidx.databinding.DataBindingUtil.setContentView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
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


public class Favorite extends Fragment implements FavAdapter.OnFavoriteRemovedListener {
    private RecyclerView recyclerView;
    private FavAdapter adapter;
    private List<Book> favoriteBooks;
    private View emptyStateView;
    private String userID;

    private BroadcastReceiver favoriteUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!userID.isEmpty()) {
                fetchFavoriteBooks(userID);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFavorites);
        emptyStateView = view.findViewById(R.id.emptyFavoritesView);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        favoriteBooks = new ArrayList<>();
        adapter = new FavAdapter(requireContext(), favoriteBooks, this);
        recyclerView.setAdapter(adapter);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", "");

        fetchFavoriteBooks(userID);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(favoriteUpdateReceiver, new IntentFilter("UPDATE_FAVORITES"));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(favoriteUpdateReceiver);
    }

    private void fetchFavoriteBooks(String userId) {
        String URL = "https://8miracle.serv00.net/Home/get_favorites.php?UserID=" + userId;

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        favoriteBooks.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            Book book = new Book(
                                    obj.getInt("BookID"),
                                    obj.getString("Title"),
                                    obj.getString("Author"),
                                    obj.getString("CoverImage")
                            );
                            favoriteBooks.add(book);
                        }
                        updateUI();
                    } catch (JSONException e) {
                        Log.e("FavoritesFragment", "Error parsing data", e);
                    }
                },
                error -> Log.e("FavoritesFragment", "Failed to fetch favorites", error)
        );
        queue.add(request);
    }

    private void updateUI() {
        if (favoriteBooks.isEmpty()) {
            emptyStateView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFavoriteRemoved(int bookId, int position) {
        removeFromFavorites(bookId, position);
    }

    private void removeFromFavorites(int bookId, int position) {
        if (userID == null) return;

        String URL = "https://8miracle.serv00.net/Home/unlike_book.php";

        StringRequest request = new StringRequest(Request.Method.POST, URL,
                response -> {
                    favoriteBooks.remove(position);
                    adapter.notifyItemRemoved(position);
                    updateUI();


                    Intent intent = new Intent("UPDATE_FAVORITE_STATUS");
                    intent.putExtra("BookID", bookId);
                    intent.putExtra("isFavorite", false); //  false عند الإزالة
                    LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
                    Log.d("Favorite", "Broadcast sent for BookID: " + bookId);
                },
                error -> Toast.makeText(requireContext(), "Failed to remove", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("UserID", userID);
                params.put("BookID", String.valueOf(bookId));
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }
}