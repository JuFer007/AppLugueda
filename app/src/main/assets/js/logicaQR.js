function processPayment() {
    Android.agregarMovimiento("Pago Bus " + document.getElementById('numeroBus').innerText, 2.0);

    const modal = document.getElementById('successModal');
    modal.style.display = 'flex';

    setTimeout(() => {
        modal.style.display = 'none';
        window.location.href = "file:///android_asset/index.html";
    }, 4000);
}

    function cancelPayment() {
        if (confirm('¿Estás seguro de que deseas cancelar el pago?')) {
        window.location.href = "file:///android_asset/index.html";
        }
    }

    function obtenerParametroBus() {
        const urlParams = new URLSearchParams(window.location.search);
        const bus = urlParams.get('bus');
        document.getElementById('numeroBus').innerText =  bus;
    }

    window.onload = function() {
    obtenerParametroBus();
};