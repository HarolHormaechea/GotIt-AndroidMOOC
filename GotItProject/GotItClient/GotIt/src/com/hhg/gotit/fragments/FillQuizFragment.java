package com.hhg.gotit.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.hhg.gotit.MainActivity;
import com.hhg.gotit.R;
import com.hhg.gotit.models.Quiz;

public class FillQuizFragment extends Fragment {
	private MainActivity activity;
	private Quiz quiz;
	private EditText[] replyBoxes;
	
	public FillQuizFragment(MainActivity activity){
		this.activity = activity;
	}
	
	/**
	 * Sets the quiz which will be shown to the user.
	 * 
	 * @param q
	 */
	public void setData(Quiz q){
		this.quiz = q;
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.emtpy_dynamic_layout, container, false);
	    LinearLayout layout = (LinearLayout) view.findViewById(R.id.dynamicLinearLayout);
	    if(layout == null){
	    	Log.e(getClass().getName(), "Null layout!");
	    }
	    //We create our editText array
	    replyBoxes = new EditText[quiz.getQuestions().size()];
	    int index = 0;
	    for(String question : quiz.getQuestions()){
	    	//For each question, we will add it's text, and an EditText object
	    	//for the reply. 
	    	TextView t = new TextView(activity.getApplicationContext());
			t.setTextColor(getResources().getColor(R.color.black));
			t.setText(question);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			t.setLayoutParams(params);
			t.setId(index+1);
				
			EditText et = new EditText(activity.getApplicationContext());
			et.setTextColor(getResources().getColor(R.color.black));
			int inputType;
			switch(quiz.getReplyDataTypes().get(index)){
			case FLOAT:
				inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL;
				break;
			case STRING:
				inputType = InputType.TYPE_CLASS_TEXT;
				break;
			default:
				inputType = InputType.TYPE_CLASS_TEXT;
				break;
			
			}
			et.setInputType(inputType);
			et.setLayoutParams(params);
			t.setId(index+1001);
			replyBoxes[index] = et;
			layout.addView(t);
			layout.addView(et);
			index++;
	    }
	    
	    //We have to finally create a submit button so our user can actually
	    //-submit- whatever responses he has chosen to put in our quiz.
	    Button but = new Button(activity.getApplicationContext());
	    but.setText(R.string.fill_quiz_send);
	    but.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				packQuiz();
				activity.getOpsManager().postQuiz(quiz);
			}
	    	
	    });
	    layout.addView(but);
		return view;
	}
	
	private void packQuiz(){
		int i = 0;
		for(EditText e : replyBoxes){
			Log.i(getClass().getName(),
					"Adding reply "+e.getText().toString());
			quiz.getReplies().set(i,e.getText().toString());
			i++;
		}
		Log.i(getClass().getName(),
				"Packaged quiz for upload with first reply: "+quiz.getReplies().get(0));
	}
}
