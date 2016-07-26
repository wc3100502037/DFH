package com.example.dfh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.dfh.R.id;



import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.*;
import android.widget.AnalogClock;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
 
  



    private String TAG="Project_Bluetooth";  

	private BluetoothAdapter mBtAdapter;
    private BluetoothDevice mBtDevice=null;
    private BluetoothSocket mBtSocket=null;
  
    
    private int REQUEST_CONNECT_DEVICE=1;
    private int REQUEST_DEVICE_ENABLE=2;
    
    public static final int STATE_CONNECT=1;
    public static final int STATE_LOST=2;
    public static final int STATE_TOAST_UNCONNECT=3;
    public static final int STATE_READ=4;
    public static final int STATE_TOAST_LOST=5;
    
    private String EXTRA_DEVICE="device_name";
    public static String DEVICE_NAME="device_name";
    public static String DEVICE_STATE="device_state";
    
    private TextView Distance,SeedNum;
    private TextView mTitle,mrighttitle;
    private Button Forward,Back,Left,Right,Stop;
    
    private Button TimeSetting;
    
    private ImageButton startseed,stopseed;
    private ImageButton KTup,KTdown,SGup,SGdown;
    
    
    private RadioButton Autoset,Manulset;
    private RadioGroup ModeSel;
    private boolean AutoModeFLAG,ManulModeFLAG;
    private boolean FLAG=false;
    
    boolean flag_color=true;
    boolean FIRST_DISPLAY=false;
    boolean BUTTON＿STATE=false;
    
    private BlueConnect mBlueconnect=null;
    private static int[] data=new int[5];
    
   private static List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();//不要忘了list的new
   private static Map<String,Object> map;       
          
   //private static MyBaseAdaper myBaseAdapter=null;
   private static ListView listview;
   
   public static int CountList=0;
   public static int Index=0;
   
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "------onCreate()-------");
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.custom_title);
		
		mTitle=(TextView) findViewById(R.id.left_title);
		mrighttitle=(TextView) findViewById(R.id.right_title);
		
		Forward=(Button) findViewById(R.id.forward);
		Back=(Button) findViewById(R.id.Back);
		Right=(Button) findViewById(R.id.Right);
		Left=(Button) findViewById(R.id.Left);
		Stop=(Button) findViewById(R.id.Stop);
		
		Autoset=(RadioButton) findViewById(R.id.Automode);
		Manulset=(RadioButton) findViewById(R.id.Manulmode);
		
		startseed=(ImageButton) findViewById(R.id.SEEDRun);
		stopseed=(ImageButton) findViewById(R.id.SEEDStop);
		
		Distance=(TextView) findViewById(R.id.Distance);
		SeedNum=(TextView) findViewById(R.id.SeedNum);
		
		TimeSetting=(Button) findViewById(R.id.timesetting);
		
		
		mBtAdapter=BluetoothAdapter.getDefaultAdapter();
		if(mBtAdapter==null){Toast.makeText(this,"您的手机没蓝牙",Toast.LENGTH_SHORT).show();}
		if(!mBtAdapter.isEnabled())
		   {mBtAdapter.enable();}
		
		 mTitle.setText("未连接");
		 mrighttitle.setText("智能免耕机");
		    
		// myBaseAdapter=new MyBaseAdaper(MainActivity.this, list);
		 Log.i(TAG, "----next is setadapter()----");
	    
		// listview.setAdapter(myBaseAdapter);
	     Log.i(TAG, "----previous is setadapter()----");
			
	    ModeSel=(RadioGroup) findViewById(R.id.setMode);
	    ModeSel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO 自动生成的方法存根
			if(arg1==Autoset.getId())
			{
				AutoModeFLAG=true;
				ManulModeFLAG=false;
			}
			else if(arg1==Manulset.getId())
			{
				ManulModeFLAG=true;
				AutoModeFLAG=false;
				
			}
				
				
			}
		});
	//*******************************落种*****************************************************************////////     
	     TimeSetting.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
		 Intent intent=new Intent(MainActivity.this,TimeSet.class);
		   startActivity(intent);
			 	
				
				
			}
		});
	     
	     
	     
