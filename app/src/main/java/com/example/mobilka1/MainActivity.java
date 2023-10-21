package com.example.mobilka1;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_CURRENT_INDEX = "currentIndex";
    public static final String KEY_EXTRA_ANSWER = "com.example.mobilka1.quiz.correctAnswer";

    private static final int REQUEST_CODE_PROMPT = 0;

    public Intent intent;


    private Button trueButton;
    private Button falseButton;
    private Button nextButton;
    private Button podpowiedzButton;
    private TextView questionTextView;
    private int currentIndex = 0;

    private static final String TAG = "MyActivity";

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Wywołanie onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Wywołanie onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Wywołanie onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Wywołanie onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Wywołanie onResume");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Wywołana została metoda: onSaveInstanceState");
        outState.putInt(KEY_CURRENT_INDEX, currentIndex);
    }

    private boolean answerWasShown;
    private int correctAnswersCount = 0;

    private int lastAnswer = 0;

    private int pom = 0;

    private int questionId;
    private boolean isTrue;


    private Question[] questions = new Question[] {
            new Question(R.string.q_activity, true),
            new Question(R.string.q_version, false),
            new Question(R.string.q_listener, true),
            new Question(R.string.q_resources, true),
            new Question(R.string.q_find_resources, false)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Wywołana została metoda cyklu życia: onCreate");
        setContentView(R.layout.activity_main);

        questionTextView = findViewById(R.id.question_text_view);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        nextButton = findViewById(R.id.next_button);
        podpowiedzButton = findViewById(R.id.podpowiedz_button);

        if(savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt(KEY_CURRENT_INDEX);
        }

        trueButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkAnswerCorrectness(true);
            }
        });

        falseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkAnswerCorrectness(false);
            }
        });

        podpowiedzButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PromptActivity.class);
                boolean correctAnswer = questions[currentIndex].isTrue();
                intent.putExtra(KEY_EXTRA_ANSWER, correctAnswer);
                startActivityForResult(intent, REQUEST_CODE_PROMPT);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                currentIndex = (currentIndex + 1) % questions.length;
                answerWasShown = false;
                try {
                    setNextQuestion();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        try {
            setNextQuestion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public class Question {
        private int questionId;
        private boolean isTrue;

        public Question(int questionId, boolean isTrue) {
            this.questionId = questionId;
            this.isTrue = isTrue;
        }

        public int getQuestionId() {
            return questionId;
        }

        public boolean isTrue() {
            return isTrue;
        }
    }



    private void checkAnswerCorrectness(boolean userAnswer) {
        int currentQuestionIndex = GetCurretIndex();
        boolean correctAnswer = questions[currentQuestionIndex].isTrue();
        int messageId = 0;
        if (answerWasShown) {
            messageId = R.string.answer_was_shown;
        } else {
            if (userAnswer == correctAnswer)
            {
                messageId = R.string.correct_answer;
                lastAnswer = 1;
            }
            else
            {
                messageId = R.string.incorrect_answer;
                lastAnswer = 0;
            }
        }
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
    }

    private void setNextQuestion() throws InterruptedException {
        //correctAnswersCount
        if(lastAnswer == 1)
        {
            correctAnswersCount++;
            lastAnswer = 0;
        }

        if(currentIndex == 0 && pom == 1)
        {
            Thread.sleep(1000);
            Toast.makeText(this, "Odpowiedziałeś na: " + Integer.toString(correctAnswersCount) + " pytania.", Toast.LENGTH_SHORT).show();
            correctAnswersCount = 0;
            pom = 0;
        }

        pom = 1;

        questionTextView.setText(questions[currentIndex].getQuestionId());
        answerWasShown = false;

    }

    private int GetCurretIndex ()
    {
        return currentIndex;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        if (requestCode == REQUEST_CODE_PROMPT) {
            if (data == null)
                return;
            answerWasShown = data.getBooleanExtra(PromptActivity.KEY_EXTRA_ANSWER_SHOWN, false);
        }
    }


}



