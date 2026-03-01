// Đảm bảo DOM đã load xong mới gán sự kiện
document.addEventListener('DOMContentLoaded', function() {
    const btn = document.querySelector('.chat-toggle-btn');
    const chatBox = document.getElementById('chatBox');

    if (btn && chatBox) {
        btn.addEventListener('click', () => {
            const chatIcon = btn.querySelector('.chat-icon');
            const closeIcon = btn.querySelector('.close-icon');

            // Kiểm tra trạng thái hiển thị
            const isHidden = chatBox.style.display === 'none' || chatBox.style.display === '';

            if (isHidden) {
                chatBox.style.display = 'flex';
                chatIcon.style.display = 'none';
                closeIcon.style.display = 'block';
            } else {
                chatBox.style.display = 'none';
                chatIcon.style.display = 'block';
                closeIcon.style.display = 'none';
            }
        });
    }
});

function sendChatMessage() {
    const input = document.getElementById('aiInput');
    const msgArea = document.getElementById('chatMessages');
    if (!input || !msgArea) return;

    const text = input.value.trim();
    if (!text) return;

    // Thêm tin nhắn user
    const userDiv = document.createElement('div');
    userDiv.className = 'message user';
    userDiv.textContent = text;
    msgArea.appendChild(userDiv);

    input.value = '';
    msgArea.scrollTop = msgArea.scrollHeight;

    // Giả lập bot
    setTimeout(() => {
        const botDiv = document.createElement('div');
        botDiv.className = 'message bot';
        botDiv.textContent = "Đang kết nối với trí tuệ nhân tạo...";
        msgArea.appendChild(botDiv);
        msgArea.scrollTop = msgArea.scrollHeight;
    }, 500);
}

function handleChatKey(e) {
    if (e.key === 'Enter') {
        sendChatMessage();
    }
}