//*****************************方向**************************************************************************//////////////
	     
	     Forward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
			//	
				//Forward.setBackgroundColor(getResources().getColor(android.R.color.background_light));
				if(BUTTON＿STATE==false)
				{ Toast.makeText(MainActivity.this,"请先连接", Toast.LENGTH_SHORT).show();}
				else{
					sendMessage((byte)21);//向前
					Toast.makeText(MainActivity.this,"向前", Toast.LENGTH_SHORT).show();
				}
			}
		}); 
		
	     
	     Back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
			//
				if(BUTTON＿STATE==false)
				{ Toast.makeText(MainActivity.this,"请先连接", Toast.LENGTH_SHORT).show();}
				else{
					//Toast.makeText(MainActivity.this,"向后", Toast.LENGTH_SHORT).show();
					sendMessage((byte)22);//向后
				}
				
			}
		});
		
	     Right.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				
				if(BUTTON＿STATE==false)
				{ Toast.makeText(MainActivity.this,"请先连接", Toast.LENGTH_SHORT).show();}
				else{
					sendMessage((byte)23);//向右
					//Toast.makeText(MainActivity.this,"向右", Toast.LENGTH_SHORT).show();
				}
			}
		});
	     
	     Left.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
			
				if(BUTTON＿STATE==false)
				{ Toast.makeText(MainActivity.this,"请先连接", Toast.LENGTH_SHORT).show();}
				else{
    				sendMessage((byte)24);//向左
    				//Toast.makeText(MainActivity.this,"向左", Toast.LENGTH_SHORT).show();
				}
			}
		});
	     
	     Stop.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO 自动生成的方法存根
				
					if(BUTTON＿STATE==false)
					{ Toast.makeText(MainActivity.this,"请先连接", Toast.LENGTH_SHORT).show();}
					else{
	    				sendMessage((byte)25);//向左
	    				//Toast.makeText(MainActivity.this,"停止", Toast.LENGTH_SHORT).show();
					}
				}
			});
	     
	 /************************************************************************************************************/
	 /************************************************************************************************************/
	 /************************************************************************************************************/
	 /************************************************************************************************************/
	    
	    if((ManulModeFLAG==true)&&(AutoModeFLAG==false)){ 
	    
	    	Forward.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO 自动生成的方法存根
					if(arg1.getAction()==MotionEvent.ACTION_DOWN)
					{
						sendMessage((byte)32);
					}else if(arg1.getAction()==MotionEvent.ACTION_UP)
					{
						sendMessage((byte)33);
						
					}
				
					return false;
				}
			});
	    	
	    	Back.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO 自动生成的方法存根
					
					if(arg1.getAction()==MotionEvent.ACTION_DOWN)
					{
						sendMessage((byte)34);
					}else if(arg1.getAction()==MotionEvent.ACTION_UP)
					{
						sendMessage((byte)35);
						
					}
			
					return false;
				}
			} );
	    	
	    	
	    	Left.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO 自动生成的方法存根
					if(arg1.getAction()==MotionEvent.ACTION_DOWN)
					{
						sendMessage((byte)36);
					}else if(arg1.getAction()==MotionEvent.ACTION_UP)
					{
						sendMessage((byte)37);
						
					}
					
					return false;
				}
			});
	    	
	    	
	    	Right.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO 自动生成的方法存根
					
					if(arg1.getAction()==MotionEvent.ACTION_DOWN)
					{
						sendMessage((byte)38);
					}else if(arg1.getAction()==MotionEvent.ACTION_UP)
					{
						sendMessage((byte)39);
						
					}
				
					return false;
				}
			});
	    	
	    	
	    	
	    	
	    	
	    	
	    	startseed.setOnClickListener(new View.OnClickListener() {
		
				@Override
				public void onClick(View arg0) {
					// TODO 自动生成的方法存根
				
					if(BUTTON＿STATE==false)
					{ Toast.makeText(MainActivity.this,"请先连接", Toast.LENGTH_SHORT).show();}
					else{
	    				sendMessage((byte)26);//向左
	    				//Toast.makeText(MainActivity.this,"停止", Toast.LENGTH_SHORT).show();
					}
				}
			});
	     
	     stopseed.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO 自动生成的方法存根
				
					if(BUTTON＿STATE==false)
					{ Toast.makeText(MainActivity.this,"请先连接", Toast.LENGTH_SHORT).show();}
					else{
	    				sendMessage((byte)27);//向左
	    				//Toast.makeText(MainActivity.this,"停止", Toast.LENGTH_SHORT).show();
					}
				}
			});
	    
	     
	     
	     //、、、、、、、、、、、、、、、手动、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、/////////////////////////
	      KTup.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				sendMessage((byte)28);//向左
			}
		});
	      
	      KTdown.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				sendMessage((byte)29);//向左
			}
		});
	      
	      SGup.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				sendMessage((byte)30);//向左
			}
		});
	      
	      SGdown.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				sendMessage((byte)31);//向左
			}
		});
	    }else{
	    	
	    Toast.makeText(this, "您目前正在使用自动模式",Toast.LENGTH_SHORT);	
	    	
	    }
	      /************************************************************************************************************/	     
	      /************************************************************************************************************/
	      /************************************************************************************************************/
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	 @Override
		protected void onDestroy() {
			super.onDestroy();
		Log.i(TAG, "-----onDestory()----------");
		 mBtAdapter.cancelDiscovery();
		 try {
			mBlueconnect.cancel();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "mBtSocket close fail");
			e.printStackTrace();
		}
		 
		 
		}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public void sendMessage(byte out)
{
Log.i(TAG, "----sendMessage start----");	
  mBlueconnect.write(out);
Log.i(TAG, "----sendMessage ends----");


}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private final Handler mhandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
		  
			switch(msg.what)
			{
			case STATE_CONNECT:
				Log.i(TAG, "----connect to "+msg.getData().getString(DEVICE_NAME)+"----");
				mTitle.setText("连接到："+msg.getData().getString(DEVICE_NAME));
 			    break;
			case STATE_TOAST_UNCONNECT:
				Log.i(TAG, "----unconnection----");
				mTitle.setText("未连接");
				Toast.makeText(MainActivity.this,msg.getData().getString(DEVICE_STATE),Toast.LENGTH_SHORT).show();
			    break;
			    
			case STATE_READ:
				Log.i(TAG, "----start read data----");
				flag_color=!flag_color;
				data[0]=msg.getData().getInt("data0");
				data[1]=msg.getData().getInt("data1");
				//data[2]=msg.getData().getInt("data2");
				//data[3]=msg.getData().getInt("data3");
				//data[4]=msg.getData().getInt("data4");
				Distance.setBackgroundColor(setColorByMyself());
				Log.i(TAG, "----data0="+String.valueOf(data[0]+"----"));
				Log.i(TAG, "----data1="+String.valueOf(data[1])+"----");
				Distance.setText("距离是："+String.valueOf(getDistance(data[0])+0.0000001).substring(0,5)+"   /m");
			     SeedNum.setText("种子："+String.valueOf(data[1]));
				break;
				
			case STATE_TOAST_LOST:
				Log.i(TAG, "----connection lost----");
				mTitle.setText(msg.getData().getString(DEVICE_STATE));
				Toast.makeText(MainActivity.this,"连接已中断",Toast.LENGTH_SHORT).show();
				break;
			}
			
	
		}
		
		
		
	};
	 
	 
