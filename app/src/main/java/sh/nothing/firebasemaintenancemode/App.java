package sh.nothing.firebasemaintenancemode;

import android.app.Application;

import com.firebase.client.Firebase;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}

