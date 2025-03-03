function handleTabChange(targetId = null) {
    if (targetId) {
        sessionStorage.setItem('activeTab', targetId);
    } else {
        targetId = sessionStorage.getItem('activeTab') || 'home'; // Lấy từ sessionStorage hoặc mặc định 'home'
    }

    // Ẩn tất cả section
    document.querySelectorAll('.content-section').forEach(section => {
        section.style.display = 'none';
    });

    // Hiển thị section tương ứng
    let activeSection = document.getElementById(targetId);
    if (activeSection) {
        activeSection.style.display = 'block';
    }

    // Cập nhật class 'active' cho nav-link
    document.querySelectorAll('.nav-link').forEach(nav => {
        nav.classList.remove('active');
    });

    let activeNavLink = document.querySelector(`.nav-link[data-target="${targetId}"]`);
    if (activeNavLink) {
        activeNavLink.classList.add('active');
    }
}

let lastMessage = sessionStorage.getItem('message') || ""; // Lấy message từ sessionStorage khi load trang

function showNotificationModal(message) {
    if (message && message.trim() !== "") {
        // Kiểm tra nếu message mới khác message cũ thì mới hiển thị
        if (message !== lastMessage) {
            let modal = new bootstrap.Modal(document.getElementById('notificationModal'));
            modal.show();

            // Lưu message mới vào sessionStorage
            sessionStorage.setItem('message', message);
            lastMessage = message; // Cập nhật biến lastMessage

            console.log("Last message saved:", lastMessage);
			setTimeout(() => {
                modal.hide();
            },1500);
        } 
    }
}


function reviewImg() {
    let inputFile = document.getElementById("file");
    let previewDiv = document.querySelector(".custum-file-upload");
    let previewImg = document.getElementById("preview");
	
	try{
		inputFile.addEventListener("change", function (event) {
	        let file = event.target.files[0];

	        if (file) {
	            let reader = new FileReader();
	            reader.onload = function (e) {
	                //previewDiv.style.backgroundImage = `url('${e.target.result}')`;
	                previewImg.src = e.target.result;
	                previewDiv.classList.add("has-new-image"); // Chỉ ẩn icon nếu người dùng chọn ảnh mới
	            };
	            reader.readAsDataURL(file);
	        }
	    });
	}catch{
		console.error("lỗi reviewImg")
	}
    
}
function openEditOrder(event, url) {
    event.preventDefault(); 
    sessionStorage.setItem("openModalEditOrder", "true"); 
    window.location.href = url; 
}
/*
function plusAndMinus(){
	const minusBtn = document.querySelector(".btn-minus");
    const plusBtn = document.querySelector(".btn-plus");
    const quantityInput = document.querySelector(".form-control");

    // Giảm số lượng nhưng không dưới 1
    minusBtn.addEventListener("click", function () {
        let value = parseInt(quantityInput.value);
        if (value > 1) {
            quantityInput.value = value - 1;
        }
    });

    // Tăng số lượng
    plusBtn.addEventListener("click", function () {
        let value = parseInt(quantityInput.value);
        quantityInput.value = value + 1;
    });
	document.querySelector(".form-control").addEventListener("input", function () {
	        if (this.value < 1) {
	            this.value = 1;
	        }
	    });
}
*/
	document.addEventListener("DOMContentLoaded", function () {
	    document.querySelectorAll('.nav-link[data-target]').forEach(link => {
	        link.addEventListener('click', function (e) {
	            e.preventDefault();
	            handleTabChange(this.getAttribute('data-target'));
	        });
	    });
		document.querySelectorAll('.edit-btn').forEach(link => {
		        link.addEventListener('click', function (e) {
					//e.preventDefault();
		            handleTabChange(this.getAttribute('data-target'));
		        });
		    });
		const urlParams = new URLSearchParams(window.location.search);
	        if (urlParams.get('loginRequired') === 'true') {
	            let loginModal = new bootstrap.Modal(document.getElementById('loginModal'));
	            loginModal.show();
	        }
		setTimeout(() => {
	        let messageElement = document.querySelector("#notificationModal .modal-body span");
	        let message = messageElement ? messageElement.textContent.trim() : "";
	
	        if (message && message !== "Đây là nội dung thông báo") {
	            showNotificationModal(message);
	        }
	    }, 300); 
	
		reviewImg();
		//plusAndMinus();
	    handleTabChange();
		document.querySelectorAll(".delete-btn").forEach(button => {
			button.addEventListener("click", function() {
				let productId = this.getAttribute("data-id");
				if (confirm("Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?")) {
					fetch(`/cart/delete?id=${productId}`, {
						method: "POST"
					}).then(response => {
						if (response.ok) {
							location.reload();
						} else {
							alert("Lỗi khi xóa sản phẩm!");
						}
					}).catch(error => {
						console.error("Lỗi:", error);
					});
				}
			});
		});
		if (sessionStorage.getItem("openModalEditOrder") === "true") {
            sessionStorage.removeItem("openModalEditOrder"); 
            let modal = new bootstrap.Modal(document.getElementById("editOrderModal"));
            modal.show(); 
        }
		
		let hasGreeted = sessionStorage.getItem('hasGreeted');

	    if (!hasGreeted) {
	        let message = "Chào mừng đến với Shopdee";
	        showNotificationModal(message);

	        sessionStorage.setItem('hasGreeted', "true"); // Đánh dấu đã hiển thị
	    }
		
		if (serverMessage && serverMessage.trim() !== "") {
		    showNotificationModal(serverMessage);
	    }
	});

