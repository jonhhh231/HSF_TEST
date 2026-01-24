const searchKeyword = () => {
    const form = document.querySelector("#form-search");
    if(form){
        form.addEventListener("submit", (e) => {
            e.preventDefault();
            const input = form.querySelector('input');
            const keyword = input.value;
            const url = new URL(window.location.href);
            if(keyword){
                url.searchParams.set("keyword", keyword);
            }
            else {
                url.searchParams.delete("keyword");
            }
            window.location.href = url.href;
        })
    }
}
searchKeyword();

const changePage = () => {
    const paginationSection = document.querySelector("[pagination]");
    if(paginationSection){
        paginationSection.addEventListener("change", () => {
            const page = paginationSection.value;
            const url = new URL(window.location.href);
            url.searchParams.set("page", page);
            window.location.href = url.href;
        });
    }
}
changePage();

/**
 * main.js
 * Xử lý các sự kiện global hoặc common
 */

document.addEventListener("DOMContentLoaded", function() {

    // --- XỬ LÝ NÚT XÓA (DELETE) CHUNG CHO TOÀN BỘ APP ---
    // Cách này giúp bạn áp dụng cho cả Category, Product, User...
    // miễn là nút bấm có class="btn-delete" và có attribute "data-api"

    const deleteButtons = document.querySelectorAll('.btn-delete');

    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();

            const apiUrl = this.getAttribute('data-api');
            const itemName = this.getAttribute('data-name') || 'mục này';


            if (confirm(`Bạn có chắc chắn muốn xóa ${itemName} không?`)) {

                const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
                const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
                const headers = {
                    'Content-Type': 'application/json'
                };

                if (csrfTokenMeta && csrfHeaderMeta) {
                    headers[csrfHeaderMeta.getAttribute('content')] = csrfTokenMeta.getAttribute('content');
                }

                fetch(apiUrl, {
                    method: 'DELETE',
                    headers: headers
                })
                    .then(response => {
                        if (response.ok) {
                            alert("Đã xóa thành công!");
                            this.closest('tr').remove();
                        } else {
                            alert("Xóa thất bại! Vui lòng kiểm tra lại.");
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert("Lỗi kết nối đến server.");
                    });
            }
        });
    });
});