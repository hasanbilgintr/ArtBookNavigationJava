package com.hasanbilgin.artbooknavigationjava;



import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.hasanbilgin.artbooknavigationjava.other.StaticData;
import com.hasanbilgin.artbooknavigationjava.page.first.FirstFragmentDirections;
import com.hasanbilgin.artbooknavigationjava.page.second.SecondFragmentDirections;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



}