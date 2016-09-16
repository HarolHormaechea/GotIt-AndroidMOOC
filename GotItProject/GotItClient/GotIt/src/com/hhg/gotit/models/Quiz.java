package com.hhg.gotit.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Conveniency object to store in JPA quizzes with their date, 
 * questions, and replies when applicable.
 * 
 * @author Harold
 *
 */
public class Quiz {
	
	public enum QUIZ_TYPE{DEFAULT}
	public enum REPLY_TYPE{FLOAT, STRING}
	public enum TimeOfDay{
		MEAL_TIME{
		    @Override
		    public String toString() {
		      return "meal time";
		    }},
	    BED_TIME{
	        @Override
	        public String toString() {
	          return "bed time";
	        }}}
	@JsonIgnore
	private static SimpleDateFormat dateFormatter= new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
	
	private int id;
	
	private UserData author;
	
	private String dateTaken;
	

	private ArrayList<String> questions = new ArrayList<String>();
	
	private ArrayList<String> replies = new ArrayList<String>();
	
	private ArrayList<REPLY_TYPE> replyDataTypes = new ArrayList<REPLY_TYPE>();
	
	public Quiz(){}
	
	public Quiz(QUIZ_TYPE type, TimeOfDay time){
		switch(type){
		case DEFAULT:
			setDefaultQuestions(time);
			break;
		}
	}
	
	private void setDefaultQuestions(TimeOfDay time){
		questions.add("What was your sugar level at "+time+"?");
		replyDataTypes.add(REPLY_TYPE.FLOAT);
		questions.add("What time did you check your blood sugar level at "+time+"?");
		replyDataTypes.add(REPLY_TYPE.STRING);
		questions.add("What did you eat at "+time+"?");
		replyDataTypes.add(REPLY_TYPE.STRING);
		questions.add("Did you administer insulin?");
		replyDataTypes.add(REPLY_TYPE.STRING);
		questions.add("Who were you with when you checked/should have checked your blood sugar?");
		replyDataTypes.add(REPLY_TYPE.STRING);
		questions.add("Where were you when you checked/should have checked your blood sugar?");
		replyDataTypes.add(REPLY_TYPE.STRING);
		questions.add("How was your mood when you checked/should have checked your blood sugar?");
		replyDataTypes.add(REPLY_TYPE.STRING);
		questions.add("(1 to 10) How was your stress level when you checked/should have checked your blood sugar?");
		replyDataTypes.add(REPLY_TYPE.STRING);
		questions.add("(1 to 10) How was your energy level when you checked/should have checked your blood sugar?");
		replyDataTypes.add(REPLY_TYPE.STRING);
		questions.add("Were any of these things happening at the time you checked/should have checked your blood sugar: Rushing; feeling tired of diabetes; feeling sick; on the road; really hungry; wanting privacy; busy and didn’t want to stop; without supplies; feeling low; feeling high; having a lot of fun; tired.");
		replyDataTypes.add(REPLY_TYPE.STRING);
		
		
		for(int i = 0; i < questions.size() ;i++){
			replies.add(null);
		}
	}
	
	/**
	 * Utility method to "pack" all the data within a quiz.
	 * 
	 * @param q Quiz to be used as package
	 * @param d date in which the quiz was done
	 * @param quizAuthor quiz author
	 * @return The packaged quiz
	 */
	public static Quiz packageQuiz(Quiz q, Date d, UserData quizAuthor){
		q.setDateTaken(dateFormatter.format(d));
		q.setAuthor(quizAuthor);
		return q;
	}
	
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UserData getAuthor() {
		return author;
	}

	public void setAuthor(UserData author) {
		this.author = author;
	}

	

	public static SimpleDateFormat getDateFormatter() {
		return dateFormatter;
	}

	public static void setDateFormatter(SimpleDateFormat dateFormatter) {
		Quiz.dateFormatter = dateFormatter;
	}

	public String getDateTaken() {
		return dateTaken;
	}

	public void setDateTaken(String dateTaken) {
		this.dateTaken = dateTaken;
	}

	public ArrayList<String> getQuestions() {
		return questions;
	}


	public void setQuestions(ArrayList<String> questions) {
		this.questions = questions;
	}

	public ArrayList<String> getReplies() {
		return replies;
	}

	public void setReplies(ArrayList<String> replies) {
		this.replies = replies;
	}

	public ArrayList<REPLY_TYPE> getReplyDataTypes() {
		return replyDataTypes;
	}

	public void setReplyDataTypes(ArrayList<REPLY_TYPE> replyDataTypes) {
		this.replyDataTypes = replyDataTypes;
	}
	
	
}
