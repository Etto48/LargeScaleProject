var current_page = 0;
var kind = "hottest";

function addGames(page) {
    $.ajax({
        url: "/api/top-games",
        type: "GET",
        data: {
            page: page,
            kind: kind
        },
        success: function (data) {
            var tempScrollTop = $(window).scrollTop();
            var template = Handlebars.compile(document.getElementById("game-template").innerHTML);
            data.forEach(function (game) {
                game.user_score = game.customAttributes.user_score;
                game.description = game.customAttributes.description;
                game.img = game.customAttributes.img;
                var href = "/game/" + encodeURIComponent(game.name);
                game.user_score = game.user_score.toFixed(1);
                var max_description_length = 500;
                if (game.description.length > max_description_length) {
                    game.description = game.description.substring(0, max_description_length) + "...";
                }
                var template_data = {game: game, href, score_good: game.user_score > 6, score_mid: game.user_score > 3 && game.user_score <= 6, score_bad: game.user_score <= 3};
                html = template(template_data);
                $(html).insertBefore("#dummy-loading-game");
            });
            window.scroll({top: tempScrollTop, behavior: "instant"});
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function setKind(new_kind) {
    kind = new_kind;
    current_page = 0;
    var main = document.getElementById("main");
    var template = document.getElementById("game-template");
    var dummy_loading_game = document.getElementById("dummy-loading-game");
    $(main).empty();
    $(main).append(template);
    $(main).append(dummy_loading_game);

    document.getElementById("hottest").disabled = false;
    document.getElementById("newest").disabled = false;
    document.getElementById("best").disabled = false;
    document.getElementById(kind).disabled = true;
    addGames(current_page++);
}

document.addEventListener("DOMContentLoaded", function (event) {
    function checkAndAddGames() {
        if (canSee("dummy-loading-game")) {
            addGames(current_page++);
        }
    }
    addGames(current_page++);
    setInterval(checkAndAddGames, 400);
});