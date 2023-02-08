$(document).ready(function(){
	$("#add2Cart").on("click",function(){
		addToCart();
	});	
});

function addToCart(){
	quantity=$("#quantity" + productId).val();
	url=contextPath + "cart/add/"+productId+ "/" +quantity;	
	$.ajax({
		type:"POST",
		url:url,
		beforeSend:function(xhr){
			xhr.setRequestHeader(csrfHeaderName,csrfValue);
		}
	}).done(function(response){
		$("#cartText").text(response);
		$("#cartModal").modal();
	}).fail(function(response){
		$("#cartText").text("Error while adding product into cart");
		$("#cartModal").modal();
	});
}