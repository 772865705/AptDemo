package com.zy.annotationtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.zy.annotations.MyAnnotation;

import java.lang.reflect.Proxy;


public class MainActivity extends AppCompatActivity {

    @MyAnnotation(value = R.id.testTv,name = "写在注释里面的")
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity_ViewBinding binding = new MainActivity_ViewBinding();
        binding.bind(this);

        mTextView.setText("绑定成功了！");
    }
}