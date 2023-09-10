package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    ImageView cam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button predict=findViewById(R.id.button);
        Button comm = findViewById(R.id.button2);
        Button knowmore = findViewById(R.id.button3);
        cam = findViewById(R.id.imageView10);
        ImageView know = findViewById(R.id.imageView2);
        ImageView community = findViewById(R.id.imageView3);
        predict.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View view) {
                                     Intent i=new Intent(MainActivity2.this,MainActivity.class);
                                     startActivity(i);
                                 }
                             }

        );


    }
}