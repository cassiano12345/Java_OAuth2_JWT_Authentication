// ============================================================
// LUDO STAR - HOME
// JavaScript da página principal
// ============================================================
window.onload = function () {

    console.log("PeerBox iniciado.");
    verificarUtilizador();

}
const $ = (selector, root = document) => root.querySelector(selector);
const $$ = (selector, root = document) => [...root.querySelectorAll(selector)];





// ============================================================
// Verificar se o User fez Login!
// ============================================================

async function verificarUtilizador() {

    const token = localStorage.getItem("accessToken");
    console.log(token);


    const response = await fetch("/me", {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        }
    });

    if (!response.ok) {
        console.log("Não esta feito");
        window.location = "/index.html";
        return;

    }
    var dados =await response.json();

    document.getElementById("username").textContent = dados.nome;
    document.getElementById("online").textContent = dados.nome;
}

// ============================================================
// ADICIONAR NOVOS AMIGOS!
// ============================================================
const friendSearchInput = document.getElementById("friendSearchInput");
const friendSearchResults = document.getElementById("friendSearchResults");

let searchTimeout;

friendSearchInput.addEventListener("input", function () {

    clearTimeout(searchTimeout);

    const username = this.value.trim();

    if (username.length < 2) {

        friendSearchResults.innerHTML = "";
        return;

    }

    searchTimeout = setTimeout(() => {

        searchUsers(username);

    }, 300);

});
async function searchUsers(username) {

    try {

        const response = await fetch(
            `/search?username=${encodeURIComponent(username)}`,
            {
                headers: {
                    Authorization: "Bearer " + localStorage.getItem("accessToken")
                }
            }
        );

        if (!response.ok) {
            throw new Error("Erro ao pesquisar utilizadores.");
        }

        const users = await response.json();

        renderSearchResults(users);

    }
    catch (e) {

        console.error(e);

    }

}
function renderSearchResults(users) {

    friendSearchResults.innerHTML = "";

    if (users.length === 0) {

        friendSearchResults.innerHTML =
            "<div class='friend-search-empty'>Nenhum jogador encontrado.</div>";

        return;

    }

    users.forEach(user => {

        let buttonText = "Adicionar";
        let buttonIcon = "fa-user-plus";
        let buttonClass = "add-friend-button";
        let disabled = "";

        switch (user.friendshipStatus) {

            case "PENDING_SENT":

                buttonText = "Enviado";
                buttonIcon = "fa-clock";
                buttonClass += " pending";
                disabled = "disabled";
                break;

            case "PENDING_RECEIVED":

                buttonText = "Responder";
                buttonIcon = "fa-envelope";
                buttonClass += " received";
                break;

            case "ACCEPTED":

                buttonText = "Amigo";
                buttonIcon = "fa-check";
                buttonClass += " friend";
                disabled = "disabled";
                break;

            case "BLOCKED":

                buttonText = "Bloqueado";
                buttonIcon = "fa-ban";
                buttonClass += " blocked";
                disabled = "disabled";
                break;

        }

        const div = document.createElement("div");

        div.className = "friend-search-user";

        div.innerHTML = `

            <div class="friend-search-left">

                <div class="avatar">

                    ${user.username.charAt(0).toUpperCase()}

                </div>

                <span>${user.username}</span>

            </div>

            <button
                class="${buttonClass}"
                data-user-id="${user.userId}"
                ${disabled}>

                <i class="fa-solid ${buttonIcon}"></i>

                ${buttonText}

            </button>

        `;

        friendSearchResults.appendChild(div);

    });

}
document.addEventListener("click", async function (e) {

    if (!e.target.closest(".add-friend-button"))
        return;

    const button = e.target.closest(".add-friend-button");

    if (button.disabled)
        return;

    const addresseeId = button.dataset.userId;

    try {

        await sendFriendRequest(addresseeId);

        button.disabled = true;
        button.classList.add("pending");

        button.innerHTML = `

            <i class="fa-solid fa-clock"></i>

            Enviado

        `;

    }
    catch (error) {

        alert(error.message);

    }

});
async function sendFriendRequest(addresseeId) {

    const response = await fetch("/api/friendships/send", {

        method: "POST",

        headers: {

            "Content-Type": "application/json",
            Authorization: "Bearer " + localStorage.getItem("accessToken")

        },

        body: JSON.stringify({

            addresseeId: addresseeId

        })

    });

    if (!response.ok) {

        throw new Error("Não foi possível enviar o pedido.");

    }

}


