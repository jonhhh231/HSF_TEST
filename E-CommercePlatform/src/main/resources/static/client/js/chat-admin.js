let stompAdminClient = null;
let currentCustomerId = null;
let currentCustomerName = null;
let isChatConnected = false;
let isChatHistoryLoaded = false;

function toggleAdminChat() {
    const chatBox = document.getElementById('adminChatBox');
    const adminIcon = document.querySelector('.admin-icon');
    const closeIcon = document.querySelector('.close-icon-admin');

    if (chatBox.style.display === 'none' || chatBox.style.display === '') {
        chatBox.style.display = 'flex';
        adminIcon.style.display = 'none';
        closeIcon.style.display = 'block';

        // Khởi tạo thông tin user từ session/localStorage nếu chưa có
        initializeUserInfo();

        // Load lịch sử chat nếu chưa load
        if(!isChatHistoryLoaded && currentCustomerId) {
            loadChatHistory();
        }

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
    if (!currentCustomerId) {
        console.error("Không thể kết nối: Chưa có thông tin user");
        return;
    }

    const socket = new SockJS('/ws-chat');
    stompAdminClient = Stomp.over(socket);

    stompAdminClient.connect({}, function (frame) {
        isChatConnected = true;
        console.log('🔥 Customer Connected: ' + frame);

        stompAdminClient.subscribe('/user/' + currentCustomerId + '/topic/messages', function (message) {
            const msgData = JSON.parse(message.body);
            displayAdminReply(msgData.content, msgData.timestamp);
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
            senderName: currentCustomerName,
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

function displayAdminReply(text, timestamp) {
    const container = document.getElementById('adminChatMessages');
    const msgDiv = document.createElement('div');
    msgDiv.className = 'message bot';

    const textNode = document.createElement('span');
    textNode.textContent = text;
    msgDiv.appendChild(textNode);

    if (timestamp) {
        const timeNode = document.createElement('small');
        timeNode.className = 'message-time';
        timeNode.textContent = timestamp;
        msgDiv.appendChild(timeNode);
    }

    container.appendChild(msgDiv);
    container.scrollTop = container.scrollHeight;
}

function displayUserMessage(text, timestamp) {
    const container = document.getElementById('adminChatMessages');
    const msgDiv = document.createElement('div');
    msgDiv.className = 'message user';

    const textNode = document.createElement('span');
    textNode.textContent = text;
    msgDiv.appendChild(textNode);

    if (timestamp) {
        const timeNode = document.createElement('small');
        timeNode.className = 'message-time';
        timeNode.textContent = timestamp;
        msgDiv.appendChild(timeNode);
    }

    container.appendChild(msgDiv);
}

// Hàm khởi tạo thông tin user từ session
function initializeUserInfo() {
    // Kiểm tra xem đã có thông tin user chưa
    if (currentCustomerId) {
        return;
    }

    // Lấy thông tin từ element có id userId và userName (thường được set bởi Thymeleaf)
    const userIdElement = document.getElementById('userId');
    const userNameElement = document.getElementById('userName');

    if (userIdElement && userIdElement.value) {
        currentCustomerId = parseInt(userIdElement.value);
    }

    if (userNameElement && userNameElement.value) {
        currentCustomerName = userNameElement.value;
    }

    // Nếu không tìm thấy, thử lấy từ localStorage hoặc từ data attribute
    if (!currentCustomerId) {
        const savedUserId = localStorage.getItem('userId');
        const savedUserName = localStorage.getItem('userName');

        if (savedUserId) {
            currentCustomerId = parseInt(savedUserId);
            currentCustomerName = savedUserName || 'Khách hàng';
        }
    }

    console.log('User initialized:', currentCustomerId, currentCustomerName);
}

// Hàm load lịch sử chat
function loadChatHistory() {
    if (!currentCustomerId) {
        console.error("Không có userId để load lịch sử");
        return;
    }

    fetch(`/api/chat/history/${currentCustomerId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Không thể tải lịch sử chat');
            }
            return response.json();
        })
        .then(messages => {
            isChatHistoryLoaded = true;

            // Xóa tin nhắn mặc định nếu có
            const container = document.getElementById('adminChatMessages');
            container.innerHTML = '';

            // Hiển thị từng tin nhắn
            messages.forEach(msg => {
                // msg.senderId === currentCustomerId nghĩa là tin nhắn do user gửi
                if (msg.senderId === currentCustomerId) {
                    displayUserMessage(msg.content, msg.timestamp);
                } else {
                    // Tin nhắn từ admin
                    displayAdminReply(msg.content, msg.timestamp);
                }
            });

            // Scroll xuống cuối
            container.scrollTop = container.scrollHeight;
        })
        .catch(error => {
            console.error('Lỗi khi load lịch sử chat:', error);
        });
}

function handleAdminChatKey(event) {
    if (event.key === 'Enter') {
        sendAdminChatMessage();
    }
}