const productIndexInput = window.document.getElementById('productIndexInput');
const quantityInput = window.document.getElementById('quantityInput');
const cartButton = window.document.getElementById('cartButton');


cartButton.addEventListener('click', () => {
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('productIndex', productIndexInput.value); // @RequestParam 이랑 같아야함.
    formData.append('count', quantityInput.value);
    xhr.open('POST', '/shopping/cart/add')
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseJson = JSON.parse(xhr.responseText);
                switch (responseJson['result']) {
                    case 'NOT_FOUND':
                        alert('더 이상 존재하지 않는 상품입니다.');
                        break;
                    case 'NOT_SIGNED':
                        alert('로그인 후 상품을 장바구니에 담을 수 있습니다.');
                        break;
                    case 'SUCCESS':
                        alert('상품을 장바구니에 추가하였습니다.');
                        break;
                    default:
                        alert('상품을 장바구니에 추가하지 못하였습니다. \n\n잠시 후 다시 시도해 주세요.');
                }
            } else {
                alert('상품을 장바구니에 추가하지 못하였습니다. \n\n잠시 후 다시 시도해 주세요.')
            }
        }
    }

    xhr.send(formData);
});