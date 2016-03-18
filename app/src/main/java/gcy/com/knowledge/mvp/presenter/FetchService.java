package gcy.com.knowledge.mvp.presenter;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import gcy.com.knowledge.mvp.model.Image;
import gcy.com.knowledge.mvp.other.DBParser;
import gcy.com.knowledge.mvp.other.ImageHelper;
import gcy.com.knowledge.mvp.view.PictureFragment;
import gcy.com.knowledge.net.API;
import gcy.com.knowledge.net.Net;
import gcy.com.knowledge.utils.Constants;
import io.realm.Realm;
import io.realm.RealmObject;
import okhttp3.Call;
import okhttp3.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class FetchService extends IntentService {

    public static final String ACTION_FETCH = "common_fetch";
    public static final String ACTION_FETCH_H_DETAIL = "fetch_h_detail";
    public static final String EXTRA_FETCHED_NUM = "fetched_num";
    public static final String EXTRA_FETCHED_RESULT = "fetched_result";
    private LocalBroadcastManager localBroadcastManager;
    public long GET_DURATION = 3000;
    private int type;
    private String data;
    private boolean stopFetchAll;
    private ImageHelper helper;
    public static Realm realm;

    public FetchService() {
        super("PictureFetchService");
    }


    public static void startActionFetch(Context context, int type, String response) {

        Intent intent = new Intent(context, FetchService.class);
        intent.setAction(ACTION_FETCH);
        intent.putExtra(Constants.TYPE, type);
        intent.putExtra(Constants.DATA, response);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        realm = Realm.getDefaultInstance();
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH.equals(action)) {
                type = intent.getIntExtra(Constants.TYPE, 0);
                data = intent.getStringExtra(Constants.DATA);
                parse(data);
            }
        }
    }


    private void parse(String response) {
        boolean isSuccess = false;

        helper = new ImageHelper(this, type);
        if (type == PictureFragment.TYPE_GANK) {
            isSuccess = save(parseGANK(response));
        } else if (PictureFragment.TYPE_GANK < type
                && type <= PictureFragment.TYPE_DB_RANK) {
            isSuccess = save(helper.saveImages(DBParser.parse(response)));
        }
        sendResult(isSuccess);
    }

    private <E extends RealmObject> boolean save(List<E> objects) {
        if (objects != null) {
            if (objects.size() >= 0) {
                FetchService.realm.beginTransaction();
                FetchService.realm.copyToRealmOrUpdate(objects);
                FetchService.realm.commitTransaction();
                return true;
            }
        }
        return false;
    }



    private List<Image> parseGANK(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray array = jsonObject.getJSONArray("results");
            final int size = array.length();
            String[] urls = new String[size];
            String[] publishAts = new String[size];

            for (int i = 0; i < size; i++) {
                jsonObject = array.getJSONObject(i);
                urls[i] = jsonObject.getString("url");
                publishAts[i] = jsonObject.getString("publishedAt");
            }
            return helper.saveImages(urls, publishAts);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void sendResult(boolean isSuccess) {
        Log.i("tag","sendResult");
        Intent broadcast = new Intent(ACTION_FETCH);
        broadcast.putExtra(EXTRA_FETCHED_RESULT, isSuccess);
        localBroadcastManager.sendBroadcast(broadcast);
    }

    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
