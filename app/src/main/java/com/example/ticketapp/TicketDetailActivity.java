package com.example.ticketapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TicketDetailActivity extends AppCompatActivity {

    private static final String TAG = "TicketDetailActivity";
    private final String mPassword  = "Base0ps9";

    private DatabaseHelper mDatabaseHelper;
    private TextView selectedLicenceTextView, selectedNotesTextView, selectedDateTimeTextView,
            selectedStateSelector, selectedInfractionSelector, selectedLocationSelector;
    private FloatingActionButton backTicketButton, editTicketButton, deleteTicketButton;
    private ImageView selectedLicencePhotoImageView, selectedTicketImageView;
    private CheckBox isTowedCheck;
    private AutoCompleteTextView inputACTV;

    private Integer mSelectedTicketId, mSelectedTypeId;
    private String mUserEnteredPass = "";
    private byte[] img1, img2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_ticket_main);

        mDatabaseHelper = new DatabaseHelper(this);
        selectedLicenceTextView = findViewById(R.id.selectedLicenceTextView);
        selectedNotesTextView = findViewById(R.id.selectedNotesTextView);
        selectedDateTimeTextView = findViewById(R.id.selectedDateTimeTextView);
        selectedStateSelector = findViewById(R.id.selectedStateSelector);
        selectedInfractionSelector = findViewById(R.id.selectedInfractionSelector);
        selectedLocationSelector = findViewById(R.id.selectedLocationSelector);
        backTicketButton = findViewById(R.id.backTicketButton);
        selectedLicencePhotoImageView = findViewById(R.id.selectedLicencePhotoImageView);
        selectedTicketImageView = findViewById(R.id.selectedTicketPhotoImageView);
        editTicketButton = findViewById(R.id.editTicketButton);
        deleteTicketButton = findViewById(R.id.deleteTicketButton);
        isTowedCheck = findViewById(R.id.selectedTowedSwitch);
        inputACTV = findViewById(R.id.input);
        img1 = null;
        img2 = null;

        getIntentExtras();

        backTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordPromptForEdit();
            }
        });

        deleteTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordPromptForDelete();
            }
        });

        if (mSelectedTypeId == 1) {
            this.setTitle("Ticket Details");
        } else {
            this.setTitle("Exception Details");
            selectedTicketImageView.setVisibility(View.GONE);
            editTicketButton.hide();
            deleteTicketButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorBlue)));
            backTicketButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorBlue)));
        }

    }

    private void getIntentExtras() {
        Intent receivedIntent = getIntent();
        mSelectedTicketId = receivedIntent.getIntExtra("ticketId", -1);
        selectedLicenceTextView.setText(receivedIntent.getStringExtra("plate"));
        selectedStateSelector.setText(receivedIntent.getStringExtra("state"));
        selectedInfractionSelector.setText(receivedIntent.getStringExtra("infraction"));
        selectedLocationSelector.setText(receivedIntent.getStringExtra("location"));
        selectedDateTimeTextView.setText(receivedIntent.getStringExtra("dateTime"));
        selectedNotesTextView.setText(receivedIntent.getStringExtra("notes"));
        try {
            selectedLicencePhotoImageView.setImageBitmap(DBBitmapUtility.getImage(receivedIntent.getByteArrayExtra("licence_photo")));
            img1 = receivedIntent.getByteArrayExtra("licence_photo");
        } catch (Exception e) {
            selectedLicencePhotoImageView.setImageResource(R.drawable.no_image_prov);
        }
        try {
            selectedTicketImageView.setImageBitmap(DBBitmapUtility.getImage(receivedIntent.getByteArrayExtra("ticket_photo")));
            img2 = receivedIntent.getByteArrayExtra("ticket_photo");
        } catch (Exception e) {
            selectedTicketImageView.setImageResource(R.drawable.no_image_prov);
        }
        mSelectedTypeId = receivedIntent.getIntExtra("type",-1);
        isTowedCheck.setChecked(receivedIntent.getIntExtra("isTowed", -1) == 1 ? true : false);
    }

    public void passwordPromptForEdit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Password Required");
        builder.setMessage("This action will allow you to EDIT the ticket. Are you sure?");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUserEnteredPass = input.getText().toString();

                if(mUserEnteredPass.equals(mPassword)) {
                    //user entered correct pass, open edit
                    Intent editScreenIntent = new Intent(TicketDetailActivity.this, EditTicketActivity.class);
                    editScreenIntent.putExtra("ticketId", mSelectedTicketId);
                    editScreenIntent.putExtra("plate", selectedLicenceTextView.getText().toString());
                    editScreenIntent.putExtra("state", selectedStateSelector.getText().toString());
                    editScreenIntent.putExtra("dateTime", selectedDateTimeTextView.getText().toString());
                    editScreenIntent.putExtra("infraction", selectedInfractionSelector.getText().toString());
                    editScreenIntent.putExtra("location", selectedLocationSelector.getText().toString());
                    editScreenIntent.putExtra("notes", selectedNotesTextView.getText().toString());
                    editScreenIntent.putExtra("licence_photo", img1);
                    editScreenIntent.putExtra("ticket_photo", img2);
                    editScreenIntent.putExtra("isTowed", isTowedCheck.isChecked() ? 1 : 0);
                    startActivity(editScreenIntent);

                } else {

                    Toast toast = Toast.makeText(TicketDetailActivity.this,"Password incorrect",Toast.LENGTH_LONG);
                    View view = toast.getView();
                    view.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);
                    TextView text = view.findViewById(android.R.id.message);
                    text.setTextColor(Color.WHITE);
                    toast.show();

                    dialog.cancel();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void passwordPromptForDelete() {
        String keyword = "";

        if (mSelectedTypeId == 1) {
            keyword = "Ticket";
        } else {
            keyword = "Exception";
        }
        final String tOrE = keyword;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Password Required");
        builder.setMessage("This action will DELETE the " + keyword + " from the database. Are you sure?");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUserEnteredPass = input.getText().toString();

                if(mUserEnteredPass.equals(mPassword)) {
                    //user entered correct pass, open delete
                    mDatabaseHelper.deleteTicket(mSelectedTicketId);
                    //record deleted record in DELETIONS db

                    Calendar cal = Calendar.getInstance(Locale.getDefault());
                    Date currentDate = cal.getTime();
                    String deletionDateTime = (new SimpleDateFormat("HH:mm  MM/dd/yyy").format(currentDate));

                    mDatabaseHelper.recordDeletion(mSelectedTicketId, selectedLicenceTextView.getText().toString(),
                            selectedStateSelector.getText().toString(), selectedDateTimeTextView.getText().toString(),
                            selectedInfractionSelector.getText().toString(), selectedLocationSelector.getText().toString(),
                            selectedNotesTextView.getText().toString(), mSelectedTypeId, deletionDateTime);

                    Toast toast = Toast.makeText(TicketDetailActivity.this,tOrE + " deleted successfully",Toast.LENGTH_LONG);
                    View view = toast.getView();
                    view.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);
                    TextView text = view.findViewById(android.R.id.message);
                    text.setTextColor(Color.WHITE);
                    toast.show();

                    dialog.cancel();
                    finish();
                } else {

                    Toast toast = Toast.makeText(TicketDetailActivity.this,"Password incorrect",Toast.LENGTH_LONG);
                    View view = toast.getView();
                    view.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);
                    TextView text = view.findViewById(android.R.id.message);
                    text.setTextColor(Color.WHITE);
                    toast.show();

                    dialog.cancel();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    }
