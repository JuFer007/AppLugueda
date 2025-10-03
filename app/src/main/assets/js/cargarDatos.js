document.addEventListener("DOMContentLoaded", function() {
    let usuarioJson = null;

    try {
        if (typeof Android !== "undefined" && Android.getUsuarioConTarjeta) {
            usuarioJson = Android.getUsuarioConTarjeta();
        }
    } catch (e) {
        console.error("No se pudo obtener el usuario desde Android:", e);
    }

    if (!usuarioJson) {
        console.warn("No hay datos de usuario disponibles.");
        return;
    }

    let usuario = null;
    try {
        usuario = JSON.parse(usuarioJson);
    } catch (e) {
        console.error("JSON del usuario inválido:", e);
        return;
    }

    if (!usuario || !usuario.nombreCompleto) {
        console.warn("Usuario incompleto:", usuario);
        return;
    }

    const usernameElem = document.getElementById("username");
    if (usernameElem) usernameElem.textContent = usuario.nombreCompleto;

    const tabItems = document.querySelectorAll("#tab1 li span.text-muted");
    if (tabItems.length >= 3) {
        tabItems[0].textContent = usuario.nombreCompleto || "";
        tabItems[1].textContent = usuario.correoElectronico || "";
        tabItems[2].textContent = usuario.dni || "";
    }

    const cardHolder = document.querySelector(".transport-card .card-holder div");
    if (cardHolder) cardHolder.textContent = usuario.nombreCompleto;

    const cardNumberSpans = document.querySelectorAll(".transport-card .card-number span");
    if (cardNumberSpans.length === 4 && usuario.tarjetaNumero) {
        cardNumberSpans[3].textContent = usuario.tarjetaNumero.slice(-4); // últimos 4 dígitos
    }

    const cardExpiry = document.querySelector(".transport-card .card-expiry div");
    if (cardExpiry && usuario.tarjetaVencimiento) {
        cardExpiry.textContent = usuario.tarjetaVencimiento;
    }

    const saldoElem = document.getElementById("saldoActual");
    if (saldoElem && usuario.saldo != null) {
        saldoElem.textContent = "S/ " + parseFloat(usuario.saldo).toFixed(2);
    }

    const btnCerrar = document.getElementById("btn-CerrarSesion");
    if (btnCerrar) {
        btnCerrar.addEventListener("click", function() {
            if (typeof Android !== "undefined" && Android.cerrarSesion) {
                Android.cerrarSesion();
            }
        });
    }
});
