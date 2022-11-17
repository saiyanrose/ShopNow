$(document).ready(function() {
		dropdownCountries=$("#country");
		dataListState=$("#listStates");
		
		dropdownCountries.on("change",function(){
			loadStateForCountry();
			$("#state").val("").focus();
		});
});
	
	function loadStateForCountry(){
		selectedCountry=$("#country option:selected");
		countryId=selectedCountry.val();
		url=contextPath+ "setting/states/"+countryId;		
		$.get(url,function(jsonResponse){
			dataListState.empty();
			
			$.each(jsonResponse,function(index,state){
				$("<option>").val(state.name).text(state.name).appendTo(dataListState);
			});
		});
	}

	function checkPasswordMatch(confirmPassword) {
		if (confirmPassword.value != $("#password").val()) {
			confirmPassword.setCustomValidity("Password do not match");
		} else {
			confirmPassword.setCustomValidity("");
		}
	}
	
	function checkEmailUnique(form) {
		url = uniqueCheckUrl;
		userEmail = $("#email").val();
		csrfValue = $("input[name='_csrf']").val();		
		params = {				
			email : userEmail,
			_csrf : csrfValue
		};		
		$.post(url, params, function(response) {
			if (response == "OK") {
				form.submit();
			} else if (response == "Duplicate") {
				showModalDialogue("Warning","Email is already present in database: " + userEmail);
			}else {
				showModalDialogue("Error","Unknown response from server");
			}
		});
		return false;
	}
	
	function showModalDialogue(title,message){
		$("#modalTitle").text(title);
		$("#modalbody").text(message);
		$("#modalDialogue").modal();
	}