// ============================================================
// LUDO STAR — LAYOUT BASE
// Interações da sidebar, chat, amizades e conta
// ============================================================

// ============================================================
// ELEMENTOS E ESTADO DA INTERFACE
// ============================================================

const $ = (s, r = document) => r.querySelector(s);
const chatPanel = $("#chatPanel"),
    sidebar = $("#sidebar"),
    backdrop = $("#backdrop");
const state = {
    view: "messages",
    conversation: null,
    requests: [
        {
            id: 1,
            name: "MariaLudo",
            letter: "M",
            text: "enviou um pedido de amizade.",
        },
        { id: 2, name: "CarlosKing", letter: "C", text: "quer jogar com você." },
    ],
    sentRequests: [
        {
            id: "sent-1",
            name: "RitaStar",
            letter: "R",
            text: "Pedido enviado. Aguardando resposta.",
        },
    ],
    conversations: [
        {
            id: "ana",
            name: "AnaLudo",
            letter: "A",
            preview: "Vamos jogar mais tarde?",
            time: "14:21",
            online: true,
            messages: [
                ["AnaLudo", "A", "Oi! Vamos jogar mais tarde?", "14:21"],
                ["Você", "S", "Claro! Estou disponível às 20h.", "14:23"],
            ],
        },
        {
            id: "joao",
            name: "JoãoKing",
            letter: "J",
            preview: "Partida muito boa! 🎲",
            time: "ontem",
            online: true,
            messages: [["JoãoKing", "J", "Partida muito boa! 🎲", "ontem"]],
        },
        {
            id: "pedro",
            name: "PedroLudo",
            letter: "P",
            preview: "Até o próximo torneio!",
            time: "sex",
            online: false,
            messages: [["PedroLudo", "P", "Até o próximo torneio!", "sex"]],
        },
    ],
    groups: {
        "ludo-friends": {
            name: "Ludo Friends",
            subtitle: "8 membros",
            letter: "🎮",
            isGroup: true,
            participants: [
                { name: "AnaLudo", letter: "A", role: "Admin" },
                { name: "JoãoKing", letter: "J", role: "Usuário normal" },
                { name: "PedroLudo", letter: "P", role: "Usuário normal" },
            ],
            messages: [
                ["AnaLudo", "A", "Alguém vai participar hoje? 🏆", "14:21"],
                ["JoãoKing", "J", "Eu vou! Já fiz minha inscrição.", "14:23"],
            ],
        },
        torneios: {
            name: "Torneios",
            subtitle: "Grupo de mensagens",
            letter: "🏆",
            isGroup: true,
            participants: [
                { name: "LudoBot", letter: "L", role: "Admin" },
                { name: "AnaLudo", letter: "A", role: "Usuário normal" },
            ],
            messages: [
                ["LudoBot", "L", "Os torneios da semana foram atualizados.", "10:00"],
            ],
        },
        amigos: {
            name: "Amigos do Ludo",
            subtitle: "Grupo de mensagens",
            letter: "🎲",
            isGroup: true,
            participants: [
                { name: "Você", letter: "S", role: "Admin" },
                { name: "AnaLudo", letter: "A", role: "Usuário normal" },
            ],
            messages: [["AnaLudo", "A", "Bem-vindos ao grupo!", "ontem"]],
        },
    },
};
// ============================================================
// FUNÇÕES AUXILIARES
// ============================================================

function avatar(l, on = false) {
    return `<span class="avatar ${on ? "online" : ""}">${l}</span>`;
}
function escapeHtml(v) {
    const e = document.createElement("div");
    e.textContent = v;
    return e.innerHTML;
}
function title(a, b) {
    $("#chatTitle").textContent = a;
    $("#chatSubtitle").textContent = b;
}
function updateBadges() {
    const n = state.requests.length,
        b = $("#requestBadge");
    b.textContent = n;
    b.style.display = n ? "grid" : "none";
}
function showChat() {
    chatPanel.classList.add("open");
    if (innerWidth <= 1120) backdrop.classList.add("visible");
}
function hidePanels() {
    chatPanel.classList.remove("open");
    sidebar.classList.remove("open");
    backdrop.classList.remove("visible");
}
function toast(t) {
    const e = $("#toast");
    e.textContent = t;
    e.classList.add("show");
    clearTimeout(toast.id);
    toast.id = setTimeout(() => e.classList.remove("show"), 2600);
}
// ============================================================
// PAINEL DE CHAT: LISTAS E CONVERSAS
// ============================================================

