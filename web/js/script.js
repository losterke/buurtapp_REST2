/* 
 * eigen shit
 * 
 */
 //rest url
var BASE_URL = "http://localhost:8080/buurtapp_REST2/webresources";
var ingelogde="";
var currentLatitude="";
var currentLongitude="";
var geocoder;
var map;
var marker;

onload = function() {
laadMeldingen();
startMap();
initializeAutocomplete();
initialize();
nieuweMelding();
nieuweReactie();
postFoto();
//meldingPagina();

$(document).on('pageshow', "#main_page_map", function () {
$('#map_canvas').gmap('refresh');
});
};

function reactieDialog(idm){
$('#hiddenMelding').attr("value",idm);
location.href="#NewReactie";
}

function fotoDialog(idm){
$('#hiddenMelding').attr("value",idm);
location.href="#Newfoto";
}

function meldingPagina(idm){
$('#melding_pict').empty();
$('.melding_div').text("");
var i = document.createElement("img");
i.src = BASE_URL + "/melding/" + idm + "/foto";
i.height = 300;
$("#melding_pict").append(i);
$.getJSON(BASE_URL + '/melding/' + idm, OnCallBack);
  function OnCallBack(data) {
$('.melding_div').append("<h2>" + data.beschrijving +  "</h2><br>"
+"Type: "+data.type+"<br>"
+"Auteur: "+data.auteur.voornaam+" "+data.auteur.naam+"<br>");

$.getJSON(BASE_URL + '/melding/'+ idm +'/comment', OnCallBack);
  
  function OnCallBack(data) {
	console.log(data);//mag later weg
	var resultLength = data.length;
	$('.comments_div').text("");
	for(var i=0;i<resultLength;i++){
	//var id = data[i].id;
	var inhoud = data[i].inhoud;
        var auteurnaam = data[i].auteur.naam;
        var auteurvoornaam = data[i].auteur.voornaam;
        $('.comments_div').append(
             auteurvoornaam + " " + auteurnaam + ": " + inhoud + "<br>"
              );
        }
  }
  
$("#reactieBtn").attr("onclick", "reactieDialog("+idm+")");
$("#uploadBtn").attr("onclick", "fotoDialog("+idm+")");
}

 location.href = "#melding_page";              


}

function laadMeldingen(){

  $.getJSON(BASE_URL + '/melding', OnCallBack);
  
  function OnCallBack(data) {
	console.log(data);//mag later weg
	var resultLength = data.length;
	var listItems = [];
	
	for(var i=0;i<resultLength;i++){
	var id = data[i].id;
	var beschrijving = data[i].beschrijving;

   //toevoegen aan array
   listItems.push("<li><a onclick='meldingPagina("+id+");'>"+id+" "+beschrijving+"</a></li>");
								}
								
//array toevoegen aan lijst en vernieuwen
$('#meldingen').append(listItems.join(' '));
$('#meldingen').listview('refresh');
			
		}
  	} 

function nieuweMelding(){
$("#nieuweMelding").click(function() {
    
    console.log(ingelogde);  
    var auteur = new Object();
    auteur.id =ingelogde;
    //auteur.naam = $('#Naam').val();
    var locatie = new Object();
    locatie.latitude = currentLatitude;
    locatie.longitude = currentLongitude;
    
    var melding = new Object();  
    //melding.id="";
    melding.type = $("#cat input[type='radio']:checked").val();
    melding.locatie=locatie;
    melding.beschrijving = $("#Omschrijving").val();
    melding.auteur= auteur ;
    postMelding(melding);
          
       
   // console.log(melding);
    
    
    
    });
}
    
function postMelding(melding){

   $.ajax({
   url: BASE_URL + '/melding' ,
   data: JSON.stringify(melding),
   type: 'POST',
   dataType: 'json',
   contentType: 'application/json',
   success: function() {
   console.log("posted");
   },
   error: function(jqXHR, exception){
       alert("error "+ jqXHR.status);
   }
   
 });
}

