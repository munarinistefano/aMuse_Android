package com.unitn.amuse;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DP extends DialogFragment{

	public int id;
	public DP newInstance(int id) {
		id=this.id;
        DP frag = new DP();
        return frag;
    }
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater(); 
		
		String a = "o"+id;
		String pn = "com.unitn.amuse";
		int resID = getResources().getIdentifier(a , "drawable", pn);
		
		View mView = View.inflate(getActivity(), R.layout.dialog_single_work, null);
		ImageView mImage = (ImageView) mView.findViewById(R.id.textView1);
		// use this ImageView to set your image
		builder.setView(mView)
			   .setMessage("Message to display")
               .setPositiveButton("POS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	Context context = null;
				Intent intent = new Intent(context, EmailActivity.class);
			    startActivity(intent);
            }
        })
        .setNegativeButton("NEG", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // ON CLICK NEGATIVE
            }
        });
	    // Create the AlertDialog object and return it
	    return builder.create();
}
}