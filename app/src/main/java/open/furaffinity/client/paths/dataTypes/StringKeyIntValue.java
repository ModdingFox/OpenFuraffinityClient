package open.furaffinity.client.paths.dataTypes;

public final class StringKeyIntValue {
    private final String key;
    private final int value;

    public StringKeyIntValue(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public int getValue() {
        return this.value;
    }
}
