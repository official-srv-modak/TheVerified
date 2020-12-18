package com.example.theverified;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.theverified.ui.main.SectionsPagerAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialise
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);
        ImageView menu = findViewById(R.id.menu);
        DrawerLayout drawer = findViewById(R.id.drawerlayout);
        NavigationView nav = findViewById(R.id.nav_bar);
        nav.setNavigationItemSelectedListener(this);

        //Action

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LoadCard ld = new LoadCard();
                    ld.execute(new URL("http://192.168.0.3/test_json.php"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = findViewById(R.id.drawerlayout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case R.id.accountSettings:
                Intent accountSettingsIntent = new Intent(MainActivity.this, AccountSettings.class);
                startActivity(accountSettingsIntent);

                break;
            case R.id.appSettings:
                Intent appSettingsIntent = new Intent(MainActivity.this, AppSettings.class);
                startActivity(appSettingsIntent);
                break;
            case R.id.contactUs:

                break;
            default:
                break;
        }
        return true;
    }

    public static JSONObject getDataFromServer(String password, String URL)
    {
        String output = "";
        try{
            URL url = new URL(URL);
            Map params = new LinkedHashMap<>();
            params.put("password", password);
            StringBuilder postData = new StringBuilder();
            Set<Map.Entry> s = params.entrySet();
            for (Map.Entry param : s) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode((String) param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                {
                    output += inputLine;
                }
            }
            in.close();
            JSONObject jsonObj = new JSONObject(output);
            return jsonObj;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    public void renderCard(String password, String URL)
    {
        try{
            JSONObject jsonObj = getDataFromServer(password,URL);
            JSONArray cards = jsonObj.getJSONArray("cards");
            for (int i = 0; i < cards.length(); i++)
            {
                List<String> tempData = new ArrayList<String>();
                JSONObject c = cards.getJSONObject(i);
                CardView cd = new CardView(this);
                String heading = c.getString("heading");
                TextView tv = (TextView) findViewById(R.id.heading4_1_1);
                tv.setText(heading);
                ImageView imageView = (ImageView) findViewById(R.id.invest11);
                Glide.with(this).load(c.getString("image")).into(imageView);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private class LoadCard extends AsyncTask<URL, Void, Integer> {
        protected Integer doInBackground(URL... urls) {
            new Thread() {
                public void run() {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            renderCard("souM#@583", urls[0].toString());
                        }

                    });
                }
            }.start();
            return 0;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {

        }

    }
}