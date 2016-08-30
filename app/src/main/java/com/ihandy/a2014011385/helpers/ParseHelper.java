package com.ihandy.a2014011385.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ihandy.a2014011385.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by huangruihao on 16-8-30.
 */
public class ParseHelper {
    Context context;

    private final String PARSE_HELPER_TAG = "ParseHelper";

    public ParseHelper(Context context) {
        this.context = context;
    }

    public HashMap<String, String> generateCategoriesHashMap(String categoriesJSON) {
        HashMap<String, String> categories = new HashMap<>();
        try {
            JSONObject responseObject = new JSONObject(categoriesJSON);
            int meta = responseObject.getInt("meta");
            if (meta == 200) { // OK
                JSONObject dataObject = responseObject.getJSONObject("data");
                JSONObject categoriesObject = dataObject.getJSONObject("categories");
                Iterator<String> categoriesIterator = categoriesObject.keys();
                while (categoriesIterator.hasNext()) {
                    String key = categoriesIterator.next();
                    categories.put(key, categoriesObject.getString(key));
                }
            } else {
                // something wrong with the server
                Toast.makeText(context, context.getString(R.string.server_error), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Log.e(PARSE_HELPER_TAG, e.getMessage());
        }
        return categories;
    }
}
