/************************************************************
* Project		: aMuse
* Team			: dVruhero
* Page			: HomeActivity.java
* Description	: Android application activity (home page)
* Released		: April 25th, 2013 (Second Deliverable)
************************************************************/

package com.unitn.amuse;

/**
 * Android application activity (works page).
 * @author dVruhero team
 */

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class WorksActivity extends Activity {
	Button sendButton;
	Button deleteButton;
	
	ArrayList<Work> worksList = new ArrayList<Work>();
	
	MyAdapter worksAdapter=null;
	Context context;
	
	/**
	 * Metodo richiamato non appena l'activity viene creata. Setta le
	 * impostazioni di base dell'activity, quali il layout di riferimento e le
	 * ottimizzazioni, come la presenza o meno di una barra di titolo.
	 * @param savedInstanceState Serve per riportare un eventuale stato
	 * dellâattivitÃ  salvato in precedenza da un'altra istanza che Ãš stata
	 * terminata. L'argomento Ãš null nel caso in cui l'activity non abbia uno
	 * stato salvato.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// La prassi richiede che, come prima riga di un metodo predefinito
		// (come questo), si richiami l'implementazione di base del metodo che
		// si sta ridefinendo, con la sintassi super."metodo_da_ridefinire"
		super.onCreate(savedInstanceState);
		
		context=this;
		
		// Nascondo la barra del titolo dall'activity corrente
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// Setto il layout definito in res/layout/activity_home.xml
		// NB: R Ãš una classe che fa da ponte tra il codice JAVA e le risorse
		// definite nei file XML, mappandole. E' una classe di appoggio
		setContentView(R.layout.activity_works);
		
		//Generate list View from ArrayList
		displayListView();
		  
		//Aggiunge gli ascoltatori sui bottoni "Delete" e "Send"
		addListenerOnSendButton();
		
		/*
		if (worksList.size()==0) {	
			AlertDialog.Builder builder=new AlertDialog.Builder(context);
				builder.setTitle("Warning!");
				builder.setMessage("Your book is empty because you haven't scanned any works yet. Please, scan the QR Code of some works and add them to your book!");
				builder.setCancelable(false);
				builder.setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id){
					workList.remove(deleteWork);
					dialog.dismiss();
					}
				});
				builder.create();
				builder.show();
		}
		*/
	}
	
	private void displayListView() {
		//Crea un'ArrayList e ci inserisce all'interno una serie di opere
		//Questo andr tolto non appena sar implementata la DBConnection funzionante perch a quel punto
		//questa ArrayList, necessaria per la creazione dell'Adapter, mi verr passata dalla pagina Scan
		
		worksList = getIntent().getParcelableArrayListExtra("worksList");
		
		for (int i=0; i<worksList.size(); i++) {
			String ex = worksList.get(i).getExhibition();
			
			if (ex.equals("1")) {
				worksList.get(i).exhibition = "Egypt";
			}
			if (ex.equals("2")) {
				worksList.get(i).exhibition = "Futurism";
			}
			if (ex.equals("3")) {
				worksList.get(i).exhibition = "The Masterpieces";
			}
		}
		 
		//Crea un Adapter prendendo come parametri this, il pattern della "checkbox" dell'opera, e l'arrayList che mi arriva dalla pagina Scan
		//(Attualmente usa l'Arraylist creata prima ma non appena DBConnection sar operativa vert modificata)
		worksAdapter = new MyAdapter(this,R.layout.works_pattern, worksList);
		  
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
		
		//Definisco la ListView e gli associo un ID univoco dall'xml
		  ListView listView = (ListView) findViewById(R.id.listView1);
		  
		//Evita che la ListView diventi nera durante lo scroll
		  listView.setCacheColorHint(Color.WHITE);
		  
		//Assegna l'Adapter alla ListView
		  listView.setAdapter(worksAdapter);
		 
		//Crea un OnItemClickListener: un OnClckListener per gli elementi della ListView
		  listView.setOnItemClickListener(new OnItemClickListener() {
		   public void onItemClick(AdapterView<?> parent, View view,
		     int position, long id) {
		//Quando un elemento viene toccato, mostra un toast con il nome dell'opera corrispondente
			   ///Work work = (Work) parent.getItemAtPosition(position);
		    ///Toast.makeText(getApplicationContext(), "Clicked on Row: " + work.getName(), Toast.LENGTH_SHORT).show();
		   }
		  });
		 
		 }
	
