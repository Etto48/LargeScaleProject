var current_page = 0;
var kind = "hottest";

function addSuggestions() {
    $.ajax({
        url: "/api/user/suggest",
        type: "GET",
        success: function (data) {
            if(Object.keys(data).length == 0 || (data.users.length == 0 && data.games.length == 0)) {
                return;
            }
            var template = Handlebars.compile(document.getElementById("suggestions-template").innerHTML);
            data.users.forEach(function (user) {
                user.url = "/user/" + encodeURIComponent(user.username);
                user.img = "/user/" + encodeURIComponent(user.username) + "/image.png";
            });
            var html = template(data);
            $(html).insertBefore("#dummy-loading-game");
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function addGames(page) {
    $.ajax({
        url: "/api/top-games",
        type: "GET",
        data: {
            page: page,
            kind: kind
        },
        success: function (data) {
            console.log(data)
            var tempScrollTop = $(window).scrollTop();
            var template = Handlebars.compile(document.getElementById("game-template").innerHTML);
            data.forEach(function (game) {

                console.log("user_review: "+game.customAttributes.user_review)
                game.user_score = game.customAttributes.user_review;
                game.description = game.customAttributes.Description;
                if (game.customAttributes.img)
                {
                    game.img = game.customAttributes.img;
                }
                else
                {
                    game.img = "/img/missing_game_image.png";
                }
                if(game.customAttributes.user_review)
                {
                    game.user_score = game.user_score.toFixed(1);
                    if(game.user_score == 10.0)
                    {
                        game.user_score = 10;
                    }
                }
                else 
                {
                    game.user_score = "-";
                }
                var href = "/game/" + encodeURIComponent(game.name);
                var max_description_length = 500;
                if (game.customAttributes.Description.length > max_description_length) {
                    game.description = game.customAttributes.Description.substring(0, max_description_length) + "...";
                }
                var score_good = game.user_score > 6;
                var score_mid = game.user_score > 3 && game.user_score <= 6;
                var score_bad = game.user_score <= 3;
                var score_none = game.user_score == "-";
                var template_data = {game: game, href, score_good, score_mid, score_bad, score_none};
                html = template(template_data);
                $(html).insertBefore("#dummy-loading-game");
            });
            addSuggestions();
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
            console.log("helloooo!");
            addGames(current_page++);
        }
    }
    addGames(current_page++);
    setInterval(checkAndAddGames, 400);
});