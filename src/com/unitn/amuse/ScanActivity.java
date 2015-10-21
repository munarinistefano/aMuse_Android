/************************************************************
* Project		: aMuse
* Team			: dVruhero
* Page			: ScanActivity.java
* Description	: Android application activity (scan page)
* Released		: May 23th, 2013 (Third and Last Deliverable)
************************************************************/

package com.unitn.amuse;

/**
 * Android application activity (scan page).
 * @author Ramponi Alan
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.unitn.amuse.CameraPreview;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SyncStateContract.Constants;
import android.util.Log;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;

import android.widget.TextView;
import android.graphics.ImageFormat;
import android.graphics.drawable.Drawable;

/* Import ZBar Class files */
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import net.sourceforge.zbar.Config;

public class ScanActivity extends Activity {

	ArrayList<Work> worksList = new ArrayList<Work>();
	
	Context context;
	
	boolean mustScan = true;
	int id;
	String pid;
	
	String desc;
	String title;
	String auth;
	String exh;
	
	TextView description_eng;
	TextView title_work;
	TextView author;

	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// Link per l'interazione col database
	private static final String url_product_detials = "http://192.168.43.200/amuse_connect/get_work.php";
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCT = "product";
	private static final String TAG_PID = "ID_work";
	private static final String TAG_NAME = "title_work";
	private static final String TAG_PRICE = "author";
	private static final String TAG_DESCRIPTION = "description_eng";
	
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    
	Button sendButton;
	Button worksButton;

    TextView scanText;
    ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    static {
        System.loadLibrary("iconv");
    } 

	/**
	 * Metodo richiamato non appena l'attivit�� viene creata. Setta le
	 * impostazioni di base dell'activity, quali il layout di riferimento e le
	 * ottimizzazioni, come la presenza o meno di una barra di titolo.
	 * @param savedInstanceState Serve per riportare un eventuale stato
	 * dell���attivit�� salvato in precedenza da un'altra istanza che �� stata
	 * terminata. L'argomento �� null nel caso in cui l���attivit�� non abbia uno
	 * stato salvato.
	 */
    public void onCreate(Bundle savedInstanceState) {
    	// La prassi richiede che, come prima riga di un metodo predefinito
    	// (come questo), si richiami l'implementazione di base del metodo che
   		// si sta ridefinendo, con la sintassi super."metodo_da_ridefinire"
        super.onCreate(savedInstanceState);
        
        context=this;
        
		// Nascondo la barra del titolo dall'activity corrente
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Setto il layout definito in res/layout/activity_home.xml
		// NB: R �� una classe che fa da ponte tra il codice JAVA e le risorse
		// definite nei file XML, mappandole. E' una classe di appoggio
        setContentView(R.layout.activity_scan);
        
    	// Associo un ascoltatore ai bottoni precedentemente dichiarati
    	addListenerOnSendButton();
    	addListenerOnWorksButton();
    	
    	// Setto l'orientamento dell'activity in questione in verticale (mod ritratto)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // Creazione di un'istanza Handler per avere un handle per l'inizializzazione
        autoFocusHandler = new Handler();
        
        //if (worksList.size() == 0) {
        	worksList = getIntent().getParcelableArrayListExtra("worksList");
        //} else {
        	//worksList = getIntent().getParcelableArrayListExtra("worksList2");
        //}
    }

	/**
	 * Metodo richiamato non appena l'attivit�� viene messa in pausa. Esso consiste nel
	 * fermare le attivit�� superflue (causa di consumo di CPU, blocco di risorse utili
	 * al sistema, ecc). L'implementazione di questo metodo deve essere il pi�� veloce
	 * possibile in quanto l'attivit�� successiva non viene avviata fintanto che la
	 * presente non termina.
	 */
    public void onPause() {
    	// La prassi richiede che, come prima riga di un metodo predefinito
    	// (come questo), si richiami l'implementazione di base del metodo che
   		// si sta ridefinendo, con la sintassi super."metodo_da_ridefinire"
        super.onPause();
        
        // Chiamo il metodo che rilascia le risorse della fotocamera permettendo
        // l'utilizzo della stessa da parte di altre applicazioni
        releaseCamera();
    }

