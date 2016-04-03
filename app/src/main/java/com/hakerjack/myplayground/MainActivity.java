package com.hakerjack.myplayground;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.project_list_view) ListView mProjectListView;
    ArrayAdapter<String> mListAdapter;
    String[] projects = {"Flickr Grid View"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        mListAdapter = new ArrayAdapter<>(this, R.layout.project_list_item, R.id.project_title, projects);
        mProjectListView.setAdapter(mListAdapter);
        mProjectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, FlickrGridViewActivity.class));
                        break;
                }
            }
        });

    }
}
