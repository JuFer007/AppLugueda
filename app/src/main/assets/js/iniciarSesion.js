function doLogin(event) {
    event.preventDefault();
    var email = document.getElementById("loginEmail").value;
    var pass = document.getElementById("loginPassword").value;

    Android.login(email, pass);
}