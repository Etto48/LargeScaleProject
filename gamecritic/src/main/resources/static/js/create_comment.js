var review_id = null;

function toggleNewComment() {
    document.getElementById("new-comment-form-container").classList.toggle("show");
}

function openNewComment() {
    document.getElementById("new-comment-form-container").classList.add("show");
}

function closeNewComment() {
    document.getElementById("new-comment-form-container").classList.remove("show");
    document.getElementById("new-comment-form").reset();
}

function appendComment(user, quote, id) {
    var template = Handlebars.compile(document.getElementById("comment-template").innerHTML);
    var html = template({comment: {author: user, quote, id}, url: "/user/" + encodeURIComponent(user), img: "/user/" + encodeURIComponent(user) + "/image.png"});
    document.getElementById("comment-list").innerHTML = html + document.getElementById("comment-list").innerHTML ;
}

function createComment() {
    var quote = document.getElementById("new-comment-quote").value;
    $.ajax({
        type: "POST",
        url: "/api/comment/new",
        data: {
            quote,
            review_id
        },
        success: function (data) {
            closeNewComment();
            appendComment(data.author, quote, data.id);
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function prepareCommentForm(id) {
    review_id = id;
    document.getElementById("new-comment-form").addEventListener("submit", function (event) {
        event.preventDefault();
        createComment();
    });
}