/* 
 * eigen shit
 * 
 */
 //rest url
var BASE_URL = "http://localhost:8080/buurtapp_REST2/webresources";


onload = function() {
laadMeldingen();
startMap();
nieuweMelding();
};

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
   listItems.push("<li><a href='#'>"+id+" "+beschrijving+"</a></li>");
								}
								
//array toevoegen aan lijst en vernieuwen
$('#meldingen').append(listItems.join(' '));
$('#meldingen').listview('refresh');
			
		}
  	} 

function nieuweMelding(){
$("#nieuweMelding").click(function() {
      
    var auteur = new Object();
    auteur.id =1;
    //auteur.naam = $('#Naam').val();
    var locatie = new Object();
    locatie.latitude = 51.3426606750;
    locatie.longitude = 4.0736160278;
    
    var melding = new Object();  
    //melding.id="";
    melding.type = $("#cat input[type='radio']:checked").val();
    melding.locatie=locatie;
    melding.beschrijving = $("#Omschrijving").val();
    melding.auteur= auteur ;
           
       
   // console.log(melding);
    
    postMelding(melding);
    
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

function startMap() {
                // Also works with: var yourStartLatLng = '59.3426606750, 18.0736160278';
                var yourStartLatLng = new google.maps.LatLng(25.3426606750, 25.0736160278);
                $('#map_canvas').gmap({'center': yourStartLatLng});
        
				
				$('#map_canvas').gmap().bind('init', function(evt, map) {
				$.getJSON( BASE_URL + '/melding', function(data) { 
				
				var resultLength = data.length;
				
				for(var i=0;i<resultLength;i++){
				var beschrijving = data[i].beschrijving;
				var type = data[i].type;
				$('#map_canvas').gmap('addMarker', { 'position': new google.maps.LatLng(data[i].locatie.latitude, data[i].locatie.longitude),
				'bounds':true 
				} ).click(function() {
				$('#map_canvas').gmap('openInfoWindow', { 'content': type+': ' + beschrijving }, this);
			});
				
				}
				});                                                                                                                                                                                                                       
			});
			//init
			
		
		
		};


