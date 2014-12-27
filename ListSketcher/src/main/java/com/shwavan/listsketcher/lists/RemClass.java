package com.shwavan.listsketcher.lists;


/**
 * Created by GANGESHWAR on 28-03-2014.
 */
public class RemClass {
    private long _rem_id;
    private long _list_id;
    private int _day;
    private int _month;
    private int _year;
    private int _hour;
    private int _minute;
    private String _status;

    public RemClass() {
    }

    public RemClass(long rem_id, long list_id, int day, int month, int year, int hour, int minute, String status) {
        this._rem_id = rem_id;
        this._list_id = list_id;
        this._day = day;
        this._month = month;
        this._year = year;
        this._hour = hour;
        this._minute = minute;
        this._status = status;
    }

    public RemClass(long list_id, int day, int month, int year, int hour, int minute, String status) {
        this._list_id = list_id;
        this._day = day;
        this._month = month;
        this._year = year;
        this._hour = hour;
        this._minute = minute;
        this._status = status;
    }

    public long get_rem_id() {
        return this._rem_id;
    }

    public void set_rem_id(long rem_id) {
        this._rem_id = rem_id;
    }

    public long get_list_id() {
        return this._list_id;
    }

    public void set_list_id(long list_id) {
        this._list_id = list_id;
    }

    public int get_day() {
        return this._day;
    }

    public void set_day(int day) {
        this._day = day;
    }

    public int get_month() {
        return this._month;
    }

    public void set_month(int month) {
        this._month = month;
    }

    public int get_hour() {
        return this._hour;
    }

    public void set_hour(int hour) {
        this._hour = hour;
    }

    public int get_minute() {
        return this._minute;
    }

    public void set_minute(int minute) {
        this._minute = minute;
    }

    public int get_year() {
        return this._year;
    }

    public void set_year(int year) {
        this._year = year;
    }

    public String get_status() {
        return this._status;
    }

    public void set_status(String _status) {
        this._status = _status;
    }
}
