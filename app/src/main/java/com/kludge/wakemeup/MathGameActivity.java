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
    int level = 1;
    int numCorrect = 0;
    int totalCorrect;
    int difficulty;
    int add_digits; // no. of digits of operands in math qn
    int multiply_digits; // no. of digits of operands in math qn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_game);

        totalCorrect = getIntent().getIntExtra("mathqns", 1);

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
        difficulty = 3;

        switch(difficulty){
            case 1:
                add_digits = 1;
                break;
            case 2:
                add_digits = 2;
                multiply_digits = 1;
                break;
            case 3:
                add_digits = 3;
                multiply_digits = 1;
                break;
            case 4:
                add_digits = 4;
                multiply_digits = 2;
                break;
        }

        textNumCorrect.setText("" + numCorrect + "/" + totalCorrect);

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
        int numberRange;
        int operandA = 1;
        int operandB = 1;

        char [] operatorArray = {'+', '-', '*', '/'};

        char operator = operatorArray[r.nextInt(4)];

        if (multiply_digits == 0){
            operator = operatorArray[r.nextInt(2)];
        }

        switch(operator){
            case '+':
                numberRange = (int) Math.pow(10, add_digits);
                operandA = r.nextInt(numberRange);
                operandB = r.nextInt(numberRange);
                correctAnswer = operandA + operandB;
                break;
            case '-':
                numberRange = (int) Math.pow(10, add_digits);
                operandA = r.nextInt(numberRange);
                operandB = r.nextInt(numberRange);
                correctAnswer = operandA - operandB;
                break;
            case '*':
                numberRange = (int) Math.pow(10,multiply_digits);
                operandA = r.nextInt(numberRange);
                operandB = r.nextInt(numberRange);
                correctAnswer = operandA * operandB;
                break;
            case '/':
                numberRange = (int) Math.pow(10,multiply_digits);
                operandA = r.nextInt(numberRange);
                operandB = r.nextInt(numberRange);
                operandB++; // operandB can't be zero
                correctAnswer = operandA;
                operandA = operandA * operandB;
                break;
        }

        textOperandA.setText("" + operandA);
        textOperandB.setText("" + operandB);
        textOperator.setText("" + operator);

    }

    private void updateScoreAndLevel(String answerGiven){

        int answer = Integer.MIN_VALUE;

        try {
            answer = Integer.parseInt(answerGiven);
        } catch(NumberFormatException e){
        }

        if(isCorrect(answer)){
            numCorrect++;
            setQuestion();
            inputAnswer.setText("");
        }

        else{
            currentScore = 0;
            numCorrect = 0;
            inputAnswer.setText("");
        }

        textNumCorrect.setText("" + numCorrect + "/" + totalCorrect);

        if (numCorrect == totalCorrect ){
            setResult(RESULT_OK);
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
