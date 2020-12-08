package open.furaffinity.client.utilities;

public class notificationItem {
    private final String name;
    private final int rowId;
    private boolean state;

    public notificationItem(String name, boolean state, int rowId) {
        this.name = name;
        this.state = state;
        this.rowId = rowId;
    }

    public String getName() {
        return name;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getRowId() {
        return rowId;
    }
}