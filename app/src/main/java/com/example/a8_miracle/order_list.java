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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class order_list extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private RequestQueue requestQueue;
    private String userID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        recyclerView = view.findViewById(R.id.order_list_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList);
        recyclerView.setAdapter(orderAdapter);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", "");

        requestQueue = Volley.newRequestQueue(requireContext());
        loadOrders();

        return view;
    }

    private void loadOrders() {
        String url = "https://8miracle.serv00.net/Home/get_order.php?UserID=" + userID;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONArray ordersArray = response.getJSONArray("orders");
                            for (int i = 0; i < ordersArray.length(); i++) {
                                JSONObject orderObj = ordersArray.getJSONObject(i);

                                int orderID = orderObj.getInt("OrderID");
                                double totalPrice = orderObj.getDouble("TotalPrice");
                                String orderDate = orderObj.getString("OrderDate");
                                String status = orderObj.getString("Status");
                                String trackingNumber = orderObj.optString("TrackingNumber", "");
                                double deliveryCost = orderObj.getDouble("DeliveryCost");
                                String userName = orderObj.getString("UserName");
                                String phoneNumber = orderObj.getString("PhoneNumber");
                                String willaya = orderObj.getString("Willaya");
                                String municipality = orderObj.getString("Municipality");
                                String deliveryType = orderObj.getString("DeliveryType");


                                JSONArray itemsArray = orderObj.getJSONArray("Items");
                                List<OrderItem> items = new ArrayList<>();
                                for (int j = 0; j < itemsArray.length(); j++) {
                                    JSONObject itemObj = itemsArray.getJSONObject(j);
                                    int bookID = itemObj.getInt("BookID");
                                    int quantity = itemObj.getInt("Quantity");
                                    double price = itemObj.getDouble("Price");
                                    String title = itemObj.getString("Title");
                                    String coverImage = itemObj.getString("CoverImage");
                                    items.add(new OrderItem(bookID, quantity, price, title, coverImage));
                                }

                                Order order = new Order(orderID, totalPrice, orderDate, status, trackingNumber, deliveryCost, userName, phoneNumber, willaya, municipality, deliveryType, items);
                                orderList.add(order);
                            }
                            orderAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "No orders found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("OrderListError", "Failed to load orders: " + error.toString());
                    Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }
}