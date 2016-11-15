/*
 *      Copyright (C) 2012-2016 DataStax Inc.
 *
 *      This software can be used solely with DataStax Enterprise. Please consult the license at
 *      http://www.datastax.com/terms/datastax-dse-driver-license-terms
 */
package com.datastax.driver.dse.mapper;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.dse.CCMDseTestsSupport;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.google.common.base.Objects;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.testng.Assert.assertEquals;

public class MapperTest extends CCMDseTestsSupport {

    @Override
    public void onTestContextInitialized() {
        execute("CREATE TABLE users (user_id uuid PRIMARY KEY, name text)");
    }

    @SuppressWarnings("unused")
    @Table(name = "users")
    public static class User {

        // Dummy constant to test that static fields are properly ignored
        public static final int FOO = 1;

        @PartitionKey
        @Column(name = "user_id")
        private UUID userId;

        private String name;

        public User() {
        }

        public User(String name) {
            this.userId = UUIDs.random();
            this.name = name;
        }

        public UUID getUserId() {
            return userId;
        }

        public void setUserId(UUID userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object other) {
            if (other == null || other.getClass() != this.getClass())
                return false;

            User that = (User) other;
            return Objects.equal(userId, that.userId)
                    && Objects.equal(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(userId, name);
        }
    }


    /**
     * Sanity check to ensure that a {@link MappingManager} instance can be created from a
     * {@link com.datastax.driver.dse.DseSession}.
     *
     * @jira_ticket JAVA-1330
     * @test_category object_mapper
     */
    @Test(groups = "short")
    public void should_be_able_to_use_mapper_with_dsecluster() {
        MappingManager manager = new MappingManager(session());
        Mapper<User> m = manager.mapper(User.class);

        // Create and retrieve user and ensure contents are the same.
        User u1 = new User("DseUser");
        m.save(u1);
        assertEquals(m.get(u1.getUserId()), u1);
    }
}
