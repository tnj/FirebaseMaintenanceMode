package sh.nothing.firebasemaintenancemode;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

public class MainActivity extends AppCompatActivity {
    private Snackbar bar;
    private MaintenanceMode maintenanceMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            }
        });

        maintenanceMode = MaintenanceMode.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        maintenanceMode.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        maintenanceMode.unregister(this);
    }

    @Produce
    public MaintenanceMode.Status getCurrentMaintenanceMode() {
        return maintenanceMode.current();
    }

    @Subscribe
    public void maintenanceModeChanged(MaintenanceMode.Status status) {
        if (!status.isActive()) {
            bar = Snackbar.make(findViewById(R.id.coordinatorLayout), status.getMessage(), Snackbar.LENGTH_INDEFINITE);
            bar.show();
        } else if (bar != null && bar.isShown()) {
            bar.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
