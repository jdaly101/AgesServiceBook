package com.agesinitiatives.servicebook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.agesinitiatives.servicebook.entities.AgesDate;
import com.agesinitiatives.servicebook.parsers.ServicesIndexParser;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String SERVICE_LIST_URL = "https://dcs.goarch.org/goa/dcs/servicesindex.json";
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 456;
    private static int prev = -1;
    private FirebaseAuth mAuth;
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

        mAuth = FirebaseAuth.getInstance();

        if (!PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PreferenceManager.KEY_HAS_SET_DEFAULT_VALUES, false)) {
            PreferenceManager.setDefaultValues(this, R.xml.prefs_all, true);
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
                        ServicesIndexParser sip = new ServicesIndexParser(response);
                        serviceDates = sip.parse();

                        serviceListAdapter = new ServiceListAdapter(
                                context,
                                sip.getDatesList(),
                                sip.getServicesHashMap()
                        );
                        expandableListView.setAdapter(serviceListAdapter);
                        scrollToToday(sip.getClosestDateIndex());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e(TAG, "Error receiving JSON response");
                    }
                }
        );

        queue.add(jsonObjectRequest);

        serviceListAdapter = new ServiceListAdapter(context, null, null);
        expandableListView = findViewById(R.id.expandableServiceList);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
                if ((prev != -1) && (prev != i)) {
                    expandableListView.collapseGroup(prev);
                }
                prev = i;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(context, ServiceView.class);
                Bundle extras = new Bundle();
                extras.putString("SERVICE_URL", serviceDates.get(groupPosition).services.get(childPosition).serviceUrl);
                extras.putString("SERVICE_TITLE", serviceDates.get(groupPosition).services.get(childPosition).getTitle());
                intent.putExtras(extras);
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
        final Context context = getApplicationContext();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(context, SettingsActivity.class);
            intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.AllPrefsFragment.class.getName());
            intent.putExtra(SettingsActivity.EXTRA_NO_HEADERS, true);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        final Context context = getApplicationContext();

        if (id == R.id.nav_about) {
            Intent intent = new Intent(context, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_contact) {
            Intent intent = new Intent(context, ContactActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_privacy) {
            Intent intent = new Intent(context, PrivacyActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

        }
    }

    public void scrollToToday(int i) {
        long packedPosition = expandableListView.getPackedPositionForChild(i, 1);
        int flatPosition = expandableListView.getFlatListPosition(packedPosition);
        expandableListView.setSelection(flatPosition);
    }
}
