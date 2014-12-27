package com.shwavan.listsketcher.lists;

import java.io.Serializable;

/**
 * Created by Gokul on 11/one/14.
 */
public class ListsClass implements Serializable {
    private long _id;
    private String _title;
    private String _status;
    private String _created_on;

    public ListsClass() {
    }

    public ListsClass(long id, String title, String status, String created_on) {
        this._id = id;
        this._title = title;
        this._status = status;
        this._created_on = created_on;
    }

    public ListsClass(String title, String status, String created_on) {
        this._title = title;
        this._status = status;
        this._created_on = created_on;
    }

    public long getID() {
        return this._id;
    }

    public void setID(long id) {
        this._id = id;
    }

    public String getTitle() {
        return this._title;
    }

    public void setTitle(String title) {
        this._title = title;
    }

    public String getStatus() {
        return this._status;
    }

    public void setStatus(String status) {
        this._status = status;
    }

    public String getCreatedOn() {
        return this._created_on;
    }

    public void setCreatedOn(String created_on) {
        this._created_on = created_on;
    }


}
