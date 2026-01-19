const sendSaveProductApi = () => {
    const form = document.querySelector("#form-save-product");
    if(form){
        const api = form.getAttribute("data-api");
        const method = form.getAttribute("data-method");
        console.log(api, method);
        form.addEventListener("submit", (e) => {
            e.preventDefault();
            const name = e.target.name.value;
            const categoryId = e.target.categoryId.value ? e.target.categoryId.value : null;
            const price = e.target.price.value;
            const stock = e.target.stock.value;
            const isActive = e.target.isActive.value;
            const description = e.target.description.value;
            const dataFinal = {
                name, categoryId, price, stock, isActive, description
            }
            const api = form.getAttribute("data-api");
            const method = form.getAttribute("data-method");

            fetch(api, {
                method: method,
                headers: {
                    "content-type": "application/json",
                },
                body: JSON.stringify(dataFinal)
            }).then(res => res.json())
            .then(data => {
                alert(data.message);
                window.location.reload();
            }).catch((err) => {
                alert(err.message || "Lỗi");
            });
        })
    }
}
sendSaveProductApi();


const sendSaveCategoryApi = () => {
    const form = document.querySelector("#form-save-category");

    if(form){
        const api = form.getAttribute("data-api");
        const method = form.getAttribute("data-method");
        console.log("Config:", api, method);

        form.addEventListener("submit", (e) => {
            e.preventDefault();
            const name = e.target.name.value;
            const description = e.target.description.value;
            const dataFinal = {
                name: name,
                description: description
            };
            const apiPath = form.getAttribute("data-api");
            const apiMethod = form.getAttribute("data-method");
            fetch(apiPath, {
                method: apiMethod,
                headers: {
                    "content-type": "application/json",
                },
                body: JSON.stringify(dataFinal)
            })
                .then(res => res.json())
                .then(data => {
                    if(data.message){
                        alert(data.message);
                    } else {
                        alert("Thành công!");
                    }
                    window.location.href = "/admin/categories/create";
                })
                .catch((err) => {
                    console.error(err);
                    alert(err.message || "Lỗi hệ thống");
                });
        })
    }
}
sendSaveCategoryApi();


