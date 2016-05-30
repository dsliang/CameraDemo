package com.tuodi.library.camerademo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fragment fragment = new FunctionList();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

}