//Questa classe estende un'ArrayAdapter in modo che risponda alle nostre esigenze.
	
private class MyAdapter extends ArrayAdapter<Work> {
		 
		  public ArrayList<Work> workList;
		  
		  public Work deleteWork;
		 
		//Il creatore di MyAdapter: prende come parametri il context, il pattern della "checkbox" dell'opera dal xml e un'ArrayList di Work, che
		//diventer l'ArrayList di MyAdapter  
		  public MyAdapter(Context context, int textViewResourceId, ArrayList<Work> workList) {
		   super(context, textViewResourceId, workList);
		   this.workList = new ArrayList<Work>();
		   this.workList.addAll(workList);
		  }
		 
		//Crea una classe contenente una checkbox e una textview, mi servir per gestire gli eventOnClick sul pattern della singola opera
		  private class ViewHolder {
			  ImageView workIMG;
			  TextView name;
			  TextView exhibition;
			  ImageView deleteIMG;
		  }
		 
		//Gestisce il pattern di una singola opera, che contiene checkbox e textView, assegnando un onClickListener alla checkbox.
		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
			  
		   ViewHolder holder = null;
		   Log.v("ConvertView", String.valueOf(position));
		 
		   if (convertView == null) {
		   LayoutInflater vi = (LayoutInflater)getSystemService(
		     Context.LAYOUT_INFLATER_SERVICE);
		   convertView = vi.inflate(R.layout.works_pattern, null);
		 
		   holder = new ViewHolder();
		   holder.workIMG = (ImageView) convertView.findViewById(R.id.WorkIMG);
		   holder.name = (TextView) convertView.findViewById(R.id.name);
		   holder.exhibition = (TextView) convertView.findViewById(R.id.exhibition);
		   holder.deleteIMG = (ImageView) convertView.findViewById(R.id.DeleteIMG);

		   convertView.setTag(holder);
		 
		   //Associa un onclicklistener al nome dell'opera (holder.name)
		    holder.workIMG.setOnClickListener( new View.OnClickListener() {  
		     public void onClick(View v) {  
		      //CheckBox cb = (CheckBox) v; 
		      //ImageView iv=(ImageView) v;
		      //Work work = (Work) cb.getTag(); 
		      //Work work = (Work) iv.getTag();
		      ///Toast.makeText(getApplicationContext(),"Clicked on WorkImage..! ",Toast.LENGTH_SHORT).show();
		      //work.setSelected(cb.isChecked());
		     }  
		    });
		    
		    holder.deleteIMG.setOnClickListener( new View.OnClickListener() {  
			     public void onClick(View v) {  
			      //CheckBox cb = (CheckBox) v; 
			      ImageView iv=(ImageView) v;
			      //Work work = (Work) cb.getTag(); 
			      Work work = (Work) iv.getTag();
			      deleteWork = work; 

			      AlertDialog.Builder builder=new AlertDialog.Builder(context);
			      builder.setTitle("Warning!");
			      builder.setMessage("Are you sure you want to delete \"" + work.name + "\"?");
			      builder.setCancelable(false);
			      builder.setPositiveButton("Si", new android.content.DialogInterface.OnClickListener() {
			              public void onClick(DialogInterface dialog, int id){
			            	  		workList.remove(deleteWork);
						      		worksAdapter = new MyAdapter(getApplicationContext(),R.layout.works_pattern, workList);
						      		TextView textView = (TextView) findViewById(R.id.textView3);
						      		textView.setText("You have " + worksAdapter.workList.size() + " works in your book!");
						      		ListView listView = (ListView) findViewById(R.id.listView1);
						      		listView.setCacheColorHint(Color.WHITE);
						      		listView.setAdapter(worksAdapter);
						      		dialog.dismiss();
						      		
			                      }
			              });
			      builder.setNegativeButton("No", new android.content.DialogInterface.OnClickListener() {
		              public void onClick(DialogInterface dialog, int id){
		                      dialog.dismiss();
		                      }
		              });
			      builder.create();
			      builder.show();
			      
			     }  
			    });
		    
		   } else {
			   		holder = (ViewHolder) convertView.getTag();
		   		}
		 
		   
		   Work work = workList.get(position);
		   