function renderMessages() {
    state.view = "messages";
    state.conversation = null;
    title("Mensagens", "Escolha uma conversa");
    $("#chatBody").innerHTML =
        state.conversations
            .map(
                (c) =>
                    `<button class="conversation-item" data-conversation-id="${c.id}">${avatar(c.letter, c.online)}<span class="conversation-content"><b>${c.name}</b><small>${c.preview}</small></span><time>${c.time}</time></button>`,
            )
            .join("") || '<p class="list-empty">Nenhuma mensagem por aqui.</p>';
    showChat();
}
function requestCard(r, kind = "received") {
    return `<article class="request-item" data-request-id="${r.id}" data-kind="${kind}">${avatar(r.letter)}<div><b>${escapeHtml(r.name)}</b><p>${r.text}</p>${kind === "received" ? '<div class="request-actions"><button class="accept" data-action="accept">Aceitar</button><button class="reject" data-action="reject">Rejeitar</button><button class="block" data-action="block">Bloquear</button></div>' : '<span class="sent-status"><i class="bi bi-clock"></i> Enviado</span>'}</div></article>`;
}
function renderRequests() {
    state.view = "requests";
    state.conversation = null;
    title("Pedidos de amizade", `${state.requests.length} recebido(s)`);
    $("#chatBody").innerHTML =
        `<section class="new-request"><h3>Fazer pedido</h3><p>Pesquise um jogador para enviar um pedido de amizade.</p><div class="friend-search-input"><i class="bi bi-search"></i><input id="friendSearchInput" type="search" autocomplete="off" placeholder="Pesquisar jogador..."></div><div id="friendSearchResults" class="friend-search-results"></div></section><section class="request-section"><h3>Pedidos recebidos <span>${state.requests.length}</span></h3>${state.requests.map((r) => requestCard(r)).join("") || '<p class="list-empty compact">Nenhum pedido recebido.</p>'}</section><section class="request-section"><h3>Pedidos enviados <span>${state.sentRequests.length}</span></h3>${state.sentRequests.map((r) => requestCard(r, "sent")).join("") || '<p class="list-empty compact">Nenhum pedido enviado.</p>'}</section>`;
    showChat();
}
function messageHtml([n, l, t, time], index) {
    const ownActions = n === "Você"
        ? `<div class="message-actions"><button type="button" data-message-action="edit" data-message-index="${index}" aria-label="Editar mensagem"><i class="bi bi-pencil-fill"></i> Editar</button><button type="button" data-message-action="delete" data-message-index="${index}" aria-label="Eliminar mensagem"><i class="bi bi-trash-fill"></i> Eliminar</button></div>`
        : "";
    return `<div class="message" data-message-index="${index}">${avatar(l)}<div class="message-content"><b>${escapeHtml(n)}</b><time>${time}</time><p>${escapeHtml(t)}</p>${ownActions}</div></div>`;
}
function groupParticipantsHtml(item) {
    if (!item.isGroup) return "";
    const count = item.participants.length;
    return `<button type="button" class="group-participants-button" data-group-participants aria-expanded="false"><i class="bi bi-people-fill"></i> Participantes (${count})</button><button type="button" class="group-participants-button leave-group-button" data-leave-group><i class="bi bi-box-arrow-left"></i> Sair da conversa do grupo</button><div class="participants-list hidden" id="participantsList">${item.participants.map((member) => `<div class="participant-row">${avatar(member.letter)}<b>${escapeHtml(member.name)}</b>${member.role === "Admin" ? '<span class="participant-role">Admin</span>' : ""}</div>`).join("")}</div>`;
}
function conversationIcon(item) {
    return item.avatarImage ? `<img src="${item.avatarImage}" alt="" class="conversation-avatar-image">` : item.letter;
}
function renderConversation(item) {
    state.view = "conversation";
    state.conversation = item;
    title(item.name, item.subtitle || (item.online ? "Online" : "Offline"));
    $("#chatBody").innerHTML =
        `<div class="conversation"><div class="messages" id="messageList"><div class="chat-welcome"><div class="placeholder-icon">${conversationIcon(item)}</div><h2>${escapeHtml(item.name)}</h2><span>Este é o começo da conversa.</span>${groupParticipantsHtml(item)}</div>${item.messages.map(messageHtml).join("")}</div><form class="message-form" id="messageForm"><input id="attachmentInput" type="file" hidden multiple><button type="button" class="plain-icon attachment-button" id="attachmentButton" aria-label="Anexar ficheiro"><i class="bi bi-plus-circle-fill"></i></button><input id="messageInput" autocomplete="off" placeholder="Enviar mensagem..."><button class="send" aria-label="Enviar"><i class="bi bi-send-fill"></i></button></form></div>`;
    showChat();
    $("#messageList").scrollTop = 99999;
}
// ============================================================
// EVENTOS DE NAVEGAÇÃO E CHAT
// ============================================================