// ============================================================
// SIDEBAR / MENU MOBILE
// ============================================================

const sidebar = $("#sidebar");
const chatPanel = $("#chatPanel");
const mobileMenuButton = $("#mobileMenuButton");
const mobileChatButton = $("#mobileChatButton");

if (mobileMenuButton) {
    mobileMenuButton.addEventListener("click", () => {
        sidebar.classList.toggle("open");
    });
}

if (mobileChatButton) {
    mobileChatButton.addEventListener("click", () => {

        if (window.innerWidth <= 1000) {
            sidebar.classList.remove("open");

            chatPanel.classList.toggle("mobile-chat-open");
        }
    });
}


// Fecha a sidebar quando clicar fora dela no celular

document.addEventListener("click", (event) => {

    if (
        window.innerWidth <= 720 &&
        sidebar &&
        sidebar.classList.contains("open") &&
        !sidebar.contains(event.target) &&
        !mobileMenuButton.contains(event.target)
    ) {
        sidebar.classList.remove("open");
    }

});


// ============================================================
// GRUPOS DE MENSAGENS
// ============================================================

const groups = $$(".group-item");

groups.forEach(group => {

    group.addEventListener("click", () => {

        // Remove active dos outros grupos
        groups.forEach(item => {
            item.classList.remove("active");
        });

        // Ativa o grupo selecionado
        group.classList.add("active");

        // Atualiza cabeçalho do chat
        const chatTitle = $("#chatTitle");
        const chatSubtitle = $("#chatSubtitle");

        if (chatTitle) {
            chatTitle.textContent = group.dataset.group;
        }

        if (chatSubtitle) {
            chatSubtitle.textContent = "Grupo de mensagens";
        }

        // Fecha menu no celular
        if (window.innerWidth <= 720) {
            sidebar.classList.remove("open");
        }

    });

});


// ============================================================
// PESQUISA DE MENSAGENS
// ============================================================

const chatSearchButton = $("#chatSearchButton");
const chatSearch = $("#chatSearch");
const chatSearchInput = $("#chatSearchInput");

if (chatSearchButton) {

    chatSearchButton.addEventListener("click", () => {

        chatSearch.classList.toggle("d-none");

        if (!chatSearch.classList.contains("d-none")) {
            chatSearchInput.focus();
        }

    });

}


// ============================================================
// ENVIO DE MENSAGENS
// ============================================================

const messageForm = $("#messageForm");
const messageInput = $("#messageInput");
const messagesContainer = $("#messages");

if (messageForm) {

    messageForm.addEventListener("submit", (event) => {

        event.preventDefault();

        const text = messageInput.value.trim();

        if (!text) {
            return;
        }

        const message = document.createElement("div");

        message.className = "message";

        message.innerHTML = `
            <span class="avatar message-avatar">
                S
            </span>

            <div>

                <div class="message-author">
                    ShadowKing

                    <time>
                        agora
                    </time>
                </div>

                <p>
                    ${escapeHtml(text)}
                </p>

            </div>
        `;

        messagesContainer.appendChild(message);

        // Limpa campo
        messageInput.value = "";

        // Scroll para última mensagem
        messagesContainer.scrollTop =
            messagesContainer.scrollHeight;

    });

}


// ============================================================
// BOTÃO DE ANEXO
// ============================================================

const attachButton = $("#attachButton");

if (attachButton) {

    attachButton.addEventListener("click", () => {

        showToast(
            "O sistema de anexos será conectado ao backend futuramente."
        );

    });

}


// ============================================================
// PROTEÇÃO CONTRA HTML INJETADO
// ============================================================

function escapeHtml(value) {

    const element = document.createElement("div");

    element.textContent = value;

    return element.innerHTML;

}


// ============================================================
// CRIAR GRUPO
// ============================================================

const createGroupButton = $("#createGroupButton");