    /**
     * Metodo "sicuro" che permette di ottenere un'istanza dell'oggetto Camera.
     * @return Camera L'oggetto di tipo Camera inizializzato.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        
        // Avvia la fotocamera con tutte le sue risorse chiamando il metodo open()
        try {
            c = Camera.open();
        } catch (Exception e) {
        }
        
        return c;
    }

	/**
	 * Metodo che permette di rilasciare tutte le risorse della fotocamera permettendo
	 * l'utilizzo della stessa da parte di altre applicazioni in background.
	 */
    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            
            // DEBUG: Riattiva la fotocamera se si ritorna allo scan con il tasto indietro
            mPreview.getHolder().removeCallback(mPreview);

            // Rilascia tutte le risorse della fotocamera chiamando il metodo release()
            mCamera.release();
            mCamera = null;
        }
    }
    
	/**
	 * Metodo richiamato non appena l'utente riprende ad interagire con la presente
	 * attivit��. Tale metodo viene eseguito sempre in seguito ad un onPause().
	 */
    @Override
    public void onResume() {
    	// La prassi richiede che, come prima riga di un metodo predefinito
    	// (come questo), si richiami l'implementazione di base del metodo che
   		// si sta ridefinendo, con la sintassi super."metodo_da_ridefinire"
        super.onResume();
        
        TextView textView = (TextView) findViewById(R.id.textView3);
        
        if (worksList.size() == 0) {
        	textView.setText("You have no works in your book!");
        }
        
        if (worksList.size() == 1) {
        	textView.setText("You have " + worksList.size() + " work in your book!");
        }

        if (worksList.size() > 1) {
        	textView.setText("You have " + worksList.size() + " works in your book!");
        }
        
        // Ottenimento dell'istanza Camera. L'attivit�� raggiunge il pieno controllo
        // dell'utente. Tutto ci�� a condizione che mCamera non sia ancora istanziata
        if (mCamera == null) {
            // DEBUG // autoFocusHandler = new Handler();
        	
        	if (mustScan == true) { 
            mCamera = getCameraInstance();

            // Istanziamento del barcode scanner per la lettura di QRCodes
            scanner = new ImageScanner();
            scanner.setConfig(0, Config.X_DENSITY, 3);
            scanner.setConfig(0, Config.Y_DENSITY, 3);

            // Istanziamento dell'anteprima della scansione
            mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
            FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
            preview.addView(mPreview);
        	}
        }
    }

    // Runnable: comando utilizzato per eseguire codice in un thread differente
    private Runnable doAutoFocus = new Runnable() {
    	
    	// Metodo che fa partire l'autofocus nel caso l'anteprima sia su true
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    // Operazioni principali di decodifica ed output in seguito ad una scansione
    PreviewCallback previewCb = new PreviewCallback() {
    	
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Parameters parameters = camera.getParameters();
                Size size = parameters.getPreviewSize();

                Image barcode = new Image(size.width, size.height, "Y800");
                barcode.setData(data);

                // Variabile intera utile a riconoscere uno scan effettuato con successo
                int result = scanner.scanImage(barcode);
                
                // Se la scansione �� avvenuta con successo, inizia a decodificare
                if (result != 0) {
                    previewing = false;
                	
                    // DEBUG: Righe attivate per evitare il duplice scan
                    mCamera.setPreviewCallback(null);
                    mCamera.stopPreview();
                    
                    // Variabile che contiene il risultato simbolico della scansione
                    SymbolSet syms = scanner.getResults();
                    
                    for (Symbol sym : syms) {
                    	// Riproduce un suono ad ogni ciclo di scan
                    	Intent sound = new Intent(ScanActivity.this,ScanSound.class);
                    	startActivityForResult(sound, 1);
                    	
                    	// Dichiaro e definisco il contesto dell'applicazione
                        Context context = getApplicationContext();
                        
                        // Stringa contenente la decodifica formato "aMuse-dVruhero-ID"
                        String scanQR = sym.getData();
                        
                        // Stringa che conterr�� unicamente l'ID dell'opera
                        String s;
                        
                        try {
                        	// aMuse-dVruhero-XXXXXX: considero dal 16' carattere
                        	int i = 0;
                        	int start = 15;
                        	int end = 16;
                       	
                        	// Considero l'ID eliminando gli zeri antecedenti
                        	while (scanQR.substring(start+i, end+i) == "0") {
                        		start = start+i;
                        		end = end+i;
                        		i++;
                        	}
                        	
                        	// Assegno l'ID effettivo dell'opera
                        	s = scanQR.substring(start, 21);
                       	
                        	// Converto da stringa a intero l'ID ricavato
                        	id = Integer.parseInt(s);
                        	
                        	pid = Integer.toString(id);
                        	Log.i("alandebug", "pid = " + id); // ok!
                        	
                        	/*
                        	// Restituisco l'ID ottenuto mediante toast message
                        	CharSequence text = "OPERA NUMERO: " + id;
                        	int duration = Toast.LENGTH_SHORT;
                        	Toast toast = Toast.makeText(context, text, duration);
                        	toast.show();
                        	*/
                        	
                        	// Interagisco col database in background
                        	new GetProductDetails().execute();

                        	//POP UP SINGOLA OPERA
                            //showpopup(id, pid);                        
                        } catch (Exception e) {
                        	// Nel caso il QRCode scansionato non sia della forma
                        	// aMuse-dVruhero-XXXXXX, avviso l'utente mediante toast
                        	Log.d("DBG", "QRCode inesistente: " + e.getMessage());
                        	CharSequence text = "QRCODE INESISTENTE!";
                        	int duration = Toast.LENGTH_SHORT;
                        	Toast toast = Toast.makeText(context, text, duration);
                        	toast.show();
                        }
                    	
                        barcodeScanned = true;
                    }
                }
            }
        };

        // Messa a fuoco automatica continua
        AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        	public void onAutoFocus(boolean success, Camera camera) {
        		// Tempo di 1000ms tra due messe a fuoco consecutive
        		autoFocusHandler.postDelayed(doAutoFocus, 1000);
        	}
        };
       
        public void showpopup(){        	
        	final Context context = this;
    		final Dialog d = new Dialog(ScanActivity.this);
    		
    		mustScan = false;
    		
    		Log.i("alandebug", "entrato nella funzione popup: id = " + id + ", pid = " + pid);
    		  
    		String uri = "drawable/o" + id;
    	    int imageResource = getResources().getIdentifier(uri, null, getPackageName());
    		
    		d.setContentView(R.layout.dialog_single_work);
    		
    		d.setTitle(title);
    		
    		Log.i("alandebug", "setto testo");
    		
    		author = (TextView) d.findViewById(R.id.author);
    		author.setText("work by " + auth);
    		
    		description_eng = (TextView) d.findViewById(R.id.textView1);
			description_eng.setText(desc);			

			Log.i("alandebug", "setto immagine");
			
    		ImageView iv = (ImageView) d.findViewById(R.id.imageview1);
    		Drawable image = getResources().getDrawable(imageResource);
    	    iv.setImageDrawable(image); 
    	    
    	    d.show();
    	    
    		Button addButton = (Button) d.findViewById(R.id.button1);
    		Button deleteButton = (Button) d.findViewById(R.id.button2);
    				
    		addButton.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View arg0) {
       				finish();
                    d.dismiss();
    				
    				Log.i("Fabio debug", "ArrayList: " + Integer.toString(worksList.size()));
    				
    				boolean alreadyScanned=false;

    				for (int i=0;i<worksList.size();i++) {
    					if (id==worksList.get(i).idWork)
    						alreadyScanned=true;
    					}
    					if (alreadyScanned==false) {
    						Work work= new Work(id, title, auth, exh, desc, "drawable/o"+id, true);
    						worksList.add(work);
    					} else if (alreadyScanned==true) {
    						Toast.makeText(getApplicationContext(), "You have already added this work to your book!", Toast.LENGTH_LONG).show();
    					}
    				
    					Log.i("Fabio debug", "ArrayList: " + Integer.toString(worksList.size()));
    				
    					Intent intent = new Intent(context, ScanActivity.class);
    					intent.putParcelableArrayListExtra("worksList", worksList);
    					startActivity(intent);   
    				}
    			});
    		
    		deleteButton.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View arg0) {
    				finish();
    				d.dismiss();

    				Intent intent = new Intent(context, ScanActivity.class);
    				intent.putParcelableArrayListExtra("worksList", worksList);
    				startActivity(intent);   
    			}
    		});

    		d.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                        KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        finish();
                        d.dismiss();
                        
        			    Intent intent = new Intent(context, ScanActivity.class);
        			    intent.putParcelableArrayListExtra("worksList", worksList);
        			    startActivity(intent);   
                    }
                    
                    return true;
                }
            });
    	}
        
    /**
     * Metodo che associa un evento al bottone "worksButton".
   	 * @see worksButton
     */
    public void addListenerOnWorksButton() {
   		// Per prassi bisogna definire che questo �� il contesto
    	final Context context = this;

    	// Definisco il bottone e gli associo un ID univoco
    	worksButton = (Button) findViewById(R.id.button1);
     
    	// Definisco l'evento al click del mouse mediante classe interna
    	worksButton.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View arg0) {
    			// l'evento in questione �� il collegamento con la pagina works
    			Intent intent = new Intent(context, WorksActivity.class);
    			intent.putParcelableArrayListExtra("worksList", worksList);
                startActivity(intent);
    		}
   		});
   	}
        
    /**
     * Metodo che associa un evento al bottone "sendButton".
     * @see scanButton
     */
    public void addListenerOnSendButton() {
    	// Per prassi bisogna definire che questo �� il contesto
    	final Context context = this;
     
    	// Definisco il bottone e gli associo un ID univoco
    	sendButton = (Button) findViewById(R.id.button2);
    		
    	// Definisco l'evento al click del mouse mediante classe interna
    	sendButton.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View arg0) {
    			// l'evento in questione �� il collegamento con la pagina email
    		    //Intent intent = new Intent(context, EmailActivity.class);
    		    //startActivity(intent);   
    			if (worksList.size()>0 && worksList.size()<=20) {
    				Intent intent = new Intent(context, EmailActivity.class);
    				intent.putParcelableArrayListExtra("worksList", worksList);
    				startActivity(intent);
    				} else if (worksList.size()>20) {
    				Toast.makeText(getApplicationContext(), "We are sorry but you can't send more then 20 works! Please, go to the 'MyWorks' section to manage the number of your favourites works. ", Toast.LENGTH_LONG).show();
    				} else if (worksList.size()<=0) {
    				Toast.makeText(getApplicationContext(), "We are sorry but your book is empty! Please, scan the QR Code of some works and add them to your book! ", Toast.LENGTH_LONG).show();
    				} else {
    				Toast.makeText(getApplicationContext(), "How the hell is that possible?!?! ", Toast.LENGTH_LONG).show();
    				}
    		}
    	});
    }
    
    
    /**
	 * Background Async Task to Get complete product details
	 * */
	class GetProductDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ScanActivity.this);
			pDialog.setMessage("Loading work details...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
			Log.i("alandebug", "loading...");
		}

		/**
		 * DB interaction
		 * */
		protected String doInBackground(String... params) {
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					// Check for success tag
					int success;
					Log.i("alandebug", "prima del try");
					try {
						Log.i("alandebug", "entrato nel try");
						
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						
						params.add(new BasicNameValuePair("ID_work", pid));

						JSONObject json = jsonParser.makeHttpRequest(
								url_product_detials, "GET", params);
						
						Log.i("alandebug", "prima del json toString. pid = " + pid);
						
						// check your log for json response
						Log.d("Work", json.toString());
						
						Log.i("alandebug", "dopo il json toString");
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						
						if (success == 1) {
							Log.i("alandebug", "successo! entrato nell'if");
							
							JSONArray productObj = json
									.getJSONArray(TAG_PRODUCT); // JSON Array

							JSONObject product = productObj.getJSONObject(0);
							
							title = product.getString(TAG_NAME);
							auth = product.getString(TAG_PRICE);
							desc = product.getString(TAG_DESCRIPTION);

							exh = product.getString("ID_exhibition");
						} else {
							// product with pid not found
							Log.e("alandebug", "non trovo niente con l'ID " + pid + "!");
						}
					} catch (JSONException e) {
						Log.i("alandebug", "successo = 0, entro nel catch :(");
						e.printStackTrace();
					}
				}
			});

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
			
			//POP UP SINGOLA OPERA
            showpopup();
		}
	}
	
	/*
	@Override
	public void onBackPressed() {
		// l'evento in questione Ãš il collegamento con la pagina email
	    Intent intent = new Intent(context, ScanActivity.class);
	    intent.putParcelableArrayListExtra("worksList", worksList);
                    startActivity(intent); 
	}
	*/
}