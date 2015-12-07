package sh.nothing.firebasemaintenancemode;

import android.support.annotation.VisibleForTesting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class MaintenanceMode {
    private static MaintenanceMode instance;

    private Status lastStatus;
    private final Bus bus;
    private int listenerCount;
    final Firebase statusRef;

    private ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            lastStatus = dataSnapshot.getValue(Status.class);
            bus.post(lastStatus);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            // nothing to do
        }
    };

    public static void init(String rootUrl) {
        if (instance == null)
            instance = new MaintenanceMode(rootUrl);
    }

    public static MaintenanceMode getInstance() {
        if (instance == null)
            throw new IllegalStateException("You have to call MaintenanceMode.init(String) first");
        return instance;
    }

    private MaintenanceMode(String rootUrl) {
        this(new Firebase(rootUrl));
    }

    private MaintenanceMode(Firebase firebase) {
        bus = new Bus(ThreadEnforcer.MAIN);
        statusRef = firebase;
        listenerCount = 0;
    }

    public void register(Object listener) {
        bus.register(listener);
        synchronized (bus) {
            if (listenerCount == 0)
                setOnline();
            listenerCount++;
        }
    }

    public void unregister(Object listener) {
        bus.unregister(listener);
        synchronized (bus) {
            listenerCount--;
            if (listenerCount == 0)
                setOffline();
        }
    }

    private void setOnline() {
        statusRef.addValueEventListener(listener);
    }

    private void setOffline() {
        statusRef.removeEventListener(listener);
    }

    public Status current() {
        return lastStatus;
    }

    @VisibleForTesting
    static void init(Firebase firebase) {
        if (instance == null)
            instance = new MaintenanceMode(firebase);
    }

    @VisibleForTesting
    int getListenerCount() {
        return listenerCount;
    }

    @VisibleForTesting
    static void shutdown() {
        if (instance != null) {
            instance.setOffline();
            instance = null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Status {
        private boolean active;
        private String message;

        public Status() {
        }

        public boolean isActive() {
            return active;
        }

        public String getMessage() {
            return message;
        }
    }
}
