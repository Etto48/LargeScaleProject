var last_entry_id = 0;

function addVectorEntry(button) {
    var target_id = button.parentNode.parentNode.id;
    var entry = $("#"+target_id + " .text-input input").val();
    if (entry != "") {
        addVectorEntryWithId(target_id, entry);
    }
    $("#"+target_id + " .text-input input").val("");
}

function addVectorEntryWithId(id, text) {
    var template = Handlebars.compile(document.getElementById("vector-entry-template").innerHTML);
    var html = template({text: text, id: "entry-" + last_entry_id++});
    $("#"+id).append(html);
}

function removeVectorEntry(entry_id) {
    document.getElementById(entry_id).remove();
}

function getVectorEntries(target_vector_input) {
    var entries = [];
    $("#" + target_vector_input + " .vector-entry-text").each(function (index) {
        entries.push($(this).text());
    });
    return entries;
}

function setupVectorInput(id) {
    $("#"+id+" .text-input input")[0].addEventListener("keydown", function (event) {
        if (event.keyCode === 13) {
            event.preventDefault();
            addVectorEntry($("#"+id+" .text-input button")[0]);
        }
    });
}

function resetVectorInput(id) {
    $("#"+id+" .vector-entry").each(function (index) {
        $(this).remove();
    });
}