var buttonLoad;
var dropdownCountries;
var buttonAddCountry;
var buttonUpdateCountry;
var buttonDeleteCountry;
var labelCountryName;
var fieldCountryName;
var fieldCountryCode;

$(document).ready(function(){
	buttonLoad=$("#buttonLoad");
	dropdownCountries=$("#dropdownCountries");
	buttonAddCountry=$("#buttonAddCountry");
	buttonUpdateCountry=$("#buttonUpdateCountry");
	buttonDeleteCountry=$("#buttonDeleteCountry");
	labelCountryName=$("#labelCountryName");
	fieldCountryName=$("#fieldCountryName");
	fieldCountryCode=$("#fieldCountryCode");
	
	buttonLoad.click(function(){
		loadCountries();
	});
	
	dropdownCountries.on("change",function(){
		changeFormState();
	});
	
	buttonAddCountry.on("click",function(){
		if(buttonAddCountry.val()=="Add"){
			addCountry();
		}else{
			changeFormStateNew();
		}		
	});
	
	buttonUpdateCountry.on("click",function(){
		updateCountry();
	});
	
	buttonDeleteCountry.on("click",function(){
		deleteCountry();
	})
});

function deleteCountry(){
	optionValue=dropdownCountries.val();
	countryId=optionValue.split("-")[0];
	url=contextPath+ "countries/delete/"+countryId;		
	$.get(url,function(){		
	
	}).done(function(){
		toastmessage("Country has been deleted!");
		
	}).fail(function(){
		toastmessage("ERROR: could connect to the server or server encountered an error");
	});
}

function updateCountry(){
	url =contextPath+ "countries/save";
	countryName=fieldCountryName.val();
	countryCode=fieldCountryCode.val();
	countryId=dropdownCountries.val().split("-")[0];
	jsonData={id:countryId,name:countryName,code:countryCode};
	
	$.ajax({
		type:'POST',
		url:url,
		beforeSend:function(xhr){
			xhr.setRequestHeader(csrfHeaderName,csrfValue);
		},
		data:JSON.stringify(jsonData),
		contentType:'application/json'
	}).done(function(){
		toastmessage("Country has been update successfully");
		
	}).fail(function(){
		toastmessage("ERROR: could connect to the server or server encountered an error");
	});
}

function addCountry(){
	url =contextPath+ "countries/save";
	formCountry=document.getElementById("formCountry");
	if(!formCountry.checkValidity()){
		formCountry.reportValidity();
		return;
	}
	countryName=fieldCountryName.val();
	countryCode=fieldCountryCode.val();
	jsonData={name:countryName,code:countryCode};
	
	$.ajax({
		type:'POST',
		url:url,
		beforeSend:function(xhr){
			xhr.setRequestHeader(csrfHeaderName,csrfValue);
		},
		data:JSON.stringify(jsonData),
		contentType:'application/json'
	}).done(function(){
		toastmessage("Country has been saved successfully");
		
	}).fail(function(){
		toastmessage("ERROR: could connect to the server or server encountered an error");
	});
}

function changeFormStateNew(){
	buttonAddCountry.val("Add");
	labelCountryName.text("Country Name:");
	buttonUpdateCountry.prop("disabled",true);
	buttonDeleteCountry.prop("disabled",true);
	fieldCountryName.val("").focus();
	fieldCountryCode.val("");
}

function changeFormState(){
	buttonAddCountry.prop("value","New");
	buttonUpdateCountry.prop("disabled",false);
	buttonDeleteCountry.prop("disabled",false);
	
	labelCountryName.text("Selected Country:");
	selectedCountryname=$("#dropdownCountries option:selected").text();
	fieldCountryName.val(selectedCountryname);
	
	countryCode=dropdownCountries.val().split("-")[1];
	fieldCountryCode.val(countryCode);
	
}

function loadCountries(){		
	url =contextPath+ "countries/list";
	$.get(url,function(jsonResponse){
		dropdownCountries.empty();
		
		$.each(jsonResponse,function(index,country){
			optionValue=country.id+"-"+country.code;
			$("<option>").val(optionValue).text(country.name).appendTo(dropdownCountries);			
		});			
		
	}).done(function(){
		buttonLoad.val("Refresh Country List");
		toastmessage("All countries have been loaded");
	}).fail(function(){
		toastmessage("ERROR: could connect to the server or server encountered an error");
	});
}

function toastmessage(message){
	$("#toast-message").text(message);
	$(".toast").toast('show');
}