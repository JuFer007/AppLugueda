document.addEventListener('DOMContentLoaded', function () {
    const toggleButtons = document.querySelectorAll('.toggle-btn');
    const forms = document.querySelectorAll('.auth-form');

    toggleButtons.forEach(button => {
        button.addEventListener('click', () => {
            const targetFormId = button.getAttribute('data-form');
            const targetForm = document.getElementById(targetFormId);

            toggleButtons.forEach(btn => btn.classList.remove('active'));
            forms.forEach(form => form.classList.remove('active'));

            button.classList.add('active');
            if (targetForm) {
                targetForm.classList.add('active');
            }
        });
    });

    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', function(event) {
            event.preventDefault();

            const email = document.getElementById('loginEmail').value;
            const password = document.getElementById('loginPassword').value;

            const registeredUser = {
                email: 'marcelo@test.com',
                password: '123',
                name: 'Marcelo Huaman',
                dni: '12345678',
                phone: '+51 987 654 321',
                bio: 'Usuario frecuente de Lugueda App. Viajando de forma inteligente y segura por la ciudad.'
            };

            if (email === registeredUser.email && password === registeredUser.password) {
                localStorage.setItem('isLoggedIn', 'true');
                localStorage.setItem('userData', JSON.stringify(registeredUser));
                window.location.href = '/index.html';
            } else {
                alert('Correo o contraseña incorrectos.');
            }
        });
    }
});
