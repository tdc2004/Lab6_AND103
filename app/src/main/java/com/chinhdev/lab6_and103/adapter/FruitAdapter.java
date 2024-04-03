package com.chinhdev.lab6_and103.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chinhdev.lab6_and103.R;
import com.chinhdev.lab6_and103.databinding.ItemFruitBinding;
import com.chinhdev.lab6_and103.model.Fruit;


import java.util.ArrayList;

public class FruitAdapter  extends RecyclerView.Adapter<FruitAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Fruit> list;
    private FruitClick fruitClick;

    public FruitAdapter(Context context, ArrayList<Fruit> list, FruitClick fruitClick) {
        this.context = context;
        this.list = list;
        this.fruitClick = fruitClick;
    }

    public interface FruitClick {
        void delete(Fruit fruit);
        void edit(Fruit fruit);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFruitBinding binding = ItemFruitBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            Fruit fruit = list.get(position);
            holder.binding.tvName.setText(fruit.getName());
            holder.binding.tvPriceQuantity.setText("price :" +fruit.getPrice()+" - quantity:" +fruit.getQuantity());
            holder.binding.tvDes.setText(fruit.getDescription());
            if (!fruit.getImage().isEmpty()) { // Kiểm tra danh sách hình ảnh không rỗng
                String url = fruit.getImage().get(0);
                String newUrl = url.replace("localhost", "10.0.2.2");
                Glide.with(context)
                        .load(newUrl)
                        .thumbnail(Glide.with(context).load(R.drawable.baseline_broken_image_24))
                        .into(holder.binding.img);
            } else {
                Toast.makeText(context, "Loi vcl", Toast.LENGTH_SHORT).show();
            }
            holder.binding.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fruitClick.delete(fruit);
                    list.remove(fruit);
                }
            });
    }


    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0; // Trả về 0 nếu danh sách rỗng
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemFruitBinding binding;
        public ViewHolder(ItemFruitBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
