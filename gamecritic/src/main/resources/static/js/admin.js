function areYouSure(action) {
    return confirm("Do you really want to "+action+"?");
}

function banUser(username) {
    if (!areYouSure("ban \""+username+"\"")) {
        return;
    }
    $.ajax({
        url: "/api/admin/ban",
        data: {username: username},
        type: "POST",
        success: function () {
            alert("User banned!");
            location.reload();
        },
        error: function () {
            alert("Error banning user!");
        }
    });
}

function deleteReview(id) {
    if (!areYouSure("delete this review ("+id+")")) {
        return;
    }
    $.ajax({
        url: "/api/admin/delete/review",
        data: {id: id},
        type: "POST",
        success: function () {
            alert("Review deleted!");
            location.reload();
        },
        error: function () {
            alert("Error deleting review!");
        }
    });
}

function deleteComment(id) {
    if (!areYouSure("delete this comment ("+id+")")) {
        return;
    }
    $.ajax({
        url: "/api/admin/delete/comment",
        data: {id: id},
        type: "POST",
        success: function () {
            alert("Comment deleted!");
            location.reload();
        },
        error: function () {
            alert("Error deleting comment!");
        }
    });
}

function deleteGame(name) {
    if (!areYouSure("delete the game \""+name+"\"")) {
        return;
    }
    $.ajax({
        url: "/api/admin/delete/game",
        data: {name: name},
        type: "POST",
        success: function () {
            alert("Game deleted!");
            location.reload();
        },
        error: function () {
            alert("Error deleting game!");
        }
    });
}
