package com.jcrawley.remindme;

import android.app.Dialog;
import android.content.DialogInterface;

public interface CustomDialogCloseListener {
    void handleDialogClose(DialogInterface dialogInterface, int minutes, int seconds);
}
