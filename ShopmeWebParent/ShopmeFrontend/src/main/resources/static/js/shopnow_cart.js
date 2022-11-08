$(document).ready(function() {

	$(".linkMinus").on("click", function(evt) {
		evt.preventDefault();
		decreaseQuantity($(this));

	});

	$(".linkPlus").on("click", function(evt) {
		evt.preventDefault();
		increaseQuantity($(this));
	});

});

function decreaseQuantity(link) {
	productId = link.attr("pid");
	quantityInput = $("#quantity" + productId);
	newQuantity = parseInt(quantityInput.val()) - 1;
	//alert(newQuantity);
	if (newQuantity > 0) {
		quantityInput.val(newQuantity);		
		updateQuantity(productId,newQuantity);
	} else {
		alert("Minimum quantity is 1");
	}
}

function increaseQuantity(link) {
	productId = link.attr("pid");
	quantityInput = $("#quantity" + productId);
	newQuantity = parseInt(quantityInput.val()) + 1;
	//alert(newQuantity);
	if (newQuantity <= 5) {
		quantityInput.val(newQuantity);
		updateQuantity(productId,newQuantity);
	} else {
		alert("Maximum quantity is 5");
	}
}

function updateQuantity(productId,quantity){
	url=contextPath + "cart/update/"+productId+ "/" +quantity;
	//alert(url);
	$.ajax({
		type:"POST",
		url:url,
		beforeSend:function(xhr){
			xhr.setRequestHeader(csrfHeaderName,csrfValue);
		}
	}).done(function(response){
		//alert(response);
		fomatSubTotal=$.number(response,2);
		 updateSubTotal(fomatSubTotal,productId);
		 updateTotal();
	}).fail(function(response){
		alert("Error while updating product to cart");
	});
}

function updateSubTotal(subTotal,productId){
	$("#subTotal" + productId).text(subTotal);	
}

function updateTotal(){
	total=0.0;
	
	$(".subtotal").each(function(index,element){		
		total+=parseFloat(element.innerHTML.replaceAll(",",""));
		//alert(total);
	});
	
	fomatTotal=$.number(total,2);
	$("#total").text(fomatTotal);
}