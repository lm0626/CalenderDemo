package com.bbk.bean;

public class HistoryBean {
    private String _id;
    private String day;
    private String des;
    private String lunar;
    private String pic;
    private String title;
    private String year;
    private String month;
    
    
    public String getDes() {
        return des;
    }


    public void setDes(String des) {
        this.des = des;
    }


    @Override
    public String toString() {
        return "Wiki [_id=" + _id + ", day=" + day + ", des=" + des
                + ", lunar=" + lunar + ", pic=" + pic + ", title=" + title
                + ", year=" + year + ", month=" + month + "]";
    }
    
//  "_id":"19071101",
//  "day":1,
//  "des":"在107年前的今天，1907年11月1日 (农历九月廿六)，电影导演吴永刚诞生。",
//  "lunar":"",
//  "month":11,
//  "pic":"",
//  "title":"电影导演吴永刚诞生",
//  "year":1907

}