$("#openSidebar").addEventListener("click", () => {
    sidebar.classList.add("open");
    backdrop.classList.add("visible");
});
$("#openChat").addEventListener("click", renderMessages);
backdrop.addEventListener("click", hidePanels);
document
    .querySelectorAll("[data-close]")
    .forEach((b) => b.addEventListener("click", hidePanels));
document.querySelectorAll("[data-chat-view]").forEach((b) =>
    b.addEventListener("click", () => {
        sidebar.classList.remove("open");
        b.dataset.chatView === "requests" ? renderRequests() : renderMessages();
    }),
);
$("#groupsList").addEventListener("click", (event) => {
    const button = event.target.closest(".group");
    if (!button) return;
    // Não deixe o clique que abriu a conversa ser tratado como clique fora.
    event.stopPropagation();
    document.querySelectorAll(".group").forEach((x) => x.classList.remove("active"));
    button.classList.add("active");
    sidebar.classList.remove("open");
    renderConversation(state.groups[button.dataset.conversation]);
});

// Conversas privadas e filtro de amigos diretamente na barra lateral.
function openFriendConversation(friend) {
    const name = $("b", friend).textContent.trim();
    const letter = $(".avatar", friend).textContent.trim() || name[0].toUpperCase();
    const online = !friend.classList.contains("offline");
    let conversation = state.conversations.find((item) => item.name === name);
    if (!conversation) {
        conversation = { id: `friend-${name.toLowerCase().replace(/[^a-z0-9]+/gi, "-")}`, name, letter, online, messages: [] };
        state.conversations.push(conversation);
    }
    conversation.online = online;
    sidebar.classList.remove("open");
    renderConversation(conversation);
}
document.querySelectorAll(".friend-item").forEach((friend) => {
    friend.addEventListener("click", () => openFriendConversation(friend));
});
$("#sidebarFriendSearch").addEventListener("input", (event) => {
    const query = event.target.value.trim().toLocaleLowerCase();
    document.querySelectorAll(".friend-item").forEach((friend) => {
        const name = $("b", friend).textContent.toLocaleLowerCase();
        friend.classList.toggle("is-filtered", Boolean(query) && !name.includes(query));
    });
});
$("#chatBody").addEventListener("click", (e) => {
    // A conversa substitui o conteúdo do painel durante este clique.
    // Impedimos que o clique seja interpretado como um clique fora do chat.
    e.stopPropagation();

    if (e.target.closest("#attachmentButton"))
        return $("#attachmentInput").click();
    if (e.target.closest("[data-group-participants]")) {
        const list = $("#participantsList");
        const button = e.target.closest("[data-group-participants]");
        list.classList.toggle("hidden");
        button.setAttribute("aria-expanded", String(!list.classList.contains("hidden")));
        return;
    }
    if (e.target.closest("[data-leave-group]")) {
        const groupName = state.conversation.name;
        renderMessages();
        toast(`Você saiu da conversa "${groupName}".`);
        return;
    }
    const messageAction = e.target.closest("[data-message-action]");
    if (messageAction) {
        const index = Number(messageAction.dataset.messageIndex);
        if (messageAction.dataset.messageAction === "delete") {
            state.conversation.messages.splice(index, 1);
            renderConversation(state.conversation);
            toast("Mensagem eliminada.");
            return;
        }
        const message = state.conversation.messages[index];
        const row = e.target.closest(".message");
        const paragraph = row.querySelector("p");
        const actions = row.querySelector(".message-actions");
        paragraph.outerHTML = `<form class="message-edit-form" data-edit-message-index="${index}"><input aria-label="Editar mensagem" value="${escapeHtml(message[2])}" maxlength="500"><button aria-label="Guardar alteração"><i class="bi bi-check-lg"></i></button><button type="button" data-cancel-edit aria-label="Cancelar edição"><i class="bi bi-x-lg"></i></button></form>`;
        actions.remove();
        row.querySelector("input").focus();
        return;
    }
    if (e.target.closest("[data-cancel-edit]")) {
        renderConversation(state.conversation);
        return;
    }
    const c = e.target.closest("[data-conversation-id]");
    if (c)
        return renderConversation(
            state.conversations.find((x) => x.id === c.dataset.conversationId),
        );
    const action = e.target.dataset.action;
    if (action) {
        const row = e.target.closest("[data-request-id]"),
            request = state.requests.find(
                (x) => x.id === Number(row.dataset.requestId),
            );
        state.requests = state.requests.filter((x) => x !== request);
        updateBadges();
        renderRequests();
        toast(
            action === "accept"
                ? `Pedido de ${request.name} aceito!`
                : action === "block"
                    ? `${request.name} foi bloqueado(a).`
                    : `Pedido de ${request.name} rejeitado.`,
        );
    }
});
$("#chatBody").addEventListener("change", (e) => {
    if (e.target.id === "attachmentInput" && e.target.files.length)
        toast(`${e.target.files.length} ficheiro(s) selecionado(s).`);
});
$("#chatBody").addEventListener("submit", (e) => {
    if (e.target.matches("[data-edit-message-index]")) {
        e.preventDefault();
        const index = Number(e.target.dataset.editMessageIndex);
        const text = $("input", e.target).value.trim();
        if (!text) return;
        state.conversation.messages[index][2] = text;
        renderConversation(state.conversation);
        toast("Mensagem editada.");
        return;
    }
    if (e.target.id !== "messageForm") return;
    e.preventDefault();
    const input = $("#messageInput"),
        text = input.value.trim();
    if (!text) return;
    state.conversation.messages.push(["Você", "S", text, "agora"]);
    renderConversation(state.conversation);
});
$("#chatSearchButton").addEventListener("click", () => {
    $("#chatSearch").classList.toggle("hidden");
    if (!$("#chatSearch").classList.contains("hidden"))
        $("#chatSearchInput").focus();
});
$("#chatSearchInput").addEventListener("input", (e) => {
    if (state.view !== "messages") return;
    const q = e.target.value.toLowerCase();
    document
        .querySelectorAll(".conversation-item")
        .forEach(
            (x) =>
                (x.style.display = x.textContent.toLowerCase().includes(q)
                    ? "flex"
                    : "none"),
        );
});
// Fecha ao clicar fora no computador. No telemóvel, o fechamento é feito
// exclusivamente pelo backdrop: isso evita que um toque numa conversa seja
// confundido com um toque fora enquanto o conteúdo do chat é redesenhado.
document.addEventListener("click", (event) => {
    if (window.innerWidth <= 720 || !chatPanel.classList.contains("open")) return;

    // composedPath conserva a origem do clique mesmo que renderConversation()
    // substitua o botão clicado antes de o evento chegar ao documento.
    const clickPath = event.composedPath();
    const isInsideChat = clickPath.includes(chatPanel);
    const isInsideSidebar = clickPath.includes(sidebar);
    const isChatTrigger = event.target.closest("[data-chat-view], #openChat");
    const isGroupModal = event.target.closest(".group-modal");

    // A sidebar contém controlos que podem abrir ou trocar a conversa. No
    // desktop, renderConversation() ocorre antes deste listener no documento;
    // sem esta verificação, o mesmo clique era tratado como clique fora e
    // fechava imediatamente a gaveta acabada de abrir.
    if (!isInsideChat && !isInsideSidebar && !isChatTrigger && !isGroupModal)
        hidePanels();
});
document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") hidePanels();
});
$("#notificationButton").addEventListener("click", (e) => {
    e.stopPropagation();
    $("#notificationsMenu").classList.toggle("hidden");
});
document.addEventListener("click", (e) => {
    if (!e.target.closest(".notifications"))
        $("#notificationsMenu").classList.add("hidden");
});
// ============================================================
// PESQUISA E PEDIDOS DE AMIZADE
// ============================================================

