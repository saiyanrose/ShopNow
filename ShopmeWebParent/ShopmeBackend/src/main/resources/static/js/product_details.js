$(document).ready(function(){
	$("a[name='link_remove_details']").each(function(index){
				$(this).click(function(){
					removeDetailSectionByIndex(index);
				});
	});
});

function addNextDetailSection(){
	allDivDetail=$("[id^=detailHeader]");
	divDetailCount=allDivDetail.length;
	//alert(divDetailCount);
	
	html=`<div class="form-inline" id="detailHeader${divDetailCount}">
		<label class="m-3">Name:</label>
		<input type="text" class="form-control w-25" name="detailName" maxlength="255">
		<label class="m-3">Value:</label>
		<input type="text" class="form-control w-25" name="detailValue" maxlength="255">
	</div>`;
	
	$("#detailDiv").append(html);
	previousDivDetailSection=allDivDetail.last();
	previousDivDetailId=previousDivDetailSection.attr("id");
	
	htmlLinkRemove=`<a class="btn fas fa-times-circle fa-2x float-right" 
						href="javascript:removeDetailSection('${previousDivDetailId}')" title="Remove this Detail">
					</a>`;
	
	previousDivDetailSection.append(htmlLinkRemove);			
}

function removeDetailSection(id){
	$("#" +id).remove();
}

function removeDetailSectionByIndex(index){
	$("#detailHeader" +index).remove();
}