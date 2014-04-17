package com.example.hubbletester;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import ali.test.hubblecommunicator.HubbleCommunicator;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	Button sendStartBtn;
	Button sendTestBtn;
	Button sendEndBtn;
	TextView giveInfo;
	
	HubbleCommunicator hubbleCommunicator;
	final String hubbleAddress = "http://auto11.yunosauto.com/hubble/savedata.php";
	final String logTag = "hubbleTester";
	
	private String taskId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		hubbleCommunicator = new HubbleCommunicator(hubbleAddress, logTag);
		
		sendTestBtn = (Button) this.findViewById(R.id.button1);
		sendStartBtn = (Button) this.findViewById(R.id.button2);
		sendEndBtn = (Button) this.findViewById(R.id.button3);
		giveInfo = (TextView) this.findViewById(R.id.gotInfo);
		
		sendTestBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String[] hubbleResponse = new String[1];
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(0);
				nameValuePair.clear();
				
				String resultStr; 
				if (hubbleCommunicator.sendMessageSync(nameValuePair, hubbleResponse)){
					resultStr = "success: " + hubbleResponse[0];
				}else{
					resultStr = "fail: ";
				}
				
				giveInfo.setText(resultStr);
			}
		});
		
		sendStartBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String[] hubbleResponse = new String[1];
				
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(6);
				nameValuePair.add(new BasicNameValuePair("version", "2.7.1-R1-20140318.0601"));
				nameValuePair.add(new BasicNameValuePair("device", "C2"));
				nameValuePair.add(new BasicNameValuePair("alias", "weidu.yjy"));
				nameValuePair.add(new BasicNameValuePair("imei", "869881000709652"));
				nameValuePair.add(new BasicNameValuePair("cmd", "fake cmd"));
				nameValuePair.add(new BasicNameValuePair("type", "distributedmonkey_start"));
				
				nameValuePair.add(new BasicNameValuePair("isTest", "true"));
				
				hubbleCommunicator.sendMessageAsync(nameValuePair);
				String resultStr; 
				if (hubbleCommunicator.waitForHubbleResponse(hubbleResponse)){
					resultStr = "success: " + hubbleResponse[0];
					
					int index = getIndexOfNumber(resultStr);
					
					if (index >= 0){
						taskId = resultStr.substring(index);
						Log.d(logTag, "The task_id is " + taskId);
					}else{
						Log.w(logTag, "Cannot parse the return task id " + resultStr);
						taskId = "0";
					}
				}else{
					resultStr = "fail ";
				}
				
				giveInfo.setText(resultStr);
			}
		});
		
		sendEndBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String[] hubbleResponse = new String[1];
				
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
				nameValuePair.add(new BasicNameValuePair("taskid", taskId));
				nameValuePair.add(new BasicNameValuePair("type", "distributedmonkey_end"));
				
				String resultStr; 
				if (hubbleCommunicator.sendMessageSync(nameValuePair, hubbleResponse)){
					resultStr = "success: " + hubbleResponse[0];
				}else{
					resultStr = "fail: ";
				}
				
				giveInfo.setText(resultStr);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	private int getIndexOfNumber(String codeStr){
		Log.d(logTag, "The got string is " + codeStr);
		
		int strLen = codeStr.length();
		if (strLen == 0)
			return 0;
		
		for (int i = 0 ; i < strLen; i++){
			if ((codeStr.charAt(i) >= '0') &&
				(codeStr.charAt(i) <= '9'))
				return i;
		}

		return -1;
	}
}