if (createGroupButton) {

    createGroupButton.addEventListener("click", () => {

        const groupName = prompt(
            "Digite o nome do novo grupo:"
        );

        if (!groupName || !groupName.trim()) {
            return;
        }

        const name = groupName.trim();

        const group = document.createElement("button");

        group.className = "group-item";

        group.dataset.group = name;

        group.innerHTML = `
            <span class="group-icon">
                💬
            </span>

            <span>
                ${escapeHtml(name)}
            </span>
        `;

        $("#groupsList").appendChild(group);


        // Evento do novo grupo

        group.addEventListener("click", () => {

            $$(".group-item").forEach(item => {
                item.classList.remove("active");
            });

            group.classList.add("active");

            $("#chatTitle").textContent = name;

            $("#chatSubtitle").textContent =
                "Novo grupo";

        });


        showToast(
            `Grupo "${name}" criado com sucesso!`
        );

    });

}


// ============================================================
// DADOS DOS TORNEIOS
// ============================================================

const rankingData = {

    "copa-reis": {

        title: "Copa dos Reis",

        format: "Liga • Ida e volta",

        trophy: "🏆"

    },

    "mega-ludo": {

        title: "Mega Ludo",

        format: "Eliminatórias • Só ida",

        trophy: "🔥"

    },

    "masters": {

        title: "Ludo Masters",

        format: "Liga • Só ida",

        trophy: "💎"

    }

};


// ============================================================
// MODAL DE CLASSIFICAÇÃO
// ============================================================

const rankingModal = $("#rankingModal");

const closeRankingButton =
    $("#closeRankingButton");


$$(".my-tournament-row").forEach(row => {

    row.addEventListener("click", () => {

        const tournamentId =
            row.dataset.tournament;

        const tournament =
            rankingData[tournamentId];

        if (!tournament) {
            return;
        }

        $("#rankingTitle").textContent =
            tournament.title;

        $("#rankingFormat").textContent =
            tournament.format;

        $("#rankingTrophy").textContent =
            tournament.trophy;

        rankingModal.classList.remove("d-none");

    });

});


if (closeRankingButton) {

    closeRankingButton.addEventListener("click", () => {

        rankingModal.classList.add("d-none");

    });

}


// Fechar modal clicando no fundo

if (rankingModal) {

    rankingModal.addEventListener("click", event => {

        if (event.target === rankingModal) {

            rankingModal.classList.add("d-none");

        }

    });

}


// ============================================================
// INSCRIÇÃO EM TORNEIOS
// ============================================================

const joinButtons = $$(".join-button");

joinButtons.forEach(button => {

    button.addEventListener("click", () => {

        const fee =
            Number(button.dataset.fee || 0);

        button.disabled = true;

        button.textContent =
            "INSCRITO ✓";

        button.style.color =
            "#64e894";

        button.style.borderColor =
            "#3dbb6c";


        showToast(
            `Inscrição simulada com sucesso! Entrada: 🪙 ${fee.toLocaleString("pt-BR")}`
        );

    });

});


// ============================================================
// MODAL DE CRIAÇÃO DE TORNEIO
// ============================================================

const createTournamentModal =
    $("#createTournamentModal");

const createTournamentButton =
    $("#createTournamentButton");

const closeCreateTournamentButton =
    $("#closeCreateTournamentButton");


if (createTournamentButton) {

    createTournamentButton.addEventListener("click", () => {

        createTournamentModal.classList.remove("d-none");

    });

}


if (closeCreateTournamentButton) {

    closeCreateTournamentButton.addEventListener("click", () => {

        createTournamentModal.classList.add("d-none");

    });

}


// Fechar clicando fora

if (createTournamentModal) {

    createTournamentModal.addEventListener("click", event => {

        if (event.target === createTournamentModal) {

            createTournamentModal.classList.add("d-none");

        }

    });

}


// ============================================================
// SELEÇÃO DE LIGA / ELIMINATÓRIAS
// ============================================================

const choiceInputs =
    $$(".choice-card input");


choiceInputs.forEach(input => {

    input.addEventListener("change", () => {

        const groupName =
            input.name;

        const groupInputs =
            $$(`.choice-card input[name="${groupName}"]`);

        groupInputs.forEach(item => {

            item
                .closest(".choice-card")
                .classList.remove("selected");

        });

        input
            .closest(".choice-card")
            .classList.add("selected");


        updateCreateForm();

    });

});


