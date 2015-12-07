package sh.nothing.firebasemaintenancemode;

import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MaintenanceModeTest  {

    private Firebase firebase;

    @Before
    public void setUp() {
        firebase = mock(Firebase.class);
        when(firebase.addValueEventListener(any(ValueEventListener.class))).thenReturn(null);
        doNothing().when(firebase).removeEventListener(any(ValueEventListener.class));
    }

    @After
    public void tearDown() {
        MaintenanceMode.shutdown();
    }

    @Test
    public void testNoInit() {
        try {
            MaintenanceMode.getInstance();
            fail("should throw exception");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testGetInstance() {
        MaintenanceMode.init(firebase);
        // duplicate init call: this should be silently ignored
        MaintenanceMode.init("https://localhost");
        assertNotNull(MaintenanceMode.getInstance());
    }

    @Test
    public void testCounter() {
        MaintenanceMode.init(firebase);
        MaintenanceMode instance = MaintenanceMode.getInstance();
        assertEquals(firebase, instance.statusRef);

        instance.register(this);
        assertEquals(1, instance.getListenerCount());
        verify(firebase).addValueEventListener(any(ValueEventListener.class));

        instance.unregister(this);
        assertEquals(0, instance.getListenerCount());
        verify(firebase).removeEventListener(any(ValueEventListener.class));
    }
}