$(document).ready(function() {
	dropdownCountries=$("#country");
	dataListState=$("#state");	
	dropdownCountries.on("change",function(){
		loadStateForCountry();		
	});
	loadStateForCountry();
});

function loadStateForCountry(){
	selectedCountry=$("#country option:selected");
	countryId=selectedCountry.val();	
	url=contextPath+ "states/"+countryId;		
	$.get(url,function(jsonResponse){	
		dataListState.empty();			
		$.each(jsonResponse,function(index,state){						
			$("<option>").val(state.name).text(state.name).appendTo(dataListState);
		});
	});
}