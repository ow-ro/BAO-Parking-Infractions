package com.example.ticketapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewExceptionActivity extends AppCompatActivity {

    private static final String TAG = "NewExceptionActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private DatabaseHelper mDatabaseHelper;
    private EditText exLicence, exNotes;
    private Spinner exState, exInfraction, exLocation;
    private Button exPhoto;
    private ImageView exImageView;
    private TextView exDateTime;
    private FloatingActionButton exDone;
    private boolean isPhotoTaken = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_exception_main);

        this.setTitle("New Exception");

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        Date currentDate = cal.getTime();
        String dateTime = (new SimpleDateFormat("HH:mm  MM/dd/yyy").format(currentDate));

        mDatabaseHelper = new DatabaseHelper(this);
        exLicence = findViewById(R.id.exLicenceTextEdit);
        exNotes = findViewById(R.id.exNotesTextEdit);
        exState = findViewById(R.id.exStateSelector);
        exInfraction = findViewById(R.id.exInfractionSelector);
        exLocation = findViewById(R.id.exLocationSelector);
        exPhoto = findViewById(R.id.exLicencePhotoLabel);
        exDateTime = findViewById(R.id.exDateTimeTextView);
        exImageView = findViewById(R.id.exLicencePhotoImageView);
        exDone = findViewById(R.id.exDoneButton);
        exDateTime.setText(dateTime);

        // default images warning user to take pics
        exImageView.setImageResource(R.drawable.no_image_prov);

        // Force caps on licence plate entry
        exLicence.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

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
        exState.setAdapter(stateSelectorDataAdapter);
        exInfraction.setAdapter(infractionSelectorDataAdapter);
        exLocation.setAdapter(locationSelectorDataAdapter);


        exPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        exDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exceptionDataValidation()) {
                    try {

                        byte[] img = null;

                        try {
                            // verify if licence photo was taken
                            exImageView.invalidate();
                            BitmapDrawable lDrawable = (BitmapDrawable) exImageView.getDrawable();
                            Bitmap lBitmap = lDrawable.getBitmap();
                            img = DBBitmapUtility.getBytes(lBitmap);
                        } catch (Exception e) {
                            // no image, so insert blank
                        }

                        // validation checks passed, so insert to database
                        boolean ticketAdded = mDatabaseHelper.addTicket(exLicence.getText().toString().toUpperCase(),
                                exState.getSelectedItem().toString(),exDateTime.getText().toString(),
                                exInfraction.getSelectedItem().toString(),exLocation.getSelectedItem().toString(),
                                exNotes.getText().toString(), img, null, 2, 3);

                        if (ticketAdded) {

                            Toast toast = Toast.makeText(NewExceptionActivity.this,"Exception added successfully",Toast.LENGTH_LONG);
                            View view = toast.getView();
                            view.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);
                            TextView text = view.findViewById(android.R.id.message);
                            text.setTextColor(Color.WHITE);
                            toast.show();

                            finish();
                        } else {
                            Toast.makeText(NewExceptionActivity.this,"Exception not added",Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(NewExceptionActivity.this,"Error adding Exception: " + e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    private boolean exceptionDataValidation() {
        boolean isValid = false;

        if (exLicence.getText().toString().equals("")) {
            Toast.makeText(NewExceptionActivity.this,"You need to enter a licence plate",Toast.LENGTH_LONG).show();
            return isValid;
        }

        if ((exState.getSelectedItemPosition() < 0) ||
                (exState.getSelectedItem().toString().equals("Select State/Province")) ||
                (exState.getSelectedItem().toString().equals("---"))) {
            Toast.makeText(NewExceptionActivity.this,"You need to select a state/province",Toast.LENGTH_LONG).show();
            return isValid;
        }

        if ((exInfraction.getSelectedItemPosition() < 0) ||
                (exInfraction.getSelectedItem().toString().equals("Select Infraction Type"))) {
            Toast.makeText(NewExceptionActivity.this,"You need to select an infraction type",Toast.LENGTH_LONG).show();
            return isValid;
        }

        if ((exLocation.getSelectedItemPosition() < 0) ||
                (exLocation.getSelectedItem().toString().equals("Select Location"))) {
            Toast.makeText(NewExceptionActivity.this,"You need to select a location",Toast.LENGTH_LONG).show();
            return isValid;
        }

        if (exNotes.getText().toString().equals("")) {
            Toast.makeText(NewExceptionActivity.this,"You need to enter a description of the exception in the Notes field",Toast.LENGTH_LONG).show();
            return isValid;
        }

        if (!isPhotoTaken) {
            Toast.makeText(NewExceptionActivity.this,"You need to take an image of the vehicle/licence",Toast.LENGTH_LONG).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Log.d("Bitmap dimensions","height: " + imageBitmap.getHeight() + ", width: " + imageBitmap.getWidth());
                exImageView.setImageBitmap(imageBitmap);
                isPhotoTaken = true;
        }
    }

}
