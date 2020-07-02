package com.example.auser.zthacker.http;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FactoryTools {

    public static float getSHeight(Context context) {
        float height = 1340f;
        float sheight = getWindowManager(context)[0];
        return sheight / height;
    }

    public static float getSWidth(Context context) {
        float width = 760f;
        float swidh = getWindowManager(context)[1];
        Log.e("swidh", swidh + "  " + swidh / width);
        return swidh / width;
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftInputMode(Context context, View windowToken) {
        InputMethodManager imm = ((InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE));
        imm.hideSoftInputFromWindow(windowToken.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 弹出软键盘
     */
    public static void showSoftInputMode(Context context, View windowToken) {
        final InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(windowToken, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 将Bitmap对象转换为byte[]二进制
     */
    public static byte[] getbyte(Bitmap bitmap) {
//FileInputStream f = new FileInputStream();
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();

    }

    /**
     * 将byte[]二进制转换为Bitmap对象
     */
    public static Bitmap getbitmap(byte[] bytes) {
        try {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static final byte[] input2byte(InputStream inStream) {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        try {
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = inStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
        } catch (Exception e) {
// TODO: handle exception
            e.printStackTrace();
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    public static long dateDiff(String startTime, int type) {
//按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        long nd = 1000 * 24 * 60 * 60;//一天的毫秒数
        long nh = 1000 * 60 * 60;//一小时的毫秒数
        long nm = 1000 * 60;//一分钟的毫秒数
        long ns = 1000;//一秒钟的毫秒数long diff;try {
        //获得两个时间的毫秒时间差异
        long diff = 0;
        String endTime = sd.format(new Date());
        try {
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
        } catch (Exception e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        long day = diff / nd;//计算差多少天
        long hour = diff % nd / nh;//计算差多少小时
        long min = diff % nd % nh / nm;//计算差多少分钟
        long sec = diff % nd % nh % nm / ns;//计算差多少秒//输出结果
        switch (type) {
            case 0:
                diff = sec;
                break;
            case 1:
                diff = min;
                break;
            case 2:
                diff = hour;
                break;
            default:
                diff = day;
                break;
        }
        return diff;
    }

    /**
     * 将对象序列化到磁盘文件中
     *
     * @paramo
     * @throwsException
     */
    public static void getWriteObject(Object obj, String path) {
        String url = Environment.getExternalStorageDirectory().getPath();//获取SD卡路径
        try {
            File f = new File(url + "/WaywardPoint");
            if (!f.exists()) {
                f.mkdir();
            }
            f = new File(path);
            if (f.exists()) {
                f.delete();//删除文件
            }
            FileOutputStream os = new FileOutputStream(f);
//ObjectOutputStream 核心类
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(obj);
            oos.close();
            os.close();
        } catch (Exception e) {
// TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 反序列化,将磁盘文件转化为对象
     *
     * @return
     * @paramf
     * @throwsException
     */
    public static Object getReadObject(String path) throws Exception {
        InputStream is = new FileInputStream(path);
//ObjectOutputStream 核心类
        ObjectInputStream ois = new ObjectInputStream(is);
        return ois.readObject();
    }

    public static String getSplitPhone(String str) {
        String strfnjie = "";
        if (str.indexOf("<{*|*}>") == -1) {
            return strfnjie;
        }
        String[] strs = str.split("\\<\\{\\|\\*\\|\\}\\>");
        String[] strshe = new String[strs.length];
        for (int i = 0; i < strs.length; i++) {
            strfnjie += strs[i].split("\\<\\{\\*\\|\\*\\}\\>")[1] + "^*";
        }
        return strfnjie.substring(0, strfnjie.length() - 2);
    }

    public static String getWeekOfDate(String dtime) {
        try {
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy/MM/dd");
            Date dt = sfd.parse(dtime);
            String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0)
                w = 0;
            return weekDays[w];
        } catch (Exception e) {
// TODO: handle exception
            return dtime;
        }
    }

    public static String getWeekOfDates(String dtime) {
        try {

            SimpleDateFormat sfd = new SimpleDateFormat("yyyy年MM月dd日");
            Date dt = sfd.parse(dtime);
            String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0)
                w = 0;
            return weekDays[w];
        } catch (Exception e) {
// TODO: handle exception
            return dtime;
        }
    }

    /**
     * 2  * 获取当前时间的星期
     * 4
     */
    public static String getWeekOfDates(Date dt) {
        try {
            String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0)
                w = 0;
            return weekDays[w];
        } catch (Exception e) {
// TODO: handle exception
            return null;
        }
    }

    /**
     * 2  * 转换时间格式
     * 4
     */
    public static String getDateStringFormat(String dtime, String pattern) {
        try {
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dt = sfd.parse(dtime);
            sfd = new SimpleDateFormat(pattern);
            return sfd.format(dt);
        } catch (Exception e) {
// TODO: handle exception
            return dtime;
        }
    }

    /**
     * 2  * 转换时间格式
     * 4
     */
    public static Date getDateFormat(String dtime, String pattern) {
        try {
            SimpleDateFormat sfd = new SimpleDateFormat(pattern);
            return sfd.parse(dtime);
        } catch (Exception e) {
// TODO: handle exception
            return new Date();
        }
    }
    /**
     * 2  * 转换时间格式
     * 4
     */
    public static String getDateFormat(Date dtime, String pattern) {
        try {
            SimpleDateFormat sfd = new SimpleDateFormat(pattern);
            return sfd.format(dtime);
        } catch (Exception e) {
// TODO: handle exception
            return "";
        }
    }

    /**
     * 2  * 获取版本号
     * 3  * @return 当前应用的版本号
     * 4
     */
    public static String getVersion(Context con) {
        try {
            PackageManager manager = con.getPackageManager();
            PackageInfo info = manager.getPackageInfo(con.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "暂无版本号";
        }
    }

    static int[] met = null;

    /**
     * 2  * 分辨率获取
     * 3  * @return 高宽数组
     * 4
     */
    public static int[] getWindowManager(Context con) {
        if (met == null) {
            Display display = ((Activity) con).getWindowManager().getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            met = new int[2];
            met[0] = displayMetrics.heightPixels;
            met[1] = displayMetrics.widthPixels;
        }
        Log.e("Pixels", "heightPixels:" + met[0] + " widthPixels:" + met[1]);
        return met;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 字体大小自适应
     */
    public static int getFontSize(Context context, int textSize) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
// screenWidth = screenWidth > screenHeight ? screenWidth :
        // screenHeight;
        int rate = (int) (textSize * (float) screenHeight / 1280);
        return px2dip(context, rate);
    }

    /**
     * 根据原图和变长绘制圆形图片
     *
     * @param source
     * @param min
     * @return
     */
    public static Bitmap toRoundCorner(Bitmap source, int min) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
/**
 * 产生一个同样大小的画布
 */
        Canvas canvas = new Canvas(target);
/**
 * 首先绘制圆形
 */
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);
/**
 * 使用SRC_IN
 */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
/**
 * 绘制图片
 */
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public static void backgroundAlpha(Context context, float bgAlpha) {
        if (context != null) {
            WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
            lp.alpha = bgAlpha; //0.0-1.0
            ((Activity) context).getWindow().setAttributes(lp);
        }
    }

    public static void setMargins(View view, float left, float top, float right, float bottom) {
        if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams) view.getLayoutParams();
            rp.setMargins(((int) left), ((int) top), ((int) right), ((int) bottom));
            view.setLayoutParams(rp);
        } else if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
            lp.setMargins(((int) left), ((int) top), ((int) right), ((int) bottom));
            view.setLayoutParams(lp);
        } else if (view.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams fp = (FrameLayout.LayoutParams) view.getLayoutParams();
            fp.setMargins(((int) left), ((int) top), ((int) right), ((int) bottom));
            view.setLayoutParams(fp);
        }

    }

    /**
     * 获取控件高宽
     */
    public static void setViewTree(final View view, final int height, final int width) {//根据控件宽度设置高度（正方形）
//创建ViewTreeObserver对象
        ViewTreeObserver vtos = view.getViewTreeObserver();
//为其添加监听器
        vtos.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                                           @Override
                                           public void onGlobalLayout() {
                                               if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                                                   RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams) view.getLayoutParams();
                                                   Log.e("rp.height", rp.height + "");
                                                   rp.height = height;
                                                   if (width != -1) {
                                                       rp.width = width;
                                                   }
                                               } else if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {

                                                   LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
                                                   lp.height = height;
                                                   if (width != -1) {
                                                       lp.width = width;
                                                   }
                                                   Log.e("rp.height", lp.height + "");
                                               } else if (view.getLayoutParams() instanceof ViewGroup.LayoutParams) {

                                                   ViewGroup.LayoutParams vg = (ViewGroup.LayoutParams) view.getLayoutParams();
//lp.height = view.getMeasuredWidth()/nmerd;
                                                   Log.e("rp.height", vg.height + "");
                                                   vg.height = 5000;
                                               }
                                           }
                                       }
        );
    }

    /**
     * 设置控件高宽
     */
    public static void setViewHeightWidth(final View view, final float height, final float width) {

        if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams) view.getLayoutParams();
            Log.e("rp.height", rp.height + "");
            rp.height = (int) height;
            if (width != -1) {
                rp.width = (int) width;
            }
            view.setLayoutParams(rp);
        } else if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
            lp.height = (int) height;
            if (width != -1) {
                lp.width = (int) width;
            }
            view.setLayoutParams(lp);
            Log.e("rp.height", lp.height + "");
        } else if (view.getLayoutParams() instanceof FrameLayout.LayoutParams) {

            FrameLayout.LayoutParams vg = (FrameLayout.LayoutParams) view.getLayoutParams();
            vg.height = (int) height;
            if (width != -1) {
                vg.width = (int) width;
            }
            view.setLayoutParams(vg);
        }

    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate) {
        long between_days = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            smdate = sdf.parse(sdf.format(smdate));
            bdate = sdf.parse(sdf.format(bdate));
            Calendar cal = Calendar.getInstance();
            cal.setTime(smdate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(bdate);
            long time2 = cal.getTimeInMillis();
            between_days = (time2 - time1) / (1000 * 3600 * 24);
        } catch (Exception e) {
// TODO: handle exception
            return -999999;
        }
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 字符串的日期格式的计算
     */
    public static int daysBetween(String smdate, String bdate) {
        long between_days = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(smdate));
            long time1 = cal.getTimeInMillis();
            cal.setTime(sdf.parse(bdate));
            long time2 = cal.getTimeInMillis();
            between_days = (time2 - time1) / (1000 * 3600 * 24);
        } catch (Exception e) {
// TODO: handle exception
            return -999999;
        }
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 动态设置ListView的高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
// pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private static Toast toast = null;

    public static void setToastShow(Context context, String txt) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), txt, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast.setText(txt);
            toast.show();
        }
    }

    /**
     * 获取是否禁用了该权限
     */
    public static boolean setPermission(Context context, String permissiontxt) {
        PackageManager pm = context.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(permissiontxt, "packageName"));
        if (permission) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param plainText 明文
     * @return 32位密文
     */
    public static String MD5Cryption(String plainText) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            re_md5 = buf.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return re_md5;
    }

    public static SpannableString getSpannableString(String spantxt, String spilt, int size, int color) {
        SpannableString msp = new SpannableString(spantxt);
        int starindex = spantxt.indexOf(spilt);
        int endindex = starindex + spilt.length();
        if (size != -1) {
            msp.setSpan(new AbsoluteSizeSpan(size, true), starindex, endindex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (color != -1) {
            msp.setSpan(new ForegroundColorSpan(color), starindex, endindex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return msp;
    }

    public static String SHA1(String decript) {
        try {
            MessageDigest digest = MessageDigest
                    .getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * Sd卡是否存在
     */
    public static boolean isSDCard(Context context) {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }else {
           setToastShow(context, "SD卡不存在");
            return false;
        }
    }



    /**
     * 服务器地址分割
     */
    public static List<String> getEonerUrl(String url) {
        String[] imgs = url.split("\\<\\{\\*\\}\\>\\<\\{\\|\\}\\>");
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < imgs.length; i++) {
            final String[] imgt = imgs[i].split("\\<\\{\\*\\}\\>");
            if (imgt[0].trim().length() != 0) {
                list.add(imgt[0]);
            }
        }
        return list;
    }
}
