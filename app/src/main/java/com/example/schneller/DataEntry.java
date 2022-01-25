package com.example.schneller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataEntry extends AppCompatActivity {
    private String currentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private EditText eanEditText, testIdEditText, testNameEditText, manufacturerEditText;
    private TextWatcher eanTextWatcher = new TextWatcher() {
        private Boolean busy = false;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // pass
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // pass
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!busy) {
                busy = true;
                String ean = eanEditText.getText().toString();
                String eanStripped = ean.replaceAll("\\s", "");
                String eanNew = "";
                if (eanStripped.length() > 13) {
                    eanEditText.setError(getResources().getString(R.string.ean_length_error));
                    eanStripped = eanStripped.substring(0, 13);
                }
                if (eanStripped.length() > 1) {
                    eanNew += eanStripped.charAt(0) + " ";
                    eanStripped = eanStripped.substring(1);
                    if (eanStripped.length() > 6) {
                        eanNew += eanStripped.substring(0, 6) + " ";
                        eanStripped = eanStripped.substring(6);
                    }
                }
                eanNew += eanStripped;
                eanEditText.setText(eanNew);
                eanEditText.setSelection(eanNew.length());
                busy = false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_entry);
        // setup toolbar
        Toolbar toolbar = findViewById(R.id.data_entry_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> {
                finish();
            });

        // set up ImageView
        currentPhotoPath = "";
        ImageView imageView = findViewById(R.id.entry_test_image);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // setup TextWatcher EAN
        eanEditText = findViewById(R.id.edit_ean);
        eanEditText.addTextChangedListener(eanTextWatcher);
        eanEditText.setOnFocusChangeListener((View v, boolean hasFocus) -> {
           if (!hasFocus) {
               String eanText = eanEditText.getText().toString().replaceAll("\\s", "");
               if (eanText.length() != 13) {
                   eanEditText.setError(getResources().getString(R.string.ean_length_error));
               }
               else {
                   eanEditText.setError(null);
               }
           }
        });

        // setup TextWatcher test id
        testIdEditText = findViewById(R.id.edit_test_id);
        testIdEditText.setOnFocusChangeListener((View v, boolean hasFocus) -> {
            String text = testIdEditText.getText().toString();
            if(hasFocus) {
                if (text.equals("")) {
                    testIdEditText.setText("AT");
                    testIdEditText.setSelection(2);
                }
            } else {
                text = text.toUpperCase();
                if (!text.matches(getString(R.string.test_id_regex))) {
                    testIdEditText.setError(getString(R.string.test_id_error));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.data_entry_menu, menu);


        // set up send button
        MenuItem sendButton = menu.findItem(R.id.send);
        sendButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                PostNewData postNewData = new PostNewData(getString(R.string.server_url));
                postNewData.execute(
                        ((EditText) findViewById(R.id.edit_ean)).getText().toString().replace(" ", ""),
                        ((EditText) findViewById(R.id.edit_test_id)).getText().toString(),
                        ((EditText) findViewById(R.id.edit_name)).getText().toString(),
                        ((EditText) findViewById(R.id.edit_manufacturer)).getText().toString(),
                        currentPhotoPath
                );
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        }
    }


    private void dispatchTakePictureIntent() {  // todo remove use of deprecated functions
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()   ) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                // pass
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        ImageView imageView = findViewById(R.id.entry_test_image);
        int targetWidth = imageView.getWidth();
        int targetHeight = imageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int actualWidth = bmOptions.outWidth;
        int actualHeight= bmOptions.outHeight;

        int scaleFactor = Math.max(1, Math.max(actualWidth/targetWidth, actualHeight/targetHeight));

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }
}
