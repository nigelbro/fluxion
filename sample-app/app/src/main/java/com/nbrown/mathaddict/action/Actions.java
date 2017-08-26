package com.nbrown.mathaddict.action;

/**
 * Created by Nigel.Brown on 8/22/2017.
 */
public interface Actions {
	String GET_NEXT_PROBLEM = "getNextProblem";
	String START_QUIZ = "startQuiz";
	String RESET_QUIZ = "resetQuiz";
	String SUBMIT_QUIZ = "submitQuiz";
	void getNextProblem(String answer);
	void startQuiz();
	void reset();
	void submitQuiz(String finalAnswer);
}
