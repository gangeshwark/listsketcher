package com.shwavan.listsketcher.lists;

import java.io.Serializable;
import java.util.List;

/**
 * Created by GANGESHWAR on 15/3/14.
 */
public class FullListObject implements Serializable {
    private ListsClass listName;
    private List<ListsItemClass> items;
    private String author;

    public FullListObject() {
    }

    public FullListObject(ListsClass listname, List<ListsItemClass> items, String author) {
        this.listName = listname;
        this.items = items;
        this.author = author;
    }

    public ListsClass getList() {
        return this.listName;
    }

    public List<ListsItemClass> getItems() {
        return items;
    }

    public String getAuthor() {
        return this.author;
    }

}