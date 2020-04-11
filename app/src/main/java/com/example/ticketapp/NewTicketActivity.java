package com.example.ticketapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NewTicketActivity extends AppCompatActivity {

    private static final String TAG = "NewTicketActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private DatabaseHelper mDatabaseHelper;
    private EditText licenceTextEdit, notesTextEdit;
    private Spinner stateSelector, infractionSelector, locationSelector;
    private TextView dateTimeTextView;
    private Button licencePhotoLabel, ticketPhotoLabel;
    private ImageView licencePhotoImageView, ticketPhotoImageView;
    private CheckBox isTowedCheck;
    private FloatingActionButton doneTicket;

    private String mButtonPressed = "";
    private boolean mIsLicencePhotoTaken = false;
    private boolean mIsTicketPhotoTaken = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_ticket_main);

        this.setTitle("New Ticket");

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        Date currentDate = cal.getTime();
        String dateTime = (new SimpleDateFormat("HH:mm  MM/dd/yyy").format(currentDate));

        mDatabaseHelper = new DatabaseHelper(this);
        licenceTextEdit = findViewById(R.id.licenceTextEdit);
        notesTextEdit = findViewById(R.id.notesTextEdit);
        stateSelector = findViewById(R.id.stateSelector);
        infractionSelector = findViewById(R.id.infractionSelector);
        locationSelector = findViewById(R.id.locationSelector);
        dateTimeTextView = findViewById(R.id.dateTimeTextView);
        doneTicket = findViewById(R.id.doneTicketButton);
        licencePhotoLabel = findViewById(R.id.licencePhotoLabel);
        licencePhotoImageView = findViewById(R.id.licencePhotoImageView);
        ticketPhotoLabel = findViewById(R.id.ticketPhotoLabel);
        ticketPhotoImageView = findViewById(R.id.ticketPhotoImageView);
        isTowedCheck = findViewById(R.id.towedSwitch);
        dateTimeTextView = findViewById(R.id.dateTimeTextView);
        dateTimeTextView.setText(dateTime);

        // default images warning user to take pics
        licencePhotoImageView.setImageResource(R.drawable.no_image_prov);
        ticketPhotoImageView.setImageResource(R.drawable.no_image_prov);

        // Force caps on licence plate entry
        licenceTextEdit.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        // Creating adapter for spinners
        final ArrayAdapter<CharSequence> stateSelectorDataAdapter = ArrayAdapter.createFromResource(this,
                R.array.states_array_main, android.R.layout.simple_spinner_item);
        final ArrayAdapter<CharSequence> infractionSelectorDataAdapter = ArrayAdapter.createFromResource(this,
                R.array.infractions_array, android.R.layout.simple_spinner_item);
        final ArrayAdapter<CharSequence> locationSelectorDataAdapter = ArrayAdapter.createFromResource(this,
                R.array.locations_array, android.R.layout.simple_spinner_item);

        // Drop down layout style - list view with radio button
        stateSelectorDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        infractionSelectorDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSelectorDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinners
        stateSelector.setAdapter(stateSelectorDataAdapter);
        infractionSelector.setAdapter(infractionSelectorDataAdapter);
        locationSelector.setAdapter(locationSelectorDataAdapter);

        licencePhotoLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonPressed = "licence";
                dispatchTakePictureIntent();
            }
        });

        ticketPhotoLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonPressed = "ticket";
                dispatchTakePictureIntent();
            }
        });

        doneTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ticketDataValidation()) {
                    try {

                        byte[] licenceImg = null;
                        byte[] ticketImg = null;

                        try {
                            // verify if licence photo was taken
                            licencePhotoImageView.invalidate();
                            BitmapDrawable lDrawable = (BitmapDrawable) licencePhotoImageView.getDrawable();
                            Bitmap lBitmap = lDrawable.getBitmap();
                            licenceImg = DBBitmapUtility.getBytes(lBitmap);

                            // verify if licence photo was taken
                            ticketPhotoImageView.invalidate();
                            BitmapDrawable tDrawable = (BitmapDrawable) ticketPhotoImageView.getDrawable();
                            Bitmap tBitmap = tDrawable.getBitmap();
                            ticketImg = DBBitmapUtility.getBytes(tBitmap);

                        } catch (Exception e) {
                            // no image, so insert blank
                        }

                        // validation checks passed, so insert to database
                        boolean ticketAdded = mDatabaseHelper.addTicket(licenceTextEdit.getText().toString().toUpperCase(),
                                stateSelector.getSelectedItem().toString(),dateTimeTextView.getText().toString(),
                                infractionSelector.getSelectedItem().toString(),locationSelector.getSelectedItem().toString(),
                                notesTextEdit.getText().toString(), licenceImg, ticketImg, 1, isTowedCheck.isChecked() ? 1 : 0);

                        if (ticketAdded) {

                            Toast toast = Toast.makeText(NewTicketActivity.this,"Ticket added successfully",Toast.LENGTH_LONG);
                            View view = toast.getView();
                            view.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);
                            TextView text = view.findViewById(android.R.id.message);
                            text.setTextColor(Color.WHITE);
                            toast.show();

                            finish();
                        } else {
                            Toast.makeText(NewTicketActivity.this,"Ticket not added",Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(NewTicketActivity.this,"Error adding Ticket: " + e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    private boolean ticketDataValidation() {
        boolean isValid = false;

        if (licenceTextEdit.getText().toString().equals("")) {
            Toast.makeText(NewTicketActivity.this,"You need to enter a licence plate",Toast.LENGTH_LONG).show();
            return isValid;
        }

        if ((stateSelector.getSelectedItemPosition() < 0) ||
                (stateSelector.getSelectedItem().toString().equals("Select State/Province")) ||
                (stateSelector.getSelectedItem().toString().equals("---"))) {
            Toast.makeText(NewTicketActivity.this,"You need to select a state/province",Toast.LENGTH_LONG).show();
            return isValid;
        }

        if ((infractionSelector.getSelectedItemPosition() < 0) ||
                (infractionSelector.getSelectedItem().toString().equals("Select Infraction Type"))) {
            Toast.makeText(NewTicketActivity.this,"You need to select an infraction type",Toast.LENGTH_LONG).show();
            return isValid;
        }

        if ((locationSelector.getSelectedItemPosition() < 0) ||
                (locationSelector.getSelectedItem().toString().equals("Select Location"))) {
            Toast.makeText(NewTicketActivity.this,"You need to select a location",Toast.LENGTH_LONG).show();
            return isValid;
        }

        if (!mIsLicencePhotoTaken) {
            Toast.makeText(NewTicketActivity.this,"You need to take an image of the vehicle/licence",Toast.LENGTH_LONG).show();
            return isValid;
        }

        if (!mIsTicketPhotoTaken) {
            Toast.makeText(NewTicketActivity.this,"You need to take an image of the infraction ticket",Toast.LENGTH_LONG).show();
            return isValid;
        }

        return true;

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private File createImageFile() {
        long timeStamp = System.currentTimeMillis();
        String imageFileName = "NAME_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (Exception e) {
        }

        return image;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Log.d("Bitmap dimensions","height: " + imageBitmap.getHeight() + ", width: " + imageBitmap.getWidth());

            if (mButtonPressed.equals("licence")) {
                licencePhotoImageView.setImageBitmap(imageBitmap);
                mIsLicencePhotoTaken = true;
            } else if (mButtonPressed.equals("ticket")) {
                ticketPhotoImageView.setImageBitmap(imageBitmap);
                mIsTicketPhotoTaken = true;
            } else {
            }
        }
    }



}
