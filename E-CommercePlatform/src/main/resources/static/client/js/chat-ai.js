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
    fetch('/chat', {
        method: "POST",
        headers: {
            "content-type": "application/json"
        },
        body: JSON.stringify({
            message: text,
        })
    }).then(res => res.text())
        .then(data => {
            const botDiv = document.createElement('div');
            botDiv.className = 'message bot';
            botDiv.textContent = data;
            msgArea.appendChild(botDiv);
            msgArea.scrollTop = msgArea.scrollHeight;
        })
        .catch(err => {
        const botDiv = document.createElement('div');
        botDiv.className = 'message bot';
        botDiv.textContent = "Xin lỗi bạn, hệ thống đang xảy ra vấn đề, vui lòng gửi lại sau!";
        msgArea.appendChild(botDiv);
        msgArea.scrollTop = msgArea.scrollHeight;
    })

}

async function sendChatMessage() {
    const input = document.getElementById('aiInput');
    const chatMessages = document.getElementById('chatMessages');
    const typingIndicator = document.getElementById('typingIndicator');
    const message = input.value.trim();

    if (!message) return;

    appendMessage(message, 'user');
    input.value = '';

    typingIndicator.style.display = 'flex';

    chatMessages.appendChild(typingIndicator);
    chatMessages.scrollTop = chatMessages.scrollHeight;

    try {
        const response = await fetch('/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message: message })
        });

        const reply = await response.text();

        typingIndicator.style.display = 'none';

        appendMessage(reply, 'bot');

    } catch (error) {
        console.error('Lỗi API:', error);
        typingIndicator.style.display = 'none';
        appendMessage("Xin lỗi, hệ thống đang bận. Thử lại sau nhé!", 'bot');
    }
}

function appendMessage(text, sender) {
    const chatMessages = document.getElementById('chatMessages');
    const typingIndicator = document.getElementById('typingIndicator');

    const msgDiv = document.createElement('div');
    msgDiv.className = `message ${sender}`;

    let formattedText = text.replace(/\[(.*?)\]\((.*?)\)/g, '<a href="$2" style="color: #007bff; text-decoration: underline;">$1</a>');

    formattedText = formattedText.replace(/\n/g, '<br>');

    msgDiv.innerHTML = formattedText;

    chatMessages.insertBefore(msgDiv, typingIndicator);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

function handleChatKey(e) {
    if (e.key === 'Enter') {
        sendChatMessage();
    }
}