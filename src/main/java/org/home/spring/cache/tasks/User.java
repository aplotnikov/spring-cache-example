package org.home.spring.cache.tasks;

import javax.annotation.Nonnull;

public class User {
    public final String name;

    public User(@Nonnull String name) {
        this.name = name;
    }
}
