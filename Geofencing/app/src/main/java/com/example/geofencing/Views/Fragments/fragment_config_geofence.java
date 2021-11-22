package com.example.geofencing.Views.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.geofencing.Interface.InterfaceConfigGeofence;
import com.example.geofencing.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class fragment_config_geofence extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private SeekBar seekBar;
    private Button  cmdConfirmar;
    private TextView lblMetros;
    private InterfaceConfigGeofence caller=null;
    private float radius;


    // TODO: Rename and change types of parameters
    public fragment_config_geofence(Activity activity, float radiusDefault) {
        this.radius= radiusDefault;
        this.caller=(InterfaceConfigGeofence) activity;
        // Required empty public constructor
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);
        handleUserExit();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.fragment_config_geofence, null);
        dialog.setContentView(contentView);

        BottomSheetBehavior<View> mBottomSheetBehavior = BottomSheetBehavior.from(((View) contentView
                .getParent()));
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.addBottomSheetCallback(mBottomSheetBehaviorCallback);
            mBottomSheetBehavior.setPeekHeight(1200);
        }

        seekBar = (SeekBar) contentView.findViewById(R.id.seekBar);
        cmdConfirmar = (Button) contentView.findViewById(R.id.cmdConfirmar);
        lblMetros = (TextView) contentView.findViewById(R.id.lblMetros);

        seekBar.setOnSeekBarChangeListener(listenerSeekBar);
        cmdConfirmar.setOnClickListener(listenerButton);

        seekBar.setProgress((int)radius);


    }
    private final BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new
            BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(View bottomSheet, int newState) {
                    String state = null;

                    switch (newState) {
                        case BottomSheetBehavior.STATE_COLLAPSED:
                            state = "STATE_COLLAPSED";
                            break;
                        case BottomSheetBehavior.STATE_DRAGGING:
                            state = "STATE_DRAGGING";
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:
                            state = "STATE_EXPANDED";
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:
                            state = "STATE_SETTLING";
                            break;
                        case BottomSheetBehavior.STATE_HIDDEN:
                            state = "STATE_HIDDEN";
                            //call ALWAYS dismiss to hide the modal background
                            handleUserExit();
                            dismiss();
                            break;
                    }

                    Log.d(fragment_config_geofence.class.getSimpleName(), state);
                }

                @Override
                public void onSlide(View bottomSheet, float slideOffset) {
                    Log.d(fragment_config_geofence.class.getSimpleName(), String.valueOf(slideOffset));
                }
    };

    private Button.OnClickListener listenerButton = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            caller.addGeofenceGraphic(radius);
            }
    };
    private SeekBar.OnSeekBarChangeListener listenerSeekBar = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            radius=(float)progress;

            lblMetros.setText(String.valueOf(progress));
            caller.updateCircleGraphic(radius);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

    };

    private void handleUserExit()
    {
        caller.clearGeofenceMaps();
    }

}


