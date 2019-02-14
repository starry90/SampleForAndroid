package com.starry.smartflexboxlayout;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.starry.smartflexboxlayout.sortlist.ContactAllAdapter;
import com.starry.smartflexboxlayout.sortlist.PinyinComparator;
import com.starry.smartflexboxlayout.sortlist.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/**
 * 所有联系人：最近联系+服务端联系人列表
 *
 * @author Starry
 * @since 18-1-2.
 */

public class ContactAllFragment extends Fragment {

    private final static String EXTRA_CONTACTS = "contacts";
    private final static String EXTRA_SELECTED_CONTACTS = "selectedContacts";
    private final static String EXTRA_SELECT_MODE = "selectMode";

    /**
     * 搜索无结果
     */
    private LinearLayout ll_contact_list_search_empty;

    /**
     * all contact ListView
     */
    private ListView lv_contact_all;
    /**
     * all contact Adapter
     */
    private ContactAllAdapter allAdapter;
    /**
     * all contact data
     */
    private ArrayList<ContactBean> contactList = new ArrayList<>();

    /**
     * a-z排序控件
     */
    private SideBar sideBar;
    /**
     * 固定在第一位置可见item首字母
     */
    private TextView tvLetter;

    /**
     * 上次第一个可见元素，用于滚动时记录标识。
     */
    private int lastFirstVisibleItem = -1;

    private Activity activity;

    /**
     * 选择模式
     */
    private boolean selectMode;

    /**
     * 选中联系人列表
     */
    private ArrayList<ContactBean> selectedContacts;

    public ArrayList<ContactBean> getAllContacts() {
        return contactList;
    }

