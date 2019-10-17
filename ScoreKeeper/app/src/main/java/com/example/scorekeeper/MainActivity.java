
package com.example.scorekeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    int teamAScore = 0;
    int teamBScore = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void displayA(int teamAScore){
        TextView scoreView = (TextView) findViewById(R.id.team_a_score);
        scoreView.setText(String.valueOf(teamAScore));
    }

    public void displayB(int teamBScore){
        TextView scoreView = (TextView) findViewById(R.id.team_b_score);
        scoreView.setText(String.valueOf(teamAScore));
    }

    public void add6ForA(View v){
        teamAScore += 6;
        displayA(teamAScore);
    }

    public void add3ForA(View v){
        teamAScore += 3;
        displayA(teamAScore);
    }

    public void add2ForA(View v){
        teamAScore += 2;
        displayA(teamAScore);
    }
    public void add6ForB(View v){
        teamAScore += 6;
        displayA(teamAScore);
    }

    public void add3ForB(View v){
        teamAScore += 3;
        displayA(teamAScore);
    }

    public void add2ForB(View v){
        teamAScore += 2;
        displayA(teamAScore);
    }

    public void reSet(View v){
        teamAScore = 0;
        teamBScore = 0;
        displayA(teamAScore);
        displayB(teamBScore);
    }


}
