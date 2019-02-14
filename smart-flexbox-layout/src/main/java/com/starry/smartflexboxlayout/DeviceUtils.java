package com.starry.smartflexboxlayout;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Starry
 * @since 18-1-2.
 */

public class DeviceUtils {

    /**
     * dip to px
     */
    public static int dip2px(Context context, float dipValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue
                , context.getResources().getDisplayMetrics());
    }

    /**
     * 隐藏系统键盘
     *
     * @param context  Context
     * @param editText EditText
     */
    public static void hideSystemKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * 显示系统键盘
     *
     * @param context  Context
     * @param editText EditText
     */
    public static void showSystemKeyboard(Context context, EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 显示或隐藏系统键盘<p>
     * 如果已经显示，则隐藏，反之则显示
     * </p>
     *
     * @param context Context
     */
    public static void updateSystemKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 关闭开启方法相同
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showToast(String message) {
        showToast(MyApplication.getInstance(), message, Toast.LENGTH_SHORT);
    }

    public static void showToast(int resId) {
        showToast(MyApplication.getInstance(), MyApplication.getInstance().getString(resId), Toast.LENGTH_SHORT);
    }

    public static void showToast(final Context context, final String msg, final int length) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            return;
        }
        //保证在子线程中也能toast
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, length).show();
            }
        });
    }

}
