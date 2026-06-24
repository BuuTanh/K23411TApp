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
import com.tranlebuutanh.models.Category;

public class CategoryAdapter extends ArrayAdapter<Category>
{
    Activity context;
    int resource;
    public CategoryAdapter(@NonNull Activity context, int resource) {
        super(context, resource);
        this.context=context;
        this.resource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View custom=inflater.inflate(resource,null);
        Category cate=getItem(position);
        TextView txtCateId=custom.findViewById(R.id.txtCategoryId);
        TextView txtCateName=custom.findViewById(R.id.txtCategoryName);
        TextView txtDescription=custom.findViewById(R.id.txtDescription);
        txtCateId.setText(cate.getCategoryId()+"");
        txtCateName.setText(cate.getCategoryName()+"");
        txtDescription.setText(cate.getDescription()+"");

        return custom;
    }
}