		   holder.exhibition.setText(" (exhibition: " +  work.getExhibition() + ")");
		   
		   holder.name.setText(" "+work.getName());
		   
		   String uri = work.getImgWork();
		   int imageResource = getResources().getIdentifier(uri, null, getPackageName());
		   Drawable image = getResources().getDrawable(imageResource);
		   holder.workIMG.setImageDrawable(image);
		   
		   String delete = "drawable/ic_delete";
		   int imageDelete = getResources().getIdentifier(delete, null, getPackageName());
		   Drawable iconDelete = getResources().getDrawable(imageDelete);
		   holder.deleteIMG.setImageDrawable(iconDelete);
		   
		   /*String uri = "drawable/o1";
		   int imageResource = getResources().getIdentifier(uri, null, getPackageName());
		   ImageView iv = (ImageView) convertView.findViewById(R.id.WorkIMG);
		   Drawable image = getResources().getDrawable(imageResource);
		   iv.setImageDrawable(image);*/
		   
		   holder.workIMG.setTag(work);
		   holder.deleteIMG.setTag(work);
		 
		   return convertView;
		 
		  }
		 
		 }
	
	/**
	 * Metodo che associa un evento al bottone "sendButton".
	 * @see sendButton
	 */
	public void addListenerOnSendButton() {
		// Per prassi bisogna definire che questo Ãš il contesto
		final Context context = this;
 
		// Definisco il bottone e gli associo un ID univoco
		sendButton = (Button) findViewById(R.id.button1);
 
		// Definisco l'evento al click del mouse mediante classe interna
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// l'evento in questione Ãš il collegamento con la pagina email
			    //Intent intent = new Intent(context, EmailActivity.class);
			    //intent.putParcelableArrayListExtra("worksList", worksAdapter.workList);
                            //startActivity(intent);   
				if (worksAdapter.workList.size()>0 && worksAdapter.workList.size()<=20) {
					Intent intent = new Intent(context, EmailActivity.class);
					intent.putParcelableArrayListExtra("worksList", worksAdapter.workList);
					startActivity(intent);
					} else if (worksAdapter.workList.size()>20) {
					Toast.makeText(getApplicationContext(), "We are sorry but you can't send more then 20 works! Please, go to the 'MyWorks' section to manage the number of your favourites works. ", Toast.LENGTH_LONG).show();
					} else if (worksAdapter.workList.size()<=0) {
					Toast.makeText(getApplicationContext(), "We are sorry but your book is empty! Please, scan the QR Code of some works and add them to your book! ", Toast.LENGTH_LONG).show();
					} else {
					Toast.makeText(getApplicationContext(), "How the hell is that possible?!?! ", Toast.LENGTH_LONG).show();
					}
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
	
	@Override
	public void onBackPressed() {
		// l'evento in questione Ãš il collegamento con la pagina email
	    Intent intent = new Intent(context, ScanActivity.class);
	    intent.putParcelableArrayListExtra("worksList", worksAdapter.workList);
                    startActivity(intent); 
	}
}