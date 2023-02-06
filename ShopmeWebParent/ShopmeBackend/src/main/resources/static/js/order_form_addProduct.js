$(document).ready(function(){
	$("#product").on("click","#linkAddProduct",function(e){
		e.preventDefault();
		link=$(this);
		url=link.attr("href");
		
		$("#addProductModal").on("shown.bs.modal",function(){
			$(this).find("iframe").attr("src",url);
		});
		
		$("#addProductModal").modal();
	});	
});

function addProduct(productId,productName){
	if(isAlreadyAdded(productId)){
		//alert("Product already present.");
		$("#confirmTextOrder").text("Product already present.");
		$("#confirmModalOrder").modal();
	}else{
		getShippingCost(productId);		
	}
}

function isAlreadyAdded(productId){
	productExist=false;
	$(".hiddenProductId").each(function(e){
		aProductId=$(this).val();
		
		if(aProductId==productId){
			productExist=true;
			return;
		}
	});
	return productExist;
}

function getProductInfo(productId,shippingCost){
	requestedUrl=contextPath+"product/get/"+productId;
	$.get(requestedUrl,function(productJSON){
		console.log(productJSON);
		productName=productJSON.name;
		productPrice=productJSON.price;
		productCost=productJSON.cost;
		mainImagePath=contextPath.substring(0,contextPath.length-1)+productJSON.imagePath;		
		htmlCode=insertProduct(productId,productName,mainImagePath,productCost,productPrice,shippingCost);
		$("#productList").append(htmlCode);
		updateOrderAmount();
	}).fail(function(err){
		alert(err.responseJSON.message);
	});
}

function insertProduct(productId,productName,mainImagePath,productCost,productPrice,shippingCost){
	productDetailCount=$(".hiddenProductId").length;
	nextCount=productDetailCount+1;
	productDetailCount++;	
	quantityId="quantity"+nextCount;
	subtotalId="subtotal"+nextCount;
	priceId="price"+nextCount;
	rowId="row"+nextCount;
	
	htmlCode=`
		<div class="border rounded p-1" id="${rowId}">
				<input type="hidden" name="detailId" value="0" />
				<input type="hidden" name="productId" value="${productId}" class="hiddenProductId">
				<div class="row">
					<div class="col-1">
						<div class="divCount">${nextCount}.</div>
						<div><a class="fas fa-trash icon link-remove" href="" rowNumber="${nextCount}"></a></div>
					</div>
					<div class="col-3">
						<img src="${mainImagePath}" class="img-fluid" width="100px"/>
					</div>
				</div>
				
				<div class="row m-2">
					<b>${productName}</b>
				</div>
				
				<div class="row m-2">
					<table>
						<tr>
							<td>Product Cost:</td>
							<td>
								<input type="text" class="form-control m-1 product-input" required
								 name="productDetailCost" value="${productCost}" style="max-width:140px" rowNumber="${nextCount}" id="${priceId}">
							</td>
						</tr>
						
						<tr>
							<td>Quantity:</td>
							<td>
								<input type="number" step="1" min="1" max="5" class="form-control m-1 quantity-input" required
								 name="quantity" id="${quantityId}" rowNumber="${nextCount}" value="1" style="max-width:140px">
							</td>
						</tr>						
						
						<tr>
							<td>Subtotal:</td>
							<td>
								<input type="text" class="form-control m-1 subtotal-input" required
								 name="productSubtotal" rowNumber="${nextCount}" id="${subtotalId}" value="${productPrice}" style="max-width:140px">
							</td>
						</tr>
						
						<tr>
							<td>Shipping Cost:</td>
							<td>
								<input type="text" class="form-control m-1 ship-input" required
								 name="productShipCost" value="${shippingCost}" style="max-width:140px" readonly="readonly">
							</td>
						</tr>
						
					</table>
				</div>
				
			</div>
	
	`;	
	return htmlCode;
}

function getShippingCost(productId){
	
	selectedCountryId=$("#country option:selected");
	countryId=selectedCountryId.val();
	state=$("#state").val();
	if(state.length==0){
		state=$("#city").val();
	}
	requestUrl=contextPath+"get_shipping_cost";
	params={productId:productId,countryId:countryId,state:state};
	$.ajax({
		type:'POST',
		url:requestUrl,
		beforeSend:function(xhr){
			xhr.setRequestHeader(csrfHeaderName,csrfValue);
		},
		data:params
	}).done(function(shippingCost){
		getProductInfo(productId,shippingCost);				
	}).fail(function(err){
		alert(err.responseJSON.message);
		shippingCost=0.0;
		getProductInfo(productId,shippingCost);
	});
}

