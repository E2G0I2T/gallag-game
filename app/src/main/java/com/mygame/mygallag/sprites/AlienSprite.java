package com.mygame.mygallag.sprites;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mygame.mygallag.MainActivity;
import com.mygame.mygallag.SpaceInvadersView;
import com.mygame.mygallag.items.HealitemSprite;
import com.mygame.mygallag.items.PowerItemSprite;
import com.mygame.mygallag.items.SpeedItemSprite;

import java.util.ArrayList;
import java.util.Random;

public class AlienSprite extends Sprite{
    private Context context;
    private SpaceInvadersView game;
    ArrayList<AlienShotSprite> alienShotSprites;
    private int aLife = 3;
    Handler fireHandler = null;
    boolean isDestroyed = false;
    private StarshipSprite starship;
    public AlienSprite(Context context, SpaceInvadersView game, StarshipSprite starship, int resId, int x, int y) {
        // 외계인 만들기
        super(context, resId, x, y);
        this.context = context;
        this.game = game;
        this.starship = starship;
        alienShotSprites = new ArrayList<>();
        aLife = 3;
        Random r = new Random();
        int randomDx = r.nextInt(5);
        int randomDy = r.nextInt(4);
        // 0 ~ 4 중 랜덤한 숫자를 randomDx, randomDy 에 담음
        dx = randomDx; // dx = 0 ~ 4
        dy = randomDy + 1; // dy = 1 ~ 4
        fireHandler = new Handler(Looper.getMainLooper());
        // Main Thread 의 MessageQueue 에 바인딩
        fireHandler.postDelayed(
                // delay를 줘서 일정 시간 후에 동작하도록 함
                new Runnable() {
                    @Override
                    public void run() {
                        Log.d("run", "동작");
                        Random r = new Random();
                        boolean isFire = r.nextInt(100) + 1 <= 30;
                        // 0~99 중 랜덤 숫자 + 1 (1 ~ 100) 이 30보다 작거나 같을 경우 (30%의 확률)로 isFire 가 true 가 됨
                        if(isFire && !isDestroyed){ // isFire 가 true, isDestroy 가 false 인 경우 if 문 실행
                            fire();
                            fireHandler.postDelayed(this, 1000); // 1 초 후 실행
                        }
                    }
                }, 1000);
        }
    @Override
    public void move() {
        super.move(); // 일단 이동

        // 1. 벽에 튕기거나 화면 아래로 사라지는 기존 코드
        if ((dx < 0) && (x < 10) || ((dx > 0) && (x > 900))) {
            dx = -dx;
        }
        if (y > game.screenH) {
            game.removeSprite(this);
            game.setCurrEnemyCount(game.getCurrEnemyCount() - 1);
        }

        float hitBoxSize = 50;
        float offsetX = (getWidth() - hitBoxSize) / 2;
        float offsetY = (getHeight() - hitBoxSize) / 2;

        getRect().left = x + offsetX;
        getRect().top = y + offsetY;
        getRect().right = getRect().left + hitBoxSize;
        getRect().bottom = getRect().top + hitBoxSize;
    }


    @Override
    public void handleCollision(Sprite Other) { // Other 파라미터를 통해 다른 Sprite를 받음
        if (Other instanceof ShotSprite) { // Other Sprite가 ShotSprite 의 인스턴스인 경우 (가져온 Sprite 가 총알인 경우)
            game.removeSprite(Other); // 총알을 없애고
            int powerLevel = starship.getPowerLevel();
            for (int i = -1; i < powerLevel; i++) {
                if (aLife > 0) {
                    aLife--;
                }
            }
            if (aLife <= 0) {
                game.removeSprite(this); // this(AlienSprite) 를 없앤다
                destroyAlien(); // destroyAlien() 을 호출해서 적 숫자 줄이고 아이템을 드랍하고... 등을 수행한다
                return;
            }
        }
        if (Other instanceof SpecialshotSprite) { // Other Sprite가 SpecialshotSprite 의 인스턴스인 경우 (가져온 Sprite 가 필살기인 경우)
            game.removeSprite(this); // this(AlienSprite) 를 없앤다
            // 위처럼 game.removeSprite(Other); 를 없애지 않는 이유 : 필살기가 Alien을 죽였을 때 없어지지 않도록 하기 위함
            destroyAlien();
            return;
        }
    }

