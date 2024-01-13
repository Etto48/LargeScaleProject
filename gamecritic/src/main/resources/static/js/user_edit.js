var email_changed = false;
var password_changed = false;

function showUserEditForm() {
    var form = document.getElementById("user-edit");
    form.classList.add("show");
}

function hideUserEditForm() {
    var form = document.getElementById("user-edit");
    form.classList.remove("show");
    document.getElementById("user-edit-form").reset();
}

function prepareUserEditForm(username) {
    var image_input = $("#user-edit-image-label");
    var image_url = "/user-image/" + encodeURIComponent(username) + ".png";
    var loader = document.getElementById("user-edit-image-loading");
    image_input.css("background-image", "url('" + image_url + "')");

    $("#user-edit-email").on("input", function(event) {
        email_changed = true;
    });

    $("#user-edit-password").on("input", function(event) {
        password_changed = true;
    });

    $("#user-edit").on("submit", function(event) {
        event.preventDefault();
        var email = $("#user-edit-email").val();
        var password = $("#user-edit-password").val();
        var password_confirm = $("#user-edit-password-confirm").val();
        if (password != password_confirm) {
            alert("Passwords do not match");
            return;
        }
        var image_data = $("#user-edit-image-input")[0].files[0];
        if (image_data) {
            if (image_data.size > 1024 * 1024) {
                alert("Image is too large");
                return;
            }
            if (image_data.type != "image/png") {
                alert("Image must be a PNG");
                return;
            }
        }
        var data = new FormData();
        if(email.length > 0 && email_changed)
        {
            data.append('email', email);
        }
        if(password.length > 0 && password_changed)
        {
            data.append('password', password);
        }
        if (image_data)
        {
            data.append('image', image_data);
        }

        $.ajax({
            url: "/api/user/edit",
            type: "POST",
            data: data,
            processData: false,
            contentType: false,
            success: function(data) {
                hideUserEditForm();
                location.reload();
            },
            error: function(xhr, status, error) {
                alert("Could not edit user: " + error);
            }
        });
    });

    $("#user-edit-image-input").on("change", function(event) {
        
        const file = event.target.files[0];
        loader.classList.add("show");
        image_input.disabled = true;

        if (file) {
            const reader = new FileReader();

            reader.onload = function (e) {
                loader.classList.remove("show");
                image_input.css("background-image", `url(${e.target.result})`);
                image_input.disabled = false;
            };

            reader.readAsDataURL(file);
        } else {
            // Reset background if no file is selected
            image_input.css("background-image", "none");
        }
    });
}