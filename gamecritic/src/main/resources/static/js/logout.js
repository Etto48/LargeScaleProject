function logout() {
    $.ajax({
        url: "/api/logout",
        type: "POST",
        success: function (data) {
            window.location.href = "/";
        },
        error: function (data) {
            window.location.href = "/";
            alert("Error logging out");
        }
    });
}