function postFoto(){
    $("#fileupload").click(function() {     
                
                var demelding = new Object();
                demelding.id = $("#hiddenMelding").val();
                var auteur = new Object();
                auteur.id =ingelogde;
    
                var file = document.getElementById("file").files[0];
                var extension = file.name.split(".").pop();

                var type;
                if (extension === "jpg" || extension === "jpeg" ||
                    extension === "JPG" || extension === "JPEG") {
                    type = "image/jpeg";
                } else if (extension === "png" || extension === "PNG") {
                    type = "image/png";
                } else {
                    document.getElementById("status").innerHTML = "Invalid file type";
                    return;
                }                
                

                var request = new XMLHttpRequest();
                request.open("POST", BASE_URL + "/melding/"+ demelding.id +"/foto");
                request.onload = function() {
                    if (request.status === 201) {
                        var fileName = request.getResponseHeader("Location").split("/").pop();
                        console.log("File created with name " + fileName);
                    } else {
                        alert("Error creating file: (" + request.status + ") " + request.responseText);
                    }
                };
                
                request.setRequestHeader("Content-auteur", auteur.id);
                request.setRequestHeader("Content-Type", type);
                request.send(file);
                
                meldingPagina(demelding.id);
            });
}

function nieuweReactie(){
$("#nieuweReactie").click(function() {
    console.log("nieuwe reactie");  
    var demelding = new Object();
    demelding.id = $("#hiddenMelding").val();
    var auteur = new Object();
    auteur.id =ingelogde;
    var comment = new Object();  
    comment.inhoud = $("#Reactie").val();
    comment.auteur= auteur;
    
           
       
    console.log(comment);
    
    postReactie(comment, demelding);
    
    meldingPagina(demelding.id);
    });
}

function postReactie(comment, demelding){

   $.ajax({
   url: BASE_URL + '/melding/'+demelding.id+'/comment' ,
   data: JSON.stringify(comment),
   type: 'POST',
   dataType: 'json',
   contentType: 'application/json',
   success: function() {
   console.log("posted");
   },
   error: function(jqXHR, exception){
       alert("error "+ jqXHR.status);
   }
   
 });
}

function initializeAutocomplete() {          
          
        google.maps.event.addDomListener(window, 'load', initialize);
    }
    
function initialize(){   
            var input = (document.getElementById('gmaps-input-address'));
            var searchBox = new google.maps.places.SearchBox(input);            

            google.maps.event.addListener(searchBox, 'places_changed', function() {
              var place = searchBox.getPlaces();
              currentLatitude = place[0].geometry.location.lat();
              currentLongitude = place[0].geometry.location.lng();
            });
      
}    

function startMap() {
        $('#map_canvas').gmap().bind('init', function(evt, map) {
        $('#map_canvas').gmap('getCurrentPosition', function(position, status) {
            if ( status === 'OK' ) {
                    currentLatitude = position.coords.latitude;
                    currentLongitude = position.coords.longitude;
                    var clientPosition = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);                        
                    $('#map_canvas').gmap('addMarker', {'position': clientPosition, 'bounds': true});
                    $('#map_canvas').gmap('addShape', 'Circle', { 
                            'strokeWeight': 0, 
                            'fillColor': "#008595", 
                            'fillOpacity': 0.25, 
                            'center': clientPosition, 
                            'radius': 15, 
                            'clickable': false 
                    });
            }
	});
        
        $.getJSON( BASE_URL + '/melding', function(data) { 

        var resultLength = data.length;

        for(var i=0;i<resultLength;i++){
            var beschrijving = data[i].beschrijving;
            var type = data[i].type;
            $('#map_canvas').gmap('addMarker', { 
                    'position': new google.maps.LatLng(data[i].locatie.latitude, data[i].locatie.longitude),
                    'bounds':false 
            }).click(function() {

                    $('#map_canvas').gmap('openInfoWindow', { 'content': type+': ' + beschrijving }, this);
                });

            };

        });    
        $('#map_canvas').gmap({ 'center': clientPosition });        
    });   
};

