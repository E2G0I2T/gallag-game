package com.taewon.mygallag;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    int characterId, effectId; // characterId - 45번째 줄에서 사용(이미지 받기), effectId - 37, 50번째 줄에서 사용( 37번째 줄에선 reload_sound 를 넘겨 받고, 50번쩨 줄에서 load 한 음원 재생)
    ImageButton startBtn; // activity_start -> 시작버튼
    TextView guideTv; // activity_start -> 가이드 버튼 ( 캐릭터를 선택하세요 )
    MediaPlayer mediaPlayer; // mediaplayer 는 상대적으로 음악과 같이 음원의 길이가 긴 것들을 한 번에 하나씩 재생하는데 용이한 구조
    ImageView imgView[] = new ImageView[6]; // 객체의 배열을 생성, 배열의 크기는 8로 설정
    Integer img_id[] = {R.id.ship_001, R.id.ship_002, R.id.ship_003, R.id.ship_004, R.id.ship_005, R.id.ship_006};
    // 배열 생성 및 ship_001..2. 저장
    Integer img[] = {R.drawable.ship_0000, R.drawable.ship_0001, R.drawable.ship_0002, R.drawable.ship_0003, R.drawable.ship_0004, R.drawable.ship_0005};
    // 배열 생성 및 ship_0000..0001.. 저장
    SoundPool soundPool; // soundPool 은 길이가 짧은 소리에 대해 효과적으로 사용할 수 있는 안드로이드 내장 라이브러리

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mediaPlayer = MediaPlayer.create(this, R.raw.robby_bgm); // 배경음 robby_bgm 저장
        mediaPlayer.setLooping(true); // 반복
        mediaPlayer.start(); // robby_bgm 실행
        soundPool = new SoundPool(5, AudioManager.USE_DEFAULT_STREAM_TYPE, 0); // 효과음
        // maxStreams - 재생할 동시 사운드 스트림의 최대 개수, streamType - 재생할 사운드의 오디오 스트림 유형을 지정, srcQuality - 사운드 재생 품질을 지정(0은 기본 품질)
        // USE_DEFAULT_STREAM_TYPE - 기본 오디오 스트림 유형을 사용 하도록 설정 (시스템이 자동으로 적절한 오디오 스트림을 선택)
        effectId = soundPool.load(this, R.raw.reload_sound, 1); // reload_sound 저장 ( priority - 우선 순위 )
        startBtn = findViewById(R.id.startBtn); // 시작 버튼
        guideTv = findViewById(R.id.guideTv); // 마지막 TextView ( 캐릭터를 선택하세요)
        // findViewById 메서드는 해당 ID에 해당하는 뷰를 찾아서 뷰 객체로 반환하므로, 이후에는 해당 뷰를 조작하거나 이벤트 처리 등을 수행할 수 있습니다.

        for (int i = 0; i < imgView.length; i++) { // 이미지뷰의 길이 만큼 반복 (8)
            imgView[i] = findViewById(img_id[i]); // img_id[0]번째 부터 for문이 종료할 떄까지 가져옴
            int index = i; // 선택한 이미지 번호 알기
            imgView[i].setOnClickListener(view -> {
                characterId = img[index]; // imgView[i]에서 i 를 index 에 담아서 characterId에 img[index]를 넘겨줌
                startBtn.setVisibility(View.VISIBLE); // 버튼 보이게 함
                startBtn.setEnabled(true); // 선택 할 수 있게 하기
                startBtn.setImageResource(characterId); // 버튼에 선택한 이미지 넣기
                guideTv.setVisibility(View.INVISIBLE); // 마지막 TextView 숨기기
                soundPool.play(effectId, 1, 1, 0, 0, 1.0f); // 소리 재생
                // 로드한 음원(reload_sound), 왼쪽 음의 크기, 오른쪽 음의 크기, 우선 순위, 반복 재생 횟수, 재생 속도
            });
        }
        init(); // 58번 method
    }
    private void init(){ // GONE = 뷰를 숨기고 공간도 제거, INVISIBLE = 뷰를 숨기지만 공간은 유지
        findViewById(R.id.startBtn).setVisibility(View.GONE); // 버튼 위치는 남겨 두고 숨기기
        findViewById(R.id.startBtn).setEnabled(false); // 선택 안되게 하기
        findViewById(R.id.startBtn).setOnClickListener(v -> { // startBtn 클릭 시
            Intent intent = new Intent(StartActivity.this, MainActivity.class); // 인텐트문으로 MainActivity 로 이동
            intent.putExtra("character", characterId); // 선택한 이미지 넘기기 ( 선택한 비행기 이미지)
            startActivity(intent); // 액티비티 실행
            finish(); // 전 액티비티 종료
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() { // 액티비티 소멸 직전 호출 mediaPlayer 가 살아 있으면 리소스 소멸 시킨다.
        super.onDestroy();
        if(mediaPlayer != null){ // onCreate 에 있는 mediaPlayer 의 값이 null 이 아닐 시
            mediaPlayer.release(); // mediaPlayer 를 소멸 시키는 작업
            mediaPlayer = null; // null 넣어주기
        }
    }
}
