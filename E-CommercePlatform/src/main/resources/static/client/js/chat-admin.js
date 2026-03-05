let stompAdminClient = null;
let currentCustomerId = 1;
let isChatConnected = false;

function toggleAdminChat() {
    const chatBox = document.getElementById('adminChatBox');
    const adminIcon = document.querySelector('.admin-icon');
    const closeIcon = document.querySelector('.close-icon-admin');

    if (chatBox.style.display === 'none' || chatBox.style.display === '') {
        chatBox.style.display = 'flex';
        adminIcon.style.display = 'none';
        closeIcon.style.display = 'block';


        if(!isChatConnected) {
            connectToChatServer();
        }
    } else {
        chatBox.style.display = 'none';
        adminIcon.style.display = 'block';
        closeIcon.style.display = 'none';
    }
}

function connectToChatServer() {
    const socket = new SockJS('/ws-chat');
    stompAdminClient = Stomp.over(socket);

    stompAdminClient.connect({}, function (frame) {
        isChatConnected = true;
        console.log('🔥 Customer Connected: ' + frame);


        stompAdminClient.subscribe('/user/' + currentCustomerId + '/topic/messages', function (message) {
            const msgData = JSON.parse(message.body);
            displayAdminReply(msgData.content);
        });
    }, function(error) {
        console.error("Lỗi kết nối WebSocket: ", error);
    });
}

function sendAdminChatMessage() {
    const input = document.getElementById('adminInput');
    const text = input.value.trim();

    if (text !== "" && stompAdminClient && isChatConnected) {
        const chatMessage = {
            senderId: currentCustomerId,
            senderName: "Nguyễn Anh",
            receiverId: null,
            content: text,
            type: 'CHAT'
        };


        stompAdminClient.send("/app/chat.sendToAdmin", {}, JSON.stringify(chatMessage));


        const container = document.getElementById('adminChatMessages');
        const msgDiv = document.createElement('div');
        msgDiv.className = 'message user';
        msgDiv.textContent = text;
        container.appendChild(msgDiv);

        input.value = '';
        container.scrollTop = container.scrollHeight;
    } else {
        console.log("Chưa kết nối được với Server!");
    }
}

function displayAdminReply(text) {
    const container = document.getElementById('adminChatMessages');
    const msgDiv = document.createElement('div');
    msgDiv.className = 'message bot';
    msgDiv.textContent = text;
    container.appendChild(msgDiv);
    container.scrollTop = container.scrollHeight;
}

function handleAdminChatKey(event) {
    if (event.key === 'Enter') {
        sendAdminChatMessage();
    }
}