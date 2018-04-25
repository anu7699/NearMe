package com.wink.anu.nearme.login;

/**
 * Created by WELLCOME on 17-03-2018.
 */

public class User {
    private String email,password,Username,phone;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return Username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;

    }

    public User()

    {

    }
    public User(String sEmail,String sPass,String sUsername,String sPhone)
    {
        email=sEmail;
        password=sPass;
        Username=sUsername;
        phone=sPhone;
    }
}
