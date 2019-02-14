package com.starry.smartflexboxlayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.starry.smartflexboxlayout.sortlist.CharacterParser;
import com.starry.smartflexboxlayout.sortlist.ContactAllAdapter;
import com.starry.smartflexboxlayout.widget.FlexBoxSmartLayout;
import com.starry.smartflexboxlayout.widget.NoScrollViewPager;

import java.util.ArrayList;

/**
 * 添加联系人
 *
 * @author Starry
 * @since 18-1-2.
 */

public class ContactActivity extends AppCompatActivity {

    public final static String EXTRA_CONTACTS = "contacts";

    private FlexBoxSmartLayout flexbox_smart_layout;

    private ArrayList<ContactBean> contacts = new ArrayList<>();

    /**
     * 所有联系人
     */
    private ContactAllFragment contactAllFragment;

    protected int getLayoutResID() {
        //在setContentView 之前设置，保证sideBar不被软键盘顶上去
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_contact_add;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResID());

        flexbox_smart_layout = (FlexBoxSmartLayout) findViewById(R.id.flexbox_smart_layout);
        ArrayList<ContactBean> contacts = getIntent().getParcelableArrayListExtra(EXTRA_CONTACTS);
        if (contacts != null) {
            for (ContactBean contact : contacts) {
                flexbox_smart_layout.addItem(contact);
            }
        }
        flexbox_smart_layout.setItemListener(new FlexBoxSmartLayout.ItemListener() {

            @Override
            public ArrayList<ContactBean> getAllContacts() {
                return contactAllFragment.getAllContacts();
            }

            @Override
            public void filterContacts(ArrayList<ContactBean> filterContacts) {
                contactAllFragment.filterContacts(filterContacts);
            }
        });

        initData();
        initViewpager();
    }


    private void initData() {
        CharacterParser charParser = CharacterParser.getInstance();
        ArrayList<ContactBean> contactList = new ArrayList<>();
        String[] contactArray = getResources().getStringArray(R.array.contacts);
        for (String name : contactArray) {
            ContactBean contactBean = new ContactBean();
            contactBean.setName(name);
            String spell = charParser.getSpell(name).toLowerCase();
            contactBean.setSpell(spell);
            contactBean.setEmail(spell + "@gmail.com");
            contactBean.setSortLetter(spell.charAt(0) + "");
            contactList.add(contactBean);
        }
        contacts.addAll(contactList);
    }

    private void initViewpager() {
        contactAllFragment = ContactAllFragment.newInstance(contacts, true, flexbox_smart_layout.getSelectedContacts());
        NoScrollViewPager noScrollViewPager = (NoScrollViewPager) findViewById(R.id.noScrollViewPager);
        noScrollViewPager.setNoScroll(true);
        noScrollViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return contactAllFragment;
            }

            @Override
            public int getCount() {
                return 1;
            }
        });
        contactAllFragment.setOnItemClick(new ContactAllAdapter.OnItemClick() {
            @Override
            public void onClick(ContactBean contactBean) {
                if (contactBean.isChecked()) {
                    flexbox_smart_layout.addItem(contactBean);
                } else {
                    flexbox_smart_layout.removeItem(contactBean);
                }
            }
        });

        //监听联系人列表滚动事件隐藏键盘
        contactAllFragment.setListScrollListener(new ContactAllFragment.ListScrollListener() {
            @Override
            public void listScroll() {
                DeviceUtils.hideSystemKeyboard(ContactActivity.this, flexbox_smart_layout.getEditText());
            }
        });
    }

    @Override
    public void finish() {
        //Activity关闭后隐藏系统键盘
        DeviceUtils.hideSystemKeyboard(this, flexbox_smart_layout.getEditText());
        super.finish();
    }
}
