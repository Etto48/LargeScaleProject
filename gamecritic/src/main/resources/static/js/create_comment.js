var parent_id = null;
var review_id = null;

function toggleNewComment() {
    document.getElementById("new-comment-form").classList.toggle("show");
}

function openNewComment(id) {
    parent_id = id;
    document.getElementById("new-comment-form").classList.add("show");
}

function closeNewReview() {
    document.getElementById("new-comment-form").classList.remove("show");
    document.getElementById("new-comment-quote").value = "";
}

function appendComment(parent, user, quote, id) {
    var template = Handlebars.compile(document.getElementById("comment-template").innerHTML);
    var html = template({comment: {author: user, quote, id}, url: "/user/" + encodeURIComponent(user), img: "/user/" + encodeURIComponent(user) + "/image.png"});
    if (parent == null) {
        document.getElementById("main").innerHTML += html;
    } else {
        var parent = document.getElementById("comment-" + parent);
        var subcomment_list = parent.getElementsByClassName("subcomment-list")[0];
        if (subcomment_list == null) {
            var subcomment_template = Handlebars.compile(document.getElementById("subcomment-template").innerHTML);
            var subcomment_html = subcomment_template();
            parent.innerHTML += subcomment_html;
            parent.getElementsByClassName("subcomment-list")[0].innerHTML += html;
        } else {
            subcomment_list.innerHTML += html;
        }
    }
}

function createComment() {
    var quote = document.getElementById("new-comment-quote").value;
    $.ajax({
        type: "POST",
        url: "/api/comment/new",
        data: {
            quote,
            review_id,
            parent_id
        },
        success: function (data) {
            closeNewReview();
            appendComment(parent_id, data.author, quote, data.id);
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