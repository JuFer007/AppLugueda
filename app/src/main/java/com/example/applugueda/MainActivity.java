package com.example.applugueda;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import com.example.applugueda.data.UsuarioManager;
import com.example.applugueda.modelo.Movimiento;
import com.example.applugueda.modelo.Tarjeta;
import com.example.applugueda.modelo.Usuario;
import com.example.applugueda.service.Notificacion;
import com.example.applugueda.service.QRScanner;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private UsuarioManager usuarioManager;
    private QRScanner qrScanner;
    private WebAppInterface webAppInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        usuarioManager = new UsuarioManager(this);

        checkNotificaciones();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        webView = findViewById(R.id.webview);
        webAppInterface = new WebAppInterface();
        webView.addJavascriptInterface(webAppInterface, "Android");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webSettings.setDomStorageEnabled(true);

        ActivityResultLauncher<ScanOptions> escanearQRLauncher =
                registerForActivityResult(new com.journeyapps.barcodescanner.ScanContract(), result -> {
                    if (result.getContents() != null) {
                        qrScanner.manejarResultado(result.getContents(), webView);
                    }
                });

        qrScanner = new QRScanner(this, escanearQRLauncher);


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

    public void solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notificaciones activadas", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notificaciones no activadas", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void checkNotificaciones() {
        SharedPreferences prefs = getSharedPreferences("appPrefs", MODE_PRIVATE);
        boolean yaPreguntado = prefs.getBoolean("preguntoNotificaciones", false);

        if (!yaPreguntado) {
            solicitarPermisoNotificaciones();
            prefs.edit().putBoolean("preguntoNotificaciones", true).apply();
        }
    }

    public void escanearQRDesdeJS(){
        runOnUiThread(() -> {
            if (webAppInterface.tieneSaldo()) {
                qrScanner.iniciarEscaneo();
            } else {
                Toast.makeText(this, "Saldo insuficiente. Recarga antes de pagar.", Toast.LENGTH_SHORT).show();
            }
        });    }

    public class WebAppInterface {
        private String correoLogueado = "";

        @JavascriptInterface
        public boolean tieneSaldo() {
            if (!correoLogueado.isEmpty()) {
                Usuario usuario = usuarioManager.obtenerUsuario(correoLogueado);
                return usuario != null && usuario.getTarjeta().getSaldoActual() > 0;
            }
            return false;
        }

        @JavascriptInterface
        public void escanearQR(){
            runOnUiThread(() -> escanearQRDesdeJS());
        }

        @JavascriptInterface
        public void login(String email, String pass) {
            runOnUiThread(() -> {
                if (usuarioManager.validarLogin(email, pass)) {
                    correoLogueado = email;
                    Toast.makeText(MainActivity.this, "Acceso Correcto", Toast.LENGTH_SHORT).show();
                    webView.loadUrl("file:///android_asset/splash.html");

                    new android.os.Handler().postDelayed(() -> {
                        webView.loadUrl("file:///android_asset/index.html");
                    }, 4000);
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
        public String agregarMovimiento(String tipo, double monto) {
            if (!correoLogueado.isEmpty()) {
                boolean exito = usuarioManager.agregarMovimiento(correoLogueado, tipo, monto);
                if (exito) {
                    Usuario usuario = usuarioManager.obtenerUsuario(correoLogueado);
                    if (usuario != null && usuario.getTarjeta().getSaldoActual() <= 0) {
                        Notificacion notificacion = new Notificacion(); notificacion.mostrarNotificacionSaldoCero(MainActivity.this);
                    } else if (usuario.getTarjeta().getSaldoActual() <= 5)
                    { Notificacion notificacion = new Notificacion();
                        notificacion.notificacionSaldoBajo(MainActivity.this);
                    } if (tipo.toLowerCase().contains("pago bus")) {
                        Toast.makeText(MainActivity.this, "Pago realizado correctamente al bus " + tipo.replace("Pago Bus ", ""), Toast.LENGTH_SHORT).show();
                    } else if (tipo.toLowerCase().contains("recarga")) {
                        Toast.makeText(MainActivity.this, "Recarga realizada correctamente", Toast.LENGTH_SHORT).show();
                    } return "exito";
                } else {
                    Toast.makeText(MainActivity.this, "Saldo insuficiente. Recarga antes de pagar.", Toast.LENGTH_SHORT).show();
                    return "fallo";
                }
            } return "fallo";
        }

        @JavascriptInterface
        public String getEstadoTarjeta() {
            if (!correoLogueado.isEmpty()) {
                Usuario usuario = usuarioManager.obtenerUsuario(correoLogueado);
                if (usuario != null && usuario.getTarjeta() != null) {
                    Tarjeta tarjeta = usuario.getTarjeta();

                    StringBuilder json = new StringBuilder("{");
                    json.append("\"saldo\":").append(tarjeta.getSaldoActual()).append(",");
                    json.append("\"movimientos\":[");

                    List<Movimiento> movimientos = tarjeta.getMovimientos();
                    for (int i = 0; i < movimientos.size(); i++) {
                        Movimiento mov = movimientos.get(i);
                        json.append("{")
                                .append("\"tipo\":\"").append(mov.getTipo()).append("\",")
                                .append("\"monto\":").append(mov.getMonto()).append(",")
                                .append("\"fecha\":\"").append(mov.getFecha()).append("\"")
                                .append("}");
                        if (i < movimientos.size() - 1) {
                            json.append(",");
                        }
                    }

                    json.append("]}");
                    return json.toString();
                }
            }
            return "{\"saldo\":0,\"movimientos\":[]}";
        }

        @JavascriptInterface
        public String getMovimientos() {
            if (!correoLogueado.isEmpty()) {
                Usuario usuario = usuarioManager.obtenerUsuario(correoLogueado);
                if (usuario != null && usuario.getTarjeta() != null) {
                    List<Movimiento> movimientos = usuario.getTarjeta().getMovimientos();
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < movimientos.size(); i++) {
                        Movimiento mov = movimientos.get(i);
                        json.append("{")
                                .append("\"tipo\":\"").append(mov.getTipo()).append("\",")
                                .append("\"monto\":").append(mov.getMonto()).append(",")
                                .append("\"fecha\":\"").append(mov.getFecha()).append("\"")
                                .append("}");
                        if (i < movimientos.size() - 1) {
                            json.append(",");
                        }
                    }
                    json.append("]");
                    return json.toString();
                }
            }
            return "[]";
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
