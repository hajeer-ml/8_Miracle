package com.example.a8_miracle;

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
import androidx.lifecycle.ViewModelProvider;
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

public class Home extends Fragment implements ChildAdapter.OnItemClickListener {
    private RequestQueue requestQueue;
    private LinearLayout parentLayout;
    private Map<Integer, ChildAdapter> adapterMap = new HashMap<>();
    private HomeViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_home, container, false);
        if (container != null) {
            container.removeAllViews();
        }
        parentLayout = myView.findViewById(R.id.parentLa);
        requestQueue = Volley.newRequestQueue(requireActivity()); // Use requireActivity() instead of requireContext()
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        return myView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchCategories();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }

    private void fetchCategories() {
        if (isAdded()) { // Ensure the fragment is attached before proceeding
            parentLayout.removeAllViews(); // Prevent duplication
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "https://akram.serv00.net/Meet/get_categories.php", null,
                    response -> {
                        requireActivity().runOnUiThread(() -> { // Ensure UI updates are on the main thread
                            Log.d("Home", "Categories Response: " + response.toString());
                            if (response.length() == 0) {
                                Log.w("Home", "No categories found.");
                                return;
                            }
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    int categoryID = jsonObject.getInt("CategoryID");
                                    String catName = jsonObject.getString("CatName");

                                    TextView categoryTitle = new TextView(requireActivity());
                                    categoryTitle.setText(catName);
                                    categoryTitle.setTextSize(18);
                                    categoryTitle.setPadding(20, 40, 20, 10);
                                    RecyclerView recyclerView = new RecyclerView(requireActivity());
                                    recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT));
                                    recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));

                                    parentLayout.addView(categoryTitle);
                                    parentLayout.addView(recyclerView);

                                    ChildAdapter adapter = new ChildAdapter(requireActivity(), new ArrayList<>());
                                    adapter.setOnItemClickListener(this);
                                    adapterMap.put(categoryID, adapter);
                                    recyclerView.setAdapter(adapter);

                                    fetchBooks(categoryID);
                                } catch (Exception e) {
                                    Log.e("Home", "Error fetching categories: " + e.getMessage());
                                }
                            }
                        });
                    }, error -> Log.e("Home", "Error fetching categories: " + error.getMessage()));

            requestQueue.add(jsonArrayRequest);
        }
    }

    private void fetchBooks(int categoryID) {
        if (isAdded()) { // Ensure the fragment is attached before proceeding
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "https://akram.serv00.net/Meet/get_books.php?category_id=" + categoryID, null,
                    response -> {
                        requireActivity().runOnUiThread(() -> { // Ensure UI updates are on the main thread
                            Log.d("Home", "Books Response: " + response.toString());
                            List<ChildMC> books = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    ChildMC book = new ChildMC(
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
                                    books.add(book);
                                } catch (Exception e) {
                                    Log.e("Home", "Error parsing book: " + e.getMessage());
                                }
                            }
                            if (adapterMap.containsKey(categoryID)) {
                                ChildAdapter adapter = adapterMap.get(categoryID);
                                adapter.childMCList(books);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }, error -> Log.e("Home", "Error fetching books: " + error.getMessage()));

            requestQueue.add(jsonArrayRequest);
        }
    }

    @Override
    public void onItemClick(ChildMC book) {
        Intent intent = new Intent(requireActivity(), BookDetails.class); // Use requireActivity() instead of requireContext()
        intent.putExtra("book_id", book.getBookID());
        intent.putExtra("title", book.getTitle());
        intent.putExtra("author", book.getAuthor());
        intent.putExtra("price", book.getPrice());
        intent.putExtra("rating", book.getRating());
        intent.putExtra("stockQuantity", book.getStockQuantity());
        intent.putExtra("description", book.getDescription());
        intent.putExtra("coverImage", book.getCoverImage());
        intent.putExtra("publishedDate", book.getPublishedDate());
        intent.putExtra("categoryID", book.getCategoryID());
        startActivity(intent);
    }

    // ViewModel class to handle lifecycle-aware data
    public static class HomeViewModel extends androidx.lifecycle.ViewModel {
        // Add any lifecycle-aware data here
    }
}