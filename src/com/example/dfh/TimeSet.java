package com.example.dfh;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TimeSet extends Activity {
   
	
	ListView TimeSetView;
	String[] data={"10CM/每播种","20CM/每播种","30CM/每播种","50CM/每播种"};
	protected void OnCreat(Bundle savedInstanceState)
	{super.onCreate(savedInstanceState);
	   setContentView(R.layout.timesetview);	
		
	   
	 TimeSetView=(ListView) findViewById(R.id.TimeSettingListview);
	 TimeSetView.setAdapter(new ArrayAdapter<String>(TimeSet.this,android.R.layout.simple_list_item_1,data));
	   
	   
	   
	   
	   
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
