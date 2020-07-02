package com.example.auser.zthacker.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.bean.UserBillInfoBean;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhengkq on 2017/8/11.
 */

public class BillInfoActivity extends BaseActivity{
    @BindView(R.id.tv_edit)
    TextView tv_edit;
    @BindView(R.id.tv_company_name)
    TextView tv_company_name;
    @BindView(R.id.tv_identification_code)
    TextView tv_identification_code;
    @BindView(R.id.tv_register_address)
    TextView tv_register_address;
    @BindView(R.id.tv_register_telephone)
    TextView tv_register_telephone;
    @BindView(R.id.tv_open_bank)
    TextView tv_open_bank;
    @BindView(R.id.tv_bank_card)
    TextView tv_bank_card;
    private UserBillInfoBean userBillInfoBean;

    public static void startActivity(Context context,UserBillInfoBean userBillInfoBean){
        Intent intent = new Intent(context,BillInfoActivity.class);
        intent.putExtra("userBillInfoBean",(Serializable) userBillInfoBean);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_info);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        setCentreText("发票信息");
        tv_edit.setText("编辑");
        Intent intent = getIntent();
        userBillInfoBean = (UserBillInfoBean) intent.getSerializableExtra("userBillInfoBean");
        tv_company_name.setText(userBillInfoBean.getCompanyName());
        tv_identification_code.setText(userBillInfoBean.getIdentificationCode());
        tv_register_address.setText(userBillInfoBean.getRegisterAddress());
        tv_register_telephone.setText(userBillInfoBean.getRegisterTelephone());
        tv_open_bank.setText(userBillInfoBean.getOpenBank());
        tv_bank_card.setText(userBillInfoBean.getBankCard());
    }

    @OnClick({R.id.image_back,R.id.tv_edit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.tv_edit:
                BillInfoEditActivity.startActivity(this,userBillInfoBean);
                break;
        }
    }
}
