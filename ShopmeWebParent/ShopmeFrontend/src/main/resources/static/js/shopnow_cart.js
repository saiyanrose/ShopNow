$(document).ready(function() {

	$(".linkMinus").on("click", function(evt) {
		evt.preventDefault();
		decreaseQuantity($(this));

	});

	$(".linkPlus").on("click", function(evt) {
		evt.preventDefault();
		increaseQuantity($(this));
	});
	
	$(".link-remove").on("click", function(evt) {
		evt.preventDefault();
		//url=$(this).attr("href");
		removeFromCart($(this));		
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
		$("#cartDeleteText").text("Error while deleting product from your cart");
		$("#cartDeleteModal").modal();
	});
}

function updateSubTotal(subTotal,productId){
	$("#subTotal" + productId).text(subTotal);	
}

function updateTotal(){
	total=0.0;
	productCount=0;
	$(".subtotal").each(function(index,element){		
		productCount++;
		total+=parseFloat(element.innerHTML.replaceAll(",",""));		
	});
	if(productCount<1){
		showEmptyShoppingCart();	
	}else{
		fomatTotal=$.number(total,2);
		$("#total").text(fomatTotal);
	}	
}

function showEmptyShoppingCart(){
	$("#estimatedTotal").hide();
}

function removeFromCart(link){
	url=link.attr("href");	
	//alert(url);
	$.ajax({
		type:"DELETE",
		url:url,
		beforeSend:function(xhr){
			xhr.setRequestHeader(csrfHeaderName,csrfValue);
		}
	}).done(function(response){		
		rowNumber=link.attr("rowNumber");
		removeProductCartDiv(rowNumber);
		updateTotal();
		updateCountNumber();
		$("#cartDeleteText").text(response);
		$("#cartDeleteModal").modal();		
	}).fail(function(response){		
		$("#cartDeleteText").text("Error while deleting product from your cart");
		$("#cartDeleteModal").modal();
	});
}

function removeProductCartDiv(rowNumber){
	$("#row" + rowNumber).remove();
	$("#blankId" + rowNumber).remove();
	$("#sectionEmptyCartMessage").removeClass("d-none");	
}

function updateCountNumber(){
	$(".divCount").each(function(index,element){
		element.innerHTML="" + (index+1);
	});
}