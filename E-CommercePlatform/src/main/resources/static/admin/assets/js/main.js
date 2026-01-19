const sendProductCreateApi = () => {
    const form = document.querySelector("#form-create-product");
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
        console.log(">>>", dataFinal);
        const api = "/admin/products";
        const method = "POST";
        fetch(api, {
            method: method,
            headers: {
                "content-type": "application/json",
            },
            body: JSON.stringify(dataFinal)
        }).then(res => res.json())
        .then(data => {
            alert(data.message);
            form.reset();
        }).catch((err) => {
            alert(err.message || "Lỗi");
        });
    })
}
sendProductCreateApi();
console.log(">> run to this file");