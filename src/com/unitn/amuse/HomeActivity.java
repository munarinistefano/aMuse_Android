/************************************************************
* Project		: aMuse
* Team			: dVruhero
* Page			: HomeActivity.java
* Description	: Android application activity (home page)
* Released		: April 25th, 2013 (Second Deliverable)
************************************************************/

package com.unitn.amuse;

/**
 * Android application activity (home page).
 * @author dVruhero team
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class HomeActivity extends Activity {
	Button scanButton;
	Button mybooksButton;
	
	ArrayList<Work> worksList=new ArrayList<Work>();

	/**
	 * Metodo richiamato non appena l'attivit�� viene creata. Setta le
	 * impostazioni di base dell'activity, quali il layout di riferimento e le
	 * ottimizzazioni, come la presenza o meno di una barra di titolo.
	 * @param savedInstanceState Serve per riportare un eventuale stato
	 * dell���attivit�� salvato in precedenza da un'altra istanza che �� stata
	 * terminata. L'argomento �� null nel caso in cui l���attivit�� non abbia uno
	 * stato salvato.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// La prassi richiede che, come prima riga di un metodo predefinito
		// (come questo), si richiami l'implementazione di base del metodo che
		// si sta ridefinendo, con la sintassi super."metodo_da_ridefinire"
		super.onCreate(savedInstanceState);
		
		// Nascondo la barra del titolo dall'activity corrente
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// Setto il layout definito in res/layout/activity_home.xml
		// NB: R �� una classe che fa da ponte tra il codice JAVA e le risorse
		// definite nei file XML, mappandole. E' una classe di appoggio
		setContentView(R.layout.activity_home);
		
		/*
		//Check if the phone is WIFI connected
				ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				//Check if 3G is connected
				boolean is3g = manager.getNetworkInfo(
				            ConnectivityManager.TYPE_MOBILE)
				                        .isConnected();
				//Check if WIFI is connected
				boolean isWifi = manager.getNetworkInfo(
				                        ConnectivityManager.TYPE_WIFI)
				                        .isConnected();

				                Log.v("",is3g + " ConnectivityManager Test "
				                        + isWifi);
				                if (!isWifi) {

				                }
			*/	                
		
		// Associo un ascoltatore ai bottoni precedentemente dichiarati
		addListenerOnScanButton();
		addListenerOnMyBooksButton();
		/*
		if (!isWifi){
			//onBackPressed();
			scanButton.setEnabled(false);
        	mybooksButton.setEnabled(false);
        	
    		new AlertDialog.Builder(this)
    	    .setMessage("In order to use aMuse app, turn on wifi and connect to Muse network.")
    	    .setTitle("Make sure your WIFI connection is ON")
    	    .show();
		}*/
		
	}
	
	/**
	 * Metodo che associa un evento al bottone "scanButton".
	 * @see scanButton
	 */
	public void addListenerOnScanButton() {
		// Per prassi bisogna definire che questo �� il contesto
		final Context context = this;
 
		// Definisco il bottone e gli associo un ID univoco
		scanButton = (Button) findViewById(R.id.button1);
		
		// Definisco l'evento al click del mouse mediante classe interna
		scanButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// l'evento in questione �� il collegamento con la pagina scan
			    Intent intent = new Intent(context, ScanActivity.class);
			    intent.putParcelableArrayListExtra("worksList", worksList);
			    startActivity(intent);   
			}
		});
	}

	/**
	 * Metodo che associa un evento al bottone "mybooksButton".
	 * @see mybooksButton
	 */
	public void addListenerOnMyBooksButton() {
		// Per prassi bisogna definire che questo �� il contesto
		final Context context = this;
 
		// Definisco il bottone e gli associo un ID univoco
		mybooksButton = (Button) findViewById(R.id.button2);
 
		// Definisco l'evento al click del mouse mediante classe interna
		mybooksButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                myWebLink.setData(Uri.parse("http://www.muse.it"));
                startActivity(myWebLink);
			}
		});
	}

	/**
	 * Metodo che permette la creazione di quel menu (chiamato OptionsMenu) che
	 * appare quando, in un'applicazione, viene premuto il tasto Menu sullo
	 * smartphone (IMPLEMENTAZIONE FACOLTATIVA).
	 * @param menu Riferimento di tipo menu al menu dell'applicazione.
	 * @return TRUE/FALSE, rispettivamente per visualizzare o meno il menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Riempie il menu, aggiungendo elementi all'action bar, se presente
		getMenuInflater().inflate(R.menu.home, menu);
		
		// ritorno TRUE per permettere la visualizzazione del menu
		return true;
	}
}