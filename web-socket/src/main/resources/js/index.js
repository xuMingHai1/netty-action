/**
 * 用户名
 */
let username;

/**
 * websocket实例对象
 */
let webSocket;

// 窗口初始化时执行，建立websocket连接
window.onload = function () {
    // 简历websocket连接
    webSocket = new WebSocket("ws://localhost:8080/chat");
    webSocket.onopen = function () {
        console.log("连接建立成功！")
        setUsername();
    }
    // 消息接收
    webSocket.onmessage = function (event) {
        console.log(event.data);
    }
    // 连接断开
    webSocket.onclose = function (event) {
        console.log(event);
    }

}








/**
 * 设置用户名
 */
function setUsername() {
    username = window.prompt("请输入用户名")
    webSocket.send(username);
}