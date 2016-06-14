package com.kludge.wakemeup;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

/*
 * Created by Yu Peng on 14/6/2016.
 */

public class MathGameActivity extends Activity {

    int correctAnswer;
    TextView textOperandA,textOperandB,textOperator,textScore,textNumCorrect;
    EditText inputAnswer;
    Button buttonSubmit;

    int currentScore = 0;
    int level = 2;
    int numCorrect = 0;
    int totalCorrect = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_game);

        textOperandA = (TextView) findViewById(R.id.game_text_operandA);
        textOperandB = (TextView) findViewById(R.id.game_text_operandB);
        textOperator = (TextView) findViewById(R.id.game_text_operator);
        textNumCorrect = (TextView) findViewById(R.id.game_text_numCorrect);
        textScore = (TextView) findViewById(R.id.game_text_score);
        inputAnswer = (EditText) findViewById(R.id.game_editText_inputAnswer);
        buttonSubmit = (Button) findViewById(R.id.game_button_submit);

        getIntent().getLongExtra("alarmId", -1);
        // get AlarmDetails from AlarmLab
        // get level from AlarmDetails
        // get totalCorrect from AlarmDetails

        textNumCorrect.setText("0");

        buttonSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                updateScoreAndLevel(inputAnswer.getText().toString());
            }
        });

        setQuestion();
    }

    private void setQuestion(){

        Random r = new Random();
        int numberRange = (int) Math.pow(10, level);
        int operandA = r.nextInt(numberRange);
        int operandB = r.nextInt(numberRange);
        operandA++; //ensure non-zero
        operandB++;

        char [] operatorArray = {'+', '-', '*', '/'};
        char operator = operatorArray[r.nextInt(4)];

        switch(operator){
            case '+':
                correctAnswer = operandA + operandB;
                break;
            case '-':
                correctAnswer = operandA - operandB;
                break;
            case '*':
                correctAnswer = operandA * operandB;
                break;
            case '/':
                correctAnswer = operandA;
                operandA = operandA * operandB;
                break;
        }

        textOperandA.setText("" + operandA);
        textOperandB.setText("" + operandB);
        textOperator.setText("" + operator);

    }

    private void updateScoreAndLevel(String answerGiven){

        if(isCorrect(Integer.parseInt(answerGiven))){
            numCorrect++;
            setQuestion();
            inputAnswer.setText("");
        }

        else{
            currentScore = 0;
            numCorrect = 0;
        }

        textNumCorrect.setText("" + numCorrect + "/" + totalCorrect);

        if (numCorrect == totalCorrect ){
            finish();
        }

    }

    private boolean isCorrect(int answerGiven){
        if (answerGiven == correctAnswer){
            Toast.makeText(getApplicationContext(),"Well done!", Toast.LENGTH_LONG).show();
            return true;
        }
        Toast.makeText(getApplicationContext(),"Sorry", Toast.LENGTH_LONG).show();
        return false;
    }


}
