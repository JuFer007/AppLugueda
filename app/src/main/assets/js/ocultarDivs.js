document.addEventListener('DOMContentLoaded', function () {
    const navLinks = document.querySelectorAll('.offcanvas-nav .nav-link');
    const sections = document.querySelectorAll('main > section');
    const offcanvasElement = document.getElementById('offcanvasNavbar');
    const offcanvas = new bootstrap.Offcanvas(offcanvasElement);

    function hideAllSections() {
        sections.forEach(section => {
            section.style.display = 'none';
        });
    }

    navLinks.forEach(link => {
        link.addEventListener('click', function (event) {
            event.preventDefault();

            hideAllSections();

            const targetId = this.getAttribute('data-target');
            const targetSection = document.querySelector(targetId);
            if (targetSection) {
                targetSection.style.display = 'block';
            }

            offcanvas.hide();
        });
    });
});