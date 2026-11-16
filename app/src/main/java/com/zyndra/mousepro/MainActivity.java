package com.zyndra.mousepro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.InputType;
import java.util.*;

public class MainActivity extends Activity {
    private static final int BG = Color.rgb(10,5,18);
    private static final int CARD = Color.rgb(28,13,45);
    private static final int PURPLE = Color.rgb(168,85,247);
    private static final int WHITE = Color.rgb(245,235,255);
    private LinearLayout root, content;
    private android.content.SharedPreferences prefs;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("zyndra", MODE_PRIVATE);
        buildShell();
        showHome();
    }

    private GradientDrawable shape(int color, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(18));
        if (stroke != 0) d.setStroke(dp(1), stroke);
        return d;
    }
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density); }

    private void buildShell() {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        TextView header = new TextView(this);
        header.setText("  Z  ZYNDRA MOUSE PRO\n     YOUR GAME. YOUR CONTROL.");
        header.setTextColor(WHITE); header.setTextSize(20); header.setTypeface(null, Typeface.BOLD);
        header.setPadding(dp(18), dp(24), dp(12), dp(18));
        header.setBackground(shape(Color.rgb(24,8,42), PURPLE));
        root.addView(header, new LinearLayout.LayoutParams(-1, dp(104)));
        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(16), dp(16), dp(24));
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);
    }

    private TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(null, Typeface.BOLD);
        t.setPadding(dp(2), dp(5), dp(2), dp(5)); return t;
    }
    private void section(String title) { content.addView(text(title, 18, WHITE, true)); }
    private void card(String title, String desc, Runnable action) {
        LinearLayout box = new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(15), dp(12), dp(15), dp(12)); box.setBackground(shape(CARD, 0));
        TextView a = text(title, 16, WHITE, true); TextView b = text(desc, 13, 0xFFD8B4FE, false);
        box.addView(a); box.addView(b);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2); lp.bottomMargin = dp(10);
        content.addView(box, lp); box.setOnClickListener(v -> action.run());
    }
    private Button button(String label, Runnable action) {
        Button b = new Button(this); b.setText(label); b.setTextColor(WHITE);
        b.setAllCaps(false); b.setBackground(shape(0xFF6B21A8, PURPLE));
        b.setOnClickListener(v -> action.run()); return b;
    }
    private void clear(String title) { content.removeAllViews(); section(title); }

    private void showHome() {
        clear("✨ Painel principal");
        card("🎮 Biblioteca de jogos", "Veja os aplicativos instalados e abra um jogo.", this::showGames);
        card("⌨️ Editor de mapeamento", "Configure teclas para posições de toque na tela.", this::showEditor);
        card("🖱️ Dispositivos conectados", deviceSummary(), this::showDevices);
        card("🟣 Painel flutuante", "Ative o atalho sobre outros aplicativos.", this::overlaySetup);
        card("⚙️ Configurações", "Permissões, acessibilidade e informações.", this::showSettings);
        card("💬 Comunidade ZYNDRA", "Suporte e novidades somente pelo Discord.", this::openDiscord);
        content.addView(text("⚠️ Versão experimental 0.1.0-alpha. Compatibilidade varia por aparelho e jogo.", 12, 0xFFFFD166, false));
    }

    private String deviceSummary() {
        int[] ids = InputDevice.getDeviceIds(); int count = 0;
        for (int id : ids) {
            InputDevice d = InputDevice.getDevice(id);
            if (d != null && !d.isVirtual() && (d.getSources() & (InputDevice.SOURCE_KEYBOARD | InputDevice.SOURCE_MOUSE)) != 0) count++;
        }
        return count + " dispositivo(s) físico(s) de teclado/mouse detectado(s).";
    }

    private void showDevices() {
        clear("🖱️ Dispositivos");
        for (int id : InputDevice.getDeviceIds()) {
            InputDevice d = InputDevice.getDevice(id);
            if (d != null && !d.isVirtual()) {
                card(d.getName(), "ID: " + id + " • " + InputDevice.getSourcesDescription(d.getSources()),
                    () -> new AlertDialog.Builder(this).setMessage(d.toString()).setPositiveButton("OK", null).show());
            }
        }
        content.addView(button("↻ Atualizar", this::showDevices));
        content.addView(button("← Voltar", this::showHome));
    }

    private void showGames() {
        clear("🎮 Jogos instalados");
        content.addView(text("Lista local de apps instalados. Nem todo app é um jogo.", 12, 0xFFD8B4FE, false));
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        Collections.sort(apps, (a,b) -> pm.getApplicationLabel(a).toString().compareToIgnoreCase(pm.getApplicationLabel(b).toString()));
        int shown = 0;
        for (ApplicationInfo app : apps) {
            if (getPackageName().equals(app.packageName)) continue;
            String label = pm.getApplicationLabel(app).toString();
            card("🎯 " + label, app.packageName, () -> {
                prefs.edit().putString("selected_game", app.packageName).apply();
                Intent launch = pm.getLaunchIntentForPackage(app.packageName);
                if (launch != null) startActivity(launch);
                else Toast.makeText(this, "Este app não oferece tela de inicialização.", Toast.LENGTH_SHORT).show();
            });
            if (++shown >= 80) break;
        }
        content.addView(button("← Voltar", this::showHome));
    }

    private void showEditor() {
        clear("⌨️ Editor de mapeamento");
        content.addView(text("Defina a posição em porcentagem da tela (0 a 100). Ex.: X=50, Y=50 é o centro.", 13, 0xFFD8B4FE, false));
        String[] keys = {"W","A","S","D","SPACE","F","E"};
        for (String key : keys) {
            LinearLayout row = new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL);
            row.setOrientation(LinearLayout.HORIZONTAL);
            TextView label = text("Tecla " + key, 14, WHITE, true);
            EditText x = edit(String.valueOf(prefs.getInt("x_"+key, key.equals("A")?25:key.equals("D")?75:50)));
            EditText y = edit(String.valueOf(prefs.getInt("y_"+key, key.equals("W")?40:key.equals("S")?60:50)));
            row.addView(label, new LinearLayout.LayoutParams(0, dp(52), 1));
            row.addView(x, new LinearLayout.LayoutParams(dp(55), dp(48)));
            row.addView(y, new LinearLayout.LayoutParams(dp(55), dp(48)));
            content.addView(row);
            Button save = button("Salvar " + key, () -> {
                try {
                    int xv = Math.max(0, Math.min(100, Integer.parseInt(x.getText().toString())));
                    int yv = Math.max(0, Math.min(100, Integer.parseInt(y.getText().toString())));
                    prefs.edit().putInt("x_"+key,xv).putInt("y_"+key,yv).apply();
                    Toast.makeText(this, "Mapeamento salvo: " + key, Toast.LENGTH_SHORT).show();
                } catch(Exception e) { Toast.makeText(this, "Digite valores de 0 a 100.", Toast.LENGTH_SHORT).show(); }
            });
            content.addView(save);
        }
        content.addView(button("Ativar serviço de mapeamento", () -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))));
        content.addView(text("O serviço usa gestos de acessibilidade. Alguns jogos bloqueiam ou ignoram gestos sintetizados; teste com cuidado. Não é compatível com todos os jogos/anticheats.", 12, 0xFFFFD166, false));
        content.addView(button("← Voltar", this::showHome));
    }

    private EditText edit(String value) {
        EditText e = new EditText(this); e.setSingleLine(true); e.setText(value); e.setTextColor(WHITE);
        e.setHintTextColor(0xFFB794D8); e.setInputType(InputType.TYPE_CLASS_NUMBER);
        e.setBackground(shape(0xFF160B24, PURPLE)); e.setGravity(Gravity.CENTER); return e;
    }

    private void overlaySetup() {
        if (!Settings.canDrawOverlays(this)) {
            new AlertDialog.Builder(this).setTitle("Permissão de sobreposição")
                .setMessage("O Android precisa permitir que o atalho flutuante apareça sobre outros apps.")
                .setNegativeButton("Agora não", null)
                .setPositiveButton("Abrir configurações", (d,w) -> startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName())))).show();
        } else {
            startService(new Intent(this, FloatingPanelService.class));
            Toast.makeText(this, "Painel flutuante iniciado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSettings() {
        clear("⚙️ Configurações");
        card("♿ Serviço de acessibilidade", "Abra as configurações do Android para ativar/desativar o mapeador.", () -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        card("🪟 Permissão de sobreposição", "Necessária para o botão flutuante.", this::overlaySetup);
        card("💜 Discord oficial", "https://discord.gg/2fBH8ud3Ur", this::openDiscord);
        card("✈️ Telegram", "Indisponível nesta versão.", () -> Toast.makeText(this, "Telegram indisponível.", Toast.LENGTH_SHORT).show());
        content.addView(text("ZYNDRA MOUSE PRO • 0.1.0-alpha\nProjeto independente. Não afiliado ao GG Mouse Pro.", 12, 0xFFD8B4FE, false));
        content.addView(button("← Voltar", this::showHome));
    }

    private void openDiscord() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/2fBH8ud3Ur")));
    }
}
