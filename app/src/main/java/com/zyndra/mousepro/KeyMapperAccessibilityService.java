package com.zyndra.mousepro;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.util.DisplayMetrics;

public class KeyMapperAccessibilityService extends AccessibilityService {
    private SharedPreferences prefs;

    @Override public void onServiceConnected() {
        super.onServiceConnected();
        prefs = getSharedPreferences("zyndra", MODE_PRIVATE);
    }
    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt() {}

    @Override protected boolean onKeyEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN || prefs == null) return false;
        String key = keyName(event.getKeyCode());
        if (key == null || !prefs.contains("x_"+key)) return false;

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        if (wm == null) return false;
        Display display = wm.getDefaultDisplay();
        display.getRealMetrics(dm);
        float x = dm.widthPixels * prefs.getInt("x_"+key, 50) / 100f;
        float y = dm.heightPixels * prefs.getInt("y_"+key, 50) / 100f;

        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.StrokeDescription stroke =
            new GestureDescription.StrokeDescription(path, 0, 65);
        GestureDescription gesture = new GestureDescription.Builder().addStroke(stroke).build();
        dispatchGesture(gesture, null, null);
        return true;
    }

    private String keyName(int code) {
        switch(code) {
            case KeyEvent.KEYCODE_W: return "W";
            case KeyEvent.KEYCODE_A: return "A";
            case KeyEvent.KEYCODE_S: return "S";
            case KeyEvent.KEYCODE_D: return "D";
            case KeyEvent.KEYCODE_SPACE: return "SPACE";
            case KeyEvent.KEYCODE_F: return "F";
            case KeyEvent.KEYCODE_E: return "E";
            default: return null;
        }
    }
}
