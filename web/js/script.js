/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var BASE_URL = "http://localhost:8080/buurtapp_Rest2/Resources";

var Meldingen = [];
var Evenementen = [];
var User = [];
var Comments = [];

var selectedMeldingenIndex = undefined;
var editingMeldingen = false;

var selectedEvenementenIndex = undefined;
var editingEvenementen = false;

var selectedUserIndex = indefined;
var editingUser = false;

var selectedCommentsIndex = undefined;
var editingComments = false;

function initialiseLists(){
    
    //meldingen laden
    var request = new XMLHttpRequest();
    request.open("GET", BASE_URL + "/melding");
    request.onload = function() {
        if(request.status == 200){
            Meldingen = JSON.parse(request.responseText);
            for (var i = 0; i < Meldingen.length; i++){
                $("#meldingenList").append(createListElementForMeldingen(i));
            }
            /*if(Meldingen.length > 0){
                // show comments from melding ?
            }else{
                $(".reminderDialogToggle").attr("disabled", true);
            }*/
        }else{
            console.log("Error loading Melding: " + request.status + " - " + request.statusText);
        }
    };
    request.send(null);
}

function createListElementForGroup(groupIndex) {
    
    var editIcon = $("<i>")
        .addClass("icon-edit icon-large pull-right")
        .click(function(event) {
            event.stopPropagation();
            
            // Prepare the dialog for editing instead of adding.
            editingMelding = true;
            $("#meldingDialog h3").text("Edit Melding");
            $("#meldingType").val(Meldingen[meldingIndex].type);
            $("#meldingBeschrijving").val(Meldingen[meldingIndex].Beschrijving);
            $("#meldingDialogDelete").show();
            $("#meldingDialog").modal("show");
        });
    
    var link = $("<a>")
        .text(Meldingen[meldingIndex].type)
        .text(Meldingen[meldingIndex].beschrijving)
        .append(editIcon);
    
    return $("<li>")
        .append(link)
        ;
}