// Retorna radio selecionado

function getValue(name) {

    const input =
        $(`input[name="${name}"]:checked`);

    return input ? input.value : null;

}


// Atualiza formulário

function updateCreateForm() {

    const mode =
        getValue("championshipMode");

    const knockoutOptions =
        $("#knockoutOptions");


    // Mostra opções de eliminatórias

    if (mode === "Eliminatórias") {

        knockoutOptions.classList.remove("d-none");

    } else {

        knockoutOptions.classList.add("d-none");

    }


    updatePrize();

}


// ============================================================
// CÁLCULO DO PRÊMIO
// ============================================================

const slotsInput =
    $("#tournamentSlotsInput");

const feeInput =
    $("#tournamentFeeInput");

const prizePreview =
    $("#prizePreview");


function updatePrize() {

    const slots =
        Number(slotsInput.value);

    const fee =
        Number(feeInput.value);


    /*
        80% da arrecadação será
        destinada ao prêmio.

        Exemplo:

        16 jogadores
        x 1000 moedas
        = 16.000

        80%
        = 12.800
    */

    const prize =
        Math.floor(
            slots * fee * 0.8
        );


    prizePreview.textContent =
        "🪙 " +
        prize.toLocaleString("pt-BR");

}


if (slotsInput) {

    slotsInput.addEventListener(
        "change",
        updatePrize
    );

}

if (feeInput) {

    feeInput.addEventListener(
        "change",
        updatePrize
    );
}


// Calcula inicialmente

updatePrize();


// ============================================================
// DATA MÍNIMA DO TORNEIO
// ============================================================

const tournamentDateInput =
    $("#tournamentDateInput");


if (tournamentDateInput) {

    const now = new Date();

    now.setMinutes(
        now.getMinutes() -
        now.getTimezoneOffset()
    );

    tournamentDateInput.min =
        now.toISOString().slice(0, 16);

}


// ============================================================
// CRIAR TORNEIO
// ============================================================

const createTournamentForm =
    $("#createTournamentForm");


if (createTournamentForm) {

    createTournamentForm.addEventListener(
        "submit",
        event => {

            event.preventDefault();


            // Dados do formulário

            const name =
                $("#tournamentNameInput")
                    .value
                    .trim();


            const mode =
                getValue("championshipMode");


            const format =
                getValue("matchFormat");


            const slots =
                $("#tournamentSlotsInput")
                    .value;


            const fee =
                Number(
                    $("#tournamentFeeInput")
                        .value
                );


            const date =
                $("#tournamentDateInput")
                    .value;


            const description =
                $("#tournamentDescriptionInput")
                    .value
                    .trim();


            let knockout = null;


            if (mode === "Eliminatórias") {

                knockout =
                    $("#knockoutTypeInput")
                        .value;

            }


            // Objeto do torneio

            const tournament = {

                name: name,

                mode: mode,

                format: format,

                slots: slots,

                fee: fee,

                date: date,

                description: description,

                knockout: knockout,

                state: "Aguardando aceitação"

            };


            // Neste momento estamos simulando.
            // Futuramente isso irá para o backend.

            console.log(
                "Novo torneio:",
                tournament
            );


            // Adiciona na tabela

            addCreatedTournament(
                tournament
            );


            // Fecha modal

            createTournamentModal
                .classList
                .add("d-none");


            // Limpa formulário

            createTournamentForm.reset();


            // Volta para as opções padrão

            $$(".choice-card")
                .forEach(card => {

                    card.classList.remove(
                        "selected"
                    );

                });


            const defaultMode =
                $('input[name="championshipMode"]');


            const defaultFormat =
                $('input[name="matchFormat"]');


            defaultMode.checked = true;

            defaultFormat.checked = true;


            defaultMode
                .closest(".choice-card")
                .classList
                .add("selected");


            defaultFormat
                .closest(".choice-card")
                .classList
                .add("selected");


            updateCreateForm();


            showToast(
                `"${name}" criado e enviado para análise!`
            );

        }
    );

}


// ============================================================
// ADICIONAR TORNEIO NA TABELA
// ============================================================

