package com.nbrown.mathaddict;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nbrown.mathaddict.action.ActionCreator;
import com.nbrown.mathaddict.store.MathStore;
import com.nigelbrown.fluxion.Annotation.React;
import com.nigelbrown.fluxion.Reaction;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nbrown.mathaddict.store.Reactions.SHOW_FIRST_PROBLEM;
import static com.nbrown.mathaddict.store.Reactions.SHOW_NEXT_PROBLEM;
import static com.nbrown.mathaddict.store.Reactions.SHOW_QUIZ_RESULTS;

public class MainActivity extends AppCompatActivity {
	private static final String RESET = "Reset";
	private static final String NEXT = "Next";
	private static final String SUBMIT = "Submit";
	@BindView(R.id.equation) TextView equation;
	@BindView(R.id.answer) EditText answer;
	@BindView(R.id.submit) Button actionButton;
	@Inject ActionCreator actionCreator;
	@Inject MathStore mathStore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		App.getInstance().getComponent().inject(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		actionCreator.startQuiz();
		answer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					actionCreator.getNextProblem(answer.getText().toString());
					answer.getText().clear();
					return true;
				}
				return false;
			}
		});
	}

	@React(reactionType = SHOW_FIRST_PROBLEM)
	public void showFirstProblem(Reaction reaction) {
		answer.getText().clear();
		answer.setVisibility(View.VISIBLE);
		actionButton.setText(NEXT);
		equation.setText((String)reaction.get(Keys.EQUATION));
	}

	@React(reactionType = SHOW_NEXT_PROBLEM)
	public void showNextProblem(Reaction reaction) {
		boolean isFinalProblem = reaction.get(Keys.IS_FINAL_PROBLEM);
		if(isFinalProblem) { actionButton.setText(SUBMIT); }
		equation.setText((String)reaction.get(Keys.EQUATION));
	}

	@React(reactionType = SHOW_QUIZ_RESULTS)
	public void showScoreResults(Reaction reaction) {
		actionButton.setText(RESET);
		answer.setVisibility(View.GONE);
		equation.setText((String)reaction.get(Keys.QUIZ_RESULTS));
	}

	@OnClick(R.id.submit)
	public void submit(Button button) {
		switch(button.getText().toString()) {
			case NEXT:
				actionCreator.getNextProblem(answer.getText().toString());
				answer.getText().clear();
				break;
			case SUBMIT:
				actionCreator.submitQuiz(answer.getText().toString());
				break;
			case RESET:
				actionCreator.reset();
				break;
		}
	}
}
