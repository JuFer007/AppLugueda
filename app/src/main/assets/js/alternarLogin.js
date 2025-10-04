const toggleButtons = document.querySelectorAll(".toggle-btn");
const forms = document.querySelectorAll(".auth-form");

toggleButtons.forEach(btn => {
    btn.addEventListener("click", () => {
        toggleButtons.forEach(b => b.classList.remove("active"));
        btn.classList.add("active");

        forms.forEach(form => form.classList.remove("active"));
        const targetForm = document.getElementById(btn.dataset.form);
        targetForm.classList.add("active");
    });
});
