package com.example.ticketapp;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;

public class MyBackupAgent extends BackupAgentHelper {

    @Override
    public void onCreate(){
        FileBackupHelper dbs = new FileBackupHelper(this, "../databases/" + DatabaseHelper.DATABASE_NAME);
        addHelper(DatabaseHelper.DATABASE_NAME, dbs);
    }
}
