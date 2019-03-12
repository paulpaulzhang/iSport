package com.zlm.run.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.zlm.run.R;
import com.zlm.run.activity.IndoorRunningActivity;
import com.zlm.run.tool.NumberPickerUtil;
import com.zlm.run.tool.ShareUtils;

import java.util.Objects;


public class IndoorRunningFragment extends Fragment implements View.OnClickListener {
    private CardView cv_indoor_target;
    private CardView cv_indoor_go;
    private TextView tv_indoor_target;
    private int target;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_indoor_running, container, false);

        findView(view);
        return view;
    }

    private void findView(View view) {
        cv_indoor_go = view.findViewById(R.id.cv_indoor_go);
        cv_indoor_target = view.findViewById(R.id.cv_indoor_target);
        tv_indoor_target = view.findViewById(R.id.tv_indoor_target);

        cv_indoor_target.setOnClickListener(this);
        cv_indoor_go.setOnClickListener(this);
        target = ShareUtils.getInt(getContext(), "run_target", 1);
        tv_indoor_target.setText(target + "公里");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cv_indoor_target:
                numberPicker();
                break;
            case R.id.cv_indoor_go:
                startActivity(new Intent(getContext(), IndoorRunningActivity.class));
                break;
            default:
        }
    }

    private void numberPicker() {
        View pickerView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_number_picker, null);
        View customerTitleView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_customer_title, null);
        NumberPicker mNumberPicker = pickerView.findViewById(R.id.dialog_np);
        mNumberPicker.setMinValue(1);
        mNumberPicker.setMaxValue(10);
        mNumberPicker.setValue(target);
        NumberPickerUtil.setNumberPickerDividerColor(mNumberPicker, R.color.colorAccent, getContext());
        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                target = i1;
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setView(pickerView)
                .setCustomTitle(customerTitleView)
                .setCancelable(true)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tv_indoor_target.setText(target + "公里");
                        ShareUtils.putInt(getContext(), "run_target", target);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
