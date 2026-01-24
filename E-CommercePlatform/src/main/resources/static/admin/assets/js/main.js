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
