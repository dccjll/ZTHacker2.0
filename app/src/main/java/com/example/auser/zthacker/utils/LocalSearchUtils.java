package com.example.auser.zthacker.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhengkq on 2017/10/30.
 */

public class LocalSearchUtils {
    /**
     * 保存搜索记录
     *
     * @param inputText 输入的历史记录
     */
    public static List<String> saveSearchHistory(Context context, String inputText) {
        List<String> searchHistory = getSearchHistory(context);
        if (TextUtil.isNull(inputText)) {
            return searchHistory;
        }

        if (searchHistory.size() > 0) {
            //移除之前重复添加的元素
            for (int i = 0; i < searchHistory.size(); i++) {
                if (inputText.equals(searchHistory.get(i))||inputText.equals("")) {
                    searchHistory.remove(i);
                    break;
                }
            }
            searchHistory.add(0, inputText);                           //将新输入的文字添加集合的第0位也就是最前面
            if (searchHistory.size() > 10) {
                searchHistory.remove(searchHistory.size() - 1);         //最多保存10条搜索记录 删除最早搜索的那一项
            }
            //逗号拼接
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < searchHistory.size(); i++) {
                sb.append(searchHistory.get(i) + ",");
            }
            //保存到sp
            SPUtils.setSharedStringData(context,"hisSearch", sb.toString());
        } else {
            //之前未添加过
            searchHistory.add(inputText);
            SPUtils.setSharedStringData(context,"hisSearch", inputText + ",");
        }
        return searchHistory;
    }

    public static List<String> getSearchHistory(Context context) {
        String longHistory = (String) SPUtils.getSharedStringData(context, "hisSearch");        //获取之前保存的历史记录
        String[] tmpHistory = longHistory.split(",");                            //逗号截取 保存在数组中
        //将改数组转换成ArrayList
        List<String> historyList = new ArrayList<String>(Arrays.asList(tmpHistory));
        if (historyList.size() == 1 && historyList.get(0).equals("")) {//如果没有搜索记录，split之后第0位是个空串的情况下
            historyList.clear();  //清空集合，这个很关键
        }
        return historyList;
    }
}
