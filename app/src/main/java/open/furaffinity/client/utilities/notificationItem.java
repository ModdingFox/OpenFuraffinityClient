package open.furaffinity.client.utilities;

public class notificationItem {
    private String name;
    private boolean state;
    private int rowId;

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