package open.furaffinity.client.utilities;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;

public class uiControls {
    private static int compareStrings(String string1, String string2) {
        if (string1.length() == 0 && string2.length() == 0) {
            return 0;
        } else if (string1.length() == 0) {
            return -1;
        } else if (string2.length() == 0) {
            return 1;
        }

        byte[] string1Lower = string1.toLowerCase().getBytes();
        byte[] string2Lower = string2.toLowerCase().getBytes();

        for (int i = 0; i < string1Lower.length && i < string2Lower.length; i++) {
            if (string1Lower[i] < string2Lower[i]) {
                return -1;
            } else if (string1Lower[i] > string2Lower[i]) {
                return 1;
            }
        }

        if (string1Lower.length < string2Lower.length) {
            return -1;
        } else if (string1Lower.length > string2Lower.length) {
            return 1;
        }

        return 0;
    }

    public static void spinnerSetAdapter(Context context, Spinner inputSpinner, HashMap<String, String> inputData, String currentValue, boolean sortKeys, boolean isNumeric) {
        if (inputData != null) {
            ArrayList<kvPair> spinnerData = new ArrayList<>();
            kvPair selectedItem = null;

            for (String key : inputData.keySet()) {
                kvPair newKVPair = new kvPair(key, inputData.get(key));
                if (key.equals(currentValue)) {
                    selectedItem = newKVPair;
                }
                spinnerData.add(newKVPair);
            }

            if (sortKeys) {
                if (isNumeric) {
                    spinnerData.sort((o1, o2) -> Integer.compare(Integer.parseInt(o1.getKey()), Integer.parseInt(o2.getKey())));
                } else {
                    spinnerData.sort((o1, o2) -> compareStrings(o1.getKey(), o2.getKey()));
                }
            }

            ArrayAdapter<kvPair> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerData);
            inputSpinner.setAdapter(spinnerAdapter);
            if (selectedItem != null) {
                inputSpinner.setSelection(spinnerAdapter.getPosition(selectedItem));
            }
        }
    }
}
