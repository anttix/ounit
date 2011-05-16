package com.googlecode.ounit.opaque;

/**
 * Information about a question.
 * Used by Moodle to determine the maximum score that
 * can be awarded to the student.
 * 
 * @author anttix
 *
 */
public class QuestionInfo {
	String plainmode = "no";
	String title;
	int maxScore;

	public int getMaxScore() {
		return maxScore;
	}
	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}
	public String getPlainmode() {
		return plainmode;
	}
	public void setPlainmode(String plainmode) {
		this.plainmode = plainmode;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
