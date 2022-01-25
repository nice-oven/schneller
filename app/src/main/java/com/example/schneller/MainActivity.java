package com.example.schneller;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

import static java.lang.Thread.sleep;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity  implements ImageAnalysis.Analyzer, LifecycleObserver {
    ListView listview;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    BfarmDbHelper dbh;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private BarcodeScanner scanner;
    private ImageAnalysis imageAnalysis;
    private final Lock eanLock = new ReentrantLock();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.second_layout);

        // listview
        listview = (ListView) findViewById(R.id.listView);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Testcheck item = (Testcheck) parent.getItemAtPosition(position);
                if (item.getName().equals(getResources().getString(R.string.no_data)) && item.getManufacturer().equals(getResources().getString(R.string.prompt_enter_data))) {
                    // new enter data activity
                    Intent intent = new Intent(MainActivity.this, DataEntry.class);
                    startActivity(intent);
                } else {
                    ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
                    modalBottomSheet.setData(item);
                    modalBottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
                }
            }
        });
        listview.setVisibility(View.GONE);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // database
        dbh = new BfarmDbHelper(MainActivity.this);
        try {
            dbh.createDatabase();
            dbh.openDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // camera
        // permission
        int permission = checkSelfPermission(Manifest.permission.CAMERA);
        if (permission == PERMISSION_DENIED) {
            boolean shouldShow = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
            // show-logic
            ActivityResultLauncher<String> activityResultLauncher =
                    registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                            isGranted -> {
                                if (isGranted) {
                                    // Permission is granted. Continue the action or workflow in your
                                    // app.
                                } else {
                                    // Explain to the user that the feature is unavailable because the
                                    // features requires a permission that the user has denied. At the
                                    // same time, respect the user's decision. Don't link to system
                                    // settings in an effort to convince the user to change their
                                    // decision.
                                }
                            });
            activityResultLauncher.launch(Manifest.permission.CAMERA);
        }

        // get size of screen below toolbar
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int freeHeight = displayMetrics.heightPixels - toolbar.getHeight();
        int freeWidth = displayMetrics.widthPixels - toolbar.getWidth();

        // ean lock
        // eanLock = new ReentrantLock();

        // barcode scanner
        previewView = (PreviewView) findViewById(R.id.previewView);
        previewView.setMinimumHeight(freeHeight);
        previewView.setMinimumWidth(freeWidth);
        BarcodeScannerOptions barcodeOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build();
        scanner = BarcodeScanning.getClient(barcodeOptions);
        imageAnalysis = new ImageAnalysis.Builder()
            .setTargetResolution(new Size(freeWidth, freeHeight))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build();
        imageAnalysis.setAnalyzer(Runnable::run, this);

        // setup preview
        setupPreview();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setupPreview();
                previewView.setVisibility(View.VISIBLE);
                listview.setVisibility(View.GONE);
                return false; // still do default behavior
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                previewView.setVisibility(View.GONE);
                listview.setVisibility(View.VISIBLE);
                unbindPreview();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void analyze(ImageProxy imageProxy) {

        int h = imageProxy.getHeight();
        int w = imageProxy.getWidth();
        Image mediaImage = imageProxy.getImage();
        if(mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            scanBarcode(image, imageProxy);
        } else {
            imageProxy.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK){
                setupPreview();
            }
        }
    }

    private void scanBarcode(InputImage image, ImageProxy imageProxy) {
        Task<List<Barcode>> result = scanner.process(image)
                .addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Barcode>> task) {
                        if (task.isSuccessful()) {
                            List<Barcode> barcodes = task.getResult();
                            if (barcodes.size() > 0) {
                                Barcode barcode = barcodes.get(0);
                                String ean = barcode.getRawValue();
                                eanDetected(ean);
                            }
                        } else {
                            System.out.println("fail");
                        }
                        imageProxy.close();
                    }
                });
    }

    void setupPreview() {
        previewView.setVisibility(View.VISIBLE);
        listview.setVisibility(View.GONE);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void unbindPreview() {
        try {
            cameraProviderFuture.get().unbindAll();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector,
                preview);
        camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector,
                imageAnalysis, preview);
    }

    private void search(String keyword) {
        ArrayList<Testcheck> tcs = dbh.search(keyword);

        if (tcs == null) {
            tcs = new ArrayList<Testcheck>();
            Testcheck enterData = new Testcheck();
            enterData.setName(getResources().getString(R.string.no_data));
            enterData.setManufacturer(getResources().getString(R.string.prompt_enter_data));
            tcs.add(enterData);
        }
        TestcheckArrayAdapter adapter = new TestcheckArrayAdapter(tcs, getApplicationContext());
        listview.setAdapter(adapter);
    }

    private void eanDetected(String ean) {
        // stop camera and detection
        // search for the ean
        // if found
        //  display bottom sheet with info
        // if not found
        //  prompt ean unknown
        //  after dismissing, continue camera
        previewView.setVisibility(View.GONE);
        listview.setVisibility(View.VISIBLE);
        unbindPreview();
        Testcheck testcheck = dbh.searchByEAN(ean);
        if (testcheck == null) {
            // prompt & restart

            Intent quickSwitch = new Intent(MainActivity.this, AlertActivity.class);
            startActivityForResult(quickSwitch, 123);
        } else {
            ModalBottomSheet bottomSheet = new ModalBottomSheet();
            bottomSheet.setData(testcheck);
            bottomSheet.setSecondActivity(this);
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        }
    }
}
