package com.pengin.poinsetia.konkatsudiary.View;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.pengin.poinsetia.konkatsudiary.R;

public class AddPersonDialogFragment extends DialogFragment{

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.flower_dialog_layout, null);
        Intent result = new Intent();
        // 年齢選択用プルダウンリスト
        Spinner selectedAge = (Spinner) view.findViewById(R.id.person_age_spinner);
        selectedAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String age = parent.getItemAtPosition(position).toString();
                    result.putExtra("age", Integer.parseInt(age));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do Nothing
            }
        });
        builder.setView(view);
        builder.setNegativeButton("閉じる", (dialog, id) -> {

            TextView nameEditText = (TextView)view.findViewById(R.id.person_name_text);
            String nameText = nameEditText.getText().toString();
            result.putExtra("name",nameText);
            if (getTargetFragment() != null) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, result);
            }
        });
        return builder.create();
    }

}
