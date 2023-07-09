package com.hpe.findlover.model;

import java.util.Date;
import java.util.List;
import javax.persistence.*;

public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String password;

    /**
     * 0：超级管理员、1：普通管理员
     */
    private Integer flag;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "last_login")
    private Date lastLogin;

    @Transient
    private List<Role> roles;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取0：超级管理员、1：普通管理员
     *
     * @return flag - 0：超级管理员、1：普通管理员
     */
    public Integer getFlag() {
        return flag;
    }

    /**
     * 设置0：超级管理员、1：普通管理员
     *
     * @param flag 0：超级管理员、1：普通管理员
     */
    public void setFlag(Integer flag) {
        this.flag = flag;
    }


    public Date getCreateTime() {
        return createTime;
    }


    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public Date getLastLogin() {
        return lastLogin;
    }


    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", flag=" + flag +
                ", createTime=" + createTime +
                ", lastLogin=" + lastLogin +
                ", roles=" + roles +
                '}';
    }
}