let searchTimer;
$("#chatBody").addEventListener("input", (e) => {
    if (e.target.id !== "friendSearchInput") return;
    clearTimeout(searchTimer);
    const q = e.target.value.trim();
    const results = $("#friendSearchResults");
    if (q.length < 2) {
        results.innerHTML = "";
        return;
    }
    searchTimer = setTimeout(() => searchUsers(q), 300);
});
async function searchUsers(username) {
    try {
        const r = await fetch(`/search?username=${encodeURIComponent(username)}`, {
            headers: {
                Authorization: "Bearer " + localStorage.getItem("accessToken"),
            },
        });
        if (!r.ok) throw Error();
        renderSearchResults(await r.json());
    } catch (e) {
        console.error("Erro ao pesquisar jogadores.", e);
    }
}
function renderSearchResults(users) {
    const results = $("#friendSearchResults");
    if (!results) return;
    results.innerHTML = "";
    if (!users.length) {
        results.innerHTML =
            '<div class="friend-search-empty">Nenhum jogador encontrado.</div>';
        return;
    }
    users.forEach((u) => {
        let text = "Adicionar",
            icon = "fa-user-plus",
            cls = "add-friend-button",
            disabled = "";
        if (u.friendshipStatus === "PENDING_SENT") {
            text = "Enviado";
            icon = "fa-clock";
            cls += " pending";
            disabled = "disabled";
        }
        if (u.friendshipStatus === "ACCEPTED") {
            text = "Amigo";
            icon = "fa-check";
            cls += " friend";
            disabled = "disabled";
        }
        if (u.friendshipStatus === "BLOCKED") {
            text = "Bloqueado";
            icon = "fa-ban";
            cls += " blocked";
            disabled = "disabled";
        }
        if (u.friendshipStatus === "PENDING_RECEIVED") {
            text = "Responder";
            icon = "fa-envelope";
        }
        const row = document.createElement("div");
        row.className = "friend-search-user";
        row.innerHTML = `<div class="friend-search-left">${avatar((u.username || "?")[0].toUpperCase())}<span>${escapeHtml(u.username)}</span></div><button class="${cls}" data-user-id="${u.userId}" ${disabled}><i class="fa-solid ${icon}"></i> ${text}</button>`;
        results.appendChild(row);
    });
}
// Os resultados são renderizados dinamicamente dentro de #chatBody. O
// listener deste contentor também interrompe a propagação para o documento,
// por isso a delegação deve ficar no próprio contentor para o fetch ser
// sempre alcançado.
$("#chatBody").addEventListener("click", async (e) => {
    const b = e.target.closest(".add-friend-button");
    if (!b || b.disabled) return;
    try {
        const r = await fetch("/api/friendships/send", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + localStorage.getItem("accessToken"),
            },
            body: JSON.stringify({ addresseeId: b.dataset.userId }),
        });
        if (!r.ok) throw Error();
        b.disabled = true;
        b.classList.add("pending");
        b.innerHTML = '<i class="fa-solid fa-clock"></i> Enviado';
    } catch {
        toast("Não foi possível enviar o pedido.");
    }
});
// ============================================================
// CRIAÇÃO LOCAL DE GRUPOS E PESQUISA DE PARTICIPANTES
// ============================================================

