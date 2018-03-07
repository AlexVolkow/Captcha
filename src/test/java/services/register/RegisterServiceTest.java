package services.register;

import org.junit.Before;
import org.junit.Test;
import store.RuntimeStore;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegisterServiceTest {
    RegisterService registerService;

    @Before
    public void prepare() {
        registerService = new RegisterService(new RuntimeStore());
    }

    @Test
    public void testNormalWay() {
        User user = registerService.registryUser();

        assertTrue(registerService.isRegistered(user.getPublicKey()));
    }

    @Test
    public void noUser() {
        assertFalse(registerService.isRegistered(UUID.randomUUID()));
    }

    @Test
    public void stressTest() {
        Set<UUID> ids = new HashSet<>();
        for (int i = 0; i < 10_000; i++) {
            ids.add(registerService.registryUser().getPublicKey());
        }

        for (UUID user : ids) {
            assertTrue(registerService.isRegistered(user));
        }
    }
}