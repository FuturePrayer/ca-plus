// 获取DOM元素
const demoBtn = document.getElementById('demo-btn');
const confirmationOverlay = document.getElementById('confirmationOverlay');
const cancelBtn = document.querySelector('.cancel-btn');
const confirmBtn = document.querySelector('.confirm-btn');

// 用于存储回调函数
let confirmCallback = null;

// 显示确认框函数
function showConfirmation(title, message, confirmText, cancelText, callback) {
    // 设置对话框内容
    document.querySelector('.dialog-title').textContent = title;
    document.querySelector('.dialog-message').textContent = message;
    confirmBtn.textContent = confirmText;
    cancelBtn.textContent = cancelText;

    // 存储回调
    confirmCallback = callback;

    // 显示对话框
    confirmationOverlay.classList.add('active');
}

// 隐藏确认框函数
function hideConfirmation() {
    confirmationOverlay.classList.remove('active');
}

// 按钮事件监听
cancelBtn.addEventListener('click', () => {
    hideConfirmation();
    if (confirmCallback) confirmCallback(false);
});

confirmBtn.addEventListener('click', () => {
    hideConfirmation();
    if (confirmCallback) confirmCallback(true);
});

// 点击遮罩层关闭
confirmationOverlay.addEventListener('click', (e) => {
    if (e.target === confirmationOverlay) {
        hideConfirmation();
        if (confirmCallback) confirmCallback(false);
    }
});

// 键盘ESC关闭
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && confirmationOverlay.classList.contains('active')) {
        hideConfirmation();
        if (confirmCallback) confirmCallback(false);
    }
});