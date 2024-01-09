function setActiveForm(form) {
    document.getElementById("edit").disabled = false;
    document.getElementById("publish").disabled = false;
    document.getElementById("delete").disabled = false;
    document.getElementById(form).disabled = true;
    document.getElementById("edit-form").classList.remove("show");
    document.getElementById("publish-form").classList.remove("show");
    document.getElementById("delete-form").classList.remove("show");
    document.getElementById(form + "-form").classList.add("show");
}

function setupForms() {
    document.getElementById("edit-form").addEventListener("submit", function (event) {
        event.preventDefault();
        editGame();
    });
    document.getElementById("publish-form").addEventListener("submit", function (event) {
        event.preventDefault();
        publishGame();
    });
    document.getElementById("delete-form").addEventListener("submit", function (event) {
        event.preventDefault();
        deleteGame();
    });
}

function editGame() {
}

function publishGame() {
}

function deleteGame() {
}