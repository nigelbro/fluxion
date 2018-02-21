package com.nbrown.mathaddict.store;

import com.nbrown.mathaddict.App;
import com.nbrown.mathaddict.Keys;
import com.nbrown.mathaddict.R;
import com.nbrown.mathaddict.action.Actions;
import com.nigelbrown.fluxion.Annotation.Action;
import com.nigelbrown.fluxion.Annotation.Store;
import com.nigelbrown.fluxion.Flux;
import com.nigelbrown.fluxion.FluxAction;
import com.nigelbrown.fluxion.FluxStore;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import static com.nbrown.mathaddict.action.Actions.GET_NEXT_PROBLEM;
import static com.nbrown.mathaddict.action.Actions.RESET_QUIZ;
import static com.nbrown.mathaddict.action.Actions.START_QUIZ;
import static com.nbrown.mathaddict.action.Actions.SUBMIT_QUIZ;
import static com.nbrown.mathaddict.store.Reactions.SHOW_FIRST_PROBLEM;
import static com.nbrown.mathaddict.store.Reactions.SHOW_NEXT_PROBLEM;
import static com.nbrown.mathaddict.store.Reactions.SHOW_QUIZ_RESULTS;

/**
 * Created by Nigel.Brown on 8/22/2017.
 */
@Store
public class MathStore extends FluxStore {
	int currentPosition = 0;
	private LinkedHashMap<String, String> solutionMap = new LinkedHashMap<>();
	private LinkedHashMap<String, String> userAnswerMap = new LinkedHashMap<>();

	@Inject
	public MathStore(Flux flux) {
		super(flux);
		registerActionSubscriber(this);
		buildAnswerKey();
	}

	@Action(actionType = START_QUIZ)
	public void startQuiz(FluxAction action) {
		List<String> equationList = getEquationList();
		emitReaction(SHOW_FIRST_PROBLEM, Keys.EQUATION, equationList.get(currentPosition));
	}

	@Action(actionType = GET_NEXT_PROBLEM)
	public void showNextProblem(FluxAction action) {
		List<String> equationList = getEquationList();
		userAnswerMap.put(equationList.get(currentPosition), (String)action.get(Keys.ANSWER));
		currentPosition++;
		int position = currentPosition;
		boolean isFinalProblem = position + 1 == equationList.size();
		emitReaction(SHOW_NEXT_PROBLEM, Keys.EQUATION, equationList.get(currentPosition), Keys.IS_FINAL_PROBLEM, isFinalProblem);
	}

	@Action(actionType = SUBMIT_QUIZ)
	public void submitQuiz(FluxAction action) throws IllegalAccessException,
			InvocationTargetException {
		List<String> equationList = getEquationList();
		userAnswerMap.put(equationList.get(currentPosition), (String)action.get(Keys.ANSWER));
		String results = gradeQuiz();
		emitReaction(SHOW_QUIZ_RESULTS, Keys.QUIZ_RESULTS, results);
		currentPosition = 0;
		userAnswerMap.clear();
	}

	@Action(actionType = RESET_QUIZ)
	public void resetQuiz(FluxAction action) {
		List<String> equationList = getEquationList();
		emitReaction(SHOW_FIRST_PROBLEM, Keys.EQUATION, equationList.get(currentPosition));
	}

	private String gradeQuiz() {
		int numberCorrect = 0;
		int numberIncorrect = 0;
		StringBuilder results = new StringBuilder();
		for(String problem : userAnswerMap.keySet()) {
			String correctAnswer = solutionMap.get(problem);
			String userAnswer = userAnswerMap.get(problem);
			if(correctAnswer.equals(userAnswer)) {
				numberCorrect++;
			}else {
				numberIncorrect++;
			}
		}
		return results.append("Quiz Results\n\n").append("Correct: ").append(numberCorrect).append("\n\n").append("Incorrect: ").append(numberIncorrect).toString();
	}

	private void buildAnswerKey() {
		List<String> equationList = getEquationList();
		for(String problem : equationList) {
			solutionMap.put(problem, calculate(problem));
		}
	}

	public List<String> getEquationList() {
		return Arrays.asList(App.getInstance().getResources().getStringArray(R.array.level_one_addition));
	}

	private String calculate(String equation) {
		String[] equationParts = equation.split(Pattern.quote("="));
		String[] problemParts = equationParts[0].split(Pattern.quote("+"));
		int total = 0;
		for(String number : problemParts) {
			total += Integer.parseInt(number.replaceAll("\\s", ""));
		}
		return String.valueOf(total);
	}
}
