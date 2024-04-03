package com.chinhdev.lab6_and103.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.chinhdev.lab6_and103.R;
import com.chinhdev.lab6_and103.adapter.FruitAdapter;
import com.chinhdev.lab6_and103.databinding.ActivityHomeBinding;
import com.chinhdev.lab6_and103.model.Fruit;
import com.chinhdev.lab6_and103.model.Response;
import com.chinhdev.lab6_and103.services.HttpRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeActivity extends AppCompatActivity implements FruitAdapter.FruitClick {
    ActivityHomeBinding binding;
    private HttpRequest httpRequest;
    private SharedPreferences sharedPreferences;
    private String token;
    private FruitAdapter adapter;
    ArrayList<Fruit> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        httpRequest = new HttpRequest();
        sharedPreferences = getSharedPreferences("INFO",MODE_PRIVATE);

        token = sharedPreferences.getString("token","");
        httpRequest.callAPI().getListFruit("Bearer " + token).enqueue(getListFruitResponse);
        userListener();
    }
    private void userListener () {
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this , AddFruitActivity.class));
            }
        });
    }



    Callback<Response<ArrayList<Fruit>>> getListFruitResponse = new Callback<Response<ArrayList<Fruit>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Fruit>>> call, retrofit2.Response<Response<ArrayList<Fruit>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() ==200) {
                    ArrayList<Fruit> ds = response.body().getData();
                    getData(ds);
//                    Toast.makeText(HomeActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Fruit>>> call, Throwable t) {

        }
    };
    private void getData (ArrayList<Fruit> ds) {
        adapter = new FruitAdapter(this, ds,this );
        binding.rcvFruit.setAdapter(adapter);
    }

    @Override
    public void delete(Fruit fruit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.create();
        builder.setMessage("Bạn có chắc chắn muốn xóa không ?");
        builder.setIcon(R.drawable.baseline_warning_24).setTitle("Cảnh báo");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFruit(fruit.get_id());
            }
        }).setNegativeButton("Cancel",null).show();
    }

    private void deleteFruit(String id) {
        httpRequest.callAPI().deleteFruits(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    httpRequest.callAPI().getListFruit("Bearer " + token).enqueue(new Callback<Response<ArrayList<Fruit>>>() {
                        @Override
                        public void onResponse(Call<Response<ArrayList<Fruit>>> call, retrofit2.Response<Response<ArrayList<Fruit>>> response) {
                            if (response.isSuccessful()) {
                                ArrayList<Fruit> newData = response.body().getData();
                                callData(newData);
                                Toast.makeText(HomeActivity.this, "Xoá thành công", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Response<ArrayList<Fruit>>> call, Throwable t) {
                            // Xử lý khi gặp lỗi
                            Toast.makeText(HomeActivity.this, "Lỗi khi lấy danh sách quả trái sau khi xoá", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Xử lý khi xóa không thành công
                    Toast.makeText(HomeActivity.this, "Xoá không thành công", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Xử lý khi gặp lỗi
                Toast.makeText(HomeActivity.this, "Lỗi khi xoá quả trái", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callData(ArrayList<Fruit> newData) {
        list.clear();
        list.addAll(newData);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void edit(Fruit fruit) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        httpRequest.callAPI().getListFruit("Bearer "+token).enqueue(getListFruitResponse);
    }

}