package com.anthonycaliendo.twitterclient;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Calendar;

/**
 * Represents a twitter user.
 */
@Table(name = "users")
public class User extends Model {

    @Column
    public String name;

    @Column
    public String username;

    @Column
    public String profileImageUrl;

    public User() {
        super();
    }
}
