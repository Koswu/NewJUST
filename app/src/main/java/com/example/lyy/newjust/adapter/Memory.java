package com.example.lyy.newjust.adapter;

/**
 * Created by lyy on 2017/10/27.
 */

public class Memory {

    private String memory_content;  //提醒的内容
    private String memory_day;      //提醒的日期

    public Memory(String content, String day) {
        this.memory_content = content;
        this.memory_day = day;
    }

    public String getMemory_content() {
        return memory_content;
    }

    public void setMemory_content(String memory_content) {
        this.memory_content = memory_content;
    }

    public String getMemory_day() {
        return memory_day;
    }

    public void setMemory_day(String memory_day) {
        this.memory_day = memory_day;
    }
}
