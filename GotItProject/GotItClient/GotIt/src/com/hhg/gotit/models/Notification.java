package com.hhg.gotit.models;


/**
 * Notifications are entities which will store references to the 
 * target user (a follower) and the Quiz which triggers it's
 * creation. This way, we will be able to persist them, and
 * allow the follower to download either a list of notifications
 * available to him, or access the quiz through the ID.
 * 
 * @author Harold
 *
 */
public class Notification {
	
	private int id;
	private UserData notificationTarget;
	private Quiz referencedQuiz;
	private UserData quizAuthor;
	
	
	private boolean read;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Utility method to create a new notification with all the required 
	 * fields. 
	 * 
	 * @param notifiedUser
	 * @param quiz
	 * @return
	 */
	public static Notification notificationBuilder(UserData notifiedUser, Quiz quiz){
		Notification notification = new Notification();
		notification.setNotificationTarget(notifiedUser);
		notification.setReferencedQuiz(quiz);
		notification.setQuizAuthor(quiz.getAuthor());
		return notification;
	}
	
	protected Notification(){}
	
	public UserData getNotificationTarget() {
		return notificationTarget;
	}
	public void setNotificationTarget(UserData notificationTarget) {
		this.notificationTarget = notificationTarget;
	}
	public Quiz getReferencedQuiz() {
		return referencedQuiz;
	}
	public void setReferencedQuiz(Quiz referencedQuizId) {
		this.referencedQuiz = referencedQuizId;
	}
	public UserData getQuizAuthor() {
		return quizAuthor;
	}
	public void setQuizAuthor(UserData quizAuthor) {
		this.quizAuthor = quizAuthor;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	
	
}
