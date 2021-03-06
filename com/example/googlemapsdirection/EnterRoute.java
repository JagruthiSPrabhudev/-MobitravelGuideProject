package com.example.googlemapsdirection;
  
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;   
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class EnterRoute extends Activity{
    
	Button mSourcePoint,mDestination,mAttraction,mSubmit;
	static LocationManager locationManager;
	static MyTrackListener locationListener;
	String locationInfo;
	String mSourceDestination,mAttractionvalues;
	String name;     
	         
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.newroute);  
           
        mSourcePoint = (Button) findViewById(R.id.button1);
        mDestination = (Button) findViewById(R.id.button2);
        mAttraction = (Button) findViewById(R.id.button3);
        mSubmit = (Button) findViewById(R.id.button4);
        
        mSourcePoint.setVisibility(View.VISIBLE);
        mDestination.setVisibility(View.VISIBLE);
        mAttraction.setVisibility(View.VISIBLE);
        mSubmit.setVisibility(View.VISIBLE);
        
        mSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.showToast(getApplicationContext(), ""+mAttractionvalues);
				
				ParseObject mpath = new ParseObject("Routepath");
				
				mpath.put("routename", name);
				mpath.put("routeattraction", mAttractionvalues);
				mpath.put("routeinfo", mSourceDestination);
				             
				            
				try{  
					Toast.makeText(getApplicationContext(), "Saving data", 6000).show();
					
					mpath.saveInBackground(new SaveCallback() {
						   public void done(ParseException e) {
						     if (e == null) {  
						    	 //finish();   
						    	
						    	 Toast.makeText(getApplicationContext(), "Data saved succesfully", 60000).show();
						     } else {
						    	 Toast.makeText(getApplicationContext(), "Data saved unsuccesfully", 60000).show();
						     }
						   }
						 });
					
				}catch(Exception e){
					e.printStackTrace();
				}   
				
			}
		});
        mAttraction.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				FetchLocation(true,false);
			}  
		});  
          
        mSourcePoint.setOnClickListener(new OnClickListener() {
        	 
			@Override
			public void onClick(View arg0) {
				FetchLocation(false,false);
				       
			}  
		});
        
        mDestination.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				FetchLocation(false,true);
			}
		});
	}
	
	public void FetchLocation(boolean isDescription,boolean isDestination){
		Criteria criteria = new Criteria();
		locationManager = (LocationManager) EnterRoute.this.getSystemService(Context.LOCATION_SERVICE);
		String best = locationManager.getBestProvider(criteria, true);
		locationListener = new MyTrackListener(isDescription,isDestination);   
		locationManager.requestLocationUpdates(best, 10000, 0, locationListener);
		
	}
	public void stopListening(LocationManager locationManager)
	{       
		      
		try
		{   
			if (locationManager != null && locationListener != null)
			{
				locationManager.removeUpdates(locationListener);
				locationManager = null;
				locationListener=null;
			}   
			        
			
		}
		catch (final Exception ex)
		{
			    
		}
	}
	private class MyTrackListener implements LocationListener {
		boolean isDescription,isDestination;
		
		public MyTrackListener(boolean state,boolean dest){
			isDescription = state;
			isDestination = dest;
		}
		public void onLocationChanged(Location location) {
			String message = String.format(
					"New Location of your friend \n Longitude: %1$s \n Latitude: %2$s",
					location.getLongitude(), location.getLatitude()
			);
			
			Utils.showToast(getApplicationContext(), "got loc");
			stopListening(locationManager);
			DiaplyAlert(isDescription,location,isDestination);   
     	     
     	    
     	 
     	    
		}

		public void onStatusChanged(String s, int i, Bundle b) {
		
			
					
		}

		public void onProviderDisabled(String s) {
			
			
		}

		public void onProviderEnabled(String s) {
			
			
		}

	}
	
	
	public void DiaplyAlert(final boolean isDescription,final Location location,final boolean isDest){

		 
		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(EnterRoute.this);
		View promptsView = li.inflate(R.layout.alertdialog, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				EnterRoute.this);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);
		final EditText description = (EditText) promptsView.findViewById(R.id.description);
		if(isDescription){
			description.setVisibility(View.VISIBLE);
		}else{
			description.setVisibility(View.GONE);
		}
		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.name);

		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("OK",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    	if(isDescription){
			    		
			    		locationInfo = location.getLatitude()+","+location.getLongitude()+"#"+userInput.getText().toString()+"#"+description.getText().toString();
			    		mAttractionvalues = mAttractionvalues+"$"+locationInfo;
			    	}else{
			    		locationInfo = location.getLatitude()+","+location.getLongitude()+"#"+userInput.getText().toString();
			    		if(isDest){
			    			mSourceDestination = mSourceDestination+"$"+locationInfo;
			    			name = name + "#" + userInput.getText().toString();
			    		}else{
			    			name = userInput.getText().toString();
			    			mSourceDestination = locationInfo;
			    			
			    		}
			    		
			    	}
			    	
			    }
			  })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });   

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	
	}
}
