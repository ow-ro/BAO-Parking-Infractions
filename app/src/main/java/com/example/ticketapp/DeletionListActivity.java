package com.example.ticketapp;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class DeletionListActivity extends AppCompatActivity {

    private static final String TAG = "DeletionListActivity";

    private ListView mListView;
    private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deletion_list_main);
        this.setTitle("Deletion Log");

        mDatabaseHelper = new DatabaseHelper(this);
        mListView = findViewById(R.id.delListView2);

        refreshDelListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDelListView();
    }

    private void refreshDelListView() {
        ArrayList delList = getListOfAllDeletions();
        mListView.setAdapter(new DeletionCustomListAdapter(this, delList));
    }

    private ArrayList getListOfAllDeletions() {
        //get data & append to list
        Cursor data = mDatabaseHelper.getAllDeletions();
        ArrayList<DeletionRecord> deletionRecords = new ArrayList<>();
        while (data.moveToNext()) {
            DeletionRecord del = new DeletionRecord();
            del.setPlate(data.getString(2));
            del.setState(data.getString(3));
            del.setDateTime(data.getString(4));
            del.setInfraction(data.getString(5));
            del.setLocation(data.getString(6));
            del.setNotes(data.getString(7));
            del.setType(data.getInt(8));
            del.setDeletionTime(data.getString(9));
            deletionRecords.add(del);
        }
        return deletionRecords;

        //create the list adapter and set the adapter
        //ListAdapter listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listData);
        //mListView.setAdapter(listAdapter);

    }

}
