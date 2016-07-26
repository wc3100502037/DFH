package com.example.dfh;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DeviceList extends Activity {

	

	private String TAG="DeviceList";
	private String EXTRA_DEVICE="device_name";
	
	private BluetoothAdapter mBtAdapter;
	private ArrayAdapter<String> mPairedArrayAdapter;
	private ArrayAdapter<String> munPairedArrayAdapter;
	
	private Button scan;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devicelist);
		
		mBtAdapter=BluetoothAdapter.getDefaultAdapter();
		scan=(Button) findViewById(R.id.start_scan);
		scan.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			 v.setVisibility(View.GONE);
			  doDiscovery();
				
			}
		});
		
		mPairedArrayAdapter=new ArrayAdapter<String>(DeviceList.this,R.layout.device_name);
		munPairedArrayAdapter=new ArrayAdapter<String>(DeviceList.this,R.layout.device_name);
		
		ListView pairedlistview=(ListView) findViewById(R.id.paired_device);
		pairedlistview.setAdapter(mPairedArrayAdapter);
		pairedlistview.setOnItemClickListener(itemclicker);
		
		ListView unListView=(ListView) findViewById(R.id.unpaired_device);
		unListView.setAdapter(munPairedArrayAdapter);
		unListView.setOnItemClickListener(itemclicker);
		
		IntentFilter intent=new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver,intent);
		
		    intent=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, intent);
		
		Set<BluetoothDevice> paireddevice=mBtAdapter.getBondedDevices();
		
		if(paireddevice.size()>0)
		{
			for(BluetoothDevice device:paireddevice)
			{
				mPairedArrayAdapter.add(device.getName()+'\n'+device.getAddress());
			}
		}else{
			mPairedArrayAdapter.add("无设备");
		}
		
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mBtAdapter!=null)
			mBtAdapter.cancelDiscovery();
		this.unregisterReceiver(mReceiver);
		
		
		
		
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void doDiscovery()
	{Log.i(TAG, "start Discovery");
	
	 setProgressBarIndeterminateVisibility(true);
	 setTitle("正在扫描。。。。。。");
	 
	 
		if(mBtAdapter.isDiscovering())
		{mBtAdapter.cancelDiscovery();}
		
		mBtAdapter.startDiscovery();
		
	}
	
	private OnItemClickListener itemclicker=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			mBtAdapter.cancelDiscovery();
			
			String info= ((TextView) v).getText().toString();
			String address=info.substring(info.length()-17);
		
			Intent intent=new Intent();
			intent.putExtra(EXTRA_DEVICE, address);
			setResult(Activity.RESULT_OK,intent);
			
			
			finish();
			
			
			
			
		}
		
		
		
	};
	
	
	private final BroadcastReceiver mReceiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			
			String action=intent.getAction();
			
			if(BluetoothDevice.ACTION_FOUND.equals(action))
			{   Log.d(TAG, "-------BroadCast------ACTION_FOUND-----");
				BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if(device.getBondState()!=BluetoothDevice.BOND_BONDED)
				{
				  mPairedArrayAdapter.add(device.getName()+'\n'+device.getAddress()); 	
				}
			}
			
			if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
			{    Log.d(TAG, "-------BroadCast------ACTION_DISCOVERY_FINISHED-----");
				setProgressBarIndeterminateVisibility(false);
				setTitle("选择设备");
				if(munPairedArrayAdapter.getCount()==0)
				{
				  munPairedArrayAdapter.add("无新设备"); 	
				}
					
			}
			
		}
	};
	
	
	
	
	
	

}
