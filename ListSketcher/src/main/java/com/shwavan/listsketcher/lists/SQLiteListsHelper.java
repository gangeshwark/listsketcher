package com.shwavan.listsketcher.lists;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Gokul on 11/one/14.
 */

public class SQLiteListsHelper extends SQLiteOpenHelper {
    // Database Name
    public static final String DATABASE_NAME = "ListSketcher.db";
    // Lists table name
    public static final String TABLE_LISTS = "lists";
    public static final String TABLE_LISTS_ITEMS = "lists_item";
    public static final String TABLE_REMINDER = "reminder";
    // Lists Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_PARSE_ID = "parse_obj_id";
    public static final String KEY_TITLE = "title";
    //"done" or "to_do" or "archive" or "delete"
    public static final String KEY_STATUS = "status";
    public static final String KEY_CREATED_ON = "created_on";
    //Tables
    private static final String CREATE_LISTS_TABLE = "CREATE TABLE " + TABLE_LISTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TITLE + " TEXT,"
            + KEY_STATUS + " TEXT,"
            + KEY_CREATED_ON + " TEXT,"
            + KEY_PARSE_ID + " TEXT " + ")";
    // Item Table Columns names
    public static final String KEY_ITEM_ID = "item_id";

    public static final String KEY_LIST_ID = "list_id";
    public static final String KEY_ITEM_NAME = "item_name";
    public static final String KEY_ITEM_IMP = "important";
    //"done" or "to_do" or "archive" or "delete" or "imp"
    public static final String KEY_ITEM_STATUS = "item_status";
    private static final String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_LISTS_ITEMS + "("
            + KEY_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_LIST_ID + " INTEGER,"
            + KEY_ITEM_NAME + " TEXT,"
            + KEY_ITEM_STATUS + " TEXT,"
            + KEY_ITEM_IMP + " TEXT,"
            + KEY_PARSE_ID + " TEXT " + ")";

    // Item Table Columns names
    public static final String KEY_REM_ID = "rem_id";
    public static final String KEY_DAY = "day";
    public static final String KEY_MONTH = "month";
    public static final String KEY_YEAR = "year";
    public static final String KEY_HOUR = "hour";
    public static final String KEY_MINUTE = "minute";
    public static final String KEY_REM_STAT = "status"; //done or not_done

    private static final String CREATE_REM_TABLE = "CREATE TABLE " + TABLE_REMINDER + "("
            + KEY_REM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_LIST_ID + " INTEGER,"
            + KEY_DAY + " INTEGER,"
            + KEY_MONTH + " INTEGER,"
            + KEY_YEAR + " INTEGER,"
            + KEY_HOUR + " INTEGER,"
            + KEY_MINUTE + " INTEGER,"
            + KEY_REM_STAT + " TEXT" + ")";
    // Database Version
    private static final int DATABASE_VERSION = 3;

