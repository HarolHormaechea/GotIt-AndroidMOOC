package com.hhg.gotit.fragments;

import java.util.Collection;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.hhg.gotit.MainActivity;
import com.hhg.gotit.OperationsManager.OPERATION_RESULT;
import com.hhg.gotit.OperationsManager.OPERATION_TYPE;
import com.hhg.gotit.R;
import com.hhg.gotit.models.UserData;

public class UserDataListFragment extends Fragment {
	private UserDataListAdapter adapter;
	private MainActivity activity;
	
	public UserDataListFragment(MainActivity mainActivity) {
		this.activity = mainActivity;
	}

	public void setData(Collection<UserData> userData){
		Log.d("UserDataFrag", "setData()");
		adapter = new UserDataListAdapter(activity);
		adapter.loadData(userData);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
		Log.d("UserDataFrag", "onCreateView()");
		View v = inflater.inflate(R.layout.user_data_list_view, null);
        ListView list = (ListView)v;
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				activity.processResult(OPERATION_TYPE.OTHERS_PROFILE, 
						OPERATION_RESULT.OK, "Retrieving profile",
						adapter.getItem(position));
			}
        	
        });
		return v;
	}
}
