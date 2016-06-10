package robotsmom.growow;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FarmsListFragment.OnFarmsListInteractionListener, FieldFragment.OnFieldViewInteractionListener {


    private static final String LOG_TAG = "MainActivity";
    private FieldFragment mFieldFragment;
    private FarmsListFragment mFarmsListFragment;
    private StatFragment statFragment = new StatFragment();
    private boolean mResizeStream = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // Crashlitycs lib
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFieldFragment.switchGrid();
            }
        });

        FloatingActionButton trap = (FloatingActionButton) findViewById(R.id.trapezoid);
        trap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFieldFragment.swapStreams();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // default menu item
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
//        onNavigationItemSelected(navigationView.getMenu().getItem(1));
        Log.d(LOG_TAG, "onCreate finish");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "onOptionsItemSelected");
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
        Log.d(LOG_TAG, "onNavigationItemSelected");
        int id = item.getItemId();

        if (id == R.id.nav_statistics)
        {
            if( mFarmsListFragment == null )
            {
                mFarmsListFragment = new FarmsListFragment();
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.mainFrame, statFragment);
            ft.replace(R.id.mainFrame, mFarmsListFragment);
            ft.addToBackStack("Farms list");
            ft.commit();

        }
        else if (id == R.id.nav_camera)
        {
            navigateToFieldView(0, 0);
        }
        else if (id == R.id.nav_manage) {

        }
        else if (id == R.id.nav_share) {

        }
        else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void navigateToFieldView(int farmID, int fieldID)
    {
        if(mFieldFragment == null) {
            mFieldFragment = new FieldFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putInt(FieldFragment.SELECTED_FARM_ID, farmID);
        bundle.putInt(FieldFragment.SELECTED_FIELD_ID, fieldID);
        mFieldFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, mFieldFragment);
        ft.addToBackStack("Field view");
        ft.commit();
    }

    /**
     * @param groupPosition
     * @param childPosition
     */
    @Override
    public void OnFarmsListInteraction(int groupPosition, int childPosition)
    {
        navigateToFieldView(groupPosition, childPosition);
    }

    @Override
    public void OnFieldViewInteraction() {
        // nothing yet
    }
}
