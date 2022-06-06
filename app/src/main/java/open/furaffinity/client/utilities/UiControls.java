package open.furaffinity.client.utilities;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UiControls {
    private static int compareStrings(String string1, String string2) {
        if (string1.length() == 0 && string2.length() == 0) {
            return 0;
        }
        else if (string1.length() == 0) {
            return -1;
        }
        else if (string2.length() == 0) {
            return 1;
        }

        byte[] string1Lower = string1.toLowerCase().getBytes();
        byte[] string2Lower = string2.toLowerCase().getBytes();

        for (int i = 0; i < string1Lower.length && i < string2Lower.length; i++) {
            if (string1Lower[i] < string2Lower[i]) {
                return -1;
            }
            else if (string1Lower[i] > string2Lower[i]) {
                return 1;
            }
        }

        if (string1Lower.length < string2Lower.length) {
            return -1;
        }
        else if (string1Lower.length > string2Lower.length) {
            return 1;
        }

        return 0;
    }

    public static void spinnerSetAdapter(Context context, Spinner inputSpinner,
                                         HashMap<String, String> inputData, String currentValue,
                                         boolean sortKeys, boolean isNumeric) {
        if (inputData != null) {
            ArrayList<KvPair> spinnerData = new ArrayList<>();
            KvPair selectedItem = null;

            for (String key : inputData.keySet()) {
                KvPair newKvPair = new KvPair(key, inputData.get(key));
                if (key.equals(currentValue)) {
                    selectedItem = newKvPair;
                }
                spinnerData.add(newKvPair);
            }

            if (sortKeys) {
                if (isNumeric) {
                    spinnerData.sort((o1, o2) -> Integer.compare(Integer.parseInt(o1.getKey()),
                        Integer.parseInt(o2.getKey())));
                }
                else {
                    spinnerData.sort((o1, o2) -> compareStrings(o1.getKey(), o2.getKey()));
                }
            }

            ArrayAdapter<KvPair> spinnerAdapter =
                new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerData);
            inputSpinner.setAdapter(spinnerAdapter);
            if (selectedItem != null) {
                inputSpinner.setSelection(spinnerAdapter.getPosition(selectedItem));
            }
        }
    }

    public static void setSpinnerText(Context context, Spinner inputSpinner, String inputData) {
        List<String> spinnerData = new ArrayList<>();
        spinnerData.add(inputData);
        ArrayAdapter<String> spinnerAdapter =
            new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerData);
        inputSpinner.setAdapter(spinnerAdapter);
        inputSpinner.setSelection(0);
    }
}
