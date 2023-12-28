function prepareSearch() {
    document.getElementById("search-form").addEventListener("submit", function(event) {
        event.preventDefault();
        search();
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
        url: "/search",
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

function sanitize(string) {
    string = string.toString();
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#x27;',
        "/": '&#x2F;',
    };
    const reg = /[&<>"'/]/ig;
    return string.replace(reg, (match)=>(map[match]));
}

function handleSearchResults(data) {
    
    var result = "";

    result += "<div class=\"search-header\">Games</div><hr class=\"mx-0 mt-0 mb-1\"/>";

    data.games.forEach(function(game) {
        result += "<a class=\"game-result search-result\" href=\"/game/" + encodeURIComponent(game.name) + "\">" + sanitize(game.name) + "</a>";
    });

    result += "<div class=\"search-header\">Users</div><hr class=\"mx-0 mt-0 mb-1\"/>";

    data.users.forEach(function(user) {
        result += "<a class=\"user-result search-result\" href=\"/user/" + encodeURIComponent(user.username) + "\"><img src=\"/user_image/"+ encodeURIComponent(user.username)+".png\" alt=\"User Image\"></image>" + sanitize(user.username) + "</a>";
    });

    document.getElementById("search-results").innerHTML = result;
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
}