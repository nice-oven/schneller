package com.example.schneller;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ModalBottomSheet extends BottomSheetDialogFragment {
    private Testcheck testcheck;
    private MainActivity mainActivity = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.bottom_sheet, container, false);
        ((TextView) v.findViewById(R.id.name)).setText(testcheck.getName());
        ((TextView) v.findViewById(R.id.manufacturer)).setText(testcheck.getManufacturer());
        ((TextView) v.findViewById(R.id.test_id)).setText(testcheck.getTest_id());
        TextView peiChecked = (TextView) v.findViewById(R.id.pei_checked);
        if (testcheck.getPeiTested() == Boolean.TRUE) {
            peiChecked.setText(R.string.tested);
            peiChecked.setBackgroundColor(getResources().getColor(R.color.green));
            String showSensitivity = String.format(getResources().getString(R.string.sensitivity), testcheck.getSensitivity());
            ((TextView) v.findViewById(R.id.sensitivity)).setText(showSensitivity);
            String showSpecificity = String.format(getResources().getString(R.string.specificity), testcheck.getSpecificity());
            ((TextView) v.findViewById(R.id.specificity)).setText(showSpecificity);
        } else {
            peiChecked.setText(R.string.not_tested);
            peiChecked.setBackgroundColor(getResources().getColor(R.color.orange));
        }
        return v;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                setupRatio(bottomSheetDialog);
            }
        });
        return d;
    }

        @Override
        public void onDestroy() {
        super.onDestroy();
        if (mainActivity != null) {
            mainActivity.setupPreview();
        }
    }


    private void setupRatio(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.standard_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
        layoutParams.height = getBottomSheetDialogDefaultHeight();
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setFitToContents(false);
        behavior.setSkipCollapsed(true);
        behavior.setExpandedOffset(0);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getBottomSheetDialogDefaultHeight() {
        return (int) (getWindowHeight() * 0.87);
    }

    private int getWindowHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public void setData(Testcheck testcheck) {
        this.testcheck = testcheck;
    }

    public void setSecondActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}
