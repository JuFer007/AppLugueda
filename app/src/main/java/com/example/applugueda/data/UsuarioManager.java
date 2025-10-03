package com.example.applugueda.data;
import android.content.Context;
import android.util.Log;
import com.example.applugueda.modelo.Tarjeta;
import com.example.applugueda.modelo.Usuario;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.text.SimpleDateFormat;

public class UsuarioManager {
    private Context context;
    private final String fileName = "usuarios.json";

    public UsuarioManager(Context context) {
        this.context = context;
    }

    private File getFile() {
        File path = context.getFilesDir();
        return new File(path, fileName);
    }

    public JSONArray cargarUsuarios() {
        try {
            File file = getFile();
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[(int) file.length()];
                fis.read(buffer);
                fis.close();

                JSONArray usuarios = new JSONArray(new String(buffer, StandardCharsets.UTF_8));
                Log.d("UsuariosJSON", usuarios.toString(4));
                return usuarios;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public boolean agregarUsuario(Usuario usuario) {
        try {
            JSONArray usuariosArray = cargarUsuarios();

            // Validar que no exista el correo
            for (int i = 0; i < usuariosArray.length(); i++) {
                JSONObject u = usuariosArray.getJSONObject(i);
                if (u.getString("correoElectronico").equalsIgnoreCase(usuario.getCorreoElectronico())) {
                    return false;
                }
            }

            JSONObject obj = new JSONObject();
            obj.put("nombreCompleto", usuario.getNombreCompleto());
            obj.put("dni", usuario.getDni());
            obj.put("correoElectronico", usuario.getCorreoElectronico());
            obj.put("contraseña", usuario.getContraseña());

            Tarjeta tarjeta = usuario.getTarjeta();
            JSONObject tarjetaObj = new JSONObject();
            tarjetaObj.put("numeroTarjeta", tarjeta.getNumeroTarjeta());
            tarjetaObj.put("fechaVencimiento", tarjeta.getFechaVencimiento());
            tarjetaObj.put("saldoActual", tarjeta.getSaldoActual());

            obj.put("tarjeta", tarjetaObj);
            usuariosArray.put(obj);

            File file = getFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(usuariosArray.toString(4).getBytes(StandardCharsets.UTF_8));
            fos.close();

            Log.d("UsuariosJSON", usuariosArray.toString(4));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //Validar login
    public boolean validarLogin(String correo, String password) {
        try {
            JSONArray usuarios = cargarUsuarios();
            for (int i = 0; i < usuarios.length(); i++) {
                JSONObject obj = usuarios.getJSONObject(i);
                // Comparar contraseña exacta
                if (obj.getString("correoElectronico").equalsIgnoreCase(correo)
                        && obj.getString("contraseña").equals(password)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //Obtener un usuario por correo
    public Usuario obtenerUsuario(String correo) {
        try {
            JSONArray usuarios = cargarUsuarios();
            for (int i = 0; i < usuarios.length(); i++) {
                JSONObject obj = usuarios.getJSONObject(i);
                if (obj.getString("correoElectronico").equalsIgnoreCase(correo)) {
                    JSONObject tarjetaObj = obj.getJSONObject("tarjeta");
                    Tarjeta tarjeta = new Tarjeta(
                            tarjetaObj.getString("numeroTarjeta"),
                            tarjetaObj.getString("fechaVencimiento"),
                            tarjetaObj.getDouble("saldoActual")
                    );
                    return new Usuario(
                            obj.getString("nombreCompleto"),
                            obj.getString("dni"),
                            obj.getString("correoElectronico"),
                            obj.getString("contraseña"),
                            tarjeta
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //agregar movimiento
    public boolean agregarMovimiento(String correo, String tipo, double monto) {
        try {
            JSONArray usuariosArray = cargarUsuarios();
            boolean encontrado = false;

            for (int i = 0; i < usuariosArray.length(); i++) {
                JSONObject usuarioObj = usuariosArray.getJSONObject(i);

                if (usuarioObj.getString("correoElectronico").equalsIgnoreCase(correo)) {
                    encontrado = true;

                    JSONObject tarjetaObj = usuarioObj.getJSONObject("tarjeta");

                    double saldoActual = tarjetaObj.optDouble("saldoActual", 0.0);
                    if (tipo.equalsIgnoreCase("Recarga")) {
                        saldoActual += monto;
                    } else if (tipo.equalsIgnoreCase("Pasaje")) {
                        saldoActual -= monto;
                    }
                    tarjetaObj.put("saldoActual", saldoActual);

                    JSONArray movimientosArray;
                    if (tarjetaObj.has("movimientos")) {
                        movimientosArray = tarjetaObj.getJSONArray("movimientos");
                    } else {
                        movimientosArray = new JSONArray();
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String fechaActual = sdf.format(new Date());

                    JSONObject movimiento = new JSONObject();
                    movimiento.put("tipo", tipo);
                    movimiento.put("monto", monto);
                    movimiento.put("fecha", fechaActual);

                    movimientosArray.put(movimiento);
                    tarjetaObj.put("movimientos", movimientosArray);

                    FileOutputStream fos = new FileOutputStream(getFile());
                    fos.write(usuariosArray.toString(4).getBytes(StandardCharsets.UTF_8));
                    fos.close();

                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
