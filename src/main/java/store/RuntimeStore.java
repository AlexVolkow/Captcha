package store;

import org.eclipse.jetty.util.ConcurrentHashSet;

import java.util.Set;
import java.util.UUID;

public class RuntimeStore implements Store {
    private Set<UUID> store = new ConcurrentHashSet<>();

    @Override
    public void addUser(UUID key) {
        store.add(key);
    }

    @Override
    public boolean contains(UUID key) {
        return store.contains(key);
    }

    @Override
    public void removeUser(UUID key) {
        store.remove(key);
    }
}
