package com.zyndra.mousepro;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;

public class FloatingPanelService extends Service {
    private WindowManager wm;
    private View bubble;
    private WindowManager.LayoutParams params;

    @Override public void onCreate() {
        super.onCreate();
        wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        TextView v = new TextView(this);
        v.setText(" Z ");
        v.setTextColor(Color.WHITE); v.setTextSize(20); v.setGravity(Gravity.CENTER);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF7E22CE); bg.setCornerRadius(100); bg.setStroke(3,0xFFC084FC);
        v.setBackground(bg);
        int type = Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
        params = new WindowManager.LayoutParams(58,58,type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP|Gravity.END; params.x=24; params.y=220;
        bubble = v;
        v.setOnClickListener(view -> {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        v.setOnTouchListener(new View.OnTouchListener() {
            private int sx, sy; private float tx, ty;
            public boolean onTouch(View view, MotionEvent e) {
                if(e.getAction()==MotionEvent.ACTION_DOWN){sx=params.x;sy=params.y;tx=e.getRawX();ty=e.getRawY();return false;}
                if(e.getAction()==MotionEvent.ACTION_MOVE){params.x=sx+(int)(tx-e.getRawX());params.y=sy+(int)(e.getRawY()-ty);wm.updateViewLayout(bubble,params);return true;}
                return false;
            }
        });
        wm.addView(bubble,params);
    }
    @Override public int onStartCommand(Intent i,int f,int id){return START_STICKY;}
    @Override public void onDestroy(){if(bubble!=null)wm.removeView(bubble);super.onDestroy();}
    @Override public IBinder onBind(Intent i){return null;}
}
