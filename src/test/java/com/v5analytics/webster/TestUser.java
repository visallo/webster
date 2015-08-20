package com.v5analytics.webster;

public class TestUser {
    private final String userId;

    public TestUser(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestUser testUser = (TestUser) o;

        return userId.equals(testUser.userId);

    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }
}
