package com.example.auser.zthacker.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.bean.UserBillInfoBean;
import com.example.auser.zthacker.utils.TextUtil;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhengkq on 2017/8/11.
 */

public class BillInfoEditActivity extends BaseActivity{
    @BindView(R.id.et_company_name)
    EditText et_company_name;
    @BindView(R.id.et_identification_code)
    EditText et_identification_code;
    @BindView(R.id.et_register_address)
    EditText et_register_address;
    @BindView(R.id.et_register_telephone)
    EditText et_register_telephone;
    @BindView(R.id.et_open_bank)
    EditText et_open_bank;
    @BindView(R.id.et_bank_card)
    EditText et_bank_card;
    @BindView(R.id.tv_next)
    TextView tv_next;
    private String companyName;
    private String identificationCode;
    private String registerAddress;
    private String registerTelephone;
    private String openBank;
    private String bankCard;
    private UserBillInfoBean userBillInfoBean;

    public static void startActivity(Context context,UserBillInfoBean userBillInfoBean){
        Intent intent = new Intent(context,BillInfoEditActivity.class);
        intent.putExtra("userBillInfoBean",(Serializable) userBillInfoBean);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billinfo_edit);
        ButterKnife.bind(this);
        initView();
    }
    private void initView() {
        setTop(R.color.black);
        Intent intent = getIntent();
        userBillInfoBean = (UserBillInfoBean) intent.getSerializableExtra("userBillInfoBean");
        if (userBillInfoBean!=null){
            setCentreText("编辑发票信息");
            et_company_name.setText(userBillInfoBean.getCompanyName());
            et_identification_code.setText(userBillInfoBean.getIdentificationCode());
            et_register_address.setText(userBillInfoBean.getRegisterAddress());
            et_register_telephone.setText(userBillInfoBean.getRegisterTelephone());
            et_open_bank.setText(userBillInfoBean.getOpenBank());
            et_bank_card.setText(userBillInfoBean.getBankCard());
        }else {
            setCentreText("发票信息");
        }
        et_company_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setConfirmBg();
            }
        });
        et_identification_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setConfirmBg();
            }
        });
    }

    private void setConfirmBg() {
        companyName = et_company_name.getText().toString().trim();
        identificationCode = et_identification_code.getText().toString().trim();
        if (!TextUtil.isNull(companyName)&&!TextUtil.isNull(identificationCode)){
            tv_next.setBackground(getResources().getDrawable(R.drawable.register_bg));
        }else {
            tv_next.setBackground(getResources().getDrawable(R.drawable.confirm_gray_bg));
        }
    }

    @OnClick({R.id.image_back,R.id.tv_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.tv_next:
                companyName = et_company_name.getText().toString().trim();
                identificationCode = et_identification_code.getText().toString().trim();
                registerAddress = et_register_address.getText().toString().trim();
                registerTelephone = et_register_telephone.getText().toString().trim();
                openBank = et_open_bank.getText().toString().trim();
                bankCard = et_bank_card.getText().toString().trim();
                if (TextUtil.isNull(companyName)||TextUtil.isNull(identificationCode)){
                    return;
                }
                UserBillInfoBean userBillInfoBean = new UserBillInfoBean();
                userBillInfoBean.setCompanyName(companyName);
                userBillInfoBean.setIdentificationCode(identificationCode);
                userBillInfoBean.setRegisterAddress(registerAddress);
                userBillInfoBean.setRegisterTelephone(registerTelephone);
                userBillInfoBean.setOpenBank(openBank);
                userBillInfoBean.setBankCard(bankCard);
                BillInfoActivity.startActivity(this,userBillInfoBean);
                finish();
                break;
        }
    }
}
