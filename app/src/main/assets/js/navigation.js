document.addEventListener('DOMContentLoaded', function () {
    const navLinks = document.querySelectorAll('.offcanvas-nav .nav-link');
    const sections = document.querySelectorAll('main > section');
    const offcanvasElement = document.getElementById('offcanvasNavbar');
    const offcanvas = new bootstrap.Offcanvas(offcanvasElement);

    // Función para ocultar todas las secciones
    function hideAllSections() {
        sections.forEach(section => {
            section.style.display = 'none';
        });
    }

    navLinks.forEach(link => {
        link.addEventListener('click', function (event) {
            event.preventDefault();

            // Ocultar todas las secciones
            hideAllSections();

            // Mostrar la sección objetivo
            const targetId = this.getAttribute('data-target');
            const targetSection = document.querySelector(targetId);
            if (targetSection) {
                targetSection.style.display = 'block';
            }

            // Opcional: cerrar el offcanvas después de hacer clic
            offcanvas.hide();
        });
    });
});