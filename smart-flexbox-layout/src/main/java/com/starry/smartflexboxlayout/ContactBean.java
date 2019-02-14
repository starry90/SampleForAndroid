package com.starry.smartflexboxlayout;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * 联系人
 *
 * @author Starry
 * @since 18-1-2.
 */
public class ContactBean implements Parcelable, Comparable<ContactBean> {

    private long date; //时间
    private String name; //名称
    private String email; //邮箱
    private String sortLetter; // 名称的首字母
    private String spell; // 名称拼音
    private boolean checked; //是否被选中

    public String getName() {
        return name;
    }

    public ContactBean setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ContactBean setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getSortLetter() {
        return sortLetter;
    }

    public ContactBean setSortLetter(String sortLetter) {
        this.sortLetter = sortLetter.toUpperCase();
        //正则匹配首字符，如果不是字母，则以‘#’代替
        if (!this.sortLetter.matches("[A-Z]")) {
            this.sortLetter = "#";
        }
        return this;
    }

    public String getSpell() {
        return spell;
    }

    public ContactBean setSpell(String spell) {
        this.spell = spell;
        return this;
    }

    public boolean isChecked() {
        return checked;
    }

    public ContactBean setChecked(boolean checked) {
        this.checked = checked;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.date);
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.sortLetter);
        dest.writeString(this.spell);
        dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
    }

    public ContactBean() {
    }

    protected ContactBean(Parcel in) {
        this.date = in.readLong();
        this.name = in.readString();
        this.email = in.readString();
        this.sortLetter = in.readString();
        this.spell = in.readString();
        this.checked = in.readByte() != 0;
    }

    public static final Creator<ContactBean> CREATOR = new Creator<ContactBean>() {
        @Override
        public ContactBean createFromParcel(Parcel source) {
            return new ContactBean(source);
        }

        @Override
        public ContactBean[] newArray(int size) {
            return new ContactBean[size];
        }
    };

    /**
     * 重写该方法用于去除重复元素
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactBean that = (ContactBean) o;

        return email != null ? email.equals(that.email) : that.email == null;

    }

    /**
     * 重写该方法用于去除重复元素
     */
    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }

    /**
     * feature_contacts_sort 按时间由近及远排序
     */
    @Override
    public int compareTo(@NonNull ContactBean o) {
        if (this.date - o.date > 0) {
            return -1;
        } else if (this.date == o.date) { //处理相等时返回0，否则会抛IllegalArgumentException
            return 0;
        } else {
            return 1;
        }
    }
}
