package com.example.hellogooglemaps;
import android.app.Activity;
import android.content.Intent;





public class Activity2 extends Activity  {
			


public int test() {
    // Do something in response to button
	Intent intent = new Intent(this, MainActivity.class);
	int message = 113576726;
	intent.putExtra("key",message);
	   startActivity(intent);
	   return(message);
}


}




