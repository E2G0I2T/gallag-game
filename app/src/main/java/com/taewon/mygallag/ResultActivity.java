package com.taewon.mygallag;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over_dialog);
        init(); // 19번째 줄 호출
    }

    private void init(){
        findViewById(R.id.goMainBtn).setOnClickListener(new View.OnClickListener() { // goMainBtn(처음으로) 버튼 클릭 시
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, StartActivity.class); // StartActivity.class 로 이동
                startActivity(intent); // 액티비티 실행
                finish(); // 전 액티비티 종료
            }
        });
        ((TextView)findViewById(R.id.userFinalScoreText)).setText(getIntent().getIntExtra("score", 0)+"");
        // SpaceInvadersView 의 engGame 메서드에서 넘겨준 score 값을 userFinalScorText에 넘겨준다 문자열로 넘겨준다
        // getIntent().getIntExtra("score", 0)을 호출하여 "score"라는 이름의 Extra 값을 가져 온다, 해당 Extra 값이 존재 하지 않을 경우 기본 값으로 0을 사용 한다.
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

}