const groupDraft = { members: [], avatarImage: "" };
let groupSearchTimer;

function openGroupModal() {
    $("#groupModal").classList.remove("hidden");
    $("#groupNameInput").focus();
}
function closeGroupModal() {
    $("#groupModal").classList.add("hidden");
    $("#createGroupForm").reset();
    groupDraft.members = [];
    groupDraft.avatarImage = "";
    $("#groupAvatarPreview").textContent = "👥";
    $("#groupSearchResults").innerHTML = "";
    renderDraftMembers();
}
function renderDraftMembers() {
    const target = $("#selectedGroupMembers");
    target.innerHTML = groupDraft.members.map((member, index) =>
        `<div class="selected-member">${avatar(member.letter)}<span>${escapeHtml(member.name)}</span><select class="member-role" data-member-role="${index}"><option value="Admin" ${member.role === "Admin" ? "selected" : ""}>Admin</option><option value="Usuário normal" ${member.role !== "Admin" ? "selected" : ""}>Usuário normal</option></select><button type="button" class="remove-member" data-remove-member="${index}" aria-label="Remover ${escapeHtml(member.name)}"><i class="bi bi-x-lg"></i></button></div>`
    ).join("") || '<p class="friend-search-empty">Nenhum participante adicionado.</p>';
}
function renderGroupSearchResults(users) {
    const target = $("#groupSearchResults");
    if (!target) return;
    const available = users.filter((user) => !groupDraft.members.some((member) => String(member.id) === String(user.userId)));
    target.innerHTML = available.length ? available.map((user) => {
        const name = user.username || "Jogador";
        return `<div class="group-search-user">${avatar(name[0].toUpperCase())}<span>${escapeHtml(name)}</span><button type="button" class="group-add-member" data-group-user-id="${escapeHtml(String(user.userId))}" data-group-user-name="${escapeHtml(name)}">Adicionar</button></div>`;
    }).join("") : '<p class="friend-search-empty">Nenhum jogador disponível.</p>';
}
async function searchGroupPlayers(username) {
    try {
        const response = await fetch(`/search?username=${encodeURIComponent(username)}`, {
            headers: { Authorization: "Bearer " + localStorage.getItem("accessToken") },
        });
        if (!response.ok) throw Error();
        renderGroupSearchResults(await response.json());
    } catch (error) {
        console.error("Erro ao pesquisar participantes.", error);
        $("#groupSearchResults").innerHTML = '<p class="friend-search-empty">Não foi possível pesquisar agora.</p>';
    }
}
function addCreatedGroup(group) {
    state.groups[group.id] = group;
    const button = document.createElement("button");
    button.className = "group";
    button.dataset.conversation = group.id;
    button.innerHTML = `<span class="group-icon">${group.avatarImage ? '<i class="bi bi-people-fill"></i>' : group.letter}</span><span>${escapeHtml(group.name)}</span>`;
    $("#groupsList").appendChild(button);
}

