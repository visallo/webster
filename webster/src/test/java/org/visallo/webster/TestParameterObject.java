package org.visallo.webster;

public class TestParameterObject {
    private final String value;

    public TestParameterObject(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestParameterObject that = (TestParameterObject) o;

        return value.equals(that.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "TestParameterObject{" +
                "value='" + value + '\'' +
                '}';
    }
}
