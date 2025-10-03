function registrarMovimiento(tipo, monto) {
if (typeof Android !== "undefined" && Android.agregarMovimiento) {
    const exito = Android.agregarMovimiento(tipo, monto);
    if (exito) {
        const lista = document.getElementById("movimientosList");
        const li = document.createElement("li");
        li.className = "list-group-item d-flex justify-content-between align-items-center";

        const infoDiv = document.createElement("div");
        const tipoDiv = document.createElement("div");
        tipoDiv.className = "fw-bold";
        tipoDiv.textContent = tipo;

        const fechaSmall = document.createElement("small");
        fechaSmall.className = "text-muted";
        fechaSmall.textContent = "Hoy";

        infoDiv.appendChild(tipoDiv);
        infoDiv.appendChild(fechaSmall);

        const badgeSpan = document.createElement("span");
        badgeSpan.className = "badge rounded-pill " + (tipo.toLowerCase().includes("recarga") ? "bg-success" : "bg-danger");
        badgeSpan.textContent = (tipo.toLowerCase().includes("recarga") ? "+ S/ " : "- S/ ") + monto.toFixed(2);

        li.appendChild(infoDiv);
        li.appendChild(badgeSpan);
        lista.prepend(li);

        // Actualizar saldo
        actualizarSaldo(tipo.toLowerCase().includes("recarga") ? monto : -monto);

        return true;
    } else {
        alert("Error al registrar el movimiento.");
        return false;
    }
}
return false;
}

//Pago Manual
document.getElementById("formPago").addEventListener("submit", (e) => {
e.preventDefault();
const monto = parseFloat(document.getElementById("fareAmount").value);
const bus = document.getElementById("busNumber").value;

if (!bus || isNaN(monto) || monto <= 0) {
    alert("Por favor, ingresa un número de bus válido y monto correcto.");
    return;
}

registrarMovimiento("Pago Bus " + bus, monto);
e.target.reset();
});

//Recarga
document.getElementById("formRecarga").addEventListener("submit", (e) => {
e.preventDefault();
const monto = parseFloat(document.getElementById("montoRecarga").value);

if (isNaN(monto) || monto <= 0) {
    alert("Ingresa un monto válido para recargar.");
    return;
}

    registrarMovimiento("Recarga", monto);
    e.target.reset();
    });

//Función para actualizar saldo en pantalla
function actualizarSaldo(cambio) {
    const saldoElem = document.getElementById("saldoActual");
    let saldo = parseFloat(saldoElem.textContent.replace("S/ ", ""));
    saldo += cambio;
    saldoElem.textContent = "S/ " + saldo.toFixed(2);
}
