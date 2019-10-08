package com.flash.message.entity.tabooword;

import javax.persistence.*;
import java.util.Date;

/**
 * 作者： chengli
 * 日期： 2019/10/1 17:19
 */
@Entity
@Table(name = "tbl_taboo_word")
public class TabooWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "taboo_word")
    private String tabooWord;
    @Column(name = "created_date")
    private Date createTime;
    @Column(name = "updated_date")
    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTabooWord() {
        return tabooWord;
    }

    public void setTabooWord(String tabooWord) {
        this.tabooWord = tabooWord;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
