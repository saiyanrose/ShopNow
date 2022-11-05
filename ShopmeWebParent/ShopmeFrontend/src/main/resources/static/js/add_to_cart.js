$(document).ready(function(){
	$("#add2Cart").on("click",function(){
		addToCart();
	});	
});

function addToCart(){
	quantity=$("#quantity" + productId).val();
	url=contextPath + "cart/add/"+productId+ "/" +quantity;
	//alert(url);
	$.ajax({
		type:"POST",
		url:url,
		beforeSend:function(xhr){
			xhr.setRequestHeader(csrfHeaderName,csrfValue);
		}
	}).done(function(response){
		alert(response);
	}).fail(function(response){
		alert("Error while adding product to cart");
	});
}