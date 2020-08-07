package com.manishbsta.easyrecharge;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class SIMChooserDialog extends DialogFragment {
    String sim = "";
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_sim_chooser, null);

        RadioGroup radioGroup = view.findViewById(R.id.rgSIM);
        final TextView txtError = view.findViewById(R.id.txtError);
        Button buttonChoose = view.findViewById(R.id.btnProceed);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbNTC:
                        sim = "412";
                        txtError.setVisibility(View.GONE);
                        break;

                    case R.id.rbNCELL:
                        sim = "102";
                        txtError.setVisibility(View.GONE);
                        break;
                }
            }
        });

        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(sim)) {
                    txtError.setVisibility(View.VISIBLE);
                } else {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("sim_code", sim);
                    startActivity(intent);
                }
            }
        });

        builder.setView(view);
        return builder.create();
    }
}
