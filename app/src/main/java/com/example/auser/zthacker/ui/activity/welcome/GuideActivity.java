package com.example.auser.zthacker.ui.activity.welcome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.utils.SPUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhengkq on 2017/8/23.
 */

public class GuideActivity extends Activity {

    @BindView(R.id.tv_airplane_model)
    TextView tv_airplane_model;
    @BindView(R.id.tv_sea_model)
    TextView tv_sea_model;
    @BindView(R.id.tv_car_model)
    TextView tv_car_model;
    @BindView(R.id.tv_architect_model)
    TextView tv_architect_model;
    @BindView(R.id.tv_teacher)
    TextView tv_teacher;
    @BindView(R.id.tv_student)
    TextView tv_student;
    @BindView(R.id.tv_lover)
    TextView tv_lover;
    @BindView(R.id.tv_organization)
    TextView tv_organization;

    private boolean airplaneSelect = true;
    private boolean seaSelect = false;
    private boolean carSelect = false;
    private boolean architectSelect = false;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, GuideActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            fixOrientation();
        }
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        initView();
    }

    private boolean isTranslucentOrFloating(){
        boolean isTranslucentOrFloating = false;
        try {
            int [] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean)m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    private boolean fixOrientation(){
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo)field.get(this);
            o.screenOrientation = -1;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void initView() {
        tv_airplane_model.setSelected(true);
    }

    @OnClick({R.id.tv_airplane_model,R.id.tv_sea_model,R.id.tv_car_model,R.id.tv_architect_model,
            R.id.tv_teacher,R.id.tv_student,R.id.tv_lover,R.id.tv_organization,R.id.tv_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_airplane_model:
                if (airplaneSelect){
                    tv_airplane_model.setTextColor(getResources().getColor(R.color.text_32));
                    tv_airplane_model.setBackground(getResources().getDrawable(R.drawable.label_select_bg));
                }else {
                    tv_airplane_model.setTextColor(getResources().getColor(R.color.text_64));
                    tv_airplane_model.setBackground(getResources().getDrawable(R.drawable.label_unselect_bg));
                }
                airplaneSelect = !airplaneSelect;
                break;
            case R.id.tv_sea_model:
                if (seaSelect){
                    tv_sea_model.setTextColor(getResources().getColor(R.color.text_32));
                    tv_sea_model.setBackground(getResources().getDrawable(R.drawable.label_select_bg));
                }else {
                    tv_sea_model.setTextColor(getResources().getColor(R.color.text_64));
                    tv_sea_model.setBackground(getResources().getDrawable(R.drawable.label_unselect_bg));
                }
                seaSelect = !seaSelect;
                break;
            case R.id.tv_car_model:
                if (carSelect){
                    tv_car_model.setTextColor(getResources().getColor(R.color.text_32));
                    tv_car_model.setBackground(getResources().getDrawable(R.drawable.label_select_bg));
                }else {
                    tv_car_model.setTextColor(getResources().getColor(R.color.text_64));
                    tv_car_model.setBackground(getResources().getDrawable(R.drawable.label_unselect_bg));
                }
                carSelect = !carSelect;
                break;
            case R.id.tv_architect_model:
                if (architectSelect){
                    tv_architect_model.setTextColor(getResources().getColor(R.color.text_32));
                    tv_architect_model.setBackground(getResources().getDrawable(R.drawable.label_select_bg));
                }else {
                    tv_architect_model.setTextColor(getResources().getColor(R.color.text_64));
                    tv_architect_model.setBackground(getResources().getDrawable(R.drawable.label_unselect_bg));
                }
                architectSelect = !architectSelect;
                break;
            case R.id.tv_teacher:
                tv_teacher.setTextColor(getResources().getColor(R.color.text_32));
                tv_teacher.setBackground(getResources().getDrawable(R.drawable.label_select_bg));
                tv_student.setTextColor(getResources().getColor(R.color.text_64));
                tv_student.setBackground(getResources().getDrawable(R.drawable.label_unselect_bg));
                tv_lover.setTextColor(getResources().getColor(R.color.text_64));
                tv_lover.setBackground(getResources().getDrawable(R.drawable.label_unselect_bg));
                tv_organization.setTextColor(getResources().getColor(R.color.text_64));
                tv_organization.setBackground(getResources().getDrawable(R.drawable.label_unselect_bg));
                break;
            case R.id.tv_student:
                tv_teacher.setTextColor(getResources().getColor(R.color.text_64));
                tv_teacher.setBackground(getResources().getDrawable(R.drawable.label_unselect_bg));
                tv_student.setTextColor(getResources().getColor(R.color.text_32));
                tv_student.setBackground(getResources().getDrawable(R.drawable.label_select_bg));
                tv_lover.setTextColor(getResources().getColor(R.color.text_64));
                tv_lover.setBackground(getResources().getDrawable(R.drawable.label_unselect_bg));
                tv_organization.setTextColor(getResources().getColor(R.color.text_64));
                tv_organization.setBackground(getResources().getDrawable(R.drawable.label_unselect_bg));
                break;
            case R.id.tv_lover:
                tv_teacher.setTextColor(getResources().getColor(R.color.text_64));
                tv_teacher.setBackground(getResources().getDrawable(R.drawable.label_unselect_bg));
                tv_student.setTextColor(getResources().getColor(R.color.text_64));
                tv_student.setBackground(getResources().getDrawable(R.drawable.label_unselect_bg));
                tv_lover.setTextColor(getResources().getColor(R.color.text_32));
                tv_lover.setBackground(getResources().getDrawable(R.drawable.label_select_bg));
                tv_organization.setTextColor(getResources().getColor(R.color.text_64));
                tv_organization.setBackground(getResources().getDrawable(R.drawable.label_unselect_bg));
                break;
            case R.id.tv_organization:
                tv_teacher.setTextColor(getResources().getColor(R.color.text_64));
                tv_teacher.setBackground(getResources().getDrawable(R.drawable.label_unselect_bg));
                tv_student.setTextColor(getResources().getColor(R.color.text_64));
                tv_student.setBackground(getResources().getDrawable(R.drawable.label_unselect_bg));
                tv_lover.setTextColor(getResources().getColor(R.color.text_64));
                tv_lover.setBackground(getResources().getDrawable(R.drawable.label_unselect_bg));
                tv_organization.setTextColor(getResources().getColor(R.color.text_32));
                tv_organization.setBackground(getResources().getDrawable(R.drawable.label_select_bg));
                break;
            case R.id.tv_next:
                SPUtils.setSharedBooleanData(this,"isGuide",true);
                startActivity(new Intent(this, PrivacyPolicyActivity.class));
                finish();
                break;
        }
    }
}
