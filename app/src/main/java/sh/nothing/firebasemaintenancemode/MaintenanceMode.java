package sh.nothing.firebasemaintenancemode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class MaintenanceMode {
    private static Status lastStatus;
    private static Bus bus;

    private static Integer count;
    private static Firebase statusRef;
    private static ValueEventListener listener = new ValueEventListener() {
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
        if (bus == null) {
            bus = new Bus(ThreadEnforcer.MAIN);
            statusRef = new Firebase(rootUrl);
            count = 0;
        }
    }

    public static void register(Object listener) {
        bus.register(listener);
        synchronized (count) {
            if (listener == 0)
                setOnline();
            count++;
        }
    }

    public static void unregister(Object listener) {
        bus.unregister(listener);
        synchronized (count) {
            count--;
            if (listener == 0)
                setOffline();
        }
    }

    private static void setOnline() {
        statusRef.addValueEventListener(listener);
    }

    private static void setOffline() {
        statusRef.removeEventListener(listener);
    }

    public static Status current() {
        return lastStatus;
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
