package com.carlosribeiro.teachtrack.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MaskUtils {

    public static void applyDateMask(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating;
            private String oldText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString().replaceAll("[^\\d]", "");

                String formatted = "";
                if (!isUpdating) {
                    if (str.length() > 2) {
                        formatted += str.substring(0, 2) + "/";
                        if (str.length() > 4) {
                            formatted += str.substring(2, 4) + "/";
                            if (str.length() > 8) {
                                formatted += str.substring(4, 8);
                            } else {
                                formatted += str.substring(4);
                            }
                        } else {
                            formatted += str.substring(2);
                        }
                    } else {
                        formatted = str;
                    }

                    isUpdating = true;
                    editText.setText(formatted);
                    editText.setSelection(formatted.length());
                    isUpdating = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // ✅ NOVO método para máscara de telefone: (99) 99999-9999
    public static void applyPhoneMask(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating;
            private String oldText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

                String str = s.toString().replaceAll("[^\\d]", "");
                StringBuilder formatted = new StringBuilder();

                int length = str.length();

                if (length > 0) {
                    formatted.append("(").append(str.substring(0, Math.min(2, length)));
                }
                if (length >= 3) {
                    formatted.append(") ").append(str.substring(2, Math.min(7, length)));
                }
                if (length >= 8) {
                    formatted.append("-").append(str.substring(7, Math.min(11, length)));
                }

                isUpdating = true;
                editText.setText(formatted.toString());
                editText.setSelection(editText.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
