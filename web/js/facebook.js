
window.fbAsyncInit = function() {
  FB.init({ appId: '577606858927436', 
  status: true, 
  cookie: true,
  xfbml: true,
  oauth: true});

  FB.Event.subscribe('auth.statusChange', handleStatusChange);	
  
};

(function(d){
     var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
     if (d.getElementById(id)) {return;}
     js = d.createElement('script'); js.id = id; js.async = true;
     js.src = "//connect.facebook.net/en_US/all.js";
     ref.parentNode.insertBefore(js, ref);
   }(document));
   

function handleStatusChange(response) {
   document.body.className = response.authResponse ? 'connected' : 'not_connected';
  
   if (response.authResponse) {
        console.log(response);
        if(typeof on_index !== 'undefined') {            
            location.href = "main.html";
        }
        else{
            toevoegenGebruiker();
        }     
  }else {
        console.log(response);
        if(window.location.href.indexOf("main") > -1) {
            location.href = "index.html";
        }
    }
 }

function logoutUser() {    
	  FB.logout(function(response) {
          // user is now logged out
          });  	
	}

function loginUser() {    
	  FB.login(function(response) { }, {scope:'email'});  	
          
	}	

function ingelogd() {
    FB.api('/me', function(response) {
    $.getJSON(BASE_URL + '/user/' + response.id, OnCallBack);
  
  function OnCallBack(user) {
	console.log(user);
	$("#profile_pic").attr("src","https://graph.facebook.com/"+response.id+"/picture");
        console.log("naam ophalen");
        $('.profile_info').append("<strong>"+ user.voornaam + " " + user.naam + "</strong><br><small>25</small>");
        console.log("profiel pagina aanpassen");
        $("#profile_pict").attr("src","https://graph.facebook.com/"+response.id+"/picture");

        $('.profile_Page').append("<h2>" + user.voornaam + " " + user.naam + "</h2><br>"
                +'<img src="https://graph.facebook.com/' 
                + user.id + '/picture">'+"<br>naam: "
                + user.naam+"<br>voornaam: "
                + user.voornaam
                +"<br>e-mail: "
                + user.email);
	ingelogde=user.id;		
        }
        
    });
}

function toevoegenGebruiker(){
    FB.api('/me', function(response) {
        console.log("gebruiker zoeken");
        $.ajax({
         url: BASE_URL + '/user/' + response.id ,
         type: 'GET',
         dataType: 'json',
         contentType: 'application/json',
         success: function() {
            console.log("Welkom " + response.name);
            ingelogd();
            },
        error: function(jqXHR, exception){
            console.log("aanmaken nieuwe user");
            var newUser = new Object();
                newUser.id = response.id;
                newUser.naam = response.last_name;
                newUser.voornaam = response.first_name;
                newUser.email = response.email;                 
                console.log(newUser);
                postUser(newUser);    
            }

        });
    });
}

function postUser(user){

   $.ajax({
   url: BASE_URL + '/user' ,
   data: JSON.stringify(user),
   type: 'POST',
   dataType: 'json',
   contentType: 'application/json',
   success: function() {
   console.log("user posted");
   ingelogd();
   },
   error: function(jqXHR, exception){
       alert("error "+ jqXHR.status);
   }
   
 });
}
