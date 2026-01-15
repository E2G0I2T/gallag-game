    package com.mygame.mygallag;

    import android.content.Context;
    import android.content.Intent;
    import android.graphics.Canvas;
    import android.graphics.Color;
    import android.graphics.Paint;
    import android.graphics.Rect;
    import android.util.Log;
    import android.view.SurfaceHolder;
    import android.view.SurfaceView;

    import androidx.annotation.NonNull;

    import com.mygame.mygallag.sprites.AlienSprite;
    import com.mygame.mygallag.sprites.Sprite;
    import com.mygame.mygallag.sprites.StarshipSprite;

    import java.util.ArrayList;
    import java.util.Random;

    public class SpaceInvadersView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
        // SurfaceView 는 스레드를 이용해 강제로 화면에 그려주므로 View보다 빠르다. 애니메이션, 영상 처리에 이용
        // SurfaceHolder.Callback Surface 의 변화 감지를 위해 필요. 지금처럼 SurfaceView 와 거의 같이 사용한다.

        private static int MAX_ENEMY_COUNT = 10; // 최대 적 수 설정
        private Context context;
        private int characterId;
        private SurfaceHolder ourHolder; // 화면에 그리는데 View 보다 빠르게 그려준다
        private Paint paint; // Paint : Canvas에 그림을 그릴 수 있도록 하는 생성자
        public int screenW, screenH;
        private Rect src, dst; // 사각형 그리는 클래스
        private ArrayList sprites = new ArrayList();
        private Sprite starship;
        private int score, currEnemyCount;
        private Thread gameThread = null;
        private volatile boolean running; // 휘발성 부울 함수
        private Canvas canvas; // 그림을 그리는 영역을 만드는 생성자
        int mapBitmapY = 0;

        public SpaceInvadersView(Context context, int characterId, int x, int y){ // 게임의 주요 동작을 담당하는 클래스
            super(context);
            this.context = context;
            this.characterId = characterId;
            ourHolder = getHolder(); // 현재 SurfaceView 를 리턴 받는다.
            paint = new Paint();
            screenW = x;
            screenH = y; // 받아온 x, y
            src = new Rect(); // 원본 사각형
            dst = new Rect(); // 사본 사각형
            dst.set(0, 0, screenW, screenH); // 시작 x, y 와 끝 x, y 룰 가져와서 사각형 크기 설정
            startGame();
        }

        private void startGame(){
            sprites.clear(); // ArrayList 지우기
            initSprites(); // initSprites 호출
            score = 0; // 초기 score 를 0 으로 설정
        }

        public void endGame(){ // 게임을 종료하고 ResultActivity 실행
            Log.e("GameOver", "GameOver");
            Intent intent = new Intent(context, ResultActivity.class); // Intent 문을 이용해서 ResultActivity 호출
            intent.putExtra("score", score); // intent에 "score" 라는 name 으로 score 값 전달
            context.startActivity(intent); // ResultActivity 실행
            gameThread.stop(); // 스레드 종료
        }

        public void removeSprite(Sprite sprite) { // 주어진 스프라이트를 제거
            sprites.remove(sprite);
        } // 스프라이트 지우는 메서드

        private void initSprites(){ // sprite 초기화
            // StarshipSprite 생성 아이템들 생성
            starship = new StarshipSprite(context, this, characterId, screenW / 2, screenH - 400, 1.5f);
            // StarshipSprite 에서 클래스에서 CharacterId를 받아 starship 을 생성, x 좌표는 가운데, y좌표는 제일 상단에서 아래로 400. 속도는 1.5f 로 초기화
            sprites.add(starship); // ArrayList 에 추가
            spawnEnemy();
            spawnEnemy(); // 적 생성
        }

        public void spawnEnemy(){ // 적 스프라이트를 생성
           Random r = new Random();
            int x = r.nextInt(300) + 100; // int x -> 100 ~ 399 사이의 무작위 정수
            int y = r.nextInt(300) + 100; // int y -> 100 ~ 399 사이의 무작위 정수
            // 외계인 아이템
            Sprite alien = new AlienSprite(context, this, (StarshipSprite) starship, R.drawable.alienship, 100 + x, 100 + y);
            // AlienSprite 객체를 현재 액티비티의 alien에  생성
            // 이미지는 ship_0002, x 좌표는 100 + int x, y 좌표는 100 + y 에 생성
            sprites.add(alien); // 생성된 alien 을 sprites ArrayList 에 추가
            currEnemyCount++; // 외계인 수 증가
        }

        public ArrayList getSprites(){ // 스프라이트 리스트 반환
            return sprites;
        } // sprites ArrayList 를 반환

        public void resume(){ // 게임을 시작, 재개
            // 사용자가 만드는 resume() 함수
            running = true; // 게임 실행 중에는 running 를 true 로 설정
            gameThread = new Thread(this); // 게임 스레드 생성
            gameThread.start(); // 게임 스레드 시작
        }


        public StarshipSprite getPlayer(){ // 플레이어 스프라이트 반환
            return (StarshipSprite) starship; // starship 을 StarshipSprite 로 형변환하여 리턴하기
        }

        public int getScore(){ // 점수를 가져옴
            return  score;
        } // 점수 가져오기

        public void setScore(int score){ // 점수를 설정
            this.score = score;
        } // 가져온 점수를 세팅하기

        public void setCurrEnemyCount(int currEnemyCount){ // 적의 개수를 설정
            this.currEnemyCount = currEnemyCount;
        }

        public int getCurrEnemyCount(){ // 현재 생성된 적의 개수를 가져옴
            return currEnemyCount;
        } // 적의 개수를 가져오기

        public void pause(){ // 게임 일시정지
            running = false; // running bool 을 false 로 전환
            try{
                gameThread.join(); // 스레드 종료 대기하기
            }catch (InterruptedException e){
            }
        }

        @Override
        public void run() { // 게임 로직을 실행하며, 스프라이트의 이동, 충돌 체크
            while(running){ //running이 true 일 때(게임이 동작 중일때) 계속해서 실행하기
                Random r = new Random();
                boolean isEnemySpawn = r.nextInt(100) + 1 < (getPlayer().speed + (int)(getPlayer().getPowerLevel() /2));
                // isEnemySpawn -> 적을 스폰할 지를 나타내는 변수
                // 1 ~ 100 까지의 랜덤한 숫자가 (getPlayer().speed + (int)(getPlayer().getPowerLevel() /2) 보다 작을 경우 true, 그렇지 않을 경우 false
                if(isEnemySpawn && currEnemyCount < MAX_ENEMY_COUNT) spawnEnemy();
                // isEnemySpawn이 true 이고, 현재 적의 수가 MAX_ENEMY_COUNT 로 선언한 수보다 작으면 spawnEnemy() 를 실행하여 적 생성
                for(int i = 0; i < sprites.size(); i++){
                    Sprite sprite = (Sprite) sprites.get(i); // sprites Arraylist 에서 하나씩 가져와서
                    sprite.move(); // 움직이기
                }
                for(int p = 0; p < sprites.size(); p++){
                    // sprites ArrayList에 들어있는 sprite의 개수만큼 실행 (ArrayList 안에 들어있는 첫 번째 sprite 부터 마지막 sprite까지 실행)
                    for(int s = p + 1; s < sprites.size(); s++){ // p + 1 부터 마지막 스프라이트까지 실행
                        try {
                            Sprite me = (Sprite) sprites.get(p);  // me 에 p 번째 스프라이트를 담고
                            Sprite other = (Sprite) sprites.get(s); // other에 s 번째 스프라이트를 담는다
                            if(me.checkCollision(other)){ // 충돌 체크
                                me.handleCollision(other);
                                other.handleCollision(me);
                                // me 와 other 가 충돌 시 충돌 처리
                            }
                        } catch (Exception e){
                            e.printStackTrace(); // 예외 발생 시 예외가 발생한 곳에서 stack 트레이스를 가져온다
                        }
                    }
                }
                draw();
                try {
                    Thread.sleep(10); // 스레드를 0.01초 동안 정지시킨다
                }catch (Exception e){
                }
            }
        }

        public void draw(){ // 게임 화면을 그리는 역할
            if(ourHolder.getSurface().isValid()){ // 캔버스가 유효할 경우 if 문 실행
                canvas = ourHolder.lockCanvas(); //ourHolder.lockCanvas() 로 캔버스를 lock 상태로 가져옴
                canvas.drawColor(Color.BLACK); // 가져온 캔버스를 통해 배경을 검은색으로 채움
                mapBitmapY++; // mapBitmapY 를 증가시켜 배경 이미지를 아래로 이동시킴
                if(mapBitmapY < 0) mapBitmapY = 0; // mapBitmapY가 음수가 되어 화면 밖으로 벗어나지 못하도록 함
                paint.setColor(Color.BLUE); // 그리기 작업에 사용할 색상 설정
                for(int i = 0; i < sprites.size(); i++){ // sprites ArrayList 에 들어있는 sprite의 개수만큼 실행
                    Sprite sprite = (Sprite) sprites.get(i); // sprites 내의 각 스프라이트를 가져와서
                    sprite.draw(canvas, paint); // sprite.draw(canvas, paint) 를 호출하여 스프라이트를 그림
                }
                ourHolder.unlockCanvasAndPost(canvas); // 캔버스를 해제하고 그려진 화면을 표시
            }
        }

        @Override
        public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
            startGame();
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

    }
