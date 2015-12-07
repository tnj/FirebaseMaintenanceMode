package sh.nothing.firebasemaintenancemode;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MaintenanceModeTest  {

    @BeforeClass
    public static void setUp() {
        Firebase firebase = mock(Firebase.class);
        when(firebase.addChildEventListener(any(ChildEventListener.class))).thenReturn(null);
        doNothing().when(firebase).removeEventListener(any(ChildEventListener.class));

        MaintenanceMode.init(firebase);
    }

    @AfterClass
    public static void tearDown() {
        MaintenanceMode.shutdown();
    }

    @Test
    public void testGetInstance() {
        // duplicate init call: this should be silently ignored
        MaintenanceMode.init("https://localhost");
        assertNotNull(MaintenanceMode.getInstance());
    }

    @Test
    public void testCounter() {
        MaintenanceMode instance = MaintenanceMode.getInstance();

        instance.register(this);
        assertTrue(instance.isOnline());
        assertEquals(1, instance.getListenerCount());

        instance.unregister(this);
        assertFalse(instance.isOnline());
        assertEquals(0, instance.getListenerCount());
    }
}