$("#createGroupButton").addEventListener("click", openGroupModal);
$("#closeGroupModal").addEventListener("click", closeGroupModal);
$("#cancelGroupModal").addEventListener("click", closeGroupModal);
$("#groupModal").addEventListener("click", (event) => { if (event.target === event.currentTarget) closeGroupModal(); });
$("#groupAvatarInput").addEventListener("change", (event) => {
    const file = event.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.addEventListener("load", () => {
        groupDraft.avatarImage = reader.result;
        $("#groupAvatarPreview").innerHTML = `<img src="${reader.result}" alt="Pré-visualização do avatar">`;
    });
    reader.readAsDataURL(file);
});
$("#groupPlayerSearch").addEventListener("input", (event) => {
    clearTimeout(groupSearchTimer);
    const query = event.target.value.trim();
    if (query.length < 2) return $("#groupSearchResults").innerHTML = "";
    groupSearchTimer = setTimeout(() => searchGroupPlayers(query), 300);
});
$("#groupSearchResults").addEventListener("click", (event) => {
    const button = event.target.closest("[data-group-user-id]");
    if (!button) return;
    groupDraft.members.push({ id: button.dataset.groupUserId, name: button.dataset.groupUserName, letter: button.dataset.groupUserName[0].toUpperCase(), role: "Usuário normal" });
    renderDraftMembers();
    $("#groupPlayerSearch").value = "";
    $("#groupSearchResults").innerHTML = "";
});
$("#selectedGroupMembers").addEventListener("click", (event) => {
    const button = event.target.closest("[data-remove-member]");
    if (!button) return;
    groupDraft.members.splice(Number(button.dataset.removeMember), 1);
    renderDraftMembers();
});
$("#selectedGroupMembers").addEventListener("change", (event) => {
    if (event.target.matches("[data-member-role]")) groupDraft.members[Number(event.target.dataset.memberRole)].role = event.target.value;
});
$("#createGroupForm").addEventListener("submit", (event) => {
    event.preventDefault();
    const name = $("#groupNameInput").value.trim();
    if (!name) return;
    const id = `group-${Date.now()}`;
    const group = {
        id, name, subtitle: `${groupDraft.members.length + 1} membro(s)`, letter: "👥", isGroup: true,
        avatarImage: groupDraft.avatarImage,
        participants: [{ name: "Você", letter: "S", role: "Admin" }, ...groupDraft.members],
        messages: [],
    };
    addCreatedGroup(group);
    closeGroupModal();
    document.querySelectorAll(".group").forEach((button) => button.classList.toggle("active", button.dataset.conversation === id));
    renderConversation(group);
    toast(`Grupo ${name} criado!`);
});

