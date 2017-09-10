package com.acadgild.drawpathongooglemapexampleandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;




public class CustomAdapter extends SimpleCursorAdapter {

    private Context mContext;
    private Context appContext;
    private int layout;
    private Cursor cr;
    private final LayoutInflater inflater;
    private DBManager db;
    String route;
    private AlertDialog dialog;

    public CustomAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
        this.layout = layout;
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.cr = c;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        super.bindView(view, context, cursor);
         //route = cursor.getString(cursor.getColumnIndex(DatabaseHelper._ID));

        final int position = cursor.getPosition();


        Button btn = (Button) view.findViewById(R.id.button7);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                cursor.moveToPosition(position);
                route = cursor.getString(cursor.getColumnIndex(DatabaseHelper._ID));
                Toast.makeText(mContext, route, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext, SavedMap.class);
                intent.putExtra("id", route);
                intent.putExtra("Uniqid",2);
                mContext.startActivity(intent);
            }
        });


        ImageButton remove=(ImageButton) view.findViewById(R.id.imageButton);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Delete");
        builder.setMessage("Are You Sure ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db = new DBManager(mContext);
                db.open();
                db.delete(route);
                Intent intent= new Intent(mContext, SavedMapsList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        }).setNegativeButton("No, Just Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog = builder.create();
        dialog.show();
    }


}