function addCreatedTournament(tournament) {

    const table =
        document.querySelector(
            ".created-row"
        ).parentElement;


    const row =
        document.createElement("div");


    row.className =
        "table-grid created-row";


    const icon =
        tournament.mode === "Liga"
            ? "🏟️"
            : "🏆";


    const feeText =
        tournament.fee > 0
            ? `🪙 ${tournament.fee.toLocaleString("pt-BR")}`
            : "Grátis";


    row.innerHTML = `

        <span class="tournament-cell">

            <i class="tournament-icon purple">
                ${icon}
            </i>

            <span>

                <b>
                    ${escapeHtml(tournament.name)}
                </b>

                <small>
                    ${escapeHtml(feeText)}
                </small>

            </span>

        </span>


        <span>

            <b>
                ${escapeHtml(tournament.mode)}
            </b>

        </span>


        <span>

            ${escapeHtml(tournament.format)}

        </span>


        <span>

            ${escapeHtml(tournament.slots)}

        </span>


        <span class="status pending">

            AGUARDANDO ACEITAÇÃO

        </span>

    `;


    table.appendChild(row);

}


// ============================================================
// BOTÃO DE MOEDAS
// ============================================================

const buyCoinsButton =
    $("#buyCoinsButton");


if (buyCoinsButton) {

    buyCoinsButton.addEventListener(
        "click",
        () => {

            showToast(
                "A loja de moedas será integrada futuramente."
            );

        }
    );

}


// ============================================================
// PERFIL
// ============================================================

const profileButton =
    $("#profileButton");


if (profileButton) {

    profileButton.addEventListener(
        "click",
        () => {

            showToast(
                "Menu de perfil pronto para receber suas opções."
            );

        }
    );

}


// ============================================================
// CONFIGURAÇÕES
// ============================================================

const settingsButton =
    $("#settingsButton");


if (settingsButton) {

    settingsButton.addEventListener(
        "click",
        () => {

            showToast(
                "Configurações do usuário."
            );

        }
    );

}


// ============================================================
// BOTÃO "VER TODOS"
// ============================================================

const viewMyTournamentsButton =
    $("#viewMyTournamentsButton");


if (viewMyTournamentsButton) {

    viewMyTournamentsButton.addEventListener(
        "click",
        () => {

            showToast(
                "Aqui será aberta a página com todos os torneios."
            );

        }
    );

}


const viewCreatedTournamentsButton =
    $("#viewCreatedTournamentsButton");


if (viewCreatedTournamentsButton) {

    viewCreatedTournamentsButton.addEventListener(
        "click",
        () => {

            showToast(
                "Aqui será aberta a gestão completa dos seus torneios."
            );

        }
    );

}


// ============================================================
// MENU INÍCIO
// ============================================================

const homeMenu =
    $("#homeMenu");


if (homeMenu) {

    homeMenu.addEventListener(
        "click",
        () => {

            window.scrollTo({
                top: 0,
                behavior: "smooth"
            });

        }
    );

}


// ============================================================
// MENU MENSAGENS
// ============================================================

const messagesMenu =
    $("#messagesMenu");


if (messagesMenu) {

    messagesMenu.addEventListener(
        "click",
        () => {

            if (window.innerWidth <= 1000) {

                chatPanel.classList.add(
                    "mobile-chat-open"
                );

            } else {

                $("#messageInput").focus();

            }

        }
    );

}


// ============================================================
// ESC FECHA MODAIS
// ============================================================

document.addEventListener(
    "keydown",
    event => {

        if (event.key !== "Escape") {
            return;
        }


        // Fecha todos os modais

        $$(".modal-overlay")
            .forEach(modal => {

                modal.classList.add(
                    "d-none"
                );

            });


        // Fecha sidebar

        sidebar.classList.remove(
            "open"
        );

    }
);


// ============================================================
// TOAST / NOTIFICAÇÃO
// ============================================================

let toastTimer;


function showToast(message) {

    const toast =
        $("#toastMessage");


    if (!toast) {
        return;
    }


    toast.textContent =
        message;


    toast.classList.add(
        "show"
    );


    clearTimeout(
        toastTimer
    );


    toastTimer =
        setTimeout(
            () => {

                toast.classList.remove(
                    "show"
                );

            },
            3000
        );

}