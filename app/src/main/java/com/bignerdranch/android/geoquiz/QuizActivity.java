package com.bignerdranch.android.geoquiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Chapter 1 Challenges:
 * Customize the Toast                  X
 * Chapter 2 Challenges:
 * Add a Listener to the TextView       X
 * Add a Previous Button                X
 * From Button to ImageButton           X
 * Chapter 3 Challenges:
 * Preventing Repeat Answers            X
 * Grading Quiz                         X
 * Chapter 4 Challenges
 * Exploring the Layout Inspector       X
 * Exploring the Allocation Tracking    X
 **/


public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;
    //Store answered question indexes to list for testing and disabling answer buttons.
    private ArrayList<Integer> answerHistory = new ArrayList<>();
    //Counter for the amount of correct answers.
    private int correctAnswers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        //Save state of application on rotation or onPause/onResume. State will remain uintil
        //back button is used or application is killed by user.
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        /*
        Clicking the question text will advance to the next question.
        Chapter 2 Challenge: Add a Listener to the TextView
         */
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }

        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        /*
        Chapter 2 Challenge: Add a Previous Button.
        */
        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                //Ensure we do not get index out of bounds errors when cycling.
                if (mCurrentIndex < 0 ) {
                    Toast.makeText(QuizActivity.this, R.string.beginning_question, Toast.LENGTH_SHORT).show();
                    mCurrentIndex = 0;
                    mPrevButton.setEnabled(false);
                } else {
                    updateQuestion();
                    hasBeenAnswered();
                    mNextButton.setEnabled(true);
                }
            }
        });

        /*
        Chapter 2 Challenge: From Button to ImageButton.
        Converted previous/next buttons to ImageButtons.
         */
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                //Stop next cycle once we reach the end of the questionBank array.
                if(mCurrentIndex == 0){
                    mCurrentIndex = mQuestionBank.length - 1;
                    mNextButton.setEnabled(false);
                    //If we reach the end without answering all questions inform the user they have
                    //unanswered questions.
                    if(answerHistory.size() != mQuestionBank.length){
                        Toast.makeText(QuizActivity.this, R.string.unanswered_questions, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    updateQuestion();
                    hasBeenAnswered();
                    mPrevButton.setEnabled(true);
                }
            }
        });

        updateQuestion();
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResID = 0;

        //Depending on the answer chosen select the correct string to display & add 1 to correctAnswers
        //counter if the answer was correct. This is used to calculate the users score.
        if (userPressedTrue == answerIsTrue) {
            messageResID = R.string.correct_toast;
            correctAnswers ++;
        } else {
            messageResID = R.string.incorrect_toast;
        }

        /*
        Display 'Correct' or 'Incorrect' to the user witha  toast.
        Challenge 1: Customize Toast. Set toast location to TOP using
        Toast.setGravity.
        */
        Toast answerToast = Toast.makeText(this, messageResID, Toast.LENGTH_SHORT);
        answerToast.setGravity(Gravity.TOP, 0,250);
        answerToast.show();

        //Immediate effect of disabling buttons.
        toggleTFButtons(false);

        answerHistory.add(mCurrentIndex);

        if (answerHistory.size() == mQuestionBank.length) {
            scoreQuiz();
        }
    }

    /*
    Chapter 3 Challenge: Preventing Repeat Answers.
     */
    private void hasBeenAnswered() {
        //Test if the current question has been answered already, if so Disable answer buttons
        //If not enable answer buttons.
        if (answerHistory.contains(mCurrentIndex)){
            toggleTFButtons(false);
        } else {
            toggleTFButtons(true);
        }
    }

    /*
    Chapter 3 Challenge: Grading Quiz
     */
    private void scoreQuiz() {
        //Calculate the grade of the quiz once complete and display it in a Toast for the user.
        float gradeTotal = ((float)correctAnswers / mQuestionBank.length) * 100;
        Toast.makeText(this, String.format(getString(R.string.quiz_score) + " %.2f%%", gradeTotal), Toast.LENGTH_LONG).show();
    }

    //Simple method to enable disable answer buttons as needed by passing boolean.
    private void toggleTFButtons(boolean state){
        mTrueButton.setEnabled(state);
        mFalseButton.setEnabled(state);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}
