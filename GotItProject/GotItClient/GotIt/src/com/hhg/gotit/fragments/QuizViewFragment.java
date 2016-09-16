package com.hhg.gotit.fragments;

import java.util.LinkedList;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.hhg.gotit.R;
import com.hhg.gotit.models.Quiz;

public class QuizViewFragment extends Fragment {
	
	private Quiz quiz;
	private Activity activity;
	
	public QuizViewFragment(Activity activity){
		this.activity = activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.emtpy_dynamic_layout, container, false);
		int index = 0;
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.dynamicLinearLayout);
		for(String question : quiz.getQuestions()){
			TextView t = new TextView(activity.getApplicationContext());
			t.setTextColor(getResources().getColor(R.color.black));
			t.setText(question+" : "+quiz.getReplies().get(index));
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			t.setLayoutParams(params);
			t.setId(index+2);
			index++;
			layout.addView(t);	
		}
		Log.i("QuizView", "Questions found: " + index);
		
		return view;
	}
	
	public void setData(Quiz quiz){
		this.quiz = quiz;
		
	}
}
