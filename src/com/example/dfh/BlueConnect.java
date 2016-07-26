package com.example.dfh;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.inputmethod.InputBinding;

public class BlueConnect  {
	
	private BluetoothDevice mBtDevice=null;
	private BluetoothSocket mBtSocket=null;
	private InputStream mBtInput;
	private OutputStream mBtOutput;
	private Handler mhandler;
	
	public int mSTATE;
	public int STATE_NONE=0;
	public int STATE_LISTEN=1;
	public int STATE_CONNECTING=2;
	public int STATE_CONNECTED=3;
	public int STATE_TRANSFORMING=4;
	public int STATE_CONNECT_LOST=5;
	
	private static UUID MY_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private String TAG="BlueConnect";
	
	private connectThread mconnectthread=null;
	private AcceptThread macceptthread=null;
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public BlueConnect(Context context,BluetoothDevice device,Handler handler) {
		// TODO Auto-generated constructor stub
		this.mBtDevice=device;
		this.mhandler=handler;
		 mSTATE=STATE_NONE;
		
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	public void connect(BluetoothDevice mDevice)
	{
		if(mconnectthread!=null) mconnectthread.cancel();
		
		 mconnectthread=new connectThread(mDevice);
		 
		 mconnectthread.start();
		 setSTATE(STATE_CONNECTED);
	    
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void setSTATE(int setstate)
	{
		Log.i(TAG, "----setSTATE()----"+mSTATE+"---->"+setstate);
		mSTATE=setstate;
	
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private int getSTATE()
	{
		
	 Log.i(TAG, "----getSTATE()----"+mSTATE);
	     return mSTATE; 
		
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void connectFail()
	{
	      this.cancel();	
	      
	  Message msg=mhandler.obtainMessage(MainActivity.STATE_TOAST_UNCONNECT);
	  Bundle bundle=new Bundle();
	  bundle.putString(MainActivity.DEVICE_STATE,"未连接");
	  msg.setData(bundle);
	  setSTATE(STATE_CONNECT_LOST);
	  mhandler.sendMessage(msg);
		
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void startTrans()
	{   if(macceptthread!=null) {macceptthread.cancel();}
	    Log.i(TAG, "----start data transport----");
		 macceptthread=new AcceptThread();
		 macceptthread.start();
		 setSTATE(STATE_TRANSFORMING);
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public void stopTrans()
	{
		
		macceptthread.cancel();
		    
		
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void write(byte out)
	{
	  	AcceptThread r;
	  	synchronized (BlueConnect.this) {
	     r=macceptthread;		
			
		}
	   r.write(out);	
		
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void Stop()
	{
		if(macceptthread!=null){macceptthread.cancel();macceptthread=null;}
		if(mconnectthread!=null){mconnectthread.cancel();mconnectthread=null;}
        setSTATE(STATE_NONE);		
		
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public class connectThread extends Thread
	{  
		private BluetoothDevice device;
		//private BluetoothSocket mBtSocket;
		public connectThread(BluetoothDevice mBtDevice) {
		
			device=mBtDevice;
		}
		
		
		@Override
		public void run() {
            Log.i(TAG, "connectThread run()");
			try {
				Log.i(TAG, "connecting-----");
				mBtSocket=device.createRfcommSocketToServiceRecord(MY_UUID);
				mBtSocket.connect();
				Log.i(TAG, "mBtSocket connect"+"To"+mBtDevice.getName());
				 Message msg= mhandler.obtainMessage(MainActivity.STATE_CONNECT);
			     Bundle bundle=new Bundle();
			     bundle.putString(MainActivity.DEVICE_NAME, device.getName());
			     msg.setData(bundle);
			     mhandler.sendMessage(msg);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				 Message msg= mhandler.obtainMessage(MainActivity.STATE_TOAST_UNCONNECT);
			     Bundle bundle=new Bundle();
			     bundle.putString(MainActivity.DEVICE_STATE,"连接失败");
			     msg.setData(bundle);
			     mhandler.sendMessage(msg);
				 setSTATE(STATE_CONNECT_LOST);
				
				Log.i(TAG, "connect fail");
				e.printStackTrace();
			}
			
			
		}
		
		public void cancel()
		{  Log.i(TAG, "connectThread cancel()");
			try {
				mBtSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.i(TAG, "close socket fail");
				e.printStackTrace();
			}
			
		}
		
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	public class AcceptThread extends Thread
	{  byte[] tembuff=new byte[1];
	   byte[] tembuff8=new byte[20];
	   byte[] convertbuff=new byte[8];
	   int[] data=new int[5]; 
	   int[] unsighbuff=new int[8];
       int i=0,j=0; 	
       int temp;
       int aviable;
       InputStream tmpinput;
       OutputStream tmpoutput;
       boolean flag1=false,flag2=false,flag3,flag4,flag5;
		public AcceptThread() {
		// TODO Auto-generated constructor stub
	
	    }	
	
	    public void run() {
		
		try {Log.i(TAG, "---get socket inputStream---");
			tmpinput=mBtSocket.getInputStream();
			tmpoutput=mBtSocket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			 try {
				mBtInput.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i(TAG,"get inputstream failed");
			e.printStackTrace();
		                        }
		mBtInput=tmpinput;
		mBtOutput=tmpoutput;
		
		
		while(true)
		{Log.i(TAG, "----InputStream read----");
		 Message msg=mhandler.obtainMessage(MainActivity.STATE_READ);
		  try{ 
			   if((aviable=mBtInput.read(tembuff8))!=0)
			   { Log.i(TAG, "---Thread.sleep()----");
				   Thread.sleep(300);
			   }
			   
               Log.w(TAG, "----while()----");
		       Log.w(TAG, "----aviable="+aviable+"----");
		     
		       flag1=false;
		       flag2=false;
		       flag3=false;
		       flag4=false;
		       flag5=false;
		       for(i=0;i<aviable;i=i+4)
		       {
		    	   if((tembuff8[i]==11)&&(tembuff8[i+3]==23))
		    	   {  Log.i(TAG, "----for1----");
		    		  data[0]=tembuff8[i+1]&0x00ff;
				      temp=tembuff8[i+2]&0x00ff;
				      temp=temp<<8;
				      data[0]=data[0]+temp;
				      flag1=true;
				      continue;
		    		   
		    	   }else if((tembuff8[i]==33)&&(tembuff8[i+3]==44))
		    	   {      Log.i(TAG, "----for2----");
		    		      data[1]=tembuff8[i+1]&0x00ff;
					      temp=tembuff8[i+2]&0x00ff;
					      temp=temp<<8;
					      data[1]=data[1]+temp; 
					      flag2=true;
					      
					      
		    		   
		    	   }
		    	   
		    	   /*
		    	   else if((tembuff8[i]==4)&&(tembuff8[i+3]==5))
		    	   {
		    		   Log.i(TAG, "----for3----");
		    		      data[2]=tembuff8[i+1]&0x00ff;
					      temp=tembuff8[i+2]&0x00ff;
					      temp=temp<<8;
					      data[2]=data[2]+temp; 
					      flag3=true;
					      continue;
		    		   
		    	   }else if((tembuff8[i]==6)&&(tembuff8[i+3]==7))
		    	   {
		    		   Log.i(TAG, "----for4----");
		    		      data[3]=tembuff8[i+1]&0x00ff;
					      temp=tembuff8[i+2]&0x00ff;
					      temp=temp<<8;
					      data[3]=data[3]+temp; 
					      flag4=true;  
					      continue;
		    	   }else if((tembuff8[i]==8)&&(tembuff8[i+3]==9))
		    	   {
		    		   Log.i(TAG, "----for5----");
		    		      data[4]=tembuff8[i+1]&0x00ff;
					      temp=tembuff8[i+2]&0x00ff;
					      temp=temp<<8;
					      data[4]=data[4]+temp; 
					      flag5=true;  
		    	   }
		    	   */
		    	   
		    	   if((flag1==true)&&(flag2==true))
		    	   {    Log.i(TAG, "---if(flag1,flag2)----");
		    		         
		    	             
		    		      Bundle bundle=new Bundle();
						  bundle.putInt("data0",data[0]);
						  bundle.putInt("data1",data[1]);  
						//  bundle.putInt("data2",data[2]);
						// bundle.putInt("data3",data[3]); 
						//  bundle.putInt("data4",data[4]);
						  msg.setData(bundle);
						  mhandler.sendMessage(msg);
						  break;
		    	   }
		    	   
		    	    
		       }
			  
	
 
		
		  }catch(Exception e)
		  { connectFail();
			Log.i(TAG, "---read() fail---");
			break;
			  
		  }
			
			
		}
		
		
	       }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		  public void write(byte out)
		{try {
			Log.i(TAG, "----mBtOutput.write()----");
			   mBtOutput.write(out);
			Log.i(TAG, "----mBtOutput.write Succeeded----");
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "----mBtOutput.write Failed----");
		}
			
			
			
		}
	
		
////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		
		public void cancel()
		{  Log.i(TAG, "----AcceptThread cancel()----");
			try {
				mBtInput.close();
				mBtSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.i(TAG, "close socket fail");
				e.printStackTrace();
			}
			
		}
		
		
	 
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public void cancel()
	{ 
		try {
			mBtSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "close socket fail");
			e.printStackTrace();
		}
		
	}
	

	
	
}
