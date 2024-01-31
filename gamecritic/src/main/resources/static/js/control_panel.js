function setActiveForm(form) {
    console.log("Setting active form to: " + form);
    document.getElementById("terminal").disabled = false;
    document.getElementById("stats").disabled = false;
    document.getElementById(form).disabled = true;
    document.getElementById("terminal-form").classList.remove("show");
    document.getElementById("stats-form").classList.remove("show");
    document.getElementById(form + "-form").classList.add("show");
    if (form == "stats") {
        document.getElementById("stats-loading").style.display = "block";
        loadStats();
    }
}

function setupPanel() {
    document.getElementById("terminal-form").addEventListener("submit", function (event) {
        event.preventDefault();
        runCommand();
    });

    setActiveForm("stats");
}

function runCommand() {
    var command = document.getElementById("terminal-input").value;
    var command_prompt = "> "
    document.getElementById("terminal-input").value = "";
    var log = document.getElementById("terminal-log");
    var template = Handlebars.compile(document.getElementById("terminal-log-entry-template").innerHTML);
    log.innerHTML += template({prompt: command_prompt, text: command, class:"text-light"});
    $.ajax({
        type: "POST",
        url: "/api/admin/terminal",
        data: { command: command },
        success: function (response) {
            if (response.text != "") {
                var color_class = "text-success"
                if (response.error) {
                    color_class = "text-danger"
                }
                log.innerHTML += template({prompt: "", text: response.text, class: color_class});
                log.scrollTop = log.scrollHeight;
            }
        },
        error: function (xhr, status, error) {
            log.innerHTML += template({prompt: "", text: "Error: " + xhr.status + " " + xhr.statusText, class:"text-danger"});
            log.scrollTop = log.scrollHeight;
        }
    });
}