package com.example.auser.zthacker.ui.activity.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.bean.AppPersonalInfoBean;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.MainActivity;
import com.example.auser.zthacker.utils.LocalSearchUtils;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.view.FlowLayout;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhengkq on 2017/10/27.
 */

public class SearchInputActivity extends BaseActivity{
    @BindView(R.id.et_input)
    EditText et_input;
    @BindView(R.id.iv_search_close)
    ImageView iv_search_close;
    @BindView(R.id.fl)
    FlowLayout fl;
    @BindView(R.id.tv_clear_history)
    TextView tv_clear_history;
    @BindView(R.id.tv_nohistory_notice)
    TextView tv_nohistory_notice;
    @BindView(R.id.lv)
    ListView lv;
    private List<String> historyList;
    private ArrayAdapter adapter;
    private String searchTxt;
    private GsonBuilder gsonBuilder;
    private String code;
    private String[] hotSearch;

    public static void startActivity(Context context){
        Intent intent = new Intent(context,SearchInputActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_input);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTop(R.color.black);
        historyList = LocalSearchUtils.getSearchHistory(SearchInputActivity.this);
        if (historyList.size() >= 1 && !historyList.get(0).equals("")){
            initHisAdapter();
        }else {
            tv_nohistory_notice.setVisibility(View.VISIBLE);
            tv_clear_history.setVisibility(View.INVISIBLE);
        }

        et_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            SearchInputActivity.this.getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(SearchInputActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    searchTxt = et_input.getText().toString().trim();
                    if (searchTxt.length()==0){
                        Toast.makeText(SearchInputActivity.this, "请输入要搜索的文字！", Toast.LENGTH_SHORT).show();
                    }else {
                        SearchListActivity.startActivity(SearchInputActivity.this,searchTxt);
                        historyList = LocalSearchUtils.saveSearchHistory(SearchInputActivity.this, searchTxt);
                        initHisAdapter();
                    }
                }
                return false;
            }
        });
        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchTxt = et_input.getText().toString().trim();
                if(searchTxt.length()>0){
                    iv_search_close.setVisibility(View.VISIBLE);
                }else {
                    iv_search_close.setVisibility(View.INVISIBLE);
                }
            }
        });
        ApiRequestData.getInstance(this).ShowDialog(null);
        getHotSearchList();
    }

    private void getHotSearchList() {
        ApiRequestData.getInstance(this).getHotSearchList(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                if (gsonBuilder == null) {
                    gsonBuilder = new GsonBuilder();
                }
                ApiRequestData.getInstance(SearchInputActivity.this).getDialogDismiss();
                NormalObjData<String[]> mData = gsonBuilder
                        .setPrettyPrinting()
                        .disableHtmlEscaping()
                        .create().fromJson(s, new TypeToken<NormalObjData<String[]>>() {
                        }.getType());
                hotSearch = mData.getData();
                setFlowlayout();
                code = mData.getCode();
                if (!TextUtil.isNull(code) && code.equals("0")) {
                    ToastUtil.show(SearchInputActivity.this, mData.getMsg());
                    return;
                }
            }
        });
    }

    /**
     * 热门搜索
     *
     */
    private void setFlowlayout() {
        for (int i = 0;i<hotSearch.length;i++){
            int left, top, right, bottom;
            left = top = right = bottom = (int) getResources().getDimension(R.dimen.x5);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(left, top, right, bottom);
            final TextView mView = new TextView(this);

            mView.setText(hotSearch[i]);
            mView.setTextSize(14);
            mView.setTextColor(getResources().getColor(R.color.text_90));
            mView.setBackgroundDrawable(getResources().getDrawable(R.drawable.label_search_bg));

            mView.setPadding(2*left, 2*left/3, 2*left, 2*left/3);
            mView.setLayoutParams(params);
            final int finalI = i;
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchListActivity.startActivity(SearchInputActivity.this,hotSearch[finalI]);
                    historyList = LocalSearchUtils.saveSearchHistory(SearchInputActivity.this,hotSearch[finalI]);
                    initHisAdapter();
                }
            });

            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.addView(mView);
            fl.addView(ll);
        }
    }

    private void initHisAdapter() {
        tv_nohistory_notice.setVisibility(View.INVISIBLE);
        tv_clear_history.setVisibility(View.VISIBLE);
        adapter = new ArrayAdapter<String>(this, R.layout.item_textview,R.id.tv_history,historyList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchListActivity.startActivity(SearchInputActivity.this,historyList.get(position));
                LocalSearchUtils.saveSearchHistory(SearchInputActivity.this,historyList.get(position));
            }
        });
    }

    @OnClick({R.id.tv_cancel,R.id.tv_clear_history,R.id.iv_search_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_clear_history:
                historyList.clear(); //清空集合
                SPUtils.setSharedStringData(this,"hisSearch", "");
                adapter.notifyDataSetChanged();
                tv_nohistory_notice.setVisibility(View.VISIBLE);
                tv_clear_history.setVisibility(View.GONE);
                break;
            case R.id.iv_search_close:
                et_input.setText("");
                break;
        }
    }
}
