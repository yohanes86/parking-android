/*
 * Copyright (C) 2013 47 Degrees, LLC
 *  http://47deg.com
 *  hello@47deg.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.parking.swipelistview.sample.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.CheckBox;

import com.parking.R;
import com.parking.swipelistview.sample.utils.PreferencesManager;

public class ExpiredPaymentInfoDialog extends DialogFragment {

	String message;
	Context ctx;
    /**
     * Constructor
     *
     */
    public ExpiredPaymentInfoDialog(String msg,Context context) {
    	this.message = msg;
    	this.ctx = context;
    }

    /**
     * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final CheckBox checkBox = new CheckBox(getActivity());
        checkBox.setText(R.string.dontShow);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            checkBox.setTextColor(Color.WHITE);
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.info_payment)
                .setMessage(message)
//                .setView(checkBox)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {                        
                    	dismiss();
                    }
                })
//                .setNegativeButton(R.string.visit47, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String url = "http://47deg.com";
//                        Intent i = new Intent(Intent.ACTION_VIEW);
//                        i.setData(Uri.parse(url));
//                        startActivity(i);
//                    }
//                })
//                .setNeutralButton(R.string.goToGitHub, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String url = "https://github.com/47deg/android-swipelistview";
//                        Intent i = new Intent(Intent.ACTION_VIEW);
//                        i.setData(Uri.parse(url));
//                        startActivity(i);
//                    }
//                })
                .create();

    }
}