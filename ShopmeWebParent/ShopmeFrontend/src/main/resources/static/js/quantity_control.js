$(document).ready(function(){
	
	$(".linkMinus").on("click",function(evt){
		evt.preventDefault();
		productId=$(this).attr("pid");
		quantityInput=$("#quantity" + productId);
		newQuantity=parseInt(quantityInput.val())-1;
		//alert(newQuantity);
		if(newQuantity>0){
			quantityInput.val(newQuantity);
		}else{
			alert("Minimum quantity is 1");
		}
	});
	
	$(".linkPlus").on("click",function(evt){
		evt.preventDefault();
		productId=$(this).attr("pid");
		quantityInput=$("#quantity" + productId);
		newQuantity=parseInt(quantityInput.val())+1;
		//alert(newQuantity);
		if(newQuantity<=5){
			quantityInput.val(newQuantity);
		}else{
			alert("Maximum quantity is 5");
		}
	});

});