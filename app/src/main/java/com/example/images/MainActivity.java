package com.example.images;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.images.databinding.ActivityMainBinding;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        clickListeners();
    }
    private void clickListeners() {
        binding.selectImage.setOnClickListener(v->{

            Log.d("MainWork", "Worked: ");
            if(ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent();
                Log.d("MainWork", "Worked:2 ");
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,10);
                Log.d("MainWork", "Worked: 3");
            }else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                Log.d("MainWork", "NOTWorked: ");
            }
        });

        binding.save.setOnClickListener(v->{
            addCustomer();
        });



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10 && resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            Context context = MainActivity.this;
            path = RealPathUtil.getRealPath(context,uri);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            binding.imageview.setImageBitmap(bitmap);

        }


    }
    public void addCustomer(){

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://192.168.1.3:5000/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        File file = new File(path);
        Log.d("TAG", "addCustomer: "+file);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        Log.d("TAG", "addCustomer: "+requestFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image",file.getName(),requestFile);
        Log.d("TAG", "addCustomer: "+file.getName());
        Log.d("TAG", "addCustomer: "+body);

        ApiService apiService = retrofit.create(ApiService.class);
        Call<AddCustomerRes> call = apiService.addCustomer(body);

        Log.d("TAG", "addCustomer: "+call);
        call.enqueue(new Callback<AddCustomerRes>() {
            @Override
            public void onResponse(Call<AddCustomerRes> call, Response<AddCustomerRes> response) {
                if(response.isSuccessful()){


                    Log.d("TAG", "onResponse: "+response.body().getPrediction());
                    binding.tvPresidentsNames.setText(response.body().getPrediction());



                }


            }

            @Override
            public void onFailure(Call<AddCustomerRes> call, Throwable t) {

                Log.d("MainWORK", "onFailure: "+t.getMessage());

            }
        });

    }
}