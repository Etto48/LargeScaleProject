function prepareSearch() {
    document.getElementById("search-form").addEventListener("submit", function(event) {
        event.preventDefault();
        search();
    });

    document.getElementById("search-text").addEventListener("input", function(event) {
        if (document.getElementById("search-text").value.length > 0) {
            search();
        } else {
            hideSearchResults();
        }
    });

    document.addEventListener("click", function(event) {
        if (!event.target.closest("#search-results")) {
            hideSearchResults();
        }
    });

    document.getElementById("search-form").addEventListener("keydown", function(event) {
        if (event.key === "Escape") {
            hideSearchResults();
        }
    });

    
    window.addEventListener("resize", function(event) {
        resizeSearchResults();
    });

    window.addEventListener("load", function(event) {
        resizeSearchResults();
    });
}

function search() {
    $.ajax({
        url: "/api/search",
        type: "GET",
        data: {
            query: document.getElementById("search-text").value
        },
        success: function (data) {
            handleSearchResults(data);
        },
        error: function (data) {
            handleSearchResults({});
        }
    });
}

function handleSearchResults(data) {

    var template = Handlebars.compile(document.getElementById("search-result-template").innerHTML);
    data.games.forEach(function (game) {
        var url = "/game/" + encodeURIComponent(game.name);
        game.url = url;
    });
    data.users.forEach(function (user) {
        var url = "/user/" + encodeURIComponent(user.username);
        user.url = url;
        var img = "/user_image/" + encodeURIComponent(user.username) + ".png";
        user.img = img;
    });
    var html = template({games: data.games, users: data.users})

    document.getElementById("search-results").innerHTML = html;
    document.getElementById("search-results").classList.add("show");
    document.getElementById("search-form").classList.add("list");
}

function hideSearchResults() {
    document.getElementById("search-results").classList.remove("show");
    document.getElementById("search-form").classList.remove("list");
    document.getElementById("search-results").innerHTML = "";
}

function resizeSearchResults() {
    var searchResults = document.getElementById("search-results");
    var searchText = document.getElementById("search-text");

    searchResults.style.width = searchText.clientWidth + "px";
    searchResults.style.left = searchText.offsetLeft + "px";
}