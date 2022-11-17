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