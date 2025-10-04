function doLogin(event) {
    event.preventDefault();
    var email = document.getElementById("loginEmail").value;
    var pass = document.getElementById("loginPassword").value;
    var rememberMe = document.getElementById("rememberMe").checked;

    Android.login(email, pass);

    if (rememberMe) {
        localStorage.setItem("rememberedEmail", email);
    } else {
        localStorage.removeItem("rememberedEmail");
    }
}

document.addEventListener("DOMContentLoaded", function () {
    var savedEmail = localStorage.getItem("rememberedEmail");
    if (savedEmail) {
        document.getElementById("loginEmail").value = savedEmail;
        document.getElementById("rememberMe").checked = true;
    }
});
