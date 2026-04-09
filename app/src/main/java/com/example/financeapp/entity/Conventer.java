package com.example.financeapp.entity;

import androidx.room.TypeConverter;

import java.util.Date;

public class Conventer {

    @TypeConverter
    public Long toLong(Date date){
        return   date == null ? null : date.getTime();
    }
    @TypeConverter
    public Date todate(Long timestamp){

        return  timestamp == null ? null : new Date(timestamp);
    }
}
