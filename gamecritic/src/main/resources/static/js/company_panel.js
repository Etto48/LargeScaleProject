function setActiveForm(form) {
    document.getElementById("edit").disabled = false;
    document.getElementById("publish").disabled = false;
    document.getElementById("delete").disabled = false;
    document.getElementById("stats").disabled = false;
    document.getElementById(form).disabled = true;
    document.getElementById("edit-form").classList.remove("show");
    document.getElementById("publish-form").classList.remove("show");
    document.getElementById("delete-form").classList.remove("show");
    document.getElementById("stats-form").classList.remove("show");
    document.getElementById(form + "-form").classList.add("show");
    if (form == "stats") {
        loadStats("company");
    }
}
function isValidDateFormat(dateString) {
  var regex = /^\d{4}-\d{2}-\d{2}$/;
  if (!regex.test(dateString)) {
    return false;
  }
  var parts = dateString.split("-");
  var year = parseInt(parts[0], 10);
  var month = parseInt(parts[1], 10);
  var day = parseInt(parts[2], 10);
  if (isNaN(year) || isNaN(month) || isNaN(day)) {
    return false;
  }
  var currentYear = new Date().getFullYear();
  if (year < 1000 || year > currentYear || month < 1 || month > 12 || day < 1 || day > 31) {
    return false;
  }
  return true;
}

function setupForms() {
    document.getElementById("edit-form").addEventListener("submit", function (event) {
        event.preventDefault();
        var items = retrieveAttributesEdit();
        if (items == "bad") return;
        console.log("id da mandare "+document.getElementById("_idHolder").textContent)
        editGame(items);
    });
    document.getElementById("publish-form").addEventListener("submit", function (event) {
        event.preventDefault();
        var items = retrieveAttributesPublish();
        if (items == "bad") return;
        publishGame(items);
    });
    document.getElementById("delete-form").addEventListener("submit", function (event) {
        event.preventDefault();
        deleteGame();
    });
}

function retrieveAttributesEdit(){
    var visible = document.querySelectorAll('div[style="display: initial;"]');
    var substring = "Editor"
    var regularAttr = [];
    visible.forEach(function(element) {
      var subset = element.querySelectorAll('div[id*="' + substring + '"]');
      regularAttr = regularAttr.concat(Array.from(subset));
    });
    console.log(regularAttr)
    var arrRegular = Array.from(regularAttr);
    var items = {};
    items["Name"] = document.getElementById("game-name-edit").value;
    if (items["Name" == ""]){
        alert("Name missing")
        return "bad"
    }
    items["Description"] = document.getElementById("game-description-edit").value;
    if (items["Description" == ""]){
                alert("Description missing")
                return "bad"
    }
    items["img"] = document.getElementById("game-img-edit").value;
    if (items["img" == ""]){
                alert("img missing")
                return "bad"
    }
    items["Released"] = {};
    for (var i = 0; i < arrRegular.length; i++){

        var name = regularAttr[i].id.replace(substring,"");
        console.log("attribute name: "+name)
        var spans = regularAttr[i].getElementsByTagName('span');

        if (name == "ReleaseDate"){

            if (spans.length > 1){
                alert("Only 1 Release Date allowed")
                return "bad"
            }
            if (spans.length == 0){
                alert("Release Date missing")
                return "bad"
            }
            if (!isValidDateFormat(spans[0].textContent)){
                alert("Insert a date with format 'yyyy-mm-dd' and sensible numbers")
                return "bad"
            }
            items["Released"]["Release Date"] = spans[0].textContent;
        }
        else if (name == "Platform"){
            if (spans.length == 0){
                alert("Platform missing")
                return "bad"
            }
            if (spans.length > 1){
                items["Released"]["Platform"] = [];
                for (var j = 0; j < spans.length; j++){
                    items["Released"]["Platform"].push(spans[j].textContent);
                }
            }
            else{
                items["Released"]["Platform"] = spans[0].textContent;
            }
        }
        else {
            if (spans.length == 0){
                alert(name+" missing")
                return "bad"
            }
            if (spans.length > 1){
                items[name] = [];
                for (var j = 0; j < spans.length; j++){
                    items[name].push(spans[j].textContent)
                }
            }
            else if (spans.length > 0){
                items[name] = spans[0].textContent;
            }
        }
    }
    console.log("edited game: "+JSON.stringify(items))
    return JSON.stringify(items)
}

