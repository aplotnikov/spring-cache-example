package org.home.spring.cache.tasks;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDao {
    private final List<User> users;

    public UserDao() {
        this.users = new ArrayList<>();
    }

    @CachePut(cacheNames = "users", key = "#user.name")
    public void saveWithCache(@Nonnull User user) {
        saveWithoutCache(user);
    }

    public void saveWithoutCache(@Nonnull User user) {
        users.add(user);
    }

    @Cacheable("users")
    @Nonnull
    public Optional<User> findByName(@Nonnull final String name) throws InterruptedException {
        Thread.sleep(3_000);

        return users.stream()
                    .filter(user -> name.equals(user.name))
                    .findFirst();
    }

    @CacheEvict(cacheNames = "users", key = "#user.name")
    public void removeWithCache(@Nonnull User user) {
        removeWithoutCache(user);
    }

    public void removeWithoutCache(@Nonnull User user) {
        users.remove(user);
    }
}
