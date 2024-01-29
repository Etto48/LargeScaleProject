var last_entry_id = 0;

function addVectorEntry(button) {
    var target_id = button.parentNode.parentNode.id;
    console.log("target id "+target_id)
    var entry = $("#"+target_id + " .text-input input").val();
    console.log("entry: "+entry);
    if (entry != "") {
        addVectorEntryWithId(target_id, entry);
    }
    $("#"+target_id + " .text-input input").val("");
}

function addNewVectorEntry(button) {
    var target_id = button.parentNode.parentNode.id;
    var name = $("#"+target_id + " .text-input input:first").val();
    var value = $("#"+target_id + " .text-input input:eq(1)").val();
    console.log("name: "+name+" value: "+value);
    if (name != "" && value != "") {
        addNewVectorEntryWithId(target_id, name, value);
    }
    $("#"+target_id + " .text-input input").val("");
}


function addVectorEntryWithId(id, text) {
    var template = Handlebars.compile(document.getElementById("vector-entry-template").innerHTML);
    var html = template({text: text, id: "entry-" + last_entry_id++});
    $("#"+id).append(html);
}

function addNewVectorEntryWithId(id, name, value) {
    var elem = document.getElementById("entry-" + name);
    var template = Handlebars.compile(document.getElementById("vector-entry-template1").innerHTML);
    if (elem){
        console.log("old id "+"NewValueentry-"+name)
        var old = document.getElementById("NewValueentry-"+name).textContent

        elem.innerHTML = template({ name: name, value: old+" | "+value, idNew: "entry-" + name });
    }
    else{
        var html = template({name: name, value:value, idNew: "entry-" + name});
        $("#"+id).append(html);
    }

}

function removeVectorEntry(entry_id) {
    console.log("entry_id: "+entry_id)
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
    var escapedId = id.replace(/([()])/g, "\\$1");
    console.log(escapedId);
    var inputElement = $("#" + escapedId + " .text-input input")[0];
    if (inputElement) console.log("yes" + escapedId)
    else console.log("no" + escapedId)
    $("#"+escapedId+" .text-input input")[0].addEventListener("keydown", function (event) {
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