    private void destroyAlien() {
        isDestroyed = true; // isDestroyed 로 하여 죽은 Alien 이 총알을 발사하지 못하게 함
        game.setCurrEnemyCount(game.getCurrEnemyCount()-1); // 적의 숫자를 -1 하여 다시 세팅함
        for (int i = 0; i < alienShotSprites.size(); i++)
            game.removeSprite(alienShotSprites.get(i)); // alienShotSprites 에 남아있는 모든 sprites 를 removeSprite로 제거함
        spawnHealItem();
        spawnPowerItem();
        spawnSpeedItem();
        // 아이템 드랍
        game.setScore(game.getScore() + 1); // 게임 스코어로 +1 함
        MainActivity.scoreTv.setText(Integer.toString(game.getScore())); // scoreTv 에 현재 게임 스코어를 가져와서 표시함

        Handler removeHandler = new Handler(Looper.getMainLooper());
        removeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                game.removeSprite(AlienSprite.this);
            }
        }, 1000); // 1초 지연 (필요에 따라 수정 가능)
    }

    private void fire() {
        AlienShotSprite alienShotSprite = new AlienShotSprite(context, game, getX(), getY()+30, 16);
        // alienShotSprite 를 가져와서 x, y + 30 의 위치에 생성, 속도는 dy : 16
        alienShotSprites.add(alienShotSprite); // alienShotSprites ArrayList에 입력
        game.getSprites().add(alienShotSprite); //getSprites() 에 입력
    }

    private void spawnSpeedItem() {
        Random r = new Random();
        int speedItemDrop = r.nextInt(100) + 1; // 1 ~ 100 사이 랜덤 숫자를 speedItemDrop에 담고
        if (speedItemDrop <= 5) { // speedItemDrop 가 5 보다 작거나 같을 경우 if 문 실행 (5% 확률)
            int dx = r.nextInt(10) + 1; // dx 속도는 1 ~ 10
            int dy = r.nextInt(10) + 10; // dy  속도는 10 ~ 19
            game.getSprites().add(new SpeedItemSprite(context, game, (int)this.getX(),
                    (int)this.getY(), dx, dy)); // getSprites() 에 이 Item 의 x, y 좌표와 dx, dy 를 담음
        }
    }

    private void spawnPowerItem() {
        Random r = new Random();
        int powerItemDrop = r.nextInt(100) + 1; // 1 ~ 100 사이 랜덤 숫자를 powerItemDrop 에 담고
        if (powerItemDrop <= 3) { // powerItemDrop 가 3 보다 작거나 같을 경우 if 문 실행 (3% 확률)
            int dx = r.nextInt(10) + 1; // dx 속도는 1 ~ 10
            int dy = r.nextInt(10) + 10; // dy  속도는 10 ~ 19
            game.getSprites().add(new PowerItemSprite(context, game, (int)this.getX(),
                    (int)this.getY(), dx, dy)); // getSprites() 에 이 Item 의 x, y 좌표와 dx, dy 를 담음
        }
    }
    private void spawnHealItem() {
        Random r = new Random();
        int healItemDrop = r.nextInt(100) + 1; // 1 ~ 100 사이 랜덤 숫자를 healItemDrop 에 담고
        if (healItemDrop <= 1) { // healItemDrop 가 1 보다 작거나 같을 경우 if 문 실행 (1% 확률)
            int dx = r.nextInt(10) + 1; // dx 속도는 1 ~ 10
            int dy = r.nextInt(10) + 10; // dy  속도는 10 ~ 19
            game.getSprites().add(new HealitemSprite(context, game, (int) this.getX(),
                    (int) this.getY(), dx, dy)); // getSprites() 에 이 Item 의 x, y 좌표와 dx, dy 를 담음
        }
    }
}
