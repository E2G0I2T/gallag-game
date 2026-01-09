package com.taewon.mygallag;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Intent userIntent;
    ArrayList<Integer> bgMusicList; // 배경 음악을 넣을 ArrayList
    public static SoundPool effectSound;
    public static float effectVolumn;
    ImageButton specialShotBtn;
    public static ImageButton fireBtn, reloadBtn;
    JoystickView joyStick;
    public static TextView scoreTv;
    LinearLayout gameFrame;
    ImageView pauseBtn;
    public static LinearLayout lifeFrame;
    SpaceInvadersView spaceInvadersView;
    public static MediaPlayer bgMusic;
    int bgMusicIndex;
    public static TextView bulletCount;
    private static ArrayList<Integer> effectSoundList;
    public static final int PLAYER_SHOT = 0;
    public static final int PLAYER_HURT = 1;
    public static final int PLAYER_RELOAD = 2;
    public static final int PLAYER_GET_ITEM = 3;

    public static final String Tag = "bgMusicIndex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userIntent = getIntent(); // startActivity 에서 보낸 characterId를 userIntent에 입력
        bgMusicIndex = 0; // bgMusincIndex 를 0으로 초기화
        bgMusicList = new ArrayList<>(); // 배경 음악 넣을 arrayList 생성
        bgMusicList.add(R.raw.main_game_bgm1);
        bgMusicList.add(R.raw.main_game_bgm2);
        bgMusicList.add(R.raw.main_game_bgm3); // bgMusicList에 배경 음악 입력


        effectSound = new SoundPool(5, AudioManager.USE_DEFAULT_STREAM_TYPE, 0);
        // maxStreams: 재생할 사운드 스트림의 최대 개수 : 5
        // AudioManager.USE_DEFAULT_STREAM_TYPE : 기본 오디오 스트림 유형을 사용하도록 설정.
        // srcQuality: 사운드 재생 품질을 지정. 0 -> 기본 품질
        effectVolumn = 1; // effectVolumn 을 1로 초기화 (0 : 음소거 상태, 1 : 소리가 들리는 상태)

        specialShotBtn = findViewById(R.id.specialShotBtn);
        joyStick = findViewById(R.id.joyStick);
        scoreTv = findViewById(R.id.score);
        fireBtn = findViewById(R.id.fireBtn);
        reloadBtn = findViewById(R.id.reloadBtn);
        gameFrame = findViewById(R.id.gameFrame); // 첫번째 LinearLayout
        pauseBtn = findViewById(R.id.pauseBtn);
        lifeFrame = findViewById(R.id.lifeFrame);
        // findViewById 메서드는 해당 ID에 해당하는 뷰를 찾아서 뷰 객체로 반환하므로, 이후에는 해당 뷰를 조작하거나 이벤트 처리 등을 수행할 수 있습니다.

        init(); // init() 호출
        setBtnBehavior(); //조이스틱 작동 함수
    }

    @Override
    protected void onResume() { // 활동이 시작(재시작 포함)될 때 호출
        super.onResume();
        bgMusic.start(); // 배경 음악 시작
        spaceInvadersView.resume();
    }

    private void init(){ // 게임 뷰 초기화, 디스플레이 설정, SpaceInvadersView 배경음악 설정, 음향 효과 초기화
        Display display = getWindowManager().getDefaultDisplay();
        // getWindowManager().getDefaultDisplay() 를 통해 view 의 display 를 얻어온다.
        Point size = new Point(); // Point는 x, y 좌표를 나타내는 클래스
        display.getSize(size); // 디스플레이의 실제 크기를 Point 클래스의 size 객체에 저장

        spaceInvadersView = new SpaceInvadersView(this, userIntent.getIntExtra("character", R.drawable.ship_0000), size.x, size.y);
        // spaceInvadersView에 characterId 값을 넣고 (없을 경우 R.drawable.ship_0000 가 입력됨)
        // 디스플레이의 가로, 세로 크기를 받아 spaceInvadersView 에 저장

        gameFrame.addView(spaceInvadersView); // gameFrame 에 받아온 아이템 넣기

        // 음악 바꾸기
        changeBgMusic(); // bgMusic.setOnCompletionListener 실행되려면 처음 1회 음악 파일 재생이 끝나야 하므로 최초 음악 재생을 위해 선언
        bgMusic.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // 음악이 끝나면
            @Override
            public void onCompletion(MediaPlayer mp) {
                changeBgMusic(); // changeBgMusic()을 호출해서 음악 변경
            }
        });

        bulletCount = findViewById(R.id.bulletCount); // 총알 개수
        bulletCount.setText(spaceInvadersView.getPlayer().getBulletsCount() + "/30"); // 총알 개수를 가져와서 (총알 개수/30) 으로 설정
        scoreTv.setText(Integer.toString(spaceInvadersView.getScore())); // spaceInvadersView 에서 스코어를 가져와서 scoreTv에 세팅

        effectSoundList = new ArrayList<>(); // 효과음 넣을 ArrayList 생성
        effectSoundList.add(PLAYER_SHOT, effectSound.load(MainActivity.this, R.raw.player_shot_sound, 1));
        effectSoundList.add(PLAYER_HURT, effectSound.load(MainActivity.this, R.raw.player_hurt_sound, 1));
        effectSoundList.add(PLAYER_RELOAD, effectSound.load(MainActivity.this, R.raw.reload_sound, 1));
        effectSoundList.add(PLAYER_GET_ITEM, effectSound.load(MainActivity.this, R.raw.player_get_item_sound, 1));
        // effectSoundList 라는 ArrayList에 효과음을 로드
        // priority : 1 -> 재생 우선 순위를 나타냄
        bgMusic.start(); // 음악이 바뀌면서 재생
    }

    private void changeBgMusic(){ // 음악 파일 변경
        bgMusic = MediaPlayer.create(this, bgMusicList.get(bgMusicIndex)); // bgMusicList 에서 bgMusicIndex 에 입력된 값 만큼의 순서에 있는 곡을 가져옴
        bgMusic.start(); // 음악 파일 재생
        bgMusic.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // 음악이 끝나면
            @Override
            public void onCompletion(MediaPlayer mp) {
                bgMusic.release();
                changeBgMusic(); // changeBgMusic()을 호출해서 음악 변경
            }
        });
        bgMusicIndex++; // 음악 바꾸기 위해 bgMusicIndex 증가
        bgMusicIndex = bgMusicIndex % bgMusicList.size(); // 음악 개수 만큼만 바뀌게 하기 위해
    }

    @Override
    protected void onPause() { // 일시 정지 시
        super.onPause();
        bgMusic.pause();
        spaceInvadersView.pause();
    }

    public static void effectSound(int flag){ // 사운드 효과 생성
        effectSound.play(effectSoundList.get(flag), effectVolumn, effectVolumn, 0, 0, 1.0f);
        // Soundpool 실행
    }

    private void setBtnBehavior(){ // 조이스틱, 발사 버튼, 재장전 버튼, 일시 정지 버튼 등의 동작 설정
        joyStick.setAutoReCenterButton(true); // 조이스틱을 터치하지 않을 때 조이스틱을 자동으로 중앙으로 이동
        joyStick.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.d("keycode", Integer.toString((i)));
                return false;
            }
        });
        // 조이스틱 이동방향으로 비행기 이동하게 한다
        joyStick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                Log.d("angle", Integer.toString(angle));
                Log.d("force", Integer.toString(strength));
                if(angle > 67.5 && angle < 112.5){
                    // 위
                    spaceInvadersView.getPlayer().moveUp(strength / 10);
                    spaceInvadersView.getPlayer().resetDx();
                }else if(angle > 247.5 && angle < 292.5){
                    // 아래
                    spaceInvadersView.getPlayer().moveDown(strength / 10);
                    spaceInvadersView.getPlayer().resetDx();
                }else if(angle > 112.5 && angle < 157.5){
                    // 왼쪽 대각선 위
                    spaceInvadersView.getPlayer().moveUp(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveLeft(strength / 10 * 0.5);
                }else if(angle > 157.5 && angle < 202.5){
                    // 왼쪽
                    spaceInvadersView.getPlayer().moveLeft(strength / 10);
                    spaceInvadersView.getPlayer().resetDy();
                }else if(angle > 202.5 && angle < 247.5) {
                    // 왼쪽 대각선 아래
                    spaceInvadersView.getPlayer().moveLeft(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveDown(strength / 10 * 0.5);
                }else if(angle > 22.5 && angle < 67.5) {
                    // 오른쪽 대각선 위
                    spaceInvadersView.getPlayer().moveUp(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveRight(strength / 10 * 0.5);
                }else if(angle > 337.5 || angle < 22.5) {
                    // 오른쪽
                    spaceInvadersView.getPlayer().moveRight(strength / 10);
                    spaceInvadersView.getPlayer().resetDy();
                }else if(angle > 292.5 && angle < 337.5) {
                    // 오른쪽 아래
                    spaceInvadersView.getPlayer().moveRight(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveDown(strength / 10 * 0.5);
                }
            }
        });

        // 총알 버튼 눌렀을 때
        fireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spaceInvadersView.getPlayer().fire();
            }
        });

        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spaceInvadersView.getPlayer().reloadBullets();
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spaceInvadersView.pause(); // spaceInvadersView 중지함
                PauseDialog pauseDialog = new PauseDialog(MainActivity.this); // MainActivity 의 인스턴스를 받아 PauseDialog를 생성
                pauseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // PaauseDialog 닫힐 때 호출되는 이벤트를 처리
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        spaceInvadersView.resume(); // spaceInvadersView 를 재 시작함
                    }
                });
                pauseDialog.show(); // pauseDialog 를 보여줌
            }
        });

        specialShotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // getSpecialShotCount() -> 기본값 : 3
                if (spaceInvadersView.getPlayer().getSpecialShotCount() >= 0)  // getSpecialShotCount() 의 값이 0 보다 크거나 같으면
                    spaceInvadersView.getPlayer().specialShot();  // spaceInvadersView.getPlayer().specialShot() 을 호출
            }
        });
    }
}