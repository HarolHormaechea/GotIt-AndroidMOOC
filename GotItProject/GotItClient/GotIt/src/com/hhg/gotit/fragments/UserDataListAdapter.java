package com.hhg.gotit.fragments;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.hhg.gotit.MainActivity;
import com.hhg.gotit.R;
import com.hhg.gotit.models.UserData;

public class UserDataListAdapter implements ListAdapter {
	private ArrayList<UserData> LIST = new ArrayList<UserData>();
	private MainActivity activity;
	
	public UserDataListAdapter(MainActivity activity){
		this.activity = activity;
	}
	
	public void loadData(Collection<UserData> collection){
		LIST.addAll(collection);		
		Log.d("Adapter", LIST.size() + " registers received.");
	}
	
	public UserData getUserDetailsById(long id){
		for(UserData user : LIST){
			if(user.getId() == id){
				return user;
			}
		}
		return null;
	}

	@Override
	public int getCount() {
		return LIST.size();
	}

	@Override
	public Object getItem(int position) {
		return LIST.get(position);
	}

	@Override
	public long getItemId(int position) {
		UserData user = (UserData)LIST.get(position);
		return user.getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("Adapter", "getView()");
		//First, we will attempt to use the ViewHolder to find a hold
		//view for the element to be seen, to achieve a more fluent UI.
		final ViewHolderItem viewHolder;
		
		//If this list is empty, we will only show a message with that
		//information.
		if(LIST.size() == 0)//if there are no elements in our list
		{
			//TODO: Show empty list message.
			Log.d("Adapter", "NO DATA");
		}else{	
			UserData item = (UserData)getItem(position);
			if(item == null){
				Log.d("Adapter", "NULL OBJ REQ");
				return null; //if there is no element to be shown, we will obviously ignore this view altogether.
			}
			// We will attempt to inflate our view, first by checking if one is already
			//available for this item, and if not, creating it anew.
			if(convertView == null)//If we are not provided any real old view and have to create a new one
			{
				LayoutInflater inflater = (LayoutInflater)activity.getApplicationContext()
						.getSystemService(activity.getApplicationContext().LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.user_data_element,
						parent, false);
				convertView.setVisibility(View.VISIBLE);
				
				//Setting up the viewHolder. assigning to it every required view :
				viewHolder = new ViewHolderItem();
				viewHolder.fullName = (TextView)convertView.findViewById(R.id.user_data_element_full_name);
				viewHolder.username = (TextView)convertView.findViewById(R.id.user_data_element_username);
				viewHolder.usertype = (TextView)convertView.findViewById(R.id.user_data_element_type);
				viewHolder.id = item.getId();
				
				convertView.setTag(viewHolder);
				Log.d("Adapter", "New view created");
			}else//If we have a previous instance of this View, we recycle it.
			{
				viewHolder = (ViewHolderItem)convertView.getTag();
				Log.d("Adapter", "Old view recovered.");
			}
			
			viewHolder.fullName.setText(
					item.getFirstName()+" "+item.getLastName());
			viewHolder.username.setText(item.getUsername());
			if(item.getUserType() == null)
				Log.d("UserList", "USERTYPENULL");
		}
		return convertView;
	}

	/**
	 * Model class for our ViewHolder items.
	 * 
	 * @author Harold
	 *
	 */
	static class ViewHolderItem 
	{
		TextView fullName, username, usertype;
		long id;
		Button followButton;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public int getItemViewType(int position) {
		return android.widget.Adapter.IGNORE_ITEM_VIEW_TYPE;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return LIST.size() == 0;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}
}
