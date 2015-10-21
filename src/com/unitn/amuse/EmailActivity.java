/************************************************************
* Project		: aMuse
* Team			: dVruhero
* Page			: HomeActivity.java
* Description	: Android application activity (home page)
* Released		: April 25th, 2013 (Second Deliverable)
************************************************************/

package com.unitn.amuse;

/**
 * Android application activity (email page).
 * @author dVruhero team
 */

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class EmailActivity extends Activity {
	Button sendButton;
	
	ArrayList<Work> worksList = new ArrayList<Work>();

	// Parametri per il caricamento su database dei dati
	String email;
	String password;
	
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();

	// Link per l'interazione col database
	private static String url_create_product = "http://192.168.43.200/amuse_connect/insert_user.php";
	private static String url_insert_book = "http://192.168.43.200/amuse_connect/insert_book.php";
	private static String url_insert_works_book = "http://192.168.43.200/amuse_connect/insert_works_book.php";
	private static String url_get_latest_book = "http://192.168.43.200/amuse_connect/get_latest_book.php";
	private static String url_get_user_password = "http://192.168.43.200/amuse_connect/get_user_password.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "products";
	private static final String TAG_PRODUCT = "product";
	private static final String TAG_PASS = "pass";
	private static final String TAG_ID_BOOK = "ID_book";
	private static final String TAG_PASSWORD = "password";

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
		setContentView(R.layout.activity_email);
		
		// Associo un ascoltatore al bottone precedentemente dichiarato
		addListenerOnSendButton();
		
		worksList = getIntent().getParcelableArrayListExtra("worksList");
	}
	
	/**
	 * Metodo che associa un evento al bottone "sendButton".
	 * @see sendButton
	 */
	public void addListenerOnSendButton() {
		// Per prassi bisogna definire che questo �� il contesto
		final Context context = this;
 
		// Definisco il bottone e gli associo un ID univoco
		sendButton = (Button) findViewById(R.id.button1);
 
		// Definisco l'evento al click del mouse mediante classe interna
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String existingPassword = null;
				
				// DATA
				Calendar c = Calendar.getInstance();
		        final int giorno = c.get(Calendar.DAY_OF_MONTH);
		        int mese = c.get(Calendar.MONTH);
		        int anno = c.get(Calendar.YEAR);
		        String suff = null;
		        DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);
		        String mese_eng = (dfs.getMonths()[mese]);
		        
		        //SUFFISSI GIORNI IN INGLESE
		        if(giorno==1) {
		            suff = "st";
		        }
		        if(giorno==2) {
		            suff = "nd";
		        }
		        if(giorno==3) {
		            suff = "rd";
		        }
		        if(giorno==31) {
		            suff = "st";
		        }
		        else{
		            suff = "th";
		        }
		        
		        //Legge l'edittext
				EditText text = (EditText)findViewById(R.id.editText1);
				final String my_mail = text.getText().toString().toLowerCase();
				final String passw;

				//controllo psw già esistente
				int succ;
				
				try {
					List<NameValuePair> existPassword = new ArrayList<NameValuePair>();
					existPassword.add(new BasicNameValuePair("email", my_mail));
					Log.i("alandebug", "my_mail: " + my_mail);

					JSONObject jsonPassword = jsonParser.makeHttpRequest(
							url_get_user_password, "GET", existPassword);
					
					Log.d("alandebug", jsonPassword.toString());

					Log.i("alandebug", "prima if pass: " + existingPassword);
					
					// json success tag
					succ = jsonPassword.getInt(TAG_SUCCESS);
					
					Log.i("alandebug", "prima if, successo?: " + succ);

					if (succ == 1) {
						JSONArray passwordObj = jsonPassword
								.getJSONArray(TAG_PRODUCT); // JSON Array
						
						JSONObject pass = passwordObj.getJSONObject(0);
						existingPassword = pass.getString(TAG_PASSWORD);
						Log.i("alandebug", "Password esistente: " + existingPassword);
					} else {
						existingPassword = null;
						Log.i("alandebug", "Password inesistente: " + existingPassword);
					}
				} catch (JSONException e) {
					Log.i("alandebug", "successo = 0, entro nel catch :(");
					e.printStackTrace();
				}

				if ((existingPassword == null)||(existingPassword == "")) {
				passw = generatePsw(my_mail);
				} else {
					passw = existingPassword;
				}
				
				// Per l'aggiunta al db
				email = my_mail;
				password = passw;
				
				// Interagisco col database in background
				new CreateNewProduct().execute();
				
				//EMAIL BODY
				final String message = "\n" +
						"  <div style=\"margin-bottom:30xp; width: 100%; float:left\"><img src=\"http://www.ramponialan.it/host/img/logo.jpg\" align=\"right\"></img></div> "+
						"<html>\n" +
						"    <head>\n" +
						"        <meta http-equiv=\\\"Content-Type\\\" content=\\\"text/html; charset=UTF-8\\\">\n" +
						"        <title>JSP Page</title>\n" +
						"    </head>\n" +
						"    <body>\n" +
						"        <div style:\"margin-top: 93px; float:left\">\n" +
						"<br/><br/><br/><br/><br/><br/><br/><br/>Greetings!<br/>" +
						"We inform you that the book relative to your visit created on "+ giorno + suff +" of "+ mese_eng +", "+anno+" has been successfully registered. " +
						"You are now able to view it, modify it, share it using the following link:<br/>"+
						"\"http://localhost:8080/aMuseWebsite/index.jsp\"" +
						"<br/><br/><br/>Your login credentials are:" +
						"<div><h3>User Name: <span style=\"font-size:12px; font-style:none; font-weight: 100;\">"+ my_mail +"</span></h3></div>\n" +
						"<div><h3>Password: <span style=\"font-size:12px; font-style:none; font-weight: 100;\">"+ passw +"</span></h3></div>\n" +
						"<br/>" +
						"<br/>" +
						"Thank you for using our services!<br/></div>" +
						"<div style=\"float: right\"><br/>" +
						"The Staff<br/>" +
						"via Roberto Da SanSeverino<br/>" +
						"38122 Trento<br/><br/><div style=\"color:white;\">.</div>" +
						"</div>" +
						"    </body>\n" +
						"</html>\n" +
						"";
				
				final GMailSender sender = new GMailSender("amuse.dvruhero@gmail.com", "ciaogabri");
				
				new AsyncTask<Void, Void, Void>() {
					@Override
					public Void doInBackground(Void... arg) {
						try {
							Log.i("Ste debug", "prima di sender.sendMail");
							sender.sendMail("aMuse experience!",
									message,
									"amuse.dvruhero@gmail.com",
									my_mail);
						} catch (Exception e) {
							Log.e("SendMail", e.getMessage(), e);
						}
						
						return null;
					}
				}.execute();

				if (my_mail.matches("")) {
					Toast toast = Toast.makeText(context, "Please insert a valid email address!", Toast.LENGTH_LONG);
					toast.show();
				} else {
					Toast toast = Toast.makeText(context, "Email sent!", Toast.LENGTH_LONG);
					toast.show();
				}

				Intent intent = new Intent(context, HomeActivity.class);
				startActivity(intent);
			}
		});
	}
	
	//PASSWORD GENERATOR
	public static String generatePsw(String mail) { 
        String ALPHABET = "0123456789" + mail;
        Random rnd = new Random(System.currentTimeMillis());
        int LENGHT = 8;
        
        StringBuilder sb;
        sb = new StringBuilder(LENGHT);
        for (int i = 0; i < LENGHT; i++) {
            char tmp = ALPHABET.charAt(rnd.nextInt(ALPHABET.length()));
            if(tmp!='.' && tmp!='@'){sb.append(tmp);}
            else{sb.append('G');}
        }
        System.out.println(sb);
        return sb.toString();
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
	
	/**
	 * Background Async Task (interaction with DB)
	 * */
	class CreateNewProduct extends AsyncTask<String, String, String> {
		
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EmailActivity.this);
			pDialog.setMessage("Sending email...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * DB interaction
		 * */
		protected String doInBackground(String... args) {
			String IDLatestBook = null;
			
			int succ;
				try {
					List<NameValuePair> latestBook = new ArrayList<NameValuePair>();
			
					JSONObject jsonBook = jsonParser.makeHttpRequest(
							url_get_latest_book, "GET", latestBook);
			
			// json success tag
			succ = jsonBook.getInt(TAG_SUCCESS);
			
			if (succ == 1) {
				JSONArray bookObj = jsonBook
						.getJSONArray(TAG_PRODUCTS); // JSON Array
			
				JSONObject product = bookObj.getJSONObject(0);
			
				IDLatestBook = product.getString(TAG_ID_BOOK);
			} else {
				// error
			}
		} catch (JSONException e) {
			Log.i("alandebug", "successo = 0, entro nel catch :(");
			e.printStackTrace();
		}

		// Setto l'ID_book di un'unità maggiore all'ultimo presente
		String ID_book = String.valueOf(Integer.parseInt(IDLatestBook)+1);
		
		// BOOK default params
		String email_user = email;
		String title = "aMuse Experience";
		String description = "INSERT A DESCRIPTION";
		String cover = "cover_default.jpg";
		
		// WORKS_BOOK default params
		String IDwork = null;
		String IDbook = null;
		String url_img_user1 = null;
		String url_img_user2 = null;
		String comment = null;

		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("email", email));
			
		// Building Parameters
		List<NameValuePair> params2 = new ArrayList<NameValuePair>();
		params2.add(new BasicNameValuePair("ID_book", ID_book));
		params2.add(new BasicNameValuePair("email_user", email_user));
		params2.add(new BasicNameValuePair("title", title));
		params2.add(new BasicNameValuePair("description", description));
		params2.add(new BasicNameValuePair("cover", cover));
			
		List<NameValuePair> params3 = new ArrayList<NameValuePair>();

		JSONObject json = jsonParser.makeHttpRequest(url_create_product,
				"POST", params);
			
		JSONObject json2 = jsonParser.makeHttpRequest(url_insert_book,
				"POST", params2);
			
		for (int i=0; i<worksList.size(); i++) {
			IDwork = String.valueOf(worksList.get(i).idWork);
				
			// Setto l'ID_book di un'unità maggiore all'ultimo presente
			IDbook = String.valueOf(Integer.parseInt(IDLatestBook)+1);
			
			// WORKS default params
			url_img_user1 = "null";
			url_img_user2 = "null";
			comment = "null";
				
			params3.add(new BasicNameValuePair("ID_work", IDwork));
			params3.add(new BasicNameValuePair("ID_book", IDbook));
			params3.add(new BasicNameValuePair("url_img_user1", url_img_user1));
			params3.add(new BasicNameValuePair("url_img_user2", url_img_user2));
			params3.add(new BasicNameValuePair("comment", comment));
				
			JSONObject json3 = jsonParser.makeHttpRequest(url_insert_works_book,
					"POST", params3);
				
			// check for success tag
			try {
				int success3 = json3.getInt(TAG_SUCCESS);
					
				if (success3 == 1) {
					//Intent i = new Intent(getApplicationContext(), HomeActivity.class);
					//startActivity(i);
					//finish();
				} else {
					// failed
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			
		// check log cat fro response
		Log.d("Create Response", json.toString());

		// check for success tag
		try {
			int success = json.getInt(TAG_SUCCESS);
				
			if (success == 1) {
					//Intent i = new Intent(getApplicationContext(), HomeActivity.class);
					//startActivity(i);
					//finish();
			} else {
					// failed
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
			
		// check for success tag
		try {
			int success2 = json2.getInt(TAG_SUCCESS);
						
			if (success2 == 1) {
				//Intent i = new Intent(getApplicationContext(), HomeActivity.class);
				//startActivity(i);
				//finish();
			} else {
				// failed to create product
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
		}
	}
}