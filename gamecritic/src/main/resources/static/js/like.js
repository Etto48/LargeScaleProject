function sendLike(button_id, type, input_data) {
    var like_button = document.getElementById(button_id);
    like_button.disabled = true;
    $.ajax({
        url: "/api/like/set/"+type,
        type: "POST",
        data: input_data,
        success: function (data) {
            like_button.disabled = false;
            var old_like_count = parseInt($(like_button).find(".like-count").html());
            if(input_data.liked) {
                $(like_button).find("i.bi").removeClass("bi-heart").addClass("bi-heart-fill");
                $(like_button).find(".like-text").html("Liked");
                $(like_button).find(".like-count").html(old_like_count+1);
            }
            else 
            {
                $(like_button).find("i.bi").removeClass("bi-heart-fill").addClass("bi-heart");
                $(like_button).find(".like-text").html("Like");
                $(like_button).find(".like-count").html(old_like_count-1);
            }
        },
        error: function (e) {
            console.log(e);
        }
    });
}

function likeGame(name) {
    var like_button = document.getElementById("like-game-button");
    var is_already_liked = $(like_button).find("i.bi").hasClass("bi-heart-fill");
    sendLike("like-game-button","game",{name:name, liked:!is_already_liked});
}

function likeReview(review_id) {
    var like_button = document.getElementById("like-review-"+review_id+"-button");
    var is_already_liked = $(like_button).find("i.bi").hasClass("bi-heart-fill");
    sendLike("like-review-"+review_id+"-button","review",{id:review_id, liked:!is_already_liked});
}

function loadLikes(button_id, type, data) {
    var like_button = document.getElementById(button_id);
    $.ajax({
        url: "/api/like/get/"+type,
        type: "GET",
        data: data,
        success: function (data) {
            $(like_button).find(".like-count").html(data.likes);
            if(data.liked) {
                $(like_button).find("i.bi").removeClass("bi-heart").addClass("bi-heart-fill");
                $(like_button).find(".like-text").html("Liked");
            }
            else {
                $(like_button).find("i.bi").removeClass("bi-heart-fill").addClass("bi-heart");
                $(like_button).find(".like-text").html("Like");
            }
            
        },
        error: function (e) {
            console.log(e);
        }
    });
}

function loadLikesGame(name) {
    loadLikes("like-game-button","game",{name:name});
    var like_button = document.getElementById("like-game-button");
    like_button.addEventListener("click", function() {
        likeGame(name);
    });
}

function loadLikesReview(review_id) {
    var button_id = "like-review-"+review_id+"-button";
    loadLikes(button_id,"review",{id:review_id});
    var like_button = document.getElementById(button_id);
    like_button.addEventListener("click", function() {
        likeReview(review_id);
    });
}