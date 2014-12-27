package com.shwavan.listsketcher;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shwavan.listsketcher.auth.SessionManager;
import com.shwavan.listsketcher.lists.ListsClass;
import com.shwavan.listsketcher.lists.ListsItemClass;
import com.shwavan.listsketcher.lists.SQLiteListsHelper;
import com.shwavan.listsketcher.util.ConnectionDetector;

import java.util.Calendar;
import java.util.List;

public class ScheduledSyncService extends IntentService {
    private static final String PREF_NAME = "SettingsPref";
    AlarmManager alarms;
    PendingIntent alarmIntent;

    public ScheduledSyncService() {
        super("ScheduledSyncService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        alarms = (AlarmManager) getSystemService(
                Context.ALARM_SERVICE);
        String ALARM_ACTION = "com.shwavan.listsketcher.SyncAlarm";
        Intent i = new Intent(ALARM_ACTION);

        alarmIntent = PendingIntent.getBroadcast(this, 0, i, 0);
    }

    private void setRecurringAlarm(Context context) {
        // we know mobiletuts updates at right around 1130 GMT.
        // let's grab new stuff at around 11:45 GMT, inexactly
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);

        if (sharedPreferences.getBoolean("sync", true)) {
            int interval = Integer.parseInt(sharedPreferences.getString("sync_frequency", "1"));
            Log.e("interval", sharedPreferences.getString("sync_frequency", "1"));
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 4);
            cal.set(Calendar.MINUTE, 0);
            alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(),
                    interval * AlarmManager.INTERVAL_DAY, alarmIntent);
        } else {
            alarms.cancel(alarmIntent);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ParseUser.getCurrentUser()==null)
        {
            onDestroy();
            stopSelf();
        }
        else {
            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            boolean con = cd.isConnectingToInternet();
            if (con) {
                setRecurringAlarm(getApplicationContext());
                Log.e("Sync", "Sync Started!!");
                download();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                upload();
                SessionManager session = new SessionManager(getApplicationContext());
                session.setKeySynced();
                Log.e("Sync", "Sync Done!!");
                Toast.makeText(getApplicationContext(), "Sync Done! Refresh List!", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("Sync", "No Connection!!");
                Toast.makeText(getApplicationContext(), "No Connection! Check your internet connection!", Toast.LENGTH_SHORT).show();
            }
            onDestroy();
            stopSelf();
        }
    }

    public void upload() {
        final SQLiteListsHelper listsHelper = new SQLiteListsHelper(getApplicationContext());
        int listscount = listsHelper.getListsCount();
        Log.e("SYNC", "listcount" + String.valueOf(listscount));
        List<ListsClass> list = listsHelper.getAllLists();
        long list_id = -1;
        Log.e("SYNC", "no of list " + String.valueOf(list.size()));
        for (ListsClass aList : list) {

            list_id = aList.getID();
            final String list_name = aList.getTitle();
            final String stat = aList.getStatus();
            final String created_on = aList.getCreatedOn();
            List<ListsItemClass> items = listsHelper.getAllListsItems(list_id);
            //check if already its uploaded...
            if (listsHelper.getListParseId(list_id) != null && !listsHelper.getListParseId(list_id).equals(""))// if parseId already exists...
            {
                if (listsHelper.getListParseId(list_id).equals("AA112233")) {
                    continue;
                }

                ParseQuery<ParseObject> query = ParseQuery.getQuery("lists");

                final ParseObject[] temp_list = {new ParseObject("lists")};

                query.getInBackground(listsHelper.getListParseId(list_id), new GetCallback<ParseObject>() {
                    public void done(ParseObject list, ParseException e) {
                        if (e == null) {
                            temp_list[0] = list;
                            // Now let's update it with some new data. In this case, only cheatMode and score
                            // will get sent to the Parse Cloud. playerName hasn't changed.
                            list.put("list_title", list_name);
                            list.put("status", stat);
                            list.put("created_on", created_on);
                            list.saveEventually();
                        } else {
                            Log.e("ListGetInBgError", "See details below!");
                            parseException(e);
                        }
                    }
                });
                for (final ListsItemClass item : items) {
                    final long item_id = item.getItemID();
                    final String title = item.getName();
                    final String imp = item.getImp();
                    final String status = item.getStatus();
                    long listId = item.getListID();
                    String objId = listsHelper.getItemParseId(item_id);
                    if (objId != null && !objId.equals("")) {
                        if (!objId.equals("AA112233")) {
                            Log.e("LOGLOGLOG", objId);
                            ParseQuery<ParseObject> itemQuery = ParseQuery.getQuery("lists_item");
                            itemQuery.whereEqualTo("parent", temp_list[0]);
                            itemQuery.whereEqualTo("user", ParseUser.getCurrentUser());
                            itemQuery.getInBackground(listsHelper.getItemParseId(item_id), new GetCallback<ParseObject>() {
                                public void done(ParseObject item, ParseException e) {
                                    if (e == null) {
                                        // Now let's update it with some new data. In this case, only cheatMode and score
                                        // will get sent to the Parse Cloud. playerName hasn't changed.
                                        item.put("item_name", title);
                                        item.put("item_status", status);
                                        item.put("important", imp);
                                        item.saveEventually();
                                    } else {
                                        Log.e("ItemGetInBGError", "See details below!");
                                        parseException(e);
                                    }
                                }
                            });
                        }
                    } else {
                        final ParseObject itemObj = new ParseObject("lists_item");
                        itemObj.put("item_id", item_id);
                        itemObj.put("item_name", title);
                        itemObj.put("item_status", status);
                        itemObj.put("important", imp);
                        itemObj.put("parent", temp_list[0]);
                        itemObj.put("list_id", list_id);
                        itemObj.put("user", ParseUser.getCurrentUser());
                        ParseACL ACL = new ParseACL(ParseUser.getCurrentUser());
                        itemObj.setACL(ACL);
                        itemObj.saveEventually(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    String item_objid = itemObj.getObjectId();

                                    Log.e("SYNC", "item_ObjID: " + item_objid + " Item id : " + item_id);
                                    listsHelper.addItemParseId(item_id, item_objid);
                                } else {
                                    Log.e("Upload Error", "See details below!");
                                    parseException(e);
                                }
                            }
                        });
                    }

                }
            } else { //if it doesn't exist
                final ParseObject listName = new ParseObject("lists");
                listName.put("list_id", list_id);
                listName.put("list_title", list_name);
                listName.put("status", stat);
                listName.put("created_on", created_on);
                listName.put("user", ParseUser.getCurrentUser());
                ParseACL ACL = new ParseACL(ParseUser.getCurrentUser());
                listName.setACL(ACL);
                Log.e("SYNC", "no of items " + String.valueOf(items.size()));

                for (int j = 0; j < items.size(); j++) {
                    final ParseObject item = new ParseObject("lists_item");
                    ListsItemClass item1 = items.get(j);
                    final long item_id = item1.getItemID();
                    String status = item1.getStatus();
                    String item_name = item1.getName();
                    String imp = item1.getImp();

                    item.put("item_id", item_id);
                    item.put("item_name", item_name);
                    item.put("item_status", status);
                    item.put("important", imp);
                    item.put("parent", listName);
                    item.put("list_id", list_id);
                    item.put("user", ParseUser.getCurrentUser());
                    ParseACL ACL1 = new ParseACL(ParseUser.getCurrentUser());
                    item.setACL(ACL1);
                    final long finalList_id = list_id;

                    item.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                String list_objid = listName.getObjectId();
                                String item_objid = item.getObjectId();
                                Log.e("SYNC", "list_ObjID: " + list_objid + " List id : " + finalList_id);
                                Log.e("SYNC", "item_ObjID: " + item_objid + " Item id : " + item_id);
                                listsHelper.addListParseId(finalList_id, list_objid);
                                listsHelper.addItemParseId(item_id, item_objid);
                            } else {
                                Log.e("Upload Error", "See details below!");
                                parseException(e);
                            }
                        }
                    });
                }
            }
        }

    }

    public void download() {
        final SQLiteListsHelper listsHelper = new SQLiteListsHelper(getApplicationContext());
        final boolean[] flag = {false};
        final ParseQuery<ParseObject> listname = ParseQuery.getQuery("lists");
        listname.whereEqualTo("user", ParseUser.getCurrentUser());
        listname.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> lists, ParseException e) {
                if (e == null) {
                    for (final ParseObject parseObject : lists) {
                        final String parseObjId = parseObject.getObjectId();
                        List<String> objectId = listsHelper.getListAllObjId();
                        int i = 0;
                        if (objectId != null) {
                            while (i < objectId.size()) {
                                if (objectId.get(i) != null) {
                                    if (objectId.get(i).equals(parseObjId)) {
                                        flag[0] = true;
                                        break;
                                    }
                                }
                                i++;
                            }
                        }
                        if (!flag[0]) {
                            String title = parseObject.getString("list_title");
                            int id = parseObject.getInt("list_id");
                            String status = parseObject.getString("status");
                            String created_on = parseObject.getString("created_on");
                            int count = listsHelper.getAllListsCount();
                            ListsClass list = new ListsClass(++count, title, status, created_on);
                            listsHelper.addNewList(list, parseObjId);
                            Log.e("DOWNLOAD", "item id " + count);
                            ParseQuery<ParseObject> item = ParseQuery.getQuery("lists_item");
                            item.whereEqualTo("parent", parseObject);
                            final int finalCount = count;
                            item.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> items, ParseException e) {
                                    if (e == null) {
                                        Log.e("QUERY", "item size " + items.size());
                                        for (ParseObject parseObject : items) {
                                            String name = parseObject.getString("item_name");
                                            String stat = parseObject.getString("item_status");
                                            String imp = parseObject.getString("important");
                                            String objId = parseObject.getObjectId();

                                            int itemcount = listsHelper.getItemsCount();
                                            ListsItemClass listsItem = new ListsItemClass(++itemcount, finalCount, name, stat, imp);
                                            Log.e("DOWNLOAD", "itemid " + itemcount + "  listid " + finalCount);
                                            listsHelper.addListItem(listsItem, objId);
                                            Log.e("QUERY", "Name: " + name);
                                        }
                                    } else {
                                        Log.e("Item_QUERY_ERROR", "See details below!");
                                        parseException(e);
                                    }
                                }
                            });
                        }
                    }
                } else {
                    Log.e("List_QUERY_ERROR", "See details below!");
                    parseException(e);
                }
            }
        });

    }

    void parseException(ParseException e) {
        switch (e.getCode()) {
            case ParseException.MUST_CREATE_USER_THROUGH_SIGNUP:
                Log.e("ParseException", "MUST_CREATE_USER_THROUGH_SIGNUP");
                break;
            case ParseException.EMAIL_TAKEN:
                Log.e("ParseException", "EMAIL_TAKEN");
                break;
            case ParseException.ACCOUNT_ALREADY_LINKED:
                Log.e("ParseException", "ACCOUNT_ALREADY_LINKED");
                break;
            case ParseException.CACHE_MISS:
                Log.e("ParseException", "CACHE_MISS");
                break;
            case ParseException.COMMAND_UNAVAILABLE:
                Log.e("ParseException", "COMMAND_UNAVAILABLE");
                break;
            case ParseException.CONNECTION_FAILED:
                Log.e("ParseException", "CONNECTION_FAILED");
                break;
            case ParseException.DUPLICATE_VALUE:
                Log.e("ParseException", "DUPLICATE_VALUE");
                break;
            case ParseException.EMAIL_MISSING:
                Log.e("ParseException", "EMAIL_MISSING");
                break;
            case ParseException.EMAIL_NOT_FOUND:
                Log.e("ParseException", "EMAIL_NOT_FOUND");
                break;
            case ParseException.FILE_DELETE_ERROR:
                Log.e("ParseException", "FILE_DELETE_ERROR");
                break;
            case ParseException.INTERNAL_SERVER_ERROR:
                Log.e("ParseException", "INTERNAL_SERVER_ERROR");
                break;
            case ParseException.INVALID_EMAIL_ADDRESS:
                Log.e("ParseException", "INVALID_EMAIL_ADDRESS");
                break;
            case ParseException.INVALID_ACL:
                Log.e("ParseException", "INVALID_ACL");
                break;
            case ParseException.INVALID_QUERY:
                Log.e("ParseException", "INVALID_QUERY");
                break;
            case ParseException.OBJECT_NOT_FOUND:
                Log.e("ParseException", "OBJECT_NOT_FOUND");
                break;
            case ParseException.LINKED_ID_MISSING:
                Log.e("ParseException", "LINKED_ID_MISSING");
                break;
            case ParseException.VALIDATION_ERROR:
                Log.e("ParseException", "VALIDATION_ERROR");
                break;
            case ParseException.USERNAME_MISSING:
                Log.e("ParseException", "USERNAME_MISSING");
                break;
            case ParseException.NOT_INITIALIZED:
                Log.e("ParseException", "NOT_INITIALIZED");
                break;
            case ParseException.EXCEEDED_QUOTA:
                Log.e("ParseException", "EXCEEDED_QUOTA");
                break;
            case ParseException.INVALID_CLASS_NAME:
                Log.e("ParseException", "INVALID_CLASS_NAME");
                break;
            case ParseException.INCORRECT_TYPE:
                Log.e("ParseException", "INCORRECT_TYPE");
                break;
            case ParseException.INVALID_CHANNEL_NAME:
                Log.e("ParseException", "INVALID_CHANNEL_NAME");
                break;
            case ParseException.INVALID_FILE_NAME:
                Log.e("ParseException", "INVALID_FILE_NAME");
                break;
            case ParseException.INVALID_JSON:
                Log.e("ParseException", "INVALID_JSON");
                break;
            case ParseException.INVALID_KEY_NAME:
                Log.e("ParseException", "INVALID_KEY_NAME");
                break;
            case ParseException.INVALID_LINKED_SESSION:
                Log.e("ParseException", "INVALID_LINKED_SESSION");
                break;
            case ParseException.INVALID_NESTED_KEY:
                Log.e("ParseException", "INVALID_NESTED_KEY");
                break;
            case ParseException.INVALID_POINTER:
                Log.e("ParseException", "INVALID_POINTER");
                break;
            case ParseException.INVALID_ROLE_NAME:
                Log.e("ParseException", "INVALID_ROLE_NAME");
                break;
            case ParseException.MISSING_OBJECT_ID:
                Log.e("ParseException", "MISSING_OBJECT_ID");
                break;
            case ParseException.OBJECT_TOO_LARGE:
                Log.e("ParseException", "OBJECT_TOO_LARGE");
                break;
            case ParseException.OPERATION_FORBIDDEN:
                Log.e("ParseException", "OPERATION_FORBIDDEN");
                break;
            case ParseException.OTHER_CAUSE:
                Log.e("ParseException", "OTHER_CAUSE");
                break;
            case ParseException.PASSWORD_MISSING:
                Log.e("ParseException", "PASSWORD_MISSING");
                break;
            case ParseException.PUSH_MISCONFIGURED:
                Log.e("ParseException", "PUSH_MISCONFIGURED");
                break;
            case ParseException.SCRIPT_ERROR:
                Log.e("ParseException", "SCRIPT_ERROR");
                break;
            case ParseException.SESSION_MISSING:
                Log.e("ParseException", "SESSION_MISSING");
                break;
            case ParseException.TIMEOUT:
                Log.e("ParseException", "TIMEOUT");
                break;
            case ParseException.UNSUPPORTED_SERVICE:
                Log.e("ParseException", "UNSUPPORTED_SERVICE");
                break;
            case ParseException.USERNAME_TAKEN:
                Log.e("ParseException", "USERNAME_TAKEN");
                break;
            default:
                Log.e("ParseException", "Exception Not Found!!");
                break;
        }
    }


}
