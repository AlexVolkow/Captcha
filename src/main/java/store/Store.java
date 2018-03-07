package store;

import java.util.UUID;

public interface Store {
    void addUser(UUID key);

    boolean contains(UUID key);

    void removeUser(UUID key);
}
