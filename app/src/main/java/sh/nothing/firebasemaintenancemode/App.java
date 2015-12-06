package sh.nothing.firebasemaintenancemode;

import android.app.Application;

import com.firebase.client.Firebase;

public class App extends Application {
    private static final String FIREBASE_ROOT = "https://fiery-inferno-8489.firebaseio.com/status";

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        MaintenanceMode.init(FIREBASE_ROOT);
    }
}