function retrieveAttributesPublish(){
    var substring = "MultiPublish"
    var regularAttr = document.querySelectorAll('div[id*="' + substring + '"]');
    var arrRegular = Array.from(regularAttr);
    var substring2 = "Values"
    var newAttr = document.querySelectorAll('div[id*="' + substring2 + '"]');
    var arrNew = Array.from(newAttr);
    var substring3 = "entry-";
    var entries = document.querySelectorAll('div[id*="' + substring3 + '"]');
    var items = {};
    items["Name"] = document.getElementById("NameSinglePublish").value;
    if (items["Name" == ""]){
            alert("Name missing")
            return "bad"
    }
    items["Description"] = document.getElementById("DescriptionSinglePublish").value;
    if (items["Description" == ""]){
            alert("Description missing")
            return "bad"
    }
    items["img"] = document.getElementById("imgSinglePublish").value;
    if (items["img" == ""]){
                alert("img missing")
                return "bad"
    }
    items["Released"] = {};
    for (var i = 0; i < arrRegular.length; i++){

        var name = regularAttr[i].id.replace(substring,"");
        var spans = regularAttr[i].getElementsByTagName('span');

        if (name == "ReleaseDate"){
            if (spans.length == 0){
                alert("Release Date missing")
                return "bad"
            }
            if (spans.length > 1){
                alert("Only 1 Release Date allowed")
                return "bad"
            }
            if (!isValidDateFormat(spans[0].textContent)){
                alert("Insert a date with format 'yyyy-mm-dd' and sensible numbers")
                return "bad"
            }
            items["Released"]["Release Date"] = spans[0].textContent;
        }
        else if (name == "Platform"){
            if (spans.length == 0){
                        alert("Platform missing")
                        return "bad"
            }
            if (spans.length > 1){
                items["Released"]["Platform"] = [];
                for (var j = 0; j < spans.length; j++){
                    items["Released"]["Platform"].push(spans[j].textContent);
                }
            }
            else{
                items["Released"]["Platform"] = spans[0].textContent;
            }
        }
        else {
            if (spans.length == 0){
                alert(name+" missing")
                return "bad"
            }
            if (spans.length > 1){
                items[name] = [];
                for (var j = 0; j < spans.length; j++){
                    items[name].push(spans[j].textContent)
                }
            }
            else {
                items[name] = spans[0].textContent;
            }
        }
    }
    for (var i = 0; i < arrNew.length; i++){
        var name = newAttr[i].id.replace(substring2,"");
        var spans = newAttr[i].getElementsByTagName('span');
        if (spans.length > 1){
            items[name] = [];
            for (var j = 0; j < spans.length; j++){
                items[name][j] = spans[j].textContent;
            }
        }
        else if (spans){
            items[name] = spans[0].textContent;
        }

    }
    return JSON.stringify(items)
}

function editGame(items) {
    data = {
        game:items,
        id: document.getElementById("_idHolder").textContent
    }

    $.ajax({
        type: "POST",
        url: "/api/company/edit-game",
        data: data,
        success: function (response) {
            if(response == "success")
            {
                alert("Game edited successfully!");
                resetAllVectorInput();
                resetForms();
            }
            else
            {   
                alert("Error editing game: " + response);
            }
        },
        error: function (xhr, status, error) {
            alert("Error editing game: " + xhr.status + " " + xhr.statusText);
        }
    });
}

function publishGame(items) {
    data = {
        game: items,
        gameName: document.getElementById("NameSinglePublish").value,
    }

    $.ajax({
        type: "POST",
        url: "/api/company/publish-game",
        data: data,
        success: function (response) {
            alert("Game published successfully!");
            resetAllVectorInput();
            resetForms();
        },
        error: function (xhr, status, error) {
            alert("Error publishing game: " + xhr.status + " " + xhr.statusText);
        }
    });
}

function deleteGame() {
    if (document.getElementById("game-name-delete").value != document.getElementById("game-name-repeat-delete").value) {
        alert("You must write the game name correctly to be sure you want to delete it!");
        return;
    }

    if (!confirm("Are you really sure you want to delete this game?")) {
        return;
    }

    data = {
        name: document.getElementById("game-name-delete").value,
    }

    $.ajax({
        type: "POST",
        url: "/api/company/delete-game",
        data: data,
        success: function (response) {
            alert("Game deleted successfully!");
            resetForms();
        },
        error: function (xhr, status, error) {
            alert("Error deleting game: " + xhr.status + " " + xhr.statusText);
        }
    });
}

