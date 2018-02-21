package com.nbrown.mathaddict.action;

import com.nbrown.mathaddict.Keys;
import com.nigelbrown.fluxion.Flux;
import com.nigelbrown.fluxion.FluxActionCreator;

import javax.inject.Inject;

/**
 * Created by Nigel.Brown on 8/22/2017.
 */
public class ActionCreator extends FluxActionCreator implements Actions {
	@Inject
	public ActionCreator(Flux flux) {
		super(flux);
	}

	@Override
	public void getNextProblem(String answer) {
		emitAction(GET_NEXT_PROBLEM, Keys.ANSWER, answer);
	}

	@Override
	public void startQuiz() {
		emitAction(START_QUIZ);
	}

	@Override
	public void reset() {
			emitAction(RESET_QUIZ);
	}

	@Override
	public void submitQuiz(String finalAnswer) {
			emitAction(SUBMIT_QUIZ,Keys.ANSWER,finalAnswer);
	}
}
