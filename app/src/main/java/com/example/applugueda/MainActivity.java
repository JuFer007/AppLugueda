package com.example.applugueda;

import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        webView = findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        webView.loadUrl("file:///android_asset/login.html");

        this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    setEnabled(false);
                    onBackPressed();
                }
            }
        });
    }

    public class WebAppInterface {
        @JavascriptInterface
        public void login(String email, String pass) {
            runOnUiThread(() -> {
                if (email.equals("admin@gmail.com") && pass.equals("1234")) {
                    Toast.makeText(MainActivity.this, "Acceso Correcto", Toast.LENGTH_SHORT).show();
                    webView.loadUrl("file:///android_asset/index.html");
                } else {
                    Toast.makeText(MainActivity.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @JavascriptInterface
        public void cerrarSesion() {
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                webView.loadUrl("file:///android_asset/login.html");
            });
        }
    }
}
