var buttonCountriesLoad;
var dropdownStates;
var labelStateName;
var fieldStateName;
var buttonAddState;
var buttonUpdateState;
var buttonDeleteState;
var dropdownCountriesForStates;

$(document).ready(function() {
	dropdownCountriesForStates = $("#dropdownCountriesForStates");
	dropdownStates = $("#dropdownStates");
	buttonCountriesLoad = $("#buttonCountriesLoad");
	fieldStateName = $("#fieldStateName");
	labelStateName = $("#labelStateName");
	buttonAddState = $("#buttonAddState");
	buttonUpdateState = $("#buttonUpdateState");
	buttonDeleteState = $("#buttonDeleteState");

	buttonCountriesLoad.click(function() {
		loadCountriesForStates();
	});

	dropdownCountriesForStates.on("change", function() {
		changeDropdownStates();
	});

	dropdownStates.on("change", function() {
		changeInputState();
	});

	buttonAddState.on("click", function() {
		if (buttonAddState.val() == "Add") {
			saveState();
		} else {
			changeInputStateNew();
		}
	});
	
	buttonUpdateState.on("click",function(){
		updateState();
	});
	
	buttonDeleteState.on("click",function(){
		deleteState();
	});
});

function changeInputStateNew() {
	buttonAddState.val("Add");
	labelStateName.text("Country Name:");
	buttonUpdateState.prop("disabled", true);
	buttonDeleteState.prop("disabled", true);
	fieldStateName.val("").focus();
}

function changeInputState() {
	buttonAddState.prop("value", "New");
	buttonUpdateState.prop("disabled", false);
	buttonDeleteState.prop("disabled", false);

	labelStateName.text("Selected State:");
	selectedStatename = $("#dropdownStates option:selected").text();
	fieldStateName.val(selectedStatename);
}

function changeDropdownStates() {
	optionValue = dropdownCountriesForStates.val();
	countryId = optionValue.split("-")[0];
	url = contextPath + "states/" + countryId;
	$.get(url, function(jsonResponse) {
		dropdownStates.empty();
		$.each(jsonResponse, function(index, state) {
			optionValueForStates = state.id;
			$("<option>").val(optionValueForStates).text(state.name).appendTo(dropdownStates);
		});
	});
}

function loadCountriesForStates() {
	url = contextPath + "countries/list";
	$.get(url, function(jsonResponse) {
		dropdownCountriesForStates.empty();
		$.each(jsonResponse, function(index, country) {
			optionValue = country.id;
			$("<option>").val(optionValue).text(country.name).appendTo(dropdownCountriesForStates);
		});
	}).done(function() {
		buttonCountriesLoad.val("Refresh Country List");
		toastmessage("Countries have been refreshed!");
	});
}

function saveState() {
	url = contextPath + "states/save";
	stateName = fieldStateName.val();
	selectedCountryname = $("#dropdownCountriesForStates option:selected");
	countryId = selectedCountryname.val();
	countryName = selectedCountryname.text();
	jsonData = { name: stateName, country: { id: countryId, name: countryName } };

	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function() {
		toastmessage("State have been saved!");
	}).fail(function() {
		toastmessage("ERROR: could connect to the server or server encountered an error");
	});
}

function updateState(){
	url = contextPath + "states/save";
	stateName = fieldStateName.val();
	selectedCountryname = $("#dropdownCountriesForStates option:selected");
	countryId = selectedCountryname.val();
	countryName = selectedCountryname.text();
	stateId=dropdownStates.val();
	jsonData = { id:stateId,name: stateName, country: { id: countryId, name: countryName } };

	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function() {
		toastmessage("State have been updated!");
	}).fail(function() {
		toastmessage("ERROR: could connect to the server or server encountered an error");
	});

}

function deleteState(){
	stateId=dropdownStates.val();	
	url = contextPath + "states/delete/"+stateId;
	$.get(url,function(){
		
	}).done(function(){
		toastmessage("State has been deleted!");
		
	}).fail(function(){
		toastmessage("ERROR: could connect to the server or server encountered an error");
	});
}

function toastmessage(message) {
	$("#toast-message").text(message);
	$(".toast").toast('show');
}