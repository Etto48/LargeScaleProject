function toggleNewReview() {
    document.getElementById("new-review-form").classList.toggle("show");
}

function closeNewReview(posted) {
    document.getElementById("new-review-form").classList.remove("show");
    if (posted) {
        var button = document.getElementById("create-new-review-button");
        var button_text = document.getElementById("create-new-review-button-text");
        button_text.classList.remove("bi-plus-circle-fill");
        button_text.classList.add("bi-check-circle-fill");
        button.disabled = true;   
    }
}

function enableSlider() {
    var comments = [
        "Overwhelmingly Negative",
        "Very Negative",
        "Negative",
        "Mostly Negative",
        "Mixed",
        "Mostly Positive",
        "Positive",
        "Very Positive",
        "Overwhelmingly Positive",
        "Perfect"
    ];

    var slider = document.getElementById("new-review-score");
    var score = document.getElementById("new-review-score-display");
    var comment = document.getElementById("new-review-comment");
    var score_bg = document.getElementById("new-review-score-display-bg");
    slider.addEventListener("input", function() {
        score.innerHTML = slider.value;
        if (slider.value < 4) {
            score_bg.classList.add("bg-danger");
            score_bg.classList.remove("bg-warning");
            score_bg.classList.remove("bg-success");
        } else if (slider.value < 7) {
            score_bg.classList.remove("bg-danger");
            score_bg.classList.add("bg-warning");
            score_bg.classList.remove("bg-success");
        } else if (slider.value < 10) {
            score_bg.classList.remove("bg-danger");
            score_bg.classList.remove("bg-warning");
            score_bg.classList.add("bg-success");
        }
        else {
            score_bg.classList.remove("bg-danger");
            score_bg.classList.remove("bg-warning");
            score_bg.classList.add("bg-success");
        }
        comment.innerHTML = comments[slider.value - 1];
    });
}

function appendReview(user, quote, id) {
    // TODO: Implement this (low priority)
}

function createReview() {
    var quote = document.getElementById("new-review-quote").value
    $.ajax({
        type: "POST",
        url: "/api/review/new",
        data: {
            game: document.getElementById("new-review-game").value,
            score: document.getElementById("new-review-score").value,
            quote
        },
        success: function (data) {
            closeNewReview(true);
            appendReview(data.author, quote, data.id)
        },
        error: function (xhr, status, error) {
            if(xhr.status == 409) {
                alert("You have already reviewed this game");
            }
            else {
                alert("Error creating review");
            }
        }
    });
}

function prepareReviewForm() {
    enableSlider();
    document.getElementById("new-review-form").addEventListener("submit", function (event) {
        event.preventDefault();
        createReview();
    });
}