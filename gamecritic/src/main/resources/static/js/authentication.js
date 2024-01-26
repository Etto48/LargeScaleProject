function login() {
    var username = document.getElementById("username_login").value;
    var password = document.getElementById("password_login").value;
    $.ajax({
        url: "/api/login",
        type: "POST",
        data: {
            username: username,
            password: password
        },
        success: function (data) {
            if (data === "success") {
                window.location.href = "/";
            } else {
                document.getElementById("main").classList.add("error");
                document.getElementById("error").innerHTML = "Invalid username or password";
            }
        },
        error: function (data) {
            document.getElementById("main").classList.add("error");
            document.getElementById("error").innerHTML = "Invalid username or password";
        }
    });
}

function sign_up() {
    var username = document.getElementById("username_sign_up").value;
    var email = document.getElementById("email_sign_up").value;
    var password = document.getElementById("password_sign_up").value;
    var confirm_password = document.getElementById("password_check_sign_up").value;
    
    if (password !== confirm_password) {
        document.getElementById("main").classList.add("error");
        document.getElementById("error").innerHTML = "Passwords do not match";
    }
    else 
    {
        $.ajax({
            url: "/api/sign_up",
            type: "POST",
            data: {
                username: username,
                email: email,
                password: password
            },
            success: function (data) {
                if (data === "success") {
                    window.location.href = "/";
                } else {
                    document.getElementById("main").classList.add("error");
                    document.getElementById("error").innerHTML = "Invalid username or password";
                }
            },
            error: function (data) {
                document.getElementById("main").classList.add("error");
                document.getElementById("error").innerHTML = "Invalid username or password";
            }
        });
    }
}

function prepareForm(id,mode) {
    document.getElementById(id).addEventListener('submit', function (event) {
        event.preventDefault(); // Prevents the default form submission
        if (mode === "login") {
            login();
        }
        else if (mode === "sign_up") {
            sign_up();
        }
        else 
        {
            throw new Error("Invalid form mode");
        }
    });

    // for each input add an event listener to remove the error class when the user starts typing
    var inputs = document.getElementById(id).getElementsByTagName("input");
    for (var i = 0; i < inputs.length; i++) {
        inputs[i].addEventListener('input', function (event) {
            document.getElementById("main").classList.remove("error");
            document.getElementById("error").innerHTML = "";
        });
    }
}