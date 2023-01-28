window.document.body.querySelectorAll('[rel=deliverySelect]').forEach(select => {
    select.addEventListener('change', () => {
        const orderIndex = select.dataset.oid;
        const orderStatusIndex = select.value;
        console.log(`${orderIndex} 주문의 배송 상태를 ${orderStatusIndex}로 변경.`);
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append('orderIndex', orderIndex);
        formData.append('orderStatusIndex', orderStatusIndex);
        xhr.open('POST', '/shopping/order/status/modify');
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const response = JSON.parse(xhr.responseText);
                    switch (response['result']) {
                        case 'NOT_FOUND':
                            alert('더 이상 존재하지 않는 주문건입니다. 페이지를 다시 불러와주세요.');
                            break;
                        case 'SUCCESS':
                            const orderStatusText = select.options[select.selectedIndex].text;
                            select.parentNode.parentNode.parentNode.querySelector('[rel=orderStatus]').innerText = orderStatusText;
                            // parentNode : ''>
                            break;
                        default:
                            alert('알 수 없는 이유로 배송상태 변경에 실패하였습니다. 잠시 후 다시 시도')
                    }
                } else {
                    alert('알 수 없는 이유로 배송상태 변경에 실패하였습니다. 잠시 후 다시 시도');
                }
            }
        };
        xhr.send(formData);
    });
});
