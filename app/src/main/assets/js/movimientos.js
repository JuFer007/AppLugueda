// Registrar movimiento
function registrarMovimiento(tipo, monto) {
    if (typeof Android !== "undefined") {
        Android.agregarMovimiento(tipo, monto);
        cargarMovimientos();

        if (!tipo.toLowerCase().includes("recarga")) {
            generarTicket(tipo, monto);
        }
    }
}

//Pago Manual
document.getElementById("formPago").addEventListener("submit", (e) => {
    e.preventDefault();
    const monto = 2.00;
    const bus = document.getElementById("busNumber").value;

    const saldoElem = document.getElementById("saldoActual");
    let saldo = parseFloat(saldoElem.textContent.replace("S/ ", ""));

    if (!bus || isNaN(monto) || monto <= 0) {
        alert("Por favor, ingresa un número de bus válido.");
        return;
    }

    if (saldo < monto) {
        alert("Saldo insuficiente. Recarga antes de pagar.");
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

//Actualizar saldo en pantalla
function actualizarSaldo(movimientos) {
    const saldoElem = document.getElementById("saldoActual");
    let saldo = 0;

    movimientos.forEach(mov => {
        if (mov.tipo.toLowerCase().includes("recarga")) {
            saldo += mov.monto;
        } else {
            saldo -= mov.monto;
        }
    });

    saldoElem.textContent = "S/ " + saldo.toFixed(2);
}

//Generar ticket de pago bus
function generarTicket(tipo, monto) {
    const busNumber = tipo.replace("Pago Bus ", "");
    const fecha = new Date().toLocaleDateString("es-PE");
    const estado = "PAGADO";

    const ticketHTML = `
        <div class="digital-ticket">
            <div class="ticket-header">Último Ticket Generado</div>
            <div class="ticket-body">
                <div class="ticket-item"><span>Bus:</span><strong>${busNumber}</strong></div>
                <div class="ticket-item"><span>Fecha:</span><strong>${fecha}</strong></div>
                <div class="ticket-item"><span>Monto:</span><strong>S/ ${monto.toFixed(2)}</strong></div>
                <div class="ticket-item"><span>Estado:</span><strong class="text-success">${estado}</strong></div>
            </div>
            <div class="ticket-footer">¡Buen Viaje!</div>
        </div>
    `;
    document.getElementById("ultimoTicketGenerado").innerHTML = ticketHTML;
}

//Cargar movimientos desde el JSON
function cargarMovimientos() {
    try {
        if (typeof Android !== "undefined") {
            const estado = JSON.parse(Android.getEstadoTarjeta());
            const movimientos = estado.movimientos || [];
            const saldo = estado.saldo;

            const contenedorMovimientos = document.querySelector(".mt-4");
            const lista = document.getElementById("movimientosList");
            lista.innerHTML = "";

            if (movimientos.length === 0) {
                contenedorMovimientos.style.display = "none";
                document.getElementById("saldoActual").textContent = "S/ " + saldo.toFixed(2);
                return;
            } else {
                contenedorMovimientos.style.display = "block";
            }

            movimientos.forEach(mov => {
                let li = document.createElement("li");
                li.classList.add("list-group-item", "d-flex", "justify-content-between", "align-items-center");

                const badgeClass = mov.tipo.toLowerCase().includes("recarga") ? "bg-success" : "bg-danger";
                const signo = mov.tipo.toLowerCase().includes("recarga") ? "+" : "-";

                li.innerHTML = `
                    <div>
                        <div class="fw-bold">${mov.tipo}</div>
                        <small class="text-muted">${mov.fecha}</small>
                    </div>
                    <span class="badge rounded-pill ${badgeClass}">
                        ${signo} S/ ${mov.monto.toFixed(2)}
                    </span>
                `;
                lista.prepend(li);
            });

            document.getElementById("saldoActual").textContent = "S/ " + saldo.toFixed(2);
        }
    } catch (e) {
        console.error("Error cargando movimientos:", e);
    }
}

document.addEventListener("DOMContentLoaded", cargarMovimientos);
