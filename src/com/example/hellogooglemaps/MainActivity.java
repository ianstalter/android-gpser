package com.example.hellogooglemaps;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;


//import android.content.Intent;
//import android.content.Intent;




public class MainActivity extends MapActivity implements LocationListener {
	
	
	MyLocationOverlay myLocationOverlay;

	public MapView mapView;
    MapController mc;
    
    OverlayItem overlayitem;
    List<Overlay> mapOverlays;
    HelloItemizedOverlay itemizedoverlay;
    GeoPoint myLocationGeoPoint;
    

	
	private MockGpsProvider mMockGpsProviderTask = null;
	//mMockGpsProviderTask = new MockGpsProvider();
	
	/* This method is called when use position will get changed */
	public void onLocationChanged(Location location) {
		
		
	    GeoPoint myLocationGeoPoint = new GeoPoint((int)(location.getLatitude() * 1e6) , (int)(location.getLongitude() * 1e6));
	    
    //	Toast.makeText(this, "changinglocation", Toast.LENGTH_SHORT).show();
    	String message = String.format(
                "New Location \n Longitude: %1$s \n Latitude: %2$s",
                (int)(location.getLongitude()), (int)(location.getLatitude())
        );
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
       // mc.setCenter(myLocationGeoPoint);
       mc.animateTo(myLocationGeoPoint);    	
	  mc.setZoom(12);
    	//}
       
      
      mapOverlays = mapView.getOverlays(); 
      overlayitem = new OverlayItem(myLocationGeoPoint, "Hola, Mundo!", "I'm in Mexico City!");
       itemizedoverlay.addOverlay(overlayitem);
       mapOverlays.add(itemizedoverlay);
       
       
	}
	

	

	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "No mock data mofo", Toast.LENGTH_SHORT).show();

	}
	public void onProviderEnabled(String provider) {
	}
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	int test()
	{
		int i1=getIntent().getIntExtra("key", -1);
		return(i1);
	}
    
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	    mapView = (MapView) findViewById(R.id.mapview);
	    mc = mapView.getController();
    mapView.setBuiltInZoomControls(true);
    mapView.setSatellite(true);
    //mc.setZoom(10);
  
     mapOverlays = mapView.getOverlays();
     Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
     itemizedoverlay = new HelloItemizedOverlay(drawable, this);
     
     GeoPoint point = new GeoPoint(19240000,-99120000);
     //GeoPoint point = new GeoPoint(19240000,test());

     //GeoPoint point = new GeoPoint(19240000,message);
     overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
     itemizedoverlay.addOverlay(overlayitem);
     mapOverlays.add(itemizedoverlay);

    //   LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    //   Criteria criteria = new Criteria();
	//	criteria.setAccuracy( Criteria.ACCURACY_COARSE );
	//	String provider = lm.getBestProvider( criteria, true );
		
	//	if ( provider == null ) {
	//		Toast.makeText(this, "No mock data mofo", Toast.LENGTH_SHORT).show();
			
	//	}
		
		   /** Setup GPS. */
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
      //  Location lastKnownLocation = locationManager.getLastKnownLocation(locationManager);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){ 
        	/// use real GPS provider if enabled on the device
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        else if(!locationManager.isProviderEnabled(MockGpsProvider.GPS_MOCK_PROVIDER)) {
        	// otherwise enable the mock GPS provider
        	locationManager.addTestProvider(MockGpsProvider.GPS_MOCK_PROVIDER, false, false,
        			false, false, true, false, false, 0, 5);
        	locationManager.setTestProviderEnabled(MockGpsProvider.GPS_MOCK_PROVIDER, true);
        	Toast.makeText(this, "mock data enabled mofo", Toast.LENGTH_SHORT).show();
        }  
        if(locationManager.isProviderEnabled(MockGpsProvider.GPS_MOCK_PROVIDER)) {
        	locationManager.requestLocationUpdates(MockGpsProvider.GPS_MOCK_PROVIDER, 0, 0, this);

        	/** Load mock GPS data from file and create mock GPS provider. */
        	try {
        		// create a list of Strings that can dynamically grow
        		List<String> data = new ArrayList<String>();

        		/** read a CSV file containing WGS84 coordinates from the 'assets' folder
        		 * (The website http://www.gpsies.com offers downloadable tracks. Select
        		 * a track and download it as a CSV file. Then add it to your assets folder.)
        		 */			
        		InputStream is = getAssets().open("mock_gps_data.csv");
        		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        		// add each line in the file to the list
        		String line = null;
        		while ((line = reader.readLine()) != null) {
        			data.add(line);
        		}

        		// convert to a simple array so we can pass it to the AsyncTask
        		String[] coordinates = new String[data.size()];
        		data.toArray(coordinates);

        		// create new AsyncTask and pass the list of GPS coordinates
        		mMockGpsProviderTask = new MockGpsProvider();
        		mMockGpsProviderTask.execute(coordinates);
        	} 
        	catch (Exception e) {}
        }
    

        
       
            }
	private class MockGpsProvider extends AsyncTask<String, Integer, Void> {
		public static final String LOG_TAG = "GpsMockProvider";
		public static final String GPS_MOCK_PROVIDER = "GpsMockProvider";
		
		/** Keeps track of the currently processed coordinate. */
		public Integer index = 0;

		@Override
		protected Void doInBackground(String... data) {			
			// process data
			for (String str : data) {
				// skip data if needed (see the Activity's savedInstanceState functionality)
				if(index < 5) {
					index++;
					continue;
				}				
				
				// let UI Thread know which coordinate we are processing
				publishProgress(index);
				
				// retrieve data from the current line of text
				Double latitude = null;
				Double longitude = null;
				Double altitude= null;
				try {
					String[] parts = str.split(",");
					latitude = Double.valueOf(parts[0]);
					longitude = Double.valueOf(parts[1]);
					altitude = Double.valueOf(parts[2]);
				}
				catch(NullPointerException e) { break; }		// no data available
				catch(Exception e) { continue; }				// empty or invalid line

				// translate to actual GPS location
				Location location = new Location(GPS_MOCK_PROVIDER);
				location.setLatitude(latitude);
				location.setLongitude(longitude);
				location.setAltitude(altitude);
				location.setTime(System.currentTimeMillis());

				// show debug message in log
				Log.d(LOG_TAG, location.toString());

				// provide the new location
				LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				locationManager.setTestProviderLocation(GPS_MOCK_PROVIDER, location);
				
				// sleep for a while before providing next location
				try {
					Thread.sleep(200);
					
					// gracefully handle Thread interruption (important!)
					if(Thread.currentThread().isInterrupted())
						throw new InterruptedException("");
				} catch (InterruptedException e) {
					break;
				}
				
				// keep track of processed locations
				index++;
			}

			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			Log.d(LOG_TAG, "onProgressUpdate():"+values[0]);
//			mMockGpsProviderIndex = values[0];
		}
		
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
    
}
