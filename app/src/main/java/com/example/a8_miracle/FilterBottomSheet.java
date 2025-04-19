package com.example.a8_miracle;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class FilterBottomSheet extends BottomSheetDialogFragment {
    private SearchActivity searchActivity;
    private String selectedCategory = "", selectedLanguage = "";
    private double minPrice = 0, maxPrice = 10000;
    private RadioGroup categoryRadioGroup;
    private RequestQueue requestQueue;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_bottom_sheet, container, false);
        requestQueue = Volley.newRequestQueue(requireContext());

        categoryRadioGroup = view.findViewById(R.id.categoryRadioGroup);
        ImageButton applyFilter = view.findViewById(R.id.confirmFilterButton);
        EditText minPriceInput = view.findViewById(R.id.minPrice);
        EditText maxPriceInput = view.findViewById(R.id.maxPrice);

        fetchCategories();

        categoryRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = group.findViewById(checkedId);
            if (selected != null) {
                selectedCategory = selected.getTag().toString();
            }
        });

        applyFilter.setOnClickListener(v -> {
            try {
                minPrice = minPriceInput.getText().toString().isEmpty() ? 0 :
                        Double.parseDouble(minPriceInput.getText().toString());
                maxPrice = maxPriceInput.getText().toString().isEmpty() ? 10000 :
                        Double.parseDouble(maxPriceInput.getText().toString());
                searchActivity.applyFilters(selectedCategory,/* selectedLanguage, */ minPrice, maxPrice);
                dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid price range", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void fetchCategories() {
        String url = "https://8miracle.serv00.net/Home/get_categories.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject category = response.getJSONObject(i);
                            String name = category.getString("CatName");
                            int id = category.getInt("CategoryID");


                            RadioButton radioButton = new RadioButton(getContext());
                            radioButton.setText(name);
                            radioButton.setTextColor(ContextCompat.getColor(getContext(), R.color.Brown3));
                            radioButton.setTextSize(18);
                            radioButton.setTypeface(null, Typeface.BOLD);

                            Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.adlam_display);
                            radioButton.setTypeface(typeface);

                            radioButton.setTag(id);

                            categoryRadioGroup.addView(radioButton);
                        }
                    } catch (JSONException e) {
                        Log.e("FilterBottomSheet", "Error parsing categories: " + e.getMessage());
                    }
                },
                error -> Log.e("FilterBottomSheet", "Error fetching categories: " + error.getMessage())
        );

        requestQueue.add(request);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SearchActivity) {
            searchActivity = (SearchActivity) context;
        } else {
            throw new RuntimeException(context + " must be SearchActivity");
        }
    }
}
