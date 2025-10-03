function doRegister(event){
    event.preventDefault();

    var name = document.getElementById("registerName").value;
    var dni = document.getElementById("registerDNI").value;
    var email = document.getElementById("registerEmail").value;
    var pass = document.getElementById("registerPass").value;

    Android.register(name, dni, email, pass);
}