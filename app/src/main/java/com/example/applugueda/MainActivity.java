package com.example.applugueda;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.example.applugueda.data.UsuarioManager;
import com.example.applugueda.modelo.Tarjeta;
import com.example.applugueda.modelo.Usuario;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private UsuarioManager usuarioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        usuarioManager = new UsuarioManager(this);

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
        private String correoLogueado = "";

        @JavascriptInterface
        public void login(String email, String pass) {
            runOnUiThread(() -> {
                if (usuarioManager.validarLogin(email, pass)) {
                    correoLogueado = email;
                    Toast.makeText(MainActivity.this, "Acceso Correcto", Toast.LENGTH_SHORT).show();
                    webView.loadUrl("file:///android_asset/index.html");
                } else {
                    Toast.makeText(MainActivity.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @JavascriptInterface
        public void register(String nombre, String dni, String email, String pass) {
            runOnUiThread(() -> {
                String numeroTarjeta = generarNumeroTarjeta();
                String fechaVencimiento = "12/30";
                double saldoInicial = 0.0;

                Tarjeta tarjeta = new Tarjeta(numeroTarjeta, fechaVencimiento, saldoInicial);
                Usuario nuevo = new Usuario(nombre, dni, email, pass, tarjeta);

                boolean exito = usuarioManager.agregarUsuario(nuevo);

                if (exito) {
                    Toast.makeText(MainActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                    webView.loadUrl("file:///android_asset/login.html");
                } else {
                    Toast.makeText(MainActivity.this, "Ya existe un usuario con ese correo", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @JavascriptInterface
        public void cerrarSesion() {
            runOnUiThread(() -> {
                correoLogueado = "";
                Toast.makeText(MainActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                webView.loadUrl("file:///android_asset/login.html");
            });
        }

        @JavascriptInterface
        public String getUsuarioConTarjeta() {
            if (!correoLogueado.isEmpty()) {
                Usuario u = usuarioManager.obtenerUsuario(correoLogueado);
                if (u != null) {
                    String numeroTarjeta = u.getTarjeta().getNumeroTarjeta();
                    String ultimos4 = numeroTarjeta.substring(numeroTarjeta.length() - 4);
                    double saldo = u.getTarjeta().getSaldoActual();
                    return "{"
                            + "\"nombreCompleto\":\"" + u.getNombreCompleto() + "\","
                            + "\"dni\":\"" + u.getDni() + "\","
                            + "\"correoElectronico\":\"" + u.getCorreoElectronico() + "\","
                            + "\"tarjetaNumero\":\"" + ultimos4 + "\","
                            + "\"tarjetaVencimiento\":\"" + u.getTarjeta().getFechaVencimiento() + "\","
                            + "\"saldo\":" + saldo
                            + "}";
                }
            }
            return "{}";
        }

        @JavascriptInterface
        public boolean agregarMovimiento(String tipo, double monto) {
            if (!correoLogueado.isEmpty()) {
                return usuarioManager.agregarMovimiento(correoLogueado, tipo, monto);
            }
            return false;
        }

        private String generarNumeroTarjeta() {
            Random random = new Random();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                sb.append(random.nextInt(10));
            }
            return sb.toString();
        }
    }
}
