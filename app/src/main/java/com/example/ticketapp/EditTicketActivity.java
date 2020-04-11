package com.example.ticketapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.ticketapp.NewTicketActivity.REQUEST_IMAGE_CAPTURE;

public class EditTicketActivity extends AppCompatActivity {


    private DatabaseHelper mDatabaseHelper;
    private int editModeTicketId;
    private TextView editModeLicenceTextView, editModeNotesTextView, editModeDateTimeTextView;
    private Spinner editModeStateSelector, editModeInfractionSelector, editModeLocationSelector;
    private FloatingActionButton saveTicketButton;
    private ImageView editModeLicencePhotoImageView, editModeTicketImageView;
    private Button editModeLicencePhotoLabel, editModeTicketPhotoLabel;
    private CheckBox isTowedCheck;

    private String mButtonPressed = "";
    private boolean mIsLicencePhotoTaken = true;
    private boolean mIsTicketPhotoTaken = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_ticket_main);

        this.setTitle("Edit Ticket");

        mDatabaseHelper = new DatabaseHelper(this);
        editModeLicenceTextView = findViewById(R.id.editModeLicence);
        editModeNotesTextView = findViewById(R.id.editModeNotesTextEdit);
        editModeDateTimeTextView = findViewById(R.id.editModeDateTimeTextView);
        editModeStateSelector = findViewById(R.id.editModeStateSelector);
        editModeInfractionSelector = findViewById(R.id.editModeInfractionSelector);
        editModeLocationSelector = findViewById(R.id.editModeLocationSelector);
        saveTicketButton = findViewById(R.id.saveTicketButton);
        editModeLicencePhotoImageView = findViewById(R.id.editModeLicencePhotoImageView);
        editModeTicketImageView = findViewById(R.id.editModeTicketPhotoImageView);
        editModeLicencePhotoLabel = findViewById(R.id.editModeLicencePhotoLabel);
        editModeTicketPhotoLabel = findViewById(R.id.editModeTicketPhotoLabel);
        isTowedCheck = findViewById(R.id.editTowedSwitch);

        // Force caps on licence plate entry
        editModeLicenceTextView.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

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
        editModeStateSelector.setAdapter(stateSelectorDataAdapter);
        editModeInfractionSelector.setAdapter(infractionSelectorDataAdapter);
        editModeLocationSelector.setAdapter(locationSelectorDataAdapter);

        editModeLicencePhotoLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonPressed = "licence";
                dispatchTakePictureIntent();
            }
        });

        editModeTicketPhotoLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonPressed = "ticket";
                dispatchTakePictureIntent();
            }
        });

        saveTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ticketDataValidation()) {

                    try {

                        byte[] licenceImg = null;
                        byte[] ticketImg = null;

                        try {
                            // verify if licence photo was taken
                            editModeLicencePhotoImageView.invalidate();
                            BitmapDrawable lDrawable = (BitmapDrawable) editModeLicencePhotoImageView.getDrawable();
                            Bitmap lBitmap = lDrawable.getBitmap();
                            licenceImg = DBBitmapUtility.getBytes(lBitmap);

                            // verify if licence photo was taken
                            editModeTicketImageView.invalidate();
                            BitmapDrawable tDrawable = (BitmapDrawable) editModeTicketImageView.getDrawable();
                            Bitmap tBitmap = tDrawable.getBitmap();
                            ticketImg = DBBitmapUtility.getBytes(tBitmap);

                        } catch (Exception e) {
                            // no image, so insert blank
                        }

                        // validation checks passed, so insert to database
                        boolean ticketUpdated = mDatabaseHelper.updateTicket(editModeTicketId, editModeLicenceTextView.getText().toString().toUpperCase(),
                                editModeStateSelector.getSelectedItem().toString(),editModeDateTimeTextView.getText().toString(),
                                editModeInfractionSelector.getSelectedItem().toString(),editModeLocationSelector.getSelectedItem().toString(),
                                editModeNotesTextView.getText().toString(), licenceImg, ticketImg, isTowedCheck.isChecked() ? 1 : 0);

                        if (ticketUpdated) {

                            Toast toast = Toast.makeText(EditTicketActivity.this,"Ticket updated successfully",Toast.LENGTH_LONG);
                            View view = toast.getView();
                            view.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);
                            TextView text = view.findViewById(android.R.id.message);
                            text.setTextColor(Color.WHITE);
                            toast.show();

                            Intent editScreenIntent = new Intent(EditTicketActivity.this, TicketListActivity.class);
                            startActivity(editScreenIntent);

                            //finish();
                        } else {
                            Toast.makeText(EditTicketActivity.this,"Ticket not updated",Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(EditTicketActivity.this,"Error updating Ticket: " + e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        getIntentExtras();

    }

    private void getIntentExtras() {
        Intent receivedIntent = getIntent();
        editModeTicketId = receivedIntent.getIntExtra("ticketId", -1);
        editModeLicenceTextView.setText(receivedIntent.getStringExtra("plate"));
        editModeStateSelector.setSelection(getSpinnerIndex(editModeStateSelector,receivedIntent.getStringExtra("state")));
        editModeInfractionSelector.setSelection(getSpinnerIndex(editModeInfractionSelector,receivedIntent.getStringExtra("infraction")));
        editModeLocationSelector.setSelection(getSpinnerIndex(editModeLocationSelector,receivedIntent.getStringExtra("location")));
        editModeDateTimeTextView.setText(receivedIntent.getStringExtra("dateTime"));
        editModeNotesTextView.setText(receivedIntent.getStringExtra("notes"));
        try {
            editModeLicencePhotoImageView.setImageBitmap(DBBitmapUtility.getImage(receivedIntent.getByteArrayExtra("licence_photo")));
        } catch (Exception e) {
            editModeLicencePhotoImageView.setImageResource(R.drawable.no_image_prov);
        }
        try {
            editModeTicketImageView.setImageBitmap(DBBitmapUtility.getImage(receivedIntent.getByteArrayExtra("ticket_photo")));
        } catch (Exception e) {
            editModeTicketImageView.setImageResource(R.drawable.no_image_prov);
        }
        isTowedCheck.setChecked(receivedIntent.getIntExtra("isTowed", -1) == 1 ? true : false);
    }

    //private method of your class
    private int getSpinnerIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
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

            if (mButtonPressed.equals("licence")) {
                editModeLicencePhotoImageView.setImageBitmap(imageBitmap);
                mIsLicencePhotoTaken = true;
            } else if (mButtonPressed.equals("ticket")) {
                editModeTicketImageView.setImageBitmap(imageBitmap);
                mIsTicketPhotoTaken = true;
            } else {
            }
        }
    }

    private boolean ticketDataValidation() {
        boolean isValid = false;

        if (editModeLicenceTextView.getText().toString().equals("")) {
            Toast.makeText(EditTicketActivity.this,"You need to enter a licence plate",Toast.LENGTH_LONG).show();
            return isValid;
        }

        if ((editModeStateSelector.getSelectedItemPosition() < 0) ||
                (editModeStateSelector.getSelectedItem().toString().equals("Select State/Province")) ||
                (editModeStateSelector.getSelectedItem().toString().equals("---"))) {
            Toast.makeText(EditTicketActivity.this,"You need to select a state/province",Toast.LENGTH_LONG).show();
            return isValid;
        }

        if ((editModeInfractionSelector.getSelectedItemPosition() < 0) ||
                (editModeInfractionSelector.getSelectedItem().toString().equals("Select Infraction Type"))) {
            Toast.makeText(EditTicketActivity.this,"You need to select an infraction type",Toast.LENGTH_LONG).show();
            return isValid;
        }

        if ((editModeLocationSelector.getSelectedItemPosition() < 0) ||
                (editModeLocationSelector.getSelectedItem().toString().equals("Select Location"))) {
            Toast.makeText(EditTicketActivity.this,"You need to select a location",Toast.LENGTH_LONG).show();
            return isValid;
        }

        if (!mIsLicencePhotoTaken) {
            Toast.makeText(EditTicketActivity.this,"You need to take an image of the vehicle/licence",Toast.LENGTH_LONG).show();
            return isValid;
        }

        if (!mIsTicketPhotoTaken) {
            Toast.makeText(EditTicketActivity.this,"You need to take an image of the infraction ticket",Toast.LENGTH_LONG).show();
            return isValid;
        }

        return true;

    }

}