function resetForms() {

    document.getElementById("edit-form").reset();
    resetVectorInput("game-genres-edit");
    resetVectorInput("game-platforms-edit");
    resetVectorInput("game-developers-edit");
    resetVectorInput("game-publishers-edit");
    resetVectorInput("game-narrative-edit");
    resetVectorInput("game-gameplay-edit");
    resetVectorInput("game-perspective-edit");
    resetVectorInput("game-setting-edit");
    resetVectorInput("game-input-devices-edit");
    document.getElementById("publish-form").reset();
    resetVectorInput("game-genres-publish");
    resetVectorInput("game-platforms-publish");
    resetVectorInput("game-developers-publish");
    resetVectorInput("game-publishers-publish");
    resetVectorInput("game-narrative-publish");
    resetVectorInput("game-gameplay-publish");
    resetVectorInput("game-perspective-publish");
    resetVectorInput("game-setting-publish");
    resetVectorInput("game-input-devices-publish");
    document.getElementById("delete-form").reset();
}

function clearBoxes(){
    var inputs = document.getElementsByTagName("input");

    for (var i = 0; i < inputs.length; i++){
        if (inputs[i].id != "")
                resetVectorInput(inputs[i].id)
    }
}

function loadGameInfo() {
    resetAllVectorInput()
    var game_name = document.getElementById("game-name-edit").value;
    if (game_name == "") {
        return;
    }
    $.ajax({
        url: "/api/game",
        type: "GET",
        data: {
            name: game_name
        },
        success: function (data) {
            console.log(data)
            console.log(data.customAttributes._id.$oid)
            var keys = Object.keys(data.customAttributes);
            var values = keys.map(function(v) { return data.customAttributes[v]; });
            var pDiv = document.getElementById('attributeHolder');
            var cDiv = pDiv.children;
            document.getElementById("game-description-edit").style.display = "initial";
            document.getElementById("game-img-edit").style.display = "initial";

            for (var i = 0; i < cDiv.length; i++) {
                if (Object.keys(data.customAttributes).includes(cDiv[i].id)){

                    cDiv[i].style.display="initial";
                    if (cDiv[i].id === "_id"){
                        cDiv[i].style.display="none";
                        document.getElementById("_idHolder").textContent = data.customAttributes._id.$oid;
                    }
                    if (cDiv[i].id === "Released"){
                        document.getElementById("ReleaseDateEditorInput").value = data.customAttributes[cDiv[i].id]["Release Date"];
                        addVectorEntry(document.getElementById("Release DateButton"))
                        if (!Array.isArray(data.customAttributes[cDiv[i].id]["Platform"])){
                            document.getElementById("PlatformEditorInput").value = data.customAttributes[cDiv[i].id]["Platform"];
                            addVectorEntry(document.getElementById("PlatformButton"))
                        }
                        else{
                            var len = data.customAttributes[cDiv[i].id]["Platform"].length;
                            console.log("lennn "+len)
                            console.log("plat"+data.customAttributes[cDiv[i].id]["Platform"])
                            for (var j = 0; j < len; j++){
                                document.getElementById("PlatformEditorInput").value = data.customAttributes[cDiv[i].id]["Platform"][j];
                                addVectorEntry(document.getElementById("PlatformButton"))
                            }
                        }


                    }
                    else{

                        if (!Array.isArray(data.customAttributes[cDiv[i].id])){
                            document.getElementById(cDiv[i].id+"EditorInput").value = data.customAttributes[cDiv[i].id]
                            addVectorEntry(document.getElementById(cDiv[i].id+"Button"))
                        }
                        else{
                            var len = data.customAttributes[cDiv[i].id].length;
                            for (var j = 0; j < len; j++){
                                document.getElementById(cDiv[i].id+"EditorInput").value = data.customAttributes[cDiv[i].id][j]
                                addVectorEntry(document.getElementById(cDiv[i].id+"Button"))
                            }
                        }
                    }

                    //cDiv[i].value = data.customAttributes[cDiv[i].id]
                }
                else{
                    cDiv[i].style.display="none";
                }
            }
            if (data.customAttributes.Description) {
                document.getElementById("game-description-edit").value = data.customAttributes.Description;
            } 
            else
            {
                document.getElementById("game-description-edit").value = "";
            }
            if (data.customAttributes.img)
            {
                document.getElementById("game-img-edit").value = data.customAttributes.img;
            }
            else
            {
                document.getElementById("game-img-edit").value = "";
            }
            resetVectorInput("game-genres-edit");
            if(data.customAttributes.Genres)
            {
                if (data.customAttributes.Genres instanceof Array)
                {
                    data.customAttributes.Genres.forEach(function (genre) {
                        addVectorEntryWithId("game-genres-edit", genre);
                    });
                }
                else
                {
                    addVectorEntryWithId("game-genres-edit", data.customAttributes.Genres);
                }
            }
            resetVectorInput("game-platforms-edit");
            if(data.customAttributes.Platforms)
            {
                if (data.customAttributes.Platforms instanceof Array)
                {
                    data.customAttributes.Platforms.forEach(function (platform) {
                        addVectorEntryWithId("game-platforms-edit", platform);
                    });
                }
                else
                {
                    addVectorEntryWithId("game-platforms-edit", data.customAttributes.Platforms);
                }
            }
            resetVectorInput("game-developers-edit");
            if(data.customAttributes.Developers)
            {
                if (data.customAttributes.Developers instanceof Array)
                {
                    data.customAttributes.Developers.forEach(function (developer) {
                        addVectorEntryWithId("game-developers-edit", developer);
                    });
                }
                else
                {
                    addVectorEntryWithId("game-developers-edit", data.customAttributes.Developers);
                }
            }
            resetVectorInput("game-publishers-edit");
            if(data.customAttributes.Publishers)
            {
                if (data.customAttributes.Publishers instanceof Array)
                {
                    data.customAttributes.Publishers.forEach(function (publisher) {
                        addVectorEntryWithId("game-publishers-edit", publisher);
                    });
                }
                else
                {
                    addVectorEntryWithId("game-publishers-edit", data.customAttributes.Publishers);
                }
            }
            //document.getElementById("game-release-date-edit").type = "date";
            //document.getElementById("game-release-date-edit").value = data.released;
            resetVectorInput("game-narrative-edit");
            if(data.customAttributes.Narrative)
            {
                if (data.customAttributes.Narrative instanceof Array)
                {
                    data.customAttributes.Narrative.forEach(function (narrative) {
                        addVectorEntryWithId("game-narrative-edit", narrative);
                    });
                }
                else
                {
                    addVectorEntryWithId("game-narrative-edit", data.customAttributes.Narrative);
                }
            }
            resetVectorInput("game-gameplay-edit");
            if(data.customAttributes.Gameplay)
            {
                if (data.customAttributes.Gameplay instanceof Array)
                {
                    data.customAttributes.Gameplay.forEach(function (gameplay) {
                        addVectorEntryWithId("game-gameplay-edit", gameplay);
                    });
                }
                else
                {
                    addVectorEntryWithId("game-gameplay-edit", data.customAttributes.Gameplay);
                }
            }
            resetVectorInput("game-perspective-edit");
            if(data.customAttributes.Perspective)
            {
                if (data.customAttributes.Perspective instanceof Array)
                {
                    data.customAttributes.Perspective.forEach(function (perspective) {
                        addVectorEntryWithId("game-perspective-edit", perspective);
                    });
                }
                else
                {
                    addVectorEntryWithId("game-perspective-edit", data.customAttributes.Perspective);
                }
            }
            resetVectorInput("game-setting-edit");
            if(data.customAttributes.Setting)
            {
                if (data.customAttributes.Setting instanceof Array)
                {
                    data.customAttributes.Setting.forEach(function (setting) {
                        addVectorEntryWithId("game-setting-edit", setting);
                    });
                }
                else
                {
                    addVectorEntryWithId("game-setting-edit", data.customAttributes.Setting);
                }
            }
            resetVectorInput("game-input-devices-edit");
            if(data.customAttributes["Input Devices Supported"])
            {
                if (data.customAttributes["Input Devices Supported"] instanceof Array)
                {
                    data.customAttributes["Input Devices Supported"].forEach(function (input_device) {
                        addVectorEntryWithId("game-input-devices-edit", input_device);
                    });
                }
                else
                {
                    addVectorEntryWithId("game-input-devices-edit", data.customAttributes["Input Devices Supported"]);
                }
            }
        },
        error: function (xhr, status, error) {
            console.log("Error loading game info: " + xhr.status + " " + xhr.statusText);
        }
    });
}

function addAttribute(){
    var button = document.getElementById('moreAttributes');

    var newDiv = document.createElement('div');

    newDiv.setAttribute('th:replace',"~{fragments/interfaces.html :: vector_input(\'game-input-devices-edit\',\'Add a supported input device\')}");

    button.parentNode.insertBefore(newDiv, button);
}