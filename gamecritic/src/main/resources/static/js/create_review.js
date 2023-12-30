function toggleNewReview() {
    document.getElementById("new-review-form").classList.toggle("show");
}

function closeNewReview() {
    document.getElementById("new-review-form").classList.remove("show");
    var button = document.getElementById("create-new-review-button");
    var button_text = document.getElementById("create-new-review-button-text");
    button_text.classList.remove("bi-plus-circle-fill");
    button_text.classList.add("bi-check-circle-fill");
    button.disabled = true;
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

function createReview() {
    $.ajax({
        type: "POST",
        url: "/api/review",
        data: {
            game: document.getElementById("new-review-game").value,
            score: document.getElementById("new-review-score").value,
            quote: document.getElementById("new-review-quote").value
        },
        success: function (data) {
            closeNewReview();
        },
        error: function (data) {
            alert("Error creating review");
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