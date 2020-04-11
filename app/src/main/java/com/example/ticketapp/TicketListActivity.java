package com.example.ticketapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TicketListActivity extends AppCompatActivity {

    private static final String TAG = "TicketListActivity";

    private ListView mListView;
    private FloatingActionButton mNewTicket, mNewException;
    DatabaseHelper mDatabaseHelper;
    private Integer mSelectedTicketId;
    private EditText mSearchFilter;
    private TicketCustomListAdapter adapter;
    private boolean isFABOpen = false;
    private FloatingActionButton mFab1, mFab2;
    private TextView btn1Text, btn2Text;
    private String mUserEnteredPass = "";
    private final String mPassword = "Base0ps9";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_list_main);
        mDatabaseHelper = new DatabaseHelper(this);
        mListView = findViewById(R.id.ticketListView2);
        mNewTicket = findViewById(R.id.addTicketButton);
        mSearchFilter = findViewById(R.id.searchFilter);
        mFab1 = findViewById(R.id.btn1);
        mFab2 = findViewById(R.id.btn2);
        btn1Text = findViewById(R.id.btn1Txt);
        btn2Text = findViewById(R.id.btn2Txt);


        mSearchFilter.setFilters(new InputFilter[] {new InputFilter.AllCaps()});


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Object obj = mListView.getAdapter().getItem(position);
                Ticket ticket = (Ticket) obj;

                //mSelectedTicketId = mDatabaseHelper.getScheduleId(schedule.getName());
                mSelectedTicketId = ticket.getTicketId();

                Intent editScreenIntent = new Intent(TicketListActivity.this, TicketDetailActivity.class);
                editScreenIntent.putExtra("ticketId", mSelectedTicketId);
                editScreenIntent.putExtra("plate", ticket.getPlate());
                editScreenIntent.putExtra("state", ticket.getState());
                editScreenIntent.putExtra("dateTime", ticket.getDateTime());
                editScreenIntent.putExtra("infraction", ticket.getInfraction());
                editScreenIntent.putExtra("location", ticket.getLocation());
                editScreenIntent.putExtra("notes", ticket.getNotes());
                editScreenIntent.putExtra("licence_photo", ticket.getLicencePhoto());
                editScreenIntent.putExtra("ticket_photo", ticket.getTicketPhoto());
                editScreenIntent.putExtra("type", ticket.getType());
                editScreenIntent.putExtra("isTowed", ticket.getIsTowed());
                startActivity(editScreenIntent);
            }
        });

        mNewTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }

                //if search bar empty, go in blank
                //Intent intent = new Intent(TicketListActivity.this, NewTicketActivity.class);
                //startActivity(intent);

            }
        });

        mFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TicketListActivity.this, NewTicketActivity.class);
                startActivity(intent);
            }
        });

        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFABMenuForTextPrompt();
                passwordPromptForException();
            }
        });

        mSearchFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshScheduleListViewOnSearch(mSearchFilter.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void showFABMenu(){
        isFABOpen=true;
        mNewTicket.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation_cw));
        mFab1.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        mFab2.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        btn1Text.setVisibility(View.VISIBLE);
        btn2Text.setVisibility(View.VISIBLE);
        btn1Text.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        btn2Text.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        mNewTicket.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation_ccw));
        mFab1.animate().translationY(0);
        mFab2.animate().translationY(0);
        btn1Text.animate().translationY(0);
        btn2Text.animate().translationY(0);
        btn1Text.setVisibility(View.GONE);
        btn2Text.setVisibility(View.GONE);
    }

    private void hideFABMenuForTextPrompt() {
        mNewTicket.hide();
        mFab2.hide();
        mFab1.hide();
        btn1Text.setVisibility(View.GONE);
        btn2Text.setVisibility(View.GONE);
    }

    private void showFABMenuForTextPrompt() {
        mNewTicket.show();
        mFab2.show();
        mFab1.show();
        btn1Text.setVisibility(View.VISIBLE);
        btn2Text.setVisibility(View.VISIBLE);
    }




    @Override
    protected void onResume() {
        super.onResume();
        mSearchFilter.setText("");
        refreshScheduleListView();
        showFABMenuForTextPrompt();
        closeFABMenu();
    }

    public void onInfoClick(MenuItem mi) {
        Intent intent = new Intent(TicketListActivity.this, InfoActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void refreshScheduleListView() {
        ArrayList scheduleList = getListOfAllTickets();
        mListView.setAdapter(new TicketCustomListAdapter(this, scheduleList));
    }

    private void refreshScheduleListViewOnSearch(String searchQuery) {
        ArrayList scheduleList = getListOfAllTicketsFromSearch(searchQuery);
        mListView.setAdapter(new TicketCustomListAdapter(this, scheduleList));
    }

    private ArrayList getListOfAllTickets() {
        //get data & append to list
        Cursor data = mDatabaseHelper.getAllTickets();
        ArrayList<Ticket> tickets = new ArrayList<>();
        while (data.moveToNext()) {
            Ticket ticket = new Ticket();
            ticket.setTicketId(data.getInt(0));
            ticket.setPlate(data.getString(1));
            ticket.setState(data.getString(2));
            ticket.setDateTime(data.getString(3));
            ticket.setInfraction(data.getString(4));
            ticket.setLocation(data.getString(5));
            ticket.setNotes(data.getString(6));
            ticket.setLicencePhoto(data.getBlob(7));
            ticket.setTicketPhoto(data.getBlob(8));
            ticket.setType(data.getInt(9));
            ticket.setIsTowed(data.getInt(10));
            ticket.setCount(mDatabaseHelper.getLicenceCount(data.getString(1),data.getString(2)));
            tickets.add(ticket);
        }
        return tickets;

        //create the list adapter and set the adapter
        //ListAdapter listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listData);
        //mListView.setAdapter(listAdapter);

    }

    private ArrayList getListOfAllTicketsFromSearch(String searchQuery) {
        //get data & append to list
        Cursor data = mDatabaseHelper.getAllTicketsFromSearch(searchQuery);
        ArrayList<Ticket> tickets = new ArrayList<>();
        while (data.moveToNext()) {
            Ticket ticket = new Ticket();
            ticket.setTicketId(data.getInt(0));
            ticket.setPlate(data.getString(1));
            ticket.setState(data.getString(2));
            ticket.setDateTime(data.getString(3));
            ticket.setInfraction(data.getString(4));
            ticket.setLocation(data.getString(5));
            ticket.setNotes(data.getString(6));
            ticket.setLicencePhoto(data.getBlob(7));
            ticket.setTicketPhoto(data.getBlob(8));
            ticket.setType(data.getInt(9));
            ticket.setIsTowed(data.getInt(10));
            ticket.setCount(mDatabaseHelper.getLicenceCount(data.getString(1),data.getString(2)));
            tickets.add(ticket);
        }
        return tickets;

        //create the list adapter and set the adapter
        //ListAdapter listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listData);
        //mListView.setAdapter(listAdapter);

    }

    public void passwordPromptForException() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Password Required");
        builder.setMessage("This action will allow you to create an exception for a permitted vehicle.");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUserEnteredPass = input.getText().toString();

                if(mUserEnteredPass.equals(mPassword)) {
                    //user entered correct pass, open edit
                    Intent editScreenIntent = new Intent(TicketListActivity.this, NewExceptionActivity.class);
                    startActivity(editScreenIntent);

                } else {

                    Toast toast = Toast.makeText(TicketListActivity.this,"Password incorrect",Toast.LENGTH_LONG);
                    View view = toast.getView();
                    view.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);
                    TextView text = view.findViewById(android.R.id.message);
                    text.setTextColor(Color.WHITE);
                    toast.show();

                    dialog.cancel();
                    showFABMenuForTextPrompt();
                    closeFABMenu();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                showFABMenuForTextPrompt();
                closeFABMenu();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showFABMenuForTextPrompt();
                closeFABMenu();
            }
        });

        builder.show();

    }

}
