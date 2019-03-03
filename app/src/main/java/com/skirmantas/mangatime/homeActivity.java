package com.skirmantas.mangatime;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class homeActivity extends AppCompatActivity {

    private String TAG = homeActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get manga JSON
    private static String url = "https://www.mangaeden.com/api/list/0/";

    ArrayList<HashMap<String, String>> mangaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mangaList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);

        new GetMangas().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetMangas extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(homeActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray manga = jsonObj.getJSONArray("manga");

                    // looping through All manga
                    for (int x = 0; x < manga.length(); x++) {
                        JSONObject c = manga.getJSONObject(x);

                        String a = c.getString("a");
                        String i = c.getString("i");
                        String t = c.getString("t");


                        // tmp hash map for single manga
                        HashMap<String, String> mangas = new HashMap<>();

                        // adding each child node to HashMap key => value

                        mangas.put("a", a);
                        mangas.put("i", i);
                        mangas.put("t", t);


                        // adding manga to manga list
                        mangaList.add(mangas);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    homeActivity.this, mangaList,
                    R.layout.list_item, new String[]{"t", "i"}, new int[]{R.id.t, R.id.i});

            lv.setAdapter(adapter);
        }

    }
}
