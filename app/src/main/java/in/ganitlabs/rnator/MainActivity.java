package in.ganitlabs.rnator;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import in.ganitlabs.rnator.Helpers.DataBaseHelper;
import in.ganitlabs.rnator.Helpers.HTMLHelper;
import in.ganitlabs.rnator.Utils.TrackedCursor;
import in.ganitlabs.rnator.Utils.VolleySingleton;


public class MainActivity extends AppCompatActivity implements Config {

    private DrawerLayout dLayout;
    private NavigationView navView;
    public JsonArrayRequest jsonArrRequest;
    public ActionBar ab;
    JSONArray possibleDownloads;
    public HashMap<String,HTMLHelper> helperDirectory = new HashMap<>();
    public WebViewClient wvClient = new WebViewClient();

    private void setToolBar() {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setNavigationDrawer() {
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.navigation);
        navView.setFocusable(true);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment frag = null;
                int itemId = menuItem.getItemId();
                Class fragClass = ROUTES.get(itemId);
                if (fragClass != null) {
                    try {
                        frag = (Fragment) fragClass.newInstance();
                        if (HTML_MENU_FILE_MAPPING.containsKey(itemId)) {
                            Bundle tag = new Bundle();
                            tag.putString("fileName", HTML_MENU_FILE_MAPPING.get(itemId));
                            frag.setArguments(tag);
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                if (frag != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, frag);
                    transaction.commit();
                    dLayout.closeDrawers();
                    setTitle(menuItem.getTitle());
                    return true;
                }
                return false;
            }
        });
    }

    protected boolean isNavDrawerOpen() {
        return dLayout != null && dLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (dLayout != null) {
            dLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavigationDrawer();
        setToolBar();
        navView.getMenu().performIdentifierAction(R.id.main, 0);

        jsonArrRequest = new JsonArrayRequest(Request.Method.GET, HOST + Versions_API, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    possibleDownloads = new JSONArray();
                    for (int i=0; i < response.length(); i++) {
                        JSONObject update = (JSONObject) response.get(i);
                        String fileName = update.getString("name");
                        int localVersion;
                        if(fileName.endsWith(".db")) {
                            localVersion = DataBaseHelper.getInstance().getVersion();
                        }else if(fileName.endsWith(".html")){
                            localVersion = helperDirectory.get(fileName).getVersion();
                        }else {
                            // get versions for other file types if needed
                            localVersion = 0;
                        }
                        int updateVersion = update.getInt("version");

                        if (updateVersion > localVersion) possibleDownloads.put(update);
                    }
                    int updateCount = possibleDownloads.length();
                    if (updateCount > 0) {
                        ((TextView) navView.getMenu().findItem(R.id.updates).getActionView()).setText(String.valueOf(updateCount));
                        Fragment visibleFragment = getVisibleFragment();
                        if(visibleFragment == null || !visibleFragment.getClass().getSimpleName().equals("FragmentUpdates")) {
                            Drawable d = getResources().getDrawable(R.drawable.ic_menu_white_24dp, null);
                            if (d != null) {
                                d.setColorFilter(Color.parseColor("#ff0000"), PorterDuff.Mode.MULTIPLY);
                            }
                            ab.setHomeAsUpIndicator(d);
                            ab.setDisplayHomeAsUpEnabled(true);
                        }
                    }else{
                        ((TextView) navView.getMenu().findItem(R.id.updates).getActionView()).setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrRequest);

        final Context context = this;

        for (String s : HTML_FILES){
            try {
                helperDirectory.put(s, new HTMLHelper(context, s));
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            dLayout.openDrawer(GravityCompat.START);
            View view = this.getCurrentFocus();
            if (view != null) {
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        }else if(getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        TrackedCursor.closeAll();
        DataBaseHelper.getInstance().close();
        VolleySingleton.close();
        super.onDestroy();
    }
}