    /**
     * 实例化ContactAllFragment
     *
     * @param contacts       联系人列表
     * @param selectMode     true为选择模式
     * @param selectContacts 选中的联系人列表
     * @return ContactAllFragment
     */
    public static ContactAllFragment newInstance(ArrayList<ContactBean> contacts
            , boolean selectMode, ArrayList<ContactBean> selectContacts) {
        ContactAllFragment fragment = new ContactAllFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_CONTACTS, contacts);
        args.putParcelableArrayList(EXTRA_SELECTED_CONTACTS, selectContacts);
        args.putBoolean(EXTRA_SELECT_MODE, selectMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResID(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    protected int getLayoutResID() {
        return R.layout.fragment_contact_all;
    }


    /**
     * init view
     */
    protected void initView(View view) {
        lv_contact_all = (ListView) view.findViewById(R.id.lv_contact_all);
        tvLetter = (TextView) view.findViewById(R.id.tv_contact_letter);
        sideBar = (SideBar) view.findViewById(R.id.sidebar);

        ll_contact_list_search_empty = (LinearLayout) view.findViewById(R.id.ll_contact_list_search_empty);
    }

    /**
     * init data
     */
    protected void initData() {
        activity = getActivity();
        Bundle arguments = getArguments();

        ArrayList<ContactBean> contacts = arguments.getParcelableArrayList(EXTRA_CONTACTS);
        contactList.addAll(contacts);
        selectedContacts = arguments.getParcelableArrayList(EXTRA_SELECTED_CONTACTS);
        selectMode = arguments.getBoolean(EXTRA_SELECT_MODE);

        initAllContact();
    }

    /**
     * 显示搜索后的联系人列表
     *
     * @param filterContacts 搜索后的联系人列表
     */
    public void filterContacts(ArrayList<ContactBean> filterContacts) {
        if (contactList.size() == filterContacts.size()) {
            tvLetter.setVisibility(View.VISIBLE);
            sideBar.setVisibility(View.VISIBLE);
            ll_contact_list_search_empty.setVisibility(View.GONE);
        } else {
            tvLetter.setVisibility(View.GONE);
            sideBar.setVisibility(View.GONE);
            if (filterContacts.size() == 0) { //无搜索结果
                ll_contact_list_search_empty.setVisibility(View.VISIBLE);
            } else {
                ll_contact_list_search_empty.setVisibility(View.GONE);
            }
        }
        Collections.sort(filterContacts, new PinyinComparator());// 根据a-z进行排序源数据
        if (allAdapter != null)
            allAdapter.setData(filterContacts);
    }

    private ContactAllAdapter.OnItemClick mOnItemClick;

    /**
     * 设置item点击事件
     *
     * @param onItemClick
     */
    public void setOnItemClick(ContactAllAdapter.OnItemClick onItemClick) {
        this.mOnItemClick = onItemClick;
    }

    /**
     * 获取字母列表
     *
     * @return 字母列表
     */
    private List<String> getLetters() {
        //TreeSet排序去重
        TreeSet<String> letters = new TreeSet<>();
        for (ContactBean contactBean : contactList) {
            letters.add(contactBean.getSortLetter());
        }
        ArrayList<String> letterList = new ArrayList<>(letters);
        if (letterList.contains("#")) {// TreeSet排序后'#'在字母前，需要把#放在字母后
            letterList.remove("#");
            letterList.add("#");
        }
        return letterList;
    }

    /**
     * 始化联系人列表
     */
    private void initAllContact() {
        boolean empty = contactList.size() == 0;
        if (!empty) {
            tvLetter.setText(contactList.get(0).getSortLetter());
        }

        //初始化时设置选中项
        int size = selectedContacts != null ? selectedContacts.size() : 0;
        for (int i = 0; i < size && contactList != null; i++) {
            ContactBean temp = selectedContacts.get(i);
            for (ContactBean contactBean : contactList) {
                if (TextUtils.equals(temp.getEmail(), contactBean.getEmail())) {
                    contactBean.setChecked(true);
                    break;
                }
            }
        }
        //显示所有联系人列表
        Collections.sort(contactList, new PinyinComparator());// 根据a-z进行排序源数据
        allAdapter = new ContactAllAdapter(activity, contactList, selectMode);
        allAdapter.setItemClick(mOnItemClick);
        lv_contact_all.setAdapter(allAdapter);

        sideBar.setLetters(getLetters());
        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = allAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    lv_contact_all.setSelection(position);
                }

            }
        });

        //所有联系人列表滑动监听
        lv_contact_all.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mListScrollListener != null) {
                    mListScrollListener.listScroll();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount <= 1) return;

                char nextSection = allAdapter.getSectionForPosition(firstVisibleItem + 1);
                //屏幕内第二个可见item的首字母第一次出现的位置
                int nextSecPosition = allAdapter.getPositionForSection(nextSection);
                if (firstVisibleItem != lastFirstVisibleItem) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tvLetter.getLayoutParams();
                    params.topMargin = 0;
                    tvLetter.setLayoutParams(params);
                    tvLetter.setText(contactList.get(firstVisibleItem).getSortLetter());
                }
                //屏幕内第二个可见item的首字母第一次出现的位置等于第二个可见item的位置，设置tvLetter的topMargin值
                if (nextSecPosition == firstVisibleItem + 1) {
                    View childView = view.getChildAt(0);
                    if (childView != null) {
                        int titleHeight = tvLetter.getHeight();
                        int bottom = childView.getBottom();
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tvLetter.getLayoutParams();
                        if (bottom < titleHeight) {
                            float pushedDistance = bottom - titleHeight;
                            params.topMargin = (int) pushedDistance;
                            tvLetter.setLayoutParams(params);
                        } else {
                            if (params.topMargin != 0) {
                                params.topMargin = 0;
                                tvLetter.setLayoutParams(params);
                            }
                        }
                    }
                }
                lastFirstVisibleItem = firstVisibleItem;
            }
        });

    }

    private ListScrollListener mListScrollListener;

    public void setListScrollListener(ListScrollListener l) {
        mListScrollListener = l;
    }

    /**
     * 联系人列表滚动监听器
     */
    public interface ListScrollListener {

        void listScroll();
    }

}
