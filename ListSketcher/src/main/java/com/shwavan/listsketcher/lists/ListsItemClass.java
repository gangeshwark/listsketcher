package com.shwavan.listsketcher.lists;

import java.io.Serializable;

/**
 * Created by Gokul on 16/one/14.
 */
public class ListsItemClass implements Serializable {
    private long _item_id;
    private long _list_id;
    private String _item_name;
    private String _status;
    private String _imp;


    public ListsItemClass() {
    }

    public ListsItemClass(long item_id, long list_id, String name, String status, String imp) {
        this._item_id = item_id;
        this._list_id = list_id;
        this._item_name = name;
        this._status = status;
        this._imp = imp;
    }

    public ListsItemClass(long listid, String name, String status, String imp) {
        this._list_id = listid;
        this._item_name = name;
        this._status = status;
        this._imp = imp;
    }

    public long getListID() {
        return this._list_id;
    }

    public void setListID(long listid) {
        this._list_id = listid;
    }

    public long getItemID() {
        return this._item_id;
    }

    public void setItemID(long itemid) {
        this._item_id = itemid;
    }

    public String getName() {
        return this._item_name;
    }

    public void setName(String itemname) {
        this._item_name = itemname;
    }

    public String getStatus() {
        return this._status;
    }

    public void setStatus(String status) {
        this._status = status;
    }

    public String getImp() {
        return this._imp;
    }

    public void setImp(String imp) {
        this._imp = imp;
    }

}
