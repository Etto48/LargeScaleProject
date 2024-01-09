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
    data = {
        name: document.getElementById("game-name-edit").value,
        description: document.getElementById("game-description-edit").value,
        image: document.getElementById("game-img-edit").value,
        genres: getVectorEntries("game-genres-edit"),
        platforms: getVectorEntries("game-platforms-edit"),
        developers: getVectorEntries("game-developers-edit"),
        publishers: getVectorEntries("game-publishers-edit"),
        release_date: document.getElementById("game-release-date-edit").value,
    }

    $.ajax({
        type: "POST",
        url: "/api/company/edit-game",
        data: data,
        success: function (response) {
            alert("Game edited successfully!");
            resetForms();
        },
        error: function (xhr, status, error) {
            alert("Error editing game: " + xhr.status + " " + xhr.statusText);
        }
    });
}

function publishGame() {
    data = {
        name: document.getElementById("game-name-publish").value,
        description: document.getElementById("game-description-publish").value,
        image: document.getElementById("game-img-publish").value,
        genres: getVectorEntries("game-genres-publish"),
        platforms: getVectorEntries("game-platforms-publish"),
        developers: getVectorEntries("game-developers-publish"),
        publishers: getVectorEntries("game-publishers-publish"),
        release_date: document.getElementById("game-release-date-publish").value,
    }

    $.ajax({
        type: "POST",
        url: "/api/company/publish-game",
        data: data,
        success: function (response) {
            alert("Game published successfully!");
            resetForms();
        },
        error: function (xhr, status, error) {
            alert("Error publishing game: " + xhr.status + " " + xhr.statusText);
        }
    });
}

function deleteGame() {
    if (document.getElementById("game-name-delete").value != document.getElementById("game-name-repeat-delete").value) {
        alert("You must write the game name correctly to be sure you want to delete it!");
        return;
    }

    if (!confirm("Are you really sure you want to delete this game?")) {
        return;
    }

    data = {
        name: document.getElementById("game-name-delete").value,
    }

    $.ajax({
        type: "POST",
        url: "/api/company/delete-game",
        data: data,
        success: function (response) {
            alert("Game deleted successfully!");
            resetForms();
        },
        error: function (xhr, status, error) {
            alert("Error deleting game: " + xhr.status + " " + xhr.statusText);
        }
    });
}

function resetForms() {
    document.getElementById("edit-form").reset();
    resetVectorInput("game-genres-edit");
    resetVectorInput("game-platforms-edit");
    resetVectorInput("game-developers-edit");
    resetVectorInput("game-publishers-edit");
    document.getElementById("publish-form").reset();
    resetVectorInput("game-genres-publish");
    resetVectorInput("game-platforms-publish");
    resetVectorInput("game-developers-publish");
    resetVectorInput("game-publishers-publish");
    document.getElementById("delete-form").reset();
}

function loadGameInfo() {
    var game_name = document.getElementById("game-name-edit").value;
    if (game_name == "") {
        return;
    }
    $.ajax({
        url: "/api/game",
        type: "GET",
        data: {
            name: game_name
        },
        success: function (data) {
            document.getElementById("game-description-edit").value = data.description;
            document.getElementById("game-img-edit").value = data.img;
            resetVectorInput("game-genres-edit");
            data.genres.forEach(function (genre) {
                addVectorEntryWithId("game-genres-edit", genre);
            });
            resetVectorInput("game-platforms-edit");
            data.platforms.forEach(function (platform) {
                addVectorEntryWithId("game-platforms-edit", platform);
            });
            resetVectorInput("game-developers-edit");
            data.developers.forEach(function (developer) {
                addVectorEntryWithId("game-developers-edit", developer);
            });
            resetVectorInput("game-publishers-edit");
            data.publishers.forEach(function (publisher) {
                addVectorEntryWithId("game-publishers-edit", publisher);
            });
            document.getElementById("game-release-date-edit").type = "date";
            document.getElementById("game-release-date-edit").value = data.release;
        },
        error: function (xhr, status, error) {
            console.log("Error loading game info: " + xhr.status + " " + xhr.statusText);
        }
    });
}