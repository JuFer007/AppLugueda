package com.example.applugueda.service;
import android.app.Activity;
import androidx.activity.result.ActivityResultLauncher;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanOptions;

public class QRScanner {
    private final Activity activity;
    private final ActivityResultLauncher<ScanOptions> launcher;

    public QRScanner(Activity activity, ActivityResultLauncher<ScanOptions> launcher) {
        this.activity = activity;
        this.launcher = launcher;
    }

    public void iniciarEscaneo(){
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(true);
        options.setCaptureActivity(CaptureActivity.class);
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setPrompt("");
        launcher.launch(options);
    }

    public void manejarResultado(String qrLeido, android.webkit.WebView webView) {
        webView.loadUrl("file:///android_asset/pagoQR.html?bus=" + qrLeido);
    }
}