//// initialise the google maps objects, and add listeners
//  function gmaps_init(){
//   
//      // create our map object
//    map = new google.maps.Map(document.getElementById("gmaps-canvas"));
//  
//    // the geocoder object allows us to do latlng lookup based on address
//    geocoder = new google.maps.Geocoder();
//  }
//  
//
//  
//  // fill in the UI elements with new position data
//  function update_ui( address, latLng ) {
//    $('#gmaps-input-address').autocomplete("close");
//    $('#gmaps-input-address').val(address);
//    currentLatitude = latLng.lat();
//    currentLongitude = latLng.lng();
//  }
//  
//  // Query the Google geocode object
//  //
//  // type: 'address' for search by address
//  //       'latLng'  for search by latLng (reverse lookup)
//  //
//  // value: search query
//  //
//  function geocode_lookup( type, value, update ) {
//    // default value: update = false
//    update = typeof update !== 'undefined' ? update : false;
//  
//    request = {};
//    request[type] = value;
//  
//    geocoder.geocode(request, function(results, status) {
//      $('#gmaps-error').html('');
//      if (status === google.maps.GeocoderStatus.OK) {
//        // Google geocoding has succeeded!
//        if (results[0]) {
//          // Always update the UI elements with new location data
//          update_ui( results[0].formatted_address,
//                     results[0].geometry.location );
//            
//        } else {
//          // Geocoder status ok but no results!?
//          $('#gmaps-error').html("Sorry, something went wrong. Try again!");
//        }
//      } else {
//        // Google Geocoding has failed. Two common reasons:
//        //   * Address not recognised (e.g. search for 'zxxzcxczxcx')
//        //   * Location doesn't map to address (e.g. click in middle of Atlantic)
//  
//        if( type === 'address' ) {
//          // User has typed in an address which we can't geocode to a location
//          $('#gmaps-error').html("Sorry! We couldn't find " + value + ". Try a different search term, or click the map." );
//        } else {
//          // User has clicked or dragged marker to somewhere that Google can't do a
//          // reverse lookup for. In this case we display a warning.
//          $('#gmaps-error').html("Woah... that's pretty remote! You're going to have to manually enter a place name." );
//          update_ui('', value);
//        }
//      };
//    });
//  };
//  
//  // initialise the jqueryUI autocomplete element
//  function autocomplete_init() {
//    $("#gmaps-input-address").autocomplete({
//  
//      // source is the list of input options shown in the autocomplete dropdown.
//      // see documentation: http://jqueryui.com/demos/autocomplete/
//      source: function(request,response) {
//  
//        // the geocode method takes an address or LatLng to search for
//        // and a callback function which should process the results into
//        // a format accepted by jqueryUI autocomplete
//        geocoder.geocode( {'address': request.term }, function(results, status) {
//          response($.map(results, function(item) {
//            return {
//              label: item.formatted_address, // appears in dropdown box
//              value: item.formatted_address, // inserted into input element when selected
//              geocode: item                  // all geocode data
//            };
//          }));
//        });
//      },
//  
//      // event triggered when drop-down option selected
//      select: function(event,ui){
//        update_ui(  ui.item.value, ui.item.geocode.geometry.location );
//        
//      }
//    });
//  
//    // triggered when user presses a key in the address box
//    $("#gmaps-input-address").bind('keydown', function(event) {
//      if(event.keyCode === 13) {
//        geocode_lookup( 'address', $('#gmaps-input-address').val(), true );
//  
//        // ensures dropdown disappears when enter is pressed
//        $('#gmaps-input-address').autocomplete("disable");
//      } else {
//        // re-enable if previously disabled above
//        $('#gmaps-input-address').autocomplete("enable");
//      }
//    });
//  }; // autocomplete_init
  

