package sh.nothing.firebasemaintenancemode;

import android.app.Application;

import com.firebase.client.Firebase;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        MaintenanceMode.init();
    }
}

