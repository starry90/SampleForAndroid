package com.starry.smartflexboxlayout.sortlist;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 * A-Z滑动组件
 *
 * @author Starry
 * @since 18-1-2.
 */
public class SideBar extends View {
    // 触摸事件
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    // 26个字母
    public static String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
            "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    private int choose = -1;// 选中
    private Paint paint = new Paint();

    private TextView mTextDialog;

    /**
     * 单个字母高度
     */
    private int singleLetterHeight = dip2px(getContext(), 14);
    /**
     * 所有字母总高度
     */
    private int letterTotalHeight = letters.length * singleLetterHeight;
    /**
     * 第一个字母的y坐标
     */
    private int yPosFirstLetter;

    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideBar(Context context) {
        super(context);
    }

    /**
     * 设置字母列表
     *
     * @param lettersList 字母列表
     */
    public void setLetters(List<String> lettersList) {
        letters = lettersList.toArray(new String[]{});
        letterTotalHeight = letters.length * singleLetterHeight;
        invalidate();
    }

    /**
     * 重写这个方法
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取焦点改变背景颜色.
        int height = getHeight();// 获取对应高度
        int width = getWidth(); // 获取对应宽度
        yPosFirstLetter = height / 2 - letterTotalHeight / 2;//获取第一个字母的y坐标

        for (int i = 0; i < letters.length; i++) {
            paint.setColor(Color.parseColor("#666666"));
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            paint.setTextSize(dip2px(getContext(), 10));
            // x坐标等于中间-字符串宽度的一半.
            float xPos = width / 2 - paint.measureText(letters[i]) / 2;
            //从第一个字母开始算第i个字母的y坐标
            float yPos = yPosFirstLetter + singleLetterHeight * i;
            canvas.drawText(letters[i], xPos, yPos, paint);
            paint.reset();// 重置画笔
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// 点击y坐标
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        //从第一个字母开始算点击y坐标在字母列表里的位置
        final int c = (int) ((y - yPosFirstLetter) / letterTotalHeight * letters.length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

        switch (action) {
            case MotionEvent.ACTION_UP:
                choose = -1;//
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;

            default:
                if (oldChoose != c) {
                    if (c >= 0 && c < letters.length) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(letters[c]);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(letters[c]);
                            mTextDialog.setVisibility(View.VISIBLE);
                        }

                        choose = c;
                        invalidate();
                    }
                }

                break;
        }
        return true;
    }

    /**
     * 向外公开的方法
     *
     * @param onTouchingLetterChangedListener OnTouchingLetterChangedListener
     */
    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    /**
     * 接口
     *
     * @author coder
     */
    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }

    /**
     * dip to px
     */
    public static int dip2px(Context context, float dipValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue
                , context.getResources().getDisplayMetrics());
    }

}