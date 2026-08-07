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
    |--------------------------------------------------------------------------
    | Notificações
    |--------------------------------------------------------------------------
    */
    stompClient.subscribe("/user/queue/notifications", function (message) {

        if (message.body === "refresh") {

            fetchNotificationsFromApi();
            return;

        }

        const data = JSON.parse(message.body);
        toast(data.content);

        fetchNotificationsFromApi();
    });

    stompClient.subscribe("/user/queue/notifications/amizade_aceite", function (message) {

        const data = JSON.parse(message.body);
        console.log(message.body);
        toast(data.mensagem);
        renderNotifications(data.notificacoes);
    });


 /*
|--------------------------------------------------------------------------
| PEDIDOS DE AMIZADE
|--------------------------------------------------------------------------
*/
    stompClient.subscribe("/user/queue/friendships", async function (message) {
        console.log("Pedido de amizade:", message.body);
        try {
            // Aguarda a lista atualizada antes de corrigir o contador e a vista aberta.
            await handleFriendshipWebSocketUpdate();
        } catch (error) {
            console.error("Não foi possível atualizar os pedidos de amizade.", error);
        }
        await handleFriendshipWebSocketUpdate();
    });

    /*
    |--------------------------------------------------------------------------
    | MENSAGENS
    |--------------------------------------------------------------------------
    */
    stompClient.subscribe("/user/queue/messages/mensagens", function (message) {


        if (message.body === "refresh") {
            //loadPrivateMessagesFromApi();
             loadnewmessages();
            return;

        }
        loadnewmessages();
    });



    stompClient.subscribe("/user/queue/messages/privadas", function (message) {

        updatenewmessagesBadge(message.body);
    });

    /*
    |--------------------------------------------------------------------------
    | AMIGOS ONLINE
    |--------------------------------------------------------------------------
    */
    stompClient.subscribe("/user/queue/friends", function (message) {



        if (message.body === "refresh") {

            loadFriendsPresenceFromApi();
            return;

        }

        const data = JSON.parse(message.body);


        loadFriendsPresenceFromApi();

        console.log("Estado dos amigos:", data);

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