/**
 * 
 */
document.addEventListener("DOMContentLoaded", function() {
    // Lấy biến từ model Spring Boot (do Controller đẩy ra)
    let activeTab = "[[${activeTab}]]"; // Thymeleaf sẽ render giá trị của activeTab

    if (activeTab) {
        let tabElement = document.querySelector(`a[href="#${activeTab}"]`);
        if (tabElement) {
            new bootstrap.Tab(tabElement).show();
        }
    }
});

