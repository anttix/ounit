package com.googlecode.ounit.test.moodle;

public interface IQuizPage {

	public abstract void createQuestions();

	public abstract IQuestionEditPage newOpaqueQuestion();

	public abstract void doPreview();

	public abstract void navigate(int i);

	public abstract int getGrade();

}