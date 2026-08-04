let stompClient = null;

function connectWebSocket() {

    const token = localStorage.getItem("accessToken");

    if (!token) {
        console.error("JWT não encontrado.");
        return;
    }

    const socket = new WebSocket(
        `ws://${window.location.host}/ws?token=${encodeURIComponent(token)}`
    );

    stompClient = Stomp.over(socket);

    // Desativa os logs da biblioteca
    stompClient.debug = null;

    stompClient.connect({}, onConnected, onError);

}

function onConnected() {

    console.log("✅ WebSocket conectado.");

    /*
     * Notificações gerais
     */
    stompClient.subscribe("/user/queue/notifications", function (message) {

        console.log("Notificação:", message.body);

    });

    /*
     * Pedidos de amizade
     */
    stompClient.subscribe("/user/queue/friendships", function (message) {

        console.log("Pedido de amizade:", message.body);
        loadFriendRequestCount();
        updateBadges();

    });

    /*
     * Mensagens privadas
     */
    stompClient.subscribe("/user/queue/messages", function (message) {

        console.log("Mensagem privada:", message.body);

    });

    /*
     * Amigos online
     */
    stompClient.subscribe("/user/queue/friends", function (message) {

        console.log("Estado dos amigos:", message.body);

    });

    /*
     * Diz ao servidor que o utilizador ficou online.
     */
    stompClient.send("/app/presence.online", {}, "");

}

function onError(error) {

    console.error("❌ WebSocket desligado.", error);

    setTimeout(connectWebSocket, 5000);

}

/*
|--------------------------------------------------------------------------
| Funções auxiliares
|--------------------------------------------------------------------------
*/

function refreshNotifications() {

    if (!stompClient || !stompClient.connected)
        return;

    stompClient.send("/app/notifications.refresh", {}, "");

}

function refreshFriendRequests() {

    if (!stompClient || !stompClient.connected)
        return;

    stompClient.send("/app/friendships.refresh", {}, "");

}

function refreshFriendsOnline() {

    if (!stompClient || !stompClient.connected)
        return;

    stompClient.send("/app/friends.online", {}, "");

}

/*
|--------------------------------------------------------------------------
| Desligar ligação
|--------------------------------------------------------------------------
*/

function disconnectWebSocket() {

    if (stompClient != null) {

        stompClient.disconnect(function () {

            console.log("WebSocket desligado.");

        });

    }

}

/*
|--------------------------------------------------------------------------
| Liga automaticamente
|--------------------------------------------------------------------------
*/

document.addEventListener("DOMContentLoaded", function () {

    connectWebSocket();

});

window.addEventListener("beforeunload", function () {

    disconnectWebSocket();

});