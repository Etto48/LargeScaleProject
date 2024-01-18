function buttonEditStopFollowing(button)
{
    var button_text = button.getElementsByTagName("span")[0];
    var icon = button.getElementsByTagName("i")[0];
    button_text.innerHTML = "Stop following";
    button.classList.add("btn-secondary");
    button.classList.remove("btn-primary");
    icon.classList.remove("bi-person-fill-add");
    icon.classList.add("bi-person-fill-dash");
}

function buttonEditFollow(button)
{
    var button_text = button.getElementsByTagName("span")[0];
    var icon = button.getElementsByTagName("i")[0];
    button_text.innerHTML = "Follow";
    button.classList.remove("btn-secondary");
    button.classList.add("btn-primary");
    icon.classList.remove("bi-person-fill-dash");
    icon.classList.add("bi-person-fill-add");
}

function prepareFollowButton(target_username)
{
    var button = document.getElementById("follow-button-"+target_username);
    var button_text = button.getElementsByTagName("span")[0];
    $.ajax({
        url: "/api/user/follows",
        data: {
            username: target_username
        },
        type: "GET",
        success: function(response) {
            if (response === true)
            {
                buttonEditStopFollowing(button);
            }
            else
            {
                buttonEditFollow(button);
            }
        },
        error: function(xhr, status, error) {
            console.log("Error getting follow status: " + error);
        }
    })


    button.addEventListener("click", function(event) {
        button.disabled = true;
        $.ajax({
            url: "/api/user/follow",
            data: {
                username: target_username,
                follow: button_text.innerHTML == "Follow"
            },
            type: "POST",
            success: function(data) {
                if (button_text.innerHTML == "Follow")
                {
                    buttonEditStopFollowing(button);
                }
                else
                {
                    buttonEditFollow(button);
                }
                button.disabled = false;
            },
            error: function(xhr, status, error) {
                console.log("Error toggling follow status: " + error);
                button.disabled = false;
            }
        });
    });
}