    public SQLiteListsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_LISTS_TABLE);
        db.execSQL(CREATE_ITEMS_TABLE);
        db.execSQL(CREATE_REM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            db.execSQL(CREATE_REM_TABLE);
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_LISTS + " ADD " + KEY_PARSE_ID + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_LISTS_ITEMS + " ADD " + KEY_PARSE_ID + " TEXT");
        }
    }

    public void clearDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LISTS_ITEMS, null, null);
        db.delete(TABLE_REMINDER, null, null);
        db.delete(TABLE_LISTS, null, null);
        db.close();
    }

    public void addNewList(ListsClass addList) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues list = new ContentValues();
        if (addList.getID() != -1) {
            list.put(KEY_ID, addList.getID());
        }

        list.put(KEY_TITLE, addList.getTitle());
        list.put(KEY_STATUS, addList.getStatus());
        list.put(KEY_CREATED_ON, addList.getCreatedOn());
        try {

            db.insert(TABLE_LISTS, null, list);
        } catch (SQLException e) {
            Log.e("Error writing new list ", e.toString());
        }
        db.close();
    }

    public void addNewList(ListsClass addList, String objId) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues list = new ContentValues();
        if (addList.getID() != -1) {
            list.put(KEY_ID, addList.getID());
        }

        list.put(KEY_TITLE, addList.getTitle());
        list.put(KEY_STATUS, addList.getStatus());
        list.put(KEY_CREATED_ON, addList.getCreatedOn());
        list.put(KEY_PARSE_ID, objId);
        try {

            db.insert(TABLE_LISTS, null, list);
        } catch (SQLException e) {
            Log.e("Error writing new list ", e.toString());
        }
        db.close();
    }

    public void addListParseId(long list_id, String ObjId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues list = new ContentValues();
        list.put(KEY_PARSE_ID, ObjId);
        String[] whereArgs = new String[]{Long.toString(list_id)};

        try {
            db.update(TABLE_LISTS, list, KEY_ID + "=?", whereArgs);

        } catch (SQLException e) {
            Log.e("Error updating the list ", e.toString());
        }
        db.close();

    }

    public void addItemParseId(long item_id, String ObjId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues list = new ContentValues();
        list.put(KEY_PARSE_ID, ObjId);
        String[] whereArgs = new String[]{Long.toString(item_id)};

        try {
            db.update(TABLE_LISTS_ITEMS, list, KEY_ITEM_ID + "=?", whereArgs);

        } catch (SQLException e) {
            Log.e("Error updating the list ", e.toString());
        }
        db.close();

    }

    public String getListParseId(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT " + KEY_PARSE_ID + " FROM " + TABLE_LISTS + " WHERE " + KEY_ID + " = " + id;
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            db.close();
            return cursor.getString(0);
        }
        db.close();
        return null;
    }

    public String getItemParseId(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT " + KEY_PARSE_ID + " FROM " + TABLE_LISTS_ITEMS + " WHERE " + KEY_ITEM_ID + " = " + id;
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            db.close();
            return cursor.getString(0);
        }
        db.close();
        return null;

    }

    public void updateList(ListsClass List) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateList = new ContentValues();
        updateList.put(KEY_TITLE, List.getTitle());
        updateList.put(KEY_STATUS, List.getStatus());
        updateList.put(KEY_CREATED_ON, List.getCreatedOn());
        String[] whereArgs = new String[]{Long.toString(List.getID())};
        try {
            db.update(TABLE_LISTS, updateList, KEY_ID + "=?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error updating the list ", e.toString());
        }
        db.close();
    }

    public void updateListToDone(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateList = new ContentValues();
        updateList.put(KEY_STATUS, "done");
        String[] whereArgs = new String[]{Long.toString(id)};
        try {
            db.update(TABLE_LISTS, updateList, KEY_ID + "=?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error updating the list ", e.toString());
        }
        db.close();
    }

    public void updateListToDelete(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateList = new ContentValues();
        updateList.put(KEY_STATUS, "delete");
        String[] whereArgs = new String[]{Long.toString(id)};
        try {
            db.update(TABLE_LISTS, updateList, KEY_ID + "=?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error updating the list ", e.toString());
        }
        db.close();
    }

    public void updateListItemsToDone(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateList = new ContentValues();
        updateList.put(KEY_ITEM_STATUS, "done");
        String[] whereArgs = new String[]{Long.toString(id)};
        try {
            db.update(TABLE_LISTS_ITEMS, updateList, KEY_LIST_ID + "=?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error updating the list ", e.toString());
        }
        db.close();
    }

    public void updateListItemsToDelete(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateList = new ContentValues();
        updateList.put(KEY_ITEM_STATUS, "delete");
        String[] whereArgs = new String[]{Long.toString(id)};
        try {
            db.update(TABLE_LISTS_ITEMS, updateList, KEY_LIST_ID + "=?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error updating the list ", e.toString());
        }
        db.close();
    }

    public void updateListItems(long id, ListsItemClass item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateList = new ContentValues();
        updateList.put(KEY_ITEM_IMP, item.getImp());
        updateList.put(KEY_ITEM_STATUS, item.getStatus());
        updateList.put(KEY_ITEM_NAME, item.getName());
        String[] whereArgs = new String[]{Long.toString(id)};
        try {
            db.update(TABLE_LISTS_ITEMS, updateList, KEY_LIST_ID + "=?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error updating the list ", e.toString());
        }
        db.close();
    }

    public void updateListItemToImp(long id, String imp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateList = new ContentValues();
        updateList.put(KEY_ITEM_IMP, imp);
        String[] whereArgs = new String[]{Long.toString(id)};
        try {
            db.update(TABLE_LISTS_ITEMS, updateList, KEY_ITEM_ID + "=?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error updating the list ", e.toString());
        }
        db.close();
    }

    public void updateListItemToDone(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateList = new ContentValues();
        updateList.put(KEY_ITEM_STATUS, "done");
        String[] whereArgs = new String[]{Long.toString(id)};
        try {
            db.update(TABLE_LISTS_ITEMS, updateList, KEY_ITEM_ID + "=?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error updating the list ", e.toString());
        }
        db.close();
    }

    public void updateListItemToDo(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateList = new ContentValues();
        updateList.put(KEY_ITEM_STATUS, "to_do");
        String[] whereArgs = new String[]{Long.toString(id)};
        try {
            db.update(TABLE_LISTS_ITEMS, updateList, KEY_ITEM_ID + "=?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error updating the list ", e.toString());
        }
        db.close();
    }

    public void deleteList(ListsClass list) {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] whereArgs = new String[]{Long.toString(list.getID())};
        try {
            db.delete(TABLE_LISTS, KEY_ID + "=?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error deleting the list ", e.toString());
        }
        db.close();
    }

    public void deleteItem(ListsItemClass item) {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] whereArgs = new String[]{Long.toString(item.getItemID())};
        try {
            db.delete(TABLE_LISTS_ITEMS, KEY_ITEM_ID + "=?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error deleting the list ", e.toString());
        }
        db.close();
    }

    public void deleteItem(Long item_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] whereArgs = new String[]{Long.toString(item_id)};
        try {
            db.delete(TABLE_LISTS_ITEMS, KEY_ITEM_ID + "=?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error deleting the list ", e.toString());
        }
        db.close();
    }

    public void deleteList(Long list_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs = new String[]{Long.toString(list_id)};
        try {
            db.delete(TABLE_LISTS, KEY_ID + "=?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error deleting the list ", e.toString());
        }
        db.close();
    }

    public ListsClass getList(long id) {
        ListsClass listsClass = new ListsClass();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_LISTS, new String[]{KEY_ID, KEY_TITLE, KEY_STATUS,
                KEY_CREATED_ON}, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            listsClass = new ListsClass(Long.parseLong(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3));
        }
        db.close();
        return listsClass;
    }


    public List<ListsClass> getAllLists(String stat) {
        List<ListsClass> lists = new ArrayList<ListsClass>();
        //Select all query
        String Query = "SELECT * FROM " + TABLE_LISTS + " WHERE " + KEY_STATUS + " = " + "\"" + stat + "\"";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor != null) {
            if (cursor.moveToLast()) {
                do {
                    ListsClass list = new ListsClass();
                    list.setID(Long.parseLong(cursor.getString(0)));
                    list.setTitle(cursor.getString(1));
                    list.setStatus(cursor.getString(2));
                    list.setCreatedOn(cursor.getString(3));
                    lists.add(list);
                } while (cursor.moveToPrevious());
            }
        }
        db.close();
        return lists;
    }

    public List<String> getListAllObjId() {
        List<String> objId = new ArrayList<String>();
        String Query = "SELECT " + KEY_PARSE_ID + " FROM " + TABLE_LISTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(Query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String ObjId;
                    ObjId = cursor.getString(0);
                    objId.add(ObjId);
                } while (cursor.moveToNext());
            }
        } else {
            return null;
        }
        db.close();
        return objId;
    }

    public List<ListsClass> getAllLists() {
        List<ListsClass> lists = new ArrayList<ListsClass>();
        //Select all query
        String Query = "SELECT * FROM " + TABLE_LISTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor != null) {
            if (cursor.moveToLast()) {
                do {
                    ListsClass list = new ListsClass();
                    list.setID(Long.parseLong(cursor.getString(0)));
                    list.setTitle(cursor.getString(1));
                    list.setStatus(cursor.getString(2));
                    list.setCreatedOn(cursor.getString(3));
                    lists.add(list);
                } while (cursor.moveToPrevious());
            }
        }
        db.close();
        return lists;
    }

    public int getAllListsCount() {
        int count;
        String countQuery = "SELECT * FROM " + TABLE_LISTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

    public int getListsCount() {
        int count;
        String countQuery = "SELECT * FROM " + TABLE_LISTS + " WHERE " + KEY_STATUS + " = \"to_do\" ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

    public int getItemsCount() {
        int count;
        String countQuery = "SELECT * FROM " + TABLE_LISTS_ITEMS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

    public int getListItemsCount(long list_id) {
        int count;
        String countQuery = "SELECT * FROM " + TABLE_LISTS_ITEMS + " WHERE " + KEY_LIST_ID + " = " + list_id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

    public int getListItemsCountNotDelete(long list_id) {
        int count;
        String countQuery = "SELECT * FROM " + TABLE_LISTS_ITEMS + " WHERE " + KEY_LIST_ID + " = " + list_id
                + " AND NOT " + KEY_ITEM_STATUS + " = \"delete\" ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

    public void addListItem(ListsItemClass addItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues list = new ContentValues();
        if (addItem.getItemID() != -1) {
            list.put(KEY_ITEM_ID, addItem.getItemID());
        }
        list.put(KEY_LIST_ID, addItem.getListID());
        list.put(KEY_ITEM_NAME, addItem.getName());
        list.put(KEY_ITEM_STATUS, addItem.getStatus());
        list.put(KEY_ITEM_IMP, addItem.getImp());
        try {
            db.insert(TABLE_LISTS_ITEMS, null, list);
        } catch (SQLException e) {
            Log.e("Error writing new list ", e.toString());
        }
        db.close();

    }

    public void addListItem(ListsItemClass addItem, String objId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues list = new ContentValues();
        if (addItem.getItemID() != -1) {
            list.put(KEY_ITEM_ID, addItem.getItemID());
        }
        list.put(KEY_LIST_ID, addItem.getListID());
        list.put(KEY_ITEM_NAME, addItem.getName());
        list.put(KEY_ITEM_STATUS, addItem.getStatus());
        list.put(KEY_ITEM_IMP, addItem.getImp());
        list.put(KEY_PARSE_ID, objId);

        try {
            db.insert(TABLE_LISTS_ITEMS, null, list);
        } catch (SQLException e) {
            Log.e("Error writing new list ", e.toString());
        }
        db.close();

    }


    public List<ListsItemClass> getAllListsItems(Long list_id) {
        List<ListsItemClass> lists = new ArrayList<ListsItemClass>();
        //Select all query
        String Query = "SELECT * FROM " + TABLE_LISTS_ITEMS + " WHERE " + KEY_LIST_ID + " = "
                + list_id + " AND NOT " + KEY_ITEM_STATUS + " = \"delete\" ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ListsItemClass list = new ListsItemClass();
                    list.setItemID(Long.parseLong(cursor.getString(0)));
                    list.setListID(Long.parseLong(cursor.getString(1)));
                    list.setName(cursor.getString(2));
                    list.setStatus(cursor.getString(3));
                    list.setImp(cursor.getString(4));
                    lists.add(list);
                } while (cursor.moveToNext());
            }
        }
        db.close();
        return lists;
    }

    public void updateLists(Long list_id, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateList = new ContentValues();
        updateList.put(KEY_TITLE, title);

        String[] whereArgs = new String[]{Long.toString(list_id)};
        try {
            db.update(TABLE_LISTS, updateList, KEY_ID + "=?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error updating the list ", e.toString());
        }
        db.close();
    }

    public void deleteListItems(Long list_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs = new String[]{Long.toString(list_id)};
        try {
            db.delete(TABLE_LISTS_ITEMS, KEY_LIST_ID + " = ?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error deleting the list ", e.toString());
        }
        db.close();
    }


    public ListsItemClass getListsItems(Long item_id) {
        ListsItemClass lists = new ListsItemClass();
        //Select all query
        String Query = "SELECT * FROM " + TABLE_LISTS_ITEMS + " WHERE " + KEY_ITEM_ID + " = "
                + item_id /*+ " AND " + KEY_ITEM_STATUS + " = \"to_do\" "*/;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    lists.setItemID(Long.parseLong(cursor.getString(0)));
                    lists.setListID(Long.parseLong(cursor.getString(1)));
                    lists.setName(cursor.getString(2));
                    lists.setStatus(cursor.getString(3));
                } while (cursor.moveToNext());
            }
        }
        db.close();
        return lists;
    }


    public void addNewRem(RemClass reminder) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues list = new ContentValues();
        if (reminder.get_rem_id() != -1) {
            list.put(KEY_REM_ID, reminder.get_rem_id());
        }

        list.put(KEY_LIST_ID, reminder.get_list_id());
        list.put(KEY_DAY, reminder.get_day());
        list.put(KEY_MONTH, reminder.get_month());
        list.put(KEY_YEAR, reminder.get_year());
        list.put(KEY_HOUR, reminder.get_hour());
        list.put(KEY_MINUTE, reminder.get_minute());
        list.put(KEY_REM_STAT, reminder.get_status());
        try {

            db.insert(TABLE_REMINDER, null, list);
        } catch (SQLException e) {
            Log.e("Error writing new list ", e.toString());
        }
        db.close();

    }

    public RemClass getReminder(long list_id) {
        RemClass remClass;
        String query = "SELECT * FROM " + TABLE_REMINDER + " WHERE " + KEY_LIST_ID + " = "
                + list_id + " AND " + KEY_REM_STAT + " = \"not_done\" ";
        //Select all query
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            remClass = new RemClass(
                    Long.parseLong(cursor.getString(0)),
                    Long.parseLong(cursor.getString(1)),
                    Integer.parseInt(cursor.getString(2)),
                    Integer.parseInt(cursor.getString(3)),
                    Integer.parseInt(cursor.getString(4)),
                    Integer.parseInt(cursor.getString(5)),
                    Integer.parseInt(cursor.getString(6)),
                    cursor.getString(7));
            db.close();
            return remClass;

        } else {
            return null;
        }
    }

    public int getRemCount() {
        int count;
        String countQuery = "SELECT * FROM " + TABLE_REMINDER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

    public void updateRemToDone(long list_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues update = new ContentValues();
        update.put(KEY_REM_STAT, "done");
        String[] whereArgs = new String[]{Long.toString(list_id)};
        try {
            db.update(TABLE_REMINDER, update, KEY_LIST_ID + "=?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error updating the list ", e.toString());
        }
        db.close();
    }
}