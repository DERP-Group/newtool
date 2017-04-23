$('#submit-request').click(submitRequest);

var output;
var urlVars;

function submitRequest(){
	var audioFileUrlInput = $('#audioFileUrlInput').val();
	var loginWithAmazonInput = $('#loginWithAmazonInput').val();
	var userIdInput = $('#userIdInput').val();
	var applicationIdInput = $('#applicationIdInput').val();
	var data = JSON.stringify({ audioFileUrl: audioFileUrlInput, userId: userIdInput, applicationId: applicationIdInput});
	var url = 'rest/audioFileRequest';
	var authorizationHeader = 'BEARER ' + loginWithAmazonInput;
	var headers = {Authorization: authorizationHeader};
	var jqxhr = $.post({
		type: 'POST',
		url: url,
		data: data,
		contentType: 'application/json',
		headers: headers
	})
	  .done(function(data) {
	    output = data;
		$('#alexaRequestCode').empty();
	    $('#alexaRequestCode').append(JSON.stringify(data.alexaSkillsRequest,null,2));
	  })
	  .fail(function() {
	    alert( "error" );
	  });
}

function getUrlVars()
{
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}

function init_accessToken(){
	urlVars = getUrlVars();
	var access_token = decodeURIComponent(urlVars['access_token']);
	if(access_token){
		$('#loginWithAmazonInput').val(access_token);
	}else{
		console.log('no access token');
	}
}

$( document ).ready(function(){
	init_accessToken();
})