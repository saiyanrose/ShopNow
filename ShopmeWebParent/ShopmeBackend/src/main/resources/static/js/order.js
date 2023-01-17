$(document).ready(function() {
	dropdownCountries=$("#country");
	dataListState=$("#state");	
	dropdownCountries.on("change",function(){
		loadStateForCountry();		
	});
	loadStateForCountry();
	
	
	$("#productList").on("change",".quantity-input",function(e){
		updateSubtotalWhenQuantityChanged($(this));
		updateOrderAmount();
		
	});	
	
	$("#productList").on("change",".product-input",function(e){		
		updateSubtotalWhenUnitChanged($(this));
		updateOrderAmount();
		
	});	
});

function updateSubtotalWhenUnitChanged(input){
	priceValue=input.val();		
	rowNumber=input.attr("rowNumber");	
	quantityField=$("#quantity" +rowNumber);		
	quantityValue=parseFloat(quantityField.val());	
	newSubtotal=parseFloat(quantityValue) * priceValue;	
	subTotalField=$("#subtotal"+rowNumber);	
	subTotalField.val(newSubtotal);
}

function updateOrderAmount(){
	subtotal=0.0;
	total=0.0;
	$(".subtotal-input").each(function(e){
		subtotalProduct=$(this);
		rowNumber=subtotalProduct.attr("rowNumber");
		subtotalEachProduct=$("#subtotal"+rowNumber).val();
		subtotal+=parseInt(subtotalEachProduct);
		overviewSubtotal=$(".over-subtotal").val(subtotal);	
		overviewShipping=$(".over-ship").val();		
		total=parseInt(overviewShipping)+subtotal;
		$(".over-total").val(total);					
	});	
}

function updateSubtotalWhenQuantityChanged(input){
	quantityValue=input.val();
	rowNumber=input.attr("rowNumber");	
	priceField=$("#price" +rowNumber);		
	priceValue=parseFloat(priceField.val());
	newSubtotal=parseFloat(quantityValue) * priceValue;	
	subTotalField=$("#subtotal"+rowNumber);	
	subTotalField.val(newSubtotal);
}

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