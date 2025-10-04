cargarMovimientos();

const openBtn = document.getElementById("openModal");
const modal = document.getElementById("manualModal");
const closeBtn = document.getElementById("closeModal");
const form = document.getElementById("formManualRecarga");
const saldoDisplay = document.getElementById("saldoActual");

openBtn.addEventListener("click", () => {
    modal.style.display = "block";
});

closeBtn.addEventListener("click", () => {
    modal.style.display = "none";
});

window.addEventListener("click", (e) => {
    if (e.target === modal) {
        modal.style.display = "none";
    }
});

form.addEventListener("submit", (e) => {
    e.preventDefault();
    const monto = parseFloat(document.getElementById("montoManual").value);

    if (isNaN(monto) || monto <= 0) {
        alert("Ingresa un monto válido.");
        return;
    }

    registrarMovimiento("Recarga", monto);

    let currentSaldo = parseFloat(saldoDisplay.textContent.replace("S/ ", ""));
    saldoDisplay.textContent = "S/ " + (currentSaldo + monto).toFixed(2);

    modal.style.display = "none";
    form.reset();
});