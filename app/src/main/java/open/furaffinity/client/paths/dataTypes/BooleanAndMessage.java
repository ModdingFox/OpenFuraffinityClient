package open.furaffinity.client.paths.dataTypes;

public final class BooleanAndMessage {
    private final String message;
    private final boolean value;

    public BooleanAndMessage(String message, boolean value) {
        this.message = message;
        this.value = value;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean getValue() {
        return this.value;
    }
}
