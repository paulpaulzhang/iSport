package com.zlm.run.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.zlm.run.R;
import com.zlm.run.activity.IndoorRunningActivity;
import com.zlm.run.activity.OutdoorRunningActivity;
import com.zlm.run.tool.NumberPickerUtil;
import com.zlm.run.tool.ShareUtils;

import java.lang.reflect.Field;
import java.util.Objects;


public class OutdoorRunningFragment extends Fragment implements View.OnClickListener {
    private CardView cv_outdoor_target;
    private CardView cv_outdoor_go;
    private TextView tv_outdoor_target;
    private int target;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outdoor_running, container, false);
        findView(view);
        return view;
    }

    private void findView(View view) {
        cv_outdoor_go = view.findViewById(R.id.cv_outdoor_go);
        cv_outdoor_target = view.findViewById(R.id.cv_outdoor_target);
        tv_outdoor_target = view.findViewById(R.id.tv_outdoor_target);

        cv_outdoor_target.setOnClickListener(this);
        cv_outdoor_go.setOnClickListener(this);
        target = ShareUtils.getInt(getContext(), "run_target", 1);
        tv_outdoor_target.setText(target + "公里");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cv_outdoor_target:
                numberPicker();
                break;
            case R.id.cv_outdoor_go:
                startActivity(new Intent(getContext(), OutdoorRunningActivity.class));
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
                        tv_outdoor_target.setText(target + "公里");
                        ShareUtils.putInt(getContext(), "run_target", target);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
