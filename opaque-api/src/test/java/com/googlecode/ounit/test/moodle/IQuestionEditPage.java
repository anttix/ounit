package com.googlecode.ounit.test.moodle;

public interface IQuestionEditPage {

	public abstract IQuizPage createQuestion(String name, String remoteid,
			String version);

}