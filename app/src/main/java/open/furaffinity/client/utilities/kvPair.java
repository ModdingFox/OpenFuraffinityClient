package open.furaffinity.client.utilities;

import android.widget.Spinner;

import androidx.annotation.NonNull;

import java.util.Objects;

public class kvPair {
    private final String key;
    private final String value;

    public kvPair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getSelectedValue(Spinner spinnerIn) {
        return ((kvPair) spinnerIn.getSelectedItem()).getKey();
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @NonNull
    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof kvPair) {
            kvPair c = (kvPair) obj;
            return Objects.equals(this.key, c.key) && Objects.equals(this.value, c.value);
        }

        return false;
    }
}
