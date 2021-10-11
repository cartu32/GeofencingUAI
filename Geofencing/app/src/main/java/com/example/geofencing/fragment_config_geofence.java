package com.example.geofencing;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.widget.SeekBar;
import android.widget.Toast;

public class fragment_config_geofence extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private SeekBar seekBar;
    private Button  cmdReinciar;
    private TextView lblMetros;


    // TODO: Rename and change types of parameters
    public fragment_config_geofence() {
        // Required empty public constructor
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
        cmdReinciar = (Button) contentView.findViewById(R.id.cmdReiniciar);
        lblMetros = (TextView) contentView.findViewById(R.id.lblMetros);

        seekBar.setOnSeekBarChangeListener(listenerSeekBar);


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

    private SeekBar.OnSeekBarChangeListener listenerSeekBar = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            lblMetros.setText(String.valueOf(progress));
            Toast.makeText(getContext(), "seekbar progress: " + progress, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Toast.makeText(getContext(), "seekbar touch started!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Toast.makeText(getContext(), "seekbar touch stopped!", Toast.LENGTH_SHORT).show();
        }

    };

}