// ============================================================
// UTILIZADOR AUTENTICADO E DADOS INICIAIS
// ============================================================

async function loadUser() {
    try {
        const r = await fetch("/me", {
            headers: {
                Authorization: "Bearer " + localStorage.getItem("accessToken"),
            },
        });
        if (!r.ok) {
            location.href = "/index.html";
            return;
        }
        const u = await r.json(),
            name = u.nome || u.username;
        $("#username").textContent = name;
        $("#onlineName").textContent = name;
    } catch (e) {
        console.error(e);
    }
}
async function loadFriendRequestCount() {
    try {
        const r = await fetch("/api/friendships/received", {
            headers: {
                Authorization: "Bearer " + localStorage.getItem("accessToken"),
            },
        });
        if (!r.ok) return;
        const data = await r.json();
        console.log(data);
        if (Array.isArray(data)) {
            state.requests = data.map((x, i) => ({
                id: x.id || x.friendshipId || i + 1,
                name: x.username || x.requesterUsername || "Jogador",
                letter: (x.username || x.requesterUsername || "J")[0].toUpperCase(),
                text: "enviou um pedido de amizade.",
            }));
            updateBadges();
        }
    } catch (e) {
        console.error(e);
    }
}
// ============================================================
// MENU DA CONTA E MODO DE COR
// ============================================================

// O tema escolhido é mantido entre visitas e o rótulo indica a ação disponível.
function setTheme(theme) {
    const light = theme === "light";
    document.body.classList.toggle("light-mode", light);
    localStorage.setItem("ludo-theme", light ? "light" : "dark");
    document.querySelectorAll('[data-account-action="theme"] span').forEach((label) => {
        label.textContent = light ? "Modo escuro" : "Modo claro";
    });
}

function closeAccountMenus() {
    document
        .querySelectorAll(".account-menu")
        .forEach((menu) => menu.classList.add("hidden"));
    document
        .querySelectorAll(".account-menu-trigger")
        .forEach((button) => button.setAttribute("aria-expanded", "false"));
}

function toggleAccountMenu(button) {
    const menu = button.parentElement.querySelector(".account-menu");
    const willOpen = menu.classList.contains("hidden");
    closeAccountMenus();
    if (willOpen) {
        menu.classList.remove("hidden");
        button.setAttribute("aria-expanded", "true");
    }
}

document.querySelectorAll(".account-menu-trigger").forEach((button) => {
    button.addEventListener("click", (event) => {
        event.stopPropagation();
        toggleAccountMenu(button);
    });
});

document.addEventListener("click", (event) => {
    if (!event.target.closest(".account-menu-wrap")) closeAccountMenus();
});

document.addEventListener("click", (event) => {
    const action = event.target.closest("[data-account-action]")?.dataset
        .accountAction;
    if (!action) return;

    if (action === "theme") {
        setTheme(document.body.classList.contains("light-mode") ? "dark" : "light");
        toast(document.body.classList.contains("light-mode") ? "Modo claro ativado." : "Modo escuro ativado.");
    } else if (action === "logout") {
        localStorage.removeItem("accessToken");
        location.href = "/index.html";
    } else {
        toast(
            action === "profile"
                ? "Meu perfil — ligue esta opção à sua página de perfil."
                : "Definições — ligue esta opção à sua página de definições.",
        );
    }
    closeAccountMenus();
});

// ============================================================
// INICIALIZAÇÃO DA PÁGINA
// ============================================================

setTheme(localStorage.getItem("ludo-theme") === "light" ? "light" : "dark");
updateBadges();
loadUser();
loadFriendRequestCount();
