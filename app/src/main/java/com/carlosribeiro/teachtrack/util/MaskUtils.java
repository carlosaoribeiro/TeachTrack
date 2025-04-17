package com.carlosribeiro.teachtrack.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MaskUtils {

    public static void applyDateMask(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private final String ddmmyyyy = "DDMMYYYY";
            private boolean isUpdating;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) return;
                isUpdating = true;

                String clean = s.toString().replaceAll("[^\\d]", "");

                StringBuilder result = new StringBuilder();
                int i = 0;
                for (char m : ddmmyyyy.toCharArray()) {
                    if (i >= clean.length()) break;
                    if (m == 'D' || m == 'M' || m == 'Y') {
                        result.append(clean.charAt(i));
                        i++;
                    }
                    if (i == 2 || i == 4) result.append('/');
                }

                current = result.toString();
                editText.setText(current);
                editText.setSelection(current.length());
                isUpdating = false;
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }
}
