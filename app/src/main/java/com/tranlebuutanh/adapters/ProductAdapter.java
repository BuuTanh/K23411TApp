package com.tranlebuutanh.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tranlebuutanh.k23411tapp.R;
import com.tranlebuutanh.models.Product;

import java.text.DecimalFormat;

public class ProductAdapter extends ArrayAdapter<Product> {
    Activity context;
    int resource;
    DecimalFormat df = new DecimalFormat("#,##0.##");

    public ProductAdapter(@NonNull Activity context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(resource, null);

        Product product = getItem(position);

        TextView txtId       = view.findViewById(R.id.txtProductId);
        TextView txtName     = view.findViewById(R.id.txtProductName);
        TextView txtPrice    = view.findViewById(R.id.txtProductPrice);
        TextView txtQuantity = view.findViewById(R.id.txtProductQuantity);
        TextView txtVAT      = view.findViewById(R.id.txtProductVAT);

        txtId.setText(product.getProductId());
        txtName.setText(product.getProductName());
        txtPrice.setText("Giá: " + df.format(product.getPrice()) + "đ");
        txtQuantity.setText("SL: " + product.getQuantity());
        txtVAT.setText("VAT: " + (int)(product.getVAT() * 100) + "%");

        return view;
    }
}
