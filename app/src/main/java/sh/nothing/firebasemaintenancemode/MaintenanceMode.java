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

    public static void init(String rootUrl) {
        bus = new Bus(ThreadEnforcer.MAIN);
        setupFirebase(rootUrl);
    }

    private static void setupFirebase(String root) {
        Firebase statusRef = new Firebase(root);
        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lastStatus = dataSnapshot.getValue(Status.class);
                bus().post(lastStatus);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // nothing to do
            }
        });
    }

    public static Status current() {
        return lastStatus;
    }

    public static Bus bus() {
        return bus;
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
