$(document).ready(function() {

	$("#buttonCancel").on("click", function() {
		window.location = "[[@{/users}]]";
	});
	
	$("#fileImage").change(function() {
		fileSize = this.files[0].size;
		if (fileSize > 1048576) {
			this.setCustomValidity("You must choose file less than 1mb");
			this.reportValidity();
		} else {
			this.setCustomValidity("");
			showImageThumbnail(this);
		}

	});
});

function showImageThumbnail(fileInput) {
	var file = fileInput.files[0];
	var reader = new FileReader();
	reader.onload = function(e) {
		$("#thumbnail").attr("src", e.target.result);
	};
	reader.readAsDataURL(file);
}

function checkPasswordMatch(confirmpassword) {
	if (confirmpassword.value != $("#password").val()) {
		confirmpassword.setCustomValidity("Password does not match!");
	} else {
		confirmpassword.setCustomValidity("");
	}
}

$(document).ready(function() {
	$("#logoutLink").on("click", function(e) {
		e.preventDefault();
		document.logoutForm.submit();
	});
	customizeDropdown();
});

function customizeDropdown() {
	$(".navbar .dropdown").hover(
		function() {
			$(this).find('.dropdown-menu').first().stop(true, true)
				.delay(250).slideDown();
		},
		function() {
			$(this).find('.dropdown-menu').first().stop(true, true)
				.delay(100).slideUp();
		});

	$(".dropdown > a").click(function() {
		location.href = this.href;
	});
}