package com.example.schneller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AlertActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.ean_not_found);
        builder.setTitle(R.string.ean_not_found);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent returnIntent = new Intent();
                setResult(AppCompatActivity.RESULT_OK, returnIntent);
                AlertActivity.this.finish();
            }
        });
        //builder.setPositiveButton();
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
