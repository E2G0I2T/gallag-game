package com.mygame.mygallag.sprites;


import android.content.Context;

import com.mygame.mygallag.SpaceInvadersView;

import java.util.Timer;
import java.util.TimerTask;

public class SpecialshotSprite extends Sprite{
    private SpaceInvadersView game;

    public SpecialshotSprite(Context context ,SpaceInvadersView game, int resId, float x, float y){
        super(context, resId, x, y);
        this.game = game;
        game.getPlayer().setSpecialShooting(true); // specialShooting 을 true 로 설정하여 필살기가 사용되도록 함
        new Timer().schedule(new TimerTask() { // timer : 주기적으로 실행되도록 함
            @Override
            public void run() {
                autoRemove();
            }
        }, 5000); // 5초 이후에 autoRemove() 로 필살기는 종료됨
    }

    @Override
    public void move(){
        super.move();
        // 플레이어의 x좌표 + (플레이어 너비 / 2) = 플레이어의 정중앙
        // 거기서 (내 너비 / 2)를 빼면 내(레이저) 중심이 플레이어 중심과 일치됨
        this.x = game.getPlayer().getX() + (game.getPlayer().getWidth() / 2) - (getWidth() / 2);

        // y좌표는 플레이어 머리 위에서 나가도록 설정
        this.y = game.getPlayer().getY() - getHeight();
    }

    public void autoRemove(){
        game.getPlayer().setSpecialShooting(false); // SpecialShooting 을 false 로 되돌림
        game.removeSprite(this); // 스프라이트 제거
    }

}
