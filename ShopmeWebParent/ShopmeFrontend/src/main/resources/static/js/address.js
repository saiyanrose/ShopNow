$("#buttonCancel").on("click", function() {
	window.location = "[[@{/address_book}]]";
});

$(document).ready(function(){	
	var country=$("#country").val();		
	changeDropdownStates(country);		
	
	dropdownCountry.on("change", function() {
		var country=$("#country").val();		
		changeDropdownStates(country);			
	});
});

function changeDropdownStates(country) {		
	url = contextPath + "setting/states/" + country;
	$.get(url, function(jsonResponse) {
		dropdownStates.empty();
		$.each(jsonResponse, function(index, state) {
			optionValueForStates = state.name;
			$("<option>").val(optionValueForStates).text(state.name).appendTo(dropdownStates);
		});
	});
}