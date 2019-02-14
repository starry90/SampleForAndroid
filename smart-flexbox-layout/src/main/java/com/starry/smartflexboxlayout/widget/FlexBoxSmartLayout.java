package com.starry.smartflexboxlayout.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.starry.smartflexboxlayout.ContactBean;
import com.starry.smartflexboxlayout.DeviceUtils;
import com.starry.smartflexboxlayout.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 带滚动条，可限制最大高度的FlexboxLayout
 *
 * @author Starry
 * @since 18-1-2.
 */

public class FlexBoxSmartLayout extends ScrollView {

    private int maxHeight;
    /**
     * 被选中的item
     */
    private View selectedItem;
    /**
     * 输入框
     */
    private EditText et_contact;
    /**
     * 联系人view
     */
    private FlexboxLayout flexBox;
    /**
     * flex item个数
     */
    private int size;
    /**
     * 上一次输入框的内容
     */
    private String lastContent;
    /**
     * 选择的联系人列表
     */
    private ArrayList<ContactBean> selectedContacts = new ArrayList<>();
    /**
     * 去重set
     */
    private Set<ContactBean> setRemoveDup = new HashSet<>();

    public ArrayList<ContactBean> getSelectedContacts() {
        return selectedContacts;
    }

    public EditText getEditText() {
        return et_contact;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (maxHeight != -1) { //设置最大宽度限制
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public FlexBoxSmartLayout(Context context) {
        super(context);
    }

    public FlexBoxSmartLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FlexBoxSmartLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightScrollView);
        maxHeight = typedArray.getDimensionPixelSize(R.styleable.MaxHeightScrollView_maxHeight, -1);
        typedArray.recycle();

        View parent = View.inflate(context, R.layout.flexbox_smart_layout, this);
        flexBox = (FlexboxLayout) parent.findViewById(R.id.flexbox_layout);
        et_contact = (EditText) parent.findViewById(R.id.et_contact);
        size = flexBox.getChildCount();

        //需要延迟调用弹出键盘的方法，否则无效
        postDelayed(new Runnable() {
            @Override
            public void run() {
                DeviceUtils.showSystemKeyboard(context, et_contact);
            }
        }, 300);
        //点击FlexboxLayout弹出软键盘
        flexBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceUtils.showSystemKeyboard(context, et_contact);
            }
        });

        //监听删除键
        et_contact.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String content = et_contact.getText().toString().trim();
                    if (TextUtils.isEmpty(lastContent) && TextUtils.isEmpty(content) && size >= 2) {
                        int index = flexBox.indexOfChild(selectedItem);
                        if (index == -1) { //selectedItem为空或已经被删除
                            selectedItem = null;
                        }

                        if (selectedItem == null) { //未选中编辑框中的联系人
                            selectedItem = flexBox.getChildAt(size - 2);
                            selectedItem.setBackgroundResource(R.drawable.calendar_contact_flex_item_selected_shape);
                        } else { //已选中编辑框中的联系人
                            ContactBean removeContact = selectedContacts.get(index);
                            removeItem(removeContact);
                            updateContact(removeContact.getEmail(), false);
                            et_contact.setText(""); //触发内容变化监听器，刷新列表
                        }
                    }
                }
                return false;
            }
        });

        //监听回车键
        et_contact.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    //处理事件
                    String content = v.getText().toString().trim();
                    if (!TextUtils.isEmpty(content)) {
                        if (Patterns.EMAIL_ADDRESS.matcher(content).matches()) {
                            ContactBean contactBean = updateContact(content, true);
                            addItem(contactBean);
                            et_contact.setText("");//触发内容变化监听器，刷新列表
                        } else {
                            DeviceUtils.showToast("请输入有效的电子邮件地址");
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        //监听内容变化
        et_contact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString();
                lastContent = keyword;

                ArrayList<ContactBean> allContacts = itemListener.getAllContacts();
                if (allContacts == null) return;

                // 根据关键字过滤联系人，刷新列表
                ArrayList<ContactBean> filterContacts = new ArrayList<>();

                if (TextUtils.isEmpty(keyword.trim())) {
                    filterContacts = allContacts;
                } else {
                    for (ContactBean contactBean : allContacts) {
                        if (contactBean.getName().contains(keyword) || contactBean.getSpell().contains(keyword)
                                || contactBean.getEmail().contains(keyword)) {
                            filterContacts.add(contactBean);
                        }
                    }
                }
                itemListener.filterContacts(filterContacts);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 更新联系人选中状态
     *
     * @param email   邮箱地址
     * @param checked 是否选中
     * @return 联系人
     */
    private ContactBean updateContact(String email, boolean checked) {
        ArrayList<ContactBean> allContacts = itemListener.getAllContacts();
        if (allContacts != null) {
            for (ContactBean contactBean : allContacts) {
                if (TextUtils.equals(email, contactBean.getEmail())) {
                    contactBean.setChecked(checked);
                    return contactBean;
                }
            }
        }
        return new ContactBean().setName(email).setEmail(email);
    }

    /**
     * 设置提示内容
     */
    private void setEditHint() {
        if (size > 1) {
            et_contact.setHint("");
        } else {
            et_contact.setHint(R.string.contact_add_edit_hint);
        }
    }

    /**
     * 添加联系人
     *
     * @param contactBean 联系人
     */
    public void addItem(ContactBean contactBean) {
        if (setRemoveDup.add(contactBean)) { //联系人不存在才添加
            selectedContacts.add(contactBean);
            View itemView = createItemView(contactBean.getName());
            flexBox.addView(itemView, size - 1);
            size++;
            //设置Margin值
            MarginLayoutParams layoutParams = (MarginLayoutParams) itemView.getLayoutParams();
            int margin = DeviceUtils.dip2px(getContext(), 3);
            layoutParams.setMargins(0, margin, margin * 2, margin);
        }

        setEditHint();
    }

    /**
     * 删除联系人
     *
     * @param contactBean 联系人
     */
    public void removeItem(ContactBean contactBean) {
        int i = selectedContacts.indexOf(contactBean);
        setRemoveDup.remove(contactBean);
        selectedContacts.remove(i);
        flexBox.removeViewAt(i);
        size--;

        setEditHint();
    }

    /**
     * 创建显示联系人item
     *
     * @param text 显示内容
     * @return View
     */
    private View createItemView(String text) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.flex_item_calendar_contact, null, false);
        TextView textView = (TextView) view.findViewById(R.id.tv_flex_item_contact);
        textView.setText(text);
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItem == null) { //未选中编辑框里的联系人
                    selectedItem = v;
                    v.setBackgroundResource(R.drawable.calendar_contact_flex_item_selected_shape);
                } else if (selectedItem != v) { //已选中联系人与当前点击的联系人不是同一个
                    v.setBackgroundResource(R.drawable.calendar_contact_flex_item_selected_shape);
                    selectedItem.setBackgroundResource(R.drawable.calendar_contact_flex_item_normal_shape);
                    selectedItem = v;
                } else { //已选中联系人与当前点击的联系人是同一人
                    selectedItem = null;
                    v.setBackgroundResource(R.drawable.calendar_contact_flex_item_normal_shape);
                }
            }
        });
        return view;
    }

    private ItemListener itemListener;

    public FlexBoxSmartLayout setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
        return this;
    }

    public interface ItemListener {
        /**
         * 获取所有联系人
         *
         * @return 所有联系人
         */
        ArrayList<ContactBean> getAllContacts();

        /**
         * 过滤联系人
         *
         * @param filterContacts 过滤后的联系人
         */
        void filterContacts(ArrayList<ContactBean> filterContacts);

    }
}
