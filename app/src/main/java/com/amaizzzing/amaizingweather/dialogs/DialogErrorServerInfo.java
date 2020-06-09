package com.amaizzzing.amaizingweather.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.amaizzzing.amaizingweather.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class DialogErrorServerInfo extends BottomSheetDialogFragment {
    public interface onUpdateServerInfo {
        void updateInfo();
    }
    private onUpdateServerInfo updListener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_error_server_info, container,
                false);

        setCancelable(false);

        initListeners(view);

        updListener = (onUpdateServerInfo) requireActivity();

        return view;


    }

    private void initListeners(View view) {
        view.findViewById(R.id.ok_DialogError).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        view.findViewById(R.id.upd_info_DialogError).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updListener.updateInfo();
                dismiss();
            }
        });
    }
    public static DialogErrorServerInfo newInstance() {
        return new DialogErrorServerInfo();
    }

}
