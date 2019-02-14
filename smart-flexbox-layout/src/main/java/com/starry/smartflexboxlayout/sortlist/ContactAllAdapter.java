package com.starry.smartflexboxlayout.sortlist;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.starry.smartflexboxlayout.ContactBean;
import com.starry.smartflexboxlayout.R;

import java.util.List;
import java.util.Locale;

/**
 * 所有联系人
 *
 * @author Starry
 * @since 18-1-2.
 */
public class ContactAllAdapter extends BaseAdapter {

    private List<ContactBean> beans;
    private Context mContext;
    /**
     * 选择模式
     */
    private boolean selectMode = false;

    public ContactAllAdapter(Context mContext, List<ContactBean> beans, boolean selectMode) {
        this.mContext = mContext;
        this.beans = beans;
        this.selectMode = selectMode;
    }

    public void setData(List<ContactBean> beans) {
        this.beans = beans;
        notifyDataSetChanged();
    }

    public int getCount() {
        return beans != null ? beans.size() : 0;
    }

    public Object getItem(int position) {
        return beans.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        final ViewHolder viewHolder;
        final ContactBean contactBean = beans.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_contact_all, null);
            viewHolder.cb_item_contact_all = (CheckBox) view.findViewById(R.id.cb_item_contact_all);
            viewHolder.tv_item_all_name = (TextView) view.findViewById(R.id.tv_item_all_name);
            viewHolder.tv_item_all_email = (TextView) view.findViewById(R.id.tv_item_all_email);
            viewHolder.view_item_all_divider = view.findViewById(R.id.view_item_all_divider);
            viewHolder.tv_item_all_letter = (TextView) view.findViewById(R.id.tv_item_all_letter);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (selectMode) {
            viewHolder.cb_item_contact_all.setVisibility(View.VISIBLE);
            viewHolder.cb_item_contact_all.setChecked(contactBean.isChecked());
        }else {
            viewHolder.cb_item_contact_all.setVisibility(View.GONE);
        }

        // 根据position获取分类的首字母的Char ascii值
        char section = getSectionForPosition(position);
        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tv_item_all_letter.setVisibility(View.VISIBLE);
            viewHolder.tv_item_all_letter.setText(contactBean.getSortLetter());
        } else {
            viewHolder.tv_item_all_letter.setVisibility(View.GONE);
        }

        if (position + 1 < getCount() && TextUtils.equals(contactBean.getSortLetter(), beans.get(position + 1).getSortLetter())) {
            viewHolder.view_item_all_divider.setVisibility(View.VISIBLE);
        } else {
            viewHolder.view_item_all_divider.setVisibility(View.GONE);
        }
        viewHolder.tv_item_all_name.setText(contactBean.getName());
        viewHolder.tv_item_all_email.setText(contactBean.getEmail());

        // item click listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectMode) {
                    contactBean.setChecked(!contactBean.isChecked());
                    viewHolder.cb_item_contact_all.setChecked(contactBean.isChecked());
                    if (itemClick != null) {
                        itemClick.onClick(contactBean);
                    }
                } else {
                    // start activity
                }
            }
        });


        return view;
    }

    final static class ViewHolder {
        TextView tv_item_all_letter;
        CheckBox cb_item_contact_all;
        TextView tv_item_all_name;
        TextView tv_item_all_email;
        View view_item_all_divider;
    }

    public interface OnItemClick {

        void onClick(ContactBean contactBean);
    }

    private OnItemClick itemClick;

    public void setItemClick(OnItemClick itemClick) {
        this.itemClick = itemClick;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public char getSectionForPosition(int position) {
        return beans.get(position).getSortLetter().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(char section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = beans.get(i).getSortLetter();
            char firstChar = sortStr.toUpperCase(Locale.ENGLISH).charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

}