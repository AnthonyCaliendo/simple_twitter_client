package com.anthonycaliendo.twitterclient;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.TableInfo;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Calendar;

/**
 * Represents a twitter user.
 */
@Table(name = "users")
public class User extends Model implements Parcelable {

    public static final String PARCELABLE_KEY = "com.anthonycaliendo.twitterclient.User";

    @Column
    public long twitterId;

    @Column
    public String name;

    @Column
    public String username;

    @Column
    public String profileImageUrl;

    @Column
    public String profileBackgroundImageUrl;

    @Column
    public String description;

    @Column
    public int followersCount;

    @Column
    public int followingCount;

    @Column
    public int tweetCount;

    public User() {
        super();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.twitterId);
        dest.writeString(this.name);
        dest.writeString(this.username);
        dest.writeString(this.profileImageUrl);
        dest.writeString(this.profileBackgroundImageUrl);
        dest.writeString(this.description);
        dest.writeInt(this.followersCount);
        dest.writeInt(this.followingCount);
        dest.writeInt(this.tweetCount);
    }

    private User(Parcel in) {
        this.twitterId = in.readLong();
        this.name = in.readString();
        this.username = in.readString();
        this.profileImageUrl = in.readString();
        this.profileBackgroundImageUrl = in.readString();
        this.description = in.readString();
        this.followersCount = in.readInt();
        this.followingCount = in.readInt();
        this.tweetCount = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
