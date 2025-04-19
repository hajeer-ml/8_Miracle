package com.example.a8_miracle;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.orderIDText.setText("Order ID: #" + order.getOrderID());
        holder.orderDateText.setText("Date: " + order.getOrderDate());
        holder.orderStatusText.setText("Status: " + order.getStatus());
        holder.orderTotalText.setText("Total: " + order.getTotalPrice() + " DZ");

        List<OrderItem> items = order.getItems();
        StringBuilder itemsText = new StringBuilder();
        for (OrderItem item : items) {
            itemsText.append("- ").append(item.getTitle()).append(": ").append(item.getQuantity()).append(" x ").append(item.getPrice()).append(" DZ\n");
        }
        holder.orderItemsText.setText(itemsText.toString());

        String trackingNumber = order.getTrackingNumber();
        if (!trackingNumber.isEmpty()) {
            holder.copyTrackingButton.setVisibility(View.VISIBLE);
            holder.copyTrackingButton.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Tracking Number", trackingNumber);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(v.getContext(), "Tracking number copied", Toast.LENGTH_SHORT).show();
            });
        } else {
            holder.copyTrackingButton.setVisibility(View.GONE); // إخفاء الزر إذا لم يكن هناك رقم تتبع
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIDText, orderDateText, orderStatusText, orderTotalText, orderItemsText;
        Button copyTrackingButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIDText = itemView.findViewById(R.id.order_id_text);
            orderDateText = itemView.findViewById(R.id.order_date_text);
            orderStatusText = itemView.findViewById(R.id.order_status_text);
            orderTotalText = itemView.findViewById(R.id.order_total_text);
            orderItemsText = itemView.findViewById(R.id.order_items_text);
            copyTrackingButton = itemView.findViewById(R.id.copy_tracking_button);
        }
    }
}