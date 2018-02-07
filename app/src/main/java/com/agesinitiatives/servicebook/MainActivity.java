package com.agesinitiatives.servicebook;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.agesinitiatives.servicebook.entities.AgesDate;
import com.agesinitiatives.servicebook.parsers.ServicesIndexParser;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String SERVICE_LIST_URL = "http://www.agesinitiatives.com/dcs/public/dcs/servicesindex.json";
    private static final String TAG = "MainActivity";
    List<AgesDate> serviceDates;
    ExpandableListView expandableListView;
    ServiceListAdapter serviceListAdapter;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        expandableListView = findViewById(R.id.expandableServiceList);
        System.out.println("foo");

        if (!PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PreferenceManager.KEY_HAS_SET_DEFAULT_VALUES, false)) {
            PreferenceManager.setDefaultValues(this, R.xml.pref_display, true);
            PreferenceManager.setDefaultValues(this, R.xml.pref_language, true);
        }



        queue = Volley.newRequestQueue(this);

        final Context context = getApplicationContext();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                SERVICE_LIST_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "Received JSON response");
                        ServicesIndexParser sip = new ServicesIndexParser(response);
                        serviceDates = sip.parse();

                        serviceListAdapter = new ServiceListAdapter(
                                context,
                                sip.getDatesList(),
                                sip.getServicesHashMap()
                        );
                        expandableListView.setAdapter(serviceListAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error receiving JSON response");
                    }
                }
        );

        queue.add(jsonObjectRequest);

        serviceListAdapter = new ServiceListAdapter(context, null, null);
        expandableListView = findViewById(R.id.expandableServiceList);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.d(TAG, "Child click. Group: " + groupPosition + ", child: " + childPosition);
                Log.d(TAG, "Service clicked: " + serviceDates.get(groupPosition).services.get(childPosition).serviceType);
                Log.d(TAG, "Service clicked: " + serviceDates.get(groupPosition).services.get(childPosition).serviceUrl);

                Intent intent = new Intent(context, ServiceView.class);
                intent.putExtra(
                        "SERVICE_URL", serviceDates.get(groupPosition).services.get(childPosition).serviceUrl
                );
                startActivity(intent);

                return false;
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            final Context context = getApplicationContext();
            Intent intent = new Intent(context, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            final Context context = getApplicationContext();
            Intent intent = new Intent(context, SettingsActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
