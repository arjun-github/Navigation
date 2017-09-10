package com.acadgild.drawpathongooglemapexampleandroid;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import static java.security.AccessController.getContext;


// SavedMapsList will open the list of all maps
public class SavedMapsList extends AppCompatActivity {

    private DBManager dbManager;
    private ListView listView;
    TextView textView_id;

    private SimpleCursorAdapter adapter;
    final String[] from = new String[] {DatabaseHelper._ID, DatabaseHelper.DATE,DatabaseHelper.TIME};


    final int[] to = new int[] {R.id.id,R.id.date,R.id.time};


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_route_list);

        dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch_list();

        listView = (ListView) findViewById(R.id.list_view);
        //listView.setEmptyView(findViewById(R.id.empty));

        TextView emptyView=(TextView)findViewById(R.id.textView2);

        //using customAdapter class to handle the single map data in list
        CustomAdapter adapter = new CustomAdapter(this,R.layout.map_view, cursor, from, to);
        listView.setAdapter(adapter);

        if (adapter.isEmpty()){
            emptyView.setText("No Saved Route");
            emptyView.setVisibility(View.VISIBLE);}
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);


    }
}
