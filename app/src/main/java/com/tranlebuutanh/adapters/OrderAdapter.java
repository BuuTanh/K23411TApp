package com.tranlebuutanh.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.tranlebuutanh.k23411tapp.R;
import com.tranlebuutanh.models.DataWareHouse;
import com.tranlebuutanh.models.Order;
import com.tranlebuutanh.models.OrderStatus;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class OrderAdapter extends ArrayAdapter<Order> {

    private final Context context;
    private final ArrayList<Order> orders;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private final DecimalFormat df = new DecimalFormat("#,##0.##");

    public OrderAdapter(@NonNull Context context, @NonNull ArrayList<Order> orders) {
        super(context, R.layout.item_custom_order, orders);
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_custom_order, parent, false);
            holder = new ViewHolder();
            holder.cardRoot      = convertView.findViewById(R.id.itemOrderRoot);
            holder.txtOrderId    = convertView.findViewById(R.id.txtOrderId);
            holder.txtOrderDate  = convertView.findViewById(R.id.txtOrderDate);
            holder.txtOrderTotal = convertView.findViewById(R.id.txtOrderTotal);
            holder.txtStatus     = convertView.findViewById(R.id.txtOrderStatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Order order = getItem(position);

        // Bind data
        holder.txtOrderId.setText(order.getOrderId());
        holder.txtOrderDate.setText(sdf.format(order.getOrderDate()));

        double total = DataWareHouse.sumOfMoneyForOrder(order);
        holder.txtOrderTotal.setText(df.format(total) + "đ");

        // Zebra stripe background
        if (position % 2 == 0) {
            holder.cardRoot.setBackgroundColor(Color.WHITE);
        } else {
            holder.cardRoot.setBackgroundColor(Color.parseColor("#F3F6FF"));
        }

        // Status badge color
        OrderStatus status = order.getOrderStatus();
        if (status == null) status = OrderStatus.ALL;

        switch (status) {
            case COMPLETED:
                holder.txtStatus.setText("✓ Completed");
                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_completed);
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.status_completed_text));
                break;
            case NOT_PAYMENT:
                holder.txtStatus.setText("✗ Not Payment");
                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_not_payment);
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.status_not_payment_text));
                break;
            case ON_LOGISTIC:
                holder.txtStatus.setText("⟳ On Logistic");
                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_on_logistic);
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.status_on_logistic_text));
                break;
            case COMPLAIN:
                holder.txtStatus.setText("! Complain");
                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_complain);
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.status_complain_text));
                break;
            default:
                holder.txtStatus.setText("All");
                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_badge);
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.status_all_text));
                break;
        }

        return convertView;
    }

    static class ViewHolder {
        View cardRoot;
        TextView txtOrderId;
        TextView txtOrderDate;
        TextView txtOrderTotal;
        TextView txtStatus;
    }
}
