var adminStompClient = null;
var customerId = "CUST_" + new Date().getTime(); // Tạo ID tạm thời

function toggleAdminChat() {
    const chatBox = document.getElementById('adminChatBox');
    const isOpening = chatBox.style.display === 'none';
    chatBox.style.display = isOpening ? 'flex' : 'none';

    if (isOpening && !adminStompClient) {
        initAdminConnection();
    }
}

function initAdminConnection() {
    const socket = new SockJS('/ws-chat');
    adminStompClient = Stomp.over(socket);
    adminStompClient.connect({}, function (frame) {
        // Lắng nghe tin nhắn trả về cho riêng khách hàng này
        adminStompClient.subscribe('/topic/public', function (message) {
            const data = JSON.parse(message.body);
            appendMessage(data.content, 'staff');
        });
    });
}

function sendAdminMessage() {
    const input = document.getElementById('adminChatInput');
    const content = input.value.trim();

    if (content && adminStompClient) {
        const msg = { sender: customerId, content: content };
        adminStompClient.send("/app/chat.send", {}, JSON.stringify(msg));
        appendMessage(content, 'user');
        input.value = '';
    }
}

function appendMessage(text, type) {
    const container = document.getElementById('adminChatMessages');
    const div = document.createElement('div');
    div.className = `admin-message ${type}`;
    div.innerText = text;
    container.appendChild(div);
    container.scrollTop = container.scrollHeight;
}

function handleAdminChatKey(event) {
    if (event.key === 'Enter') sendAdminMessage();
}