///////////////////////////////////////////////////////////////////////////////////////////////////	 
	  @Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if(requestCode==REQUEST_CONNECT_DEVICE)
			{
			 if(resultCode==Activity.RESULT_OK)
			  {
				
				 mTitle.setText("连接中......");
				 String address=data.getExtras().getString(EXTRA_DEVICE);
				 BluetoothDevice device=mBtAdapter.getRemoteDevice(address);
				 
				  mBlueconnect=new BlueConnect(MainActivity.this,device,mhandler);
				  mBlueconnect.connect(device);
				  
				  if((mBlueconnect.mSTATE==mBlueconnect.STATE_CONNECT_LOST)||(mBlueconnect.mSTATE==mBlueconnect.STATE_NONE))
				    {    BUTTON＿STATE=false;
				    	
				    }else{
				    	BUTTON＿STATE=true;
				    
			           
				    	
				    }
				 
			  }
				
			}
	//////////////////////////////////////////////////////////////////////////////////////////////
			if(requestCode==REQUEST_DEVICE_ENABLE)
			{
				
				
				
			}
			
			
		}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.add(0,Menu.FIRST,0,"开启扫描");
		menu.add(0,Menu.FIRST+1,0,"开启传输");
		
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		switch(item.getItemId())
		{
		case Menu.FIRST:ensureDiscovery();
		           break;
		case Menu.FIRST+1:ensureTrans();
		         FLAG=!FLAG;
		         if(FLAG){
		     item.setTitle("取消传输");}
		         else{item.setTitle("开始传输");}  
		           break;
		}
		
		
		return super.onOptionsItemSelected(item);
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public void ensureDiscovery()
	{  Log.i(TAG, "ensureDiscovery");
	 if(!mBtAdapter.isEnabled()){mBtAdapter.enable();}
		
	  Intent intent=new Intent(MainActivity.this,DeviceList.class);
	  startActivityForResult(intent,REQUEST_CONNECT_DEVICE);
	  	
	}
	
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void ensureTrans()
	{   if(!FLAG){
		Log.i(TAG, "----startTrans()----");
		mBlueconnect.startTrans();
	}else{Log.i(TAG, "----stopTrans()----");
		mBlueconnect.stopTrans();}
	}
/////////////////////////////////////////////////////////////////////////////
	public int setColorByMyself()
	{  int res;
		if(flag_color==true)
		{res=Color.GREEN;
			return res;
		}
		else return Color.RED;
		
		
		
		
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 public void getRealValue(int data)
{
  	


}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private double getDistance(int data)
{double dis=0;
 dis=data*5*0.000001*340;
 return dis;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		
		
	
	
}
