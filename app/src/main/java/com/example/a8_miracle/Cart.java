package com.example.a8_miracle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Cart extends Fragment {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private TextView subtotalText, deliveryCostText;
    private RadioButton radioOffice, radioHome;
    private Button checkoutButton;
    private Spinner wilayaSpinner;
    private RequestQueue requestQueue;
    private String userID;
    private SharedPreferences sharedPreferences;
    private Map<String, Map<String, Double>> wilayaDeliveryCosts;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.cart_recycler);
        subtotalText = view.findViewById(R.id.subtotal_text);
        deliveryCostText = view.findViewById(R.id.delivery_cost_text);
        wilayaSpinner = view.findViewById(R.id.wilaya_spinner);
        radioOffice = view.findViewById(R.id.delivery_office);
        radioHome = view.findViewById(R.id.delivery_home);
        checkoutButton = view.findViewById(R.id.checkout_button);
        requestQueue = Volley.newRequestQueue(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(requireContext(), cartItems, this::removeFromCart);
        recyclerView.setAdapter(cartAdapter);

        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", "");

        initializeWilayaDeliveryCosts();
        setupWilayaSpinner();
        setupRadioButtons(view);
        loadCartItems();

        checkoutButton.setOnClickListener(v -> confirmOrder());

        return view;
    }

    private void initializeWilayaDeliveryCosts() {
        wilayaDeliveryCosts = new HashMap<>();
        wilayaDeliveryCosts.put("Adrar", new HashMap<String, Double>() {{
            put("Home", 500.0);
            put("Office", 400.0);
        }});
        wilayaDeliveryCosts.put("Tindouf", new HashMap<String, Double>() {{
            put("Home", 500.0);
            put("Office", 400.0);
        }});
        wilayaDeliveryCosts.put("Chlef", new HashMap<String, Double>() {{
            put("Home", 300.0);
            put("Office", 200.0);
        }});
        wilayaDeliveryCosts.put("Algiers", new HashMap<String, Double>() {{
            put("Home", 200.0);
            put("Office", 100.0);
        }});
        wilayaDeliveryCosts.put("Oran", new HashMap<String, Double>() {{
            put("Home", 400.0);
            put("Office", 300.0);
        }});
        wilayaDeliveryCosts.put("Tlemcen", new HashMap<String, Double>() {{
            put("Home", 400.0);
            put("Office", 300.0);
        }});
    }

    private void setupWilayaSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>(wilayaDeliveryCosts.keySet()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wilayaSpinner.setAdapter(adapter);

        wilayaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateDeliveryCostBasedOnSelection();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupRadioButtons(View view) {
        RadioGroup deliveryOptions = view.findViewById(R.id.delivery_options);
        deliveryOptions.setOnCheckedChangeListener((group, checkedId) -> updateDeliveryCostBasedOnSelection());
    }


    private void updateDeliveryCostBasedOnSelection() {
        String selectedWilaya = (String) wilayaSpinner.getSelectedItem();
        if (selectedWilaya == null) {
            return;
        }

        String deliveryType = radioHome.isChecked() ? "Home" : "Office";
        double deliveryCost = wilayaDeliveryCosts.get(selectedWilaya).get(deliveryType);
        updateDeliveryCost(deliveryCost);
    }

    private void updateDeliveryCost(double deliveryCost) {
        deliveryCostText.setText(String.format(Locale.getDefault(), "Delivery Cost: %.2f DZ", deliveryCost));
        updateTotalPrice(deliveryCost);
    }

    private void updateTotalPrice(double deliveryCost) {
        double subtotal = calculateSubtotal();
        double totalPrice = subtotal + deliveryCost;
        subtotalText.setText(String.format(Locale.getDefault(), " %.2f DZ", totalPrice));
    }

    private double calculateSubtotal() {
        double subtotal = 0.0;
        for (CartItem item : cartItems) {
            subtotal += item.getPrice() * item.getQuantity();
        }
        return subtotal;
    }

    private void loadCartItems() {
        String url = "https://8miracle.serv00.net/Home/get_cart_items.php?UserID=" + userID;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        cartItems.clear();
                        double totalPrice = 0.0;

                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONArray cartItemsArray = response.getJSONArray("cartItems");

                            for (int i = 0; i < cartItemsArray.length(); i++) {
                                JSONObject item = cartItemsArray.getJSONObject(i);
                                int cartItemId = item.getInt("CartItemID");
                                int bookId = item.getInt("BookID");
                                String title = item.getString("Title");
                                double price = item.getDouble("Price");
                                int quantity = item.getInt("Quantity");
                                String image = item.getString("CoverImage");

                                totalPrice += price * quantity;
                                cartItems.add(new CartItem(cartItemId, bookId, title, price, quantity, image));
                            }
                        }

                        cartAdapter.notifyDataSetChanged();
                        subtotalText.setText(String.format(Locale.getDefault(), "%.2f DZ", totalPrice));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("CartError", "Failed to load cart: " + error.toString());
                    Toast.makeText(getContext(), "Failed to load cart", Toast.LENGTH_SHORT).show();
                }
        );
        requestQueue.add(request);
    }

    private void removeFromCart(CartItem item) {
        String url = "https://8miracle.serv00.net/Home/remove_from_cart.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            loadCartItems();
                            Toast.makeText(getContext(), "The book has been removed from the basket.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to delete item", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Response parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Failed to delete", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("CartItemID", String.valueOf(item.getCartItemId()));
                return params;
            }
        };
        requestQueue.add(request);
    }

    private void confirmOrder() {
        Spinner wilayaSpinner = getView().findViewById(R.id.wilaya_spinner);
        String selectedWilaya = (String) wilayaSpinner.getSelectedItem();
        String deliveryType = radioHome.isChecked() ? "Home" : "Office";
        double deliveryCost = wilayaDeliveryCosts.get(selectedWilaya).get(deliveryType);

        if (selectedWilaya == null || selectedWilaya.isEmpty()) {
            Toast.makeText(getContext(), "Please select your state.", Toast.LENGTH_SHORT).show();
            return;
        }

        String createAddressUrl = "https://8miracle.serv00.net/Home/create_address.php";
        StringRequest createAddressRequest = new StringRequest(Request.Method.POST, createAddressUrl,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            int addressID = jsonResponse.getInt("AddressID");
                            confirmOrderWithAddress(addressID, deliveryCost);
                        } else {
                            Toast.makeText(getContext(), "Failed to create address", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Failed to create address", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("UserID", String.valueOf(userID));
                params.put("Willaya", selectedWilaya);
                params.put("DeliveryType", deliveryType);
                return params;
            }
        };
        requestQueue.add(createAddressRequest);
    }

    private void confirmOrderWithAddress(int addressID, double deliveryCost) {
        String url = "https://8miracle.serv00.net/Home/confirm_order.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            Toast.makeText(getContext(), "Order confirmed!", Toast.LENGTH_SHORT).show();
                            clearCart();
                        } else {
                            Toast.makeText(getContext(), "Failed to confirm order", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Failed to confirm request", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("UserID", String.valueOf(userID));
                params.put("AddressID", String.valueOf(addressID));
                params.put("DeliveryCost", String.valueOf(deliveryCost));
                return params;
            }
        };
        requestQueue.add(request);
    }

    private void clearCart() {
        cartItems.clear();
        cartAdapter.notifyDataSetChanged();
        subtotalText.setText("");
    }
}
