package com.rit.control;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import com.getcapacitor.BridgeActivity;

/* RIT Control: lets the web app switch the phone to FULL SCREEN (hides the status bar and
   the navigation bar) while the Car Control page is open. */
public class MainActivity extends BridgeActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (Build.VERSION.SDK_INT >= 28) {            // draw under the camera notch too
      WindowManager.LayoutParams lp = getWindow().getAttributes();
      lp.layoutInDisplayCutoutMode = (Build.VERSION.SDK_INT >= 30)
          ? WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
          : WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
      getWindow().setAttributes(lp);
    }

    getBridge().getWebView().addJavascriptInterface(new Object() {
      @JavascriptInterface
      public void setImmersive(final boolean on) {
        runOnUiThread(new Runnable() {
          @Override
          public void run() { applyImmersive(on); }
        });
      }
    }, "RITNative");
  }

  private void applyImmersive(boolean on) {
    try {
      if (Build.VERSION.SDK_INT >= 30) {
        WindowInsetsController c = getWindow().getInsetsController();
        if (c == null) return;
        if (on) {
          c.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
          c.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
          c.show(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        }
      } else {
        View d = getWindow().getDecorView();
        if (on) {
          d.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
              | View.SYSTEM_UI_FLAG_FULLSCREEN
              | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
              | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        } else {
          d.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
      }
    } catch (Exception e) { }
  }
}
