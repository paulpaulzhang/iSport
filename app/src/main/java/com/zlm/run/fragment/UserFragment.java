package com.zlm.run.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.don.pieviewlibrary.AnimationPercentPieView;
import com.don.pieviewlibrary.LinePieView;
import com.zlm.run.R;
import com.zlm.run.activity.SettingActivity;
import com.zlm.run.activity.UserDataActivity;
import com.zlm.run.bmob.MyUser;

import java.util.Objects;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;


public class UserFragment extends Fragment implements View.OnClickListener {
    private CircleImageView user_avatar;
    private ImageView user_gender;
    private TextView user_nick;
    private TextView user_talking;
    private TextView tv_run_kcal;
    private TextView tv_ride_kcal;
    private TextView tv_walk_kcal;
    private TextView tv_walk_number;
    private TextView tv_yoga_time;
    private TextView user_sports_energy;
    private Toolbar user_tool_bar;
    private LinearLayout user_layout;
    private AnimationPercentPieView pv_calorie;
    private LinePieView pv_kilometer;
    private ConstraintLayout setting_layout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        user_tool_bar = view.findViewById(R.id.user_tool_bar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(user_tool_bar);
        user_tool_bar.setTitle("我");

        user_avatar = view.findViewById(R.id.user_avatar);
        user_gender = view.findViewById(R.id.user_gender);
        user_talking = view.findViewById(R.id.user_talking);
        user_nick = view.findViewById(R.id.user_nick);
        user_layout = view.findViewById(R.id.user_layout);

        pv_calorie = view.findViewById(R.id.pv_calorie);
        pv_kilometer = view.findViewById(R.id.pv_kilometer);

        tv_run_kcal = view.findViewById(R.id.tv_run_kcal);
        tv_ride_kcal = view.findViewById(R.id.tv_ride_kcal);
        tv_walk_kcal = view.findViewById(R.id.tv_walk_kcal);
        tv_walk_number = view.findViewById(R.id.tv_walk_number);
        tv_yoga_time = view.findViewById(R.id.tv_yoga_time);
        user_sports_energy = view.findViewById(R.id.sports_energy);

        setting_layout = view.findViewById(R.id.setting_layout);

        user_layout.setOnClickListener(this);
        setting_layout.setOnClickListener(this);
        initUserData();
    }

    @SuppressLint("SetTextI18n")
    private void initUserData() {
        MyUser user = BmobUser.getCurrentUser(MyUser.class);

        if (user.getNickName() != null) {
            user_nick.setText(user.getNickName());
        } else {
            user_nick.setText(user.getUsername());
        }

        if (user.getAvatar() != null) {
            Glide.with(Objects.requireNonNull(getContext())).load(user.getAvatar().getFileUrl()).into(user_avatar);
        }

        if (user.getGender().equals("男")) {
            user_gender.setImageResource(R.drawable.men);
        } else {
            user_gender.setImageResource(R.drawable.women);
        }

        if (user.getTalking() != null) {
            user_talking.setText(user.getTalking());
        } else {
            user_talking.setText("这个人很懒，什么也没有留下");
        }

        user_sports_energy.setText("运动能量 " + user.getSports_energy() + "❂");

        int data_kcal[] = new int[]{user.getRunning_calorie(), user.getRiding_calorie(), user.getWalk_calorie()};
        String name[] = new String[]{"跑步", "骑行", "健走"};
        int[] color = new int[]{
                getResources().getColor(R.color.data_run_color, null),
                getResources().getColor(R.color.data_ride_color, null),
                getResources().getColor(R.color.data_walk_color, null)};
        pv_calorie.setData(data_kcal, name, color);

        double running_kilometer = user.getRunning_kilometers();
        int run_km = (int) running_kilometer;
        double riding_kilometer = user.getRiding_kilometers();
        int ride_km = (int) riding_kilometer;
        double walking_kilometer = user.getWalk_kilometers();
        int walk_km = (int) walking_kilometer;

        int data_km[] = new int[]{run_km, ride_km, walk_km};
        pv_kilometer.setData(data_km, name, color);

        tv_run_kcal.setText("跑步 " + user.getRunning_calorie() + " 千卡");
        tv_ride_kcal.setText("骑行 " + user.getRiding_calorie() + " 千卡");
        tv_walk_kcal.setText("健走 " + user.getWalk_calorie() + " 千卡");
        tv_walk_number.setText("你走了 " + user.getWalk_number() + " 步");
        tv_yoga_time.setText("Yoga " + user.getYoga_time() + " 分钟");
    }

    @Override
    public void onResume() {
        super.onResume();
        initUserData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_layout:
                startActivity(new Intent(getContext(), UserDataActivity.class));
                break;
            case R.id.setting_layout:
                startActivity(new Intent(getContext(), SettingActivity.class));
                break;
            default:
        }

    }
}

