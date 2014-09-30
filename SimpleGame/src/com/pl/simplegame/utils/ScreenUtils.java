package com.pl.simplegame.utils;

import org.andengine.entity.sprite.Sprite;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

public class ScreenUtils {

	private static Point deviceDisplay(Activity activity){
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		return point;
	}
	
	public static int getDeviceWidth(Activity activity){
		return deviceDisplay(activity).x;
	}
	
	public static int getDeviceHeight(Activity activity){
		return deviceDisplay(activity).y;
	}
	
	public static Point getCenterScreenPoint(Activity activity){
		int width = getDeviceWidth(activity);
		int height = getDeviceHeight(activity);
		int x = width/2;
		int y = height/2;
		return new Point(x, y);
	}
	
	public static Point getCenterScreenPointForSprite(Activity activity,Sprite sprite){
		int x = getCenterScreenPoint(activity).x;
		int y = getCenterScreenPoint(activity).y;
		int spriteX = (int) sprite.getWidth();
		int spriteY = (int) sprite.getHeight();
		y = y - (spriteY / 2);
		x = x - (spriteX / 2);
		return new Point(x, y);
	}
	
	public static int getFloorPointForSprite(Activity activity,Sprite sprite){
		return (int) (getDeviceHeight(activity) - sprite.getHeight());
	}
	
}
