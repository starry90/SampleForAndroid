package com.starry.smartflexboxlayout.sortlist;

import com.starry.smartflexboxlayout.ContactBean;

import java.util.Comparator;

/**
 * 比较器
 *
 * @author Starry
 * @since 18-1-2.
 */
public class PinyinComparator implements Comparator<ContactBean> {

    public int compare(ContactBean o1, ContactBean o2) {
        if (o1.getSortLetter().equals("@") || o2.getSortLetter().equals("#")) {
            return -1;
        } else if (o1.getSortLetter().equals("#") || o2.getSortLetter().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetter().compareTo(o2.getSortLetter());
        }
    }

}
