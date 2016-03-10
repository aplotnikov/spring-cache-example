package org.home.spring.cache.tasks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractUserDaoTest {
    private static final String USER_NAME = "Bob";

    @Inject
    private UserDao userDao;
    private User user;

    @Before
    public void setUp() throws Exception {
        user = new User(USER_NAME);
    }

    @Test
    @DirtiesContext
    public void shouldCachingAnnotationImprovePerformanceForFindMethod() throws InterruptedException {
        userDao.saveWithoutCache(user);

        Duration firstFindMethodExecutionTime = detectFindMethodExecutionTime(USER_NAME);
        Duration secondFindMethodExecutionTime = detectFindMethodExecutionTime(USER_NAME);

        assertThat(firstFindMethodExecutionTime).isGreaterThan(secondFindMethodExecutionTime);
    }

    private Duration detectFindMethodExecutionTime(String userName) throws InterruptedException {
        Instant start = Instant.now();

        userDao.findByName(userName);

        Instant end = Instant.now();

        return Duration.between(start, end);
    }

    @Test
    @DirtiesContext
    public void shouldCachePutAnnotationImprovePerformanceForFindMethod() throws InterruptedException {
        userDao.saveWithCache(user);

        Duration firstFindMethodExecutionTime = detectFindMethodExecutionTime(USER_NAME);
        Duration secondFindMethodExecutionTime = detectFindMethodExecutionTime(USER_NAME);

        assertThat(firstFindMethodExecutionTime).isEqualByComparingTo(secondFindMethodExecutionTime);
    }

    @Test
    public void shouldCacheEvictAnnotationImprovePerformanceForFindMethod() throws InterruptedException {
        String secondUserName = "John";
        User secondUser = new User(secondUserName);

        userDao.saveWithCache(user);
        userDao.saveWithCache(secondUser);

        userDao.removeWithoutCache(user);
        userDao.removeWithCache(secondUser);

        Duration findMethodExecutionTimeWhenCacheIsNotCleaned = detectFindMethodExecutionTime(USER_NAME);
        Duration findMethodExecutionTimeWhenCacheIsCleaned = detectFindMethodExecutionTime(secondUserName);

        assertThat(findMethodExecutionTimeWhenCacheIsCleaned).isGreaterThan(findMethodExecutionTimeWhenCacheIsNotCleaned);
    }
}
