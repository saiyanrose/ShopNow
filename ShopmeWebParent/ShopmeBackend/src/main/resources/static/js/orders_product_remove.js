$(document).ready(function(){
	$("#productList").on("click",".link-remove",function(e){
		e.preventDefault();
		if(atleastProduct()){
			alert("Could not remove: Atleast 1 product is present.");
		}else{
			removeProduct($(this));
			updateOrderAmount()
		}		
	});
});

function removeProduct(link){
	rowNumber=link.attr("rowNumber");
	$("#row"+rowNumber).remove();
	
	$(".divCount").each(function(index,element){
		element.innerHTML=""+(index+1);
	});
}

function atleastProduct(){
	productCount=$(".hiddenProductId").length;
	return productCount==1;
}