// =====================================================
// ELEMENTOS
// =====================================================

const messages =
    document.getElementById("messages");

const messageForm =
    document.getElementById("messageForm");

const messageInput =
    document.getElementById("messageInput");

const chatSearchButton =
    document.getElementById("chatSearchButton");

const chatSearch =
    document.getElementById("chatSearch");

const chatSearchInput =
    document.getElementById("chatSearchInput");


// =====================================================
// TROCAR GRUPO
// =====================================================

const groupItems =
    document.querySelectorAll(".group-item");


groupItems.forEach(function (group) {

    group.addEventListener(
        "click",
        function () {

            groupItems.forEach(function (item) {
                item.classList.remove("active");
            });

            group.classList.add("active");

            const groupName =
                group.querySelector("span:nth-child(2)")
                    ?.textContent
                    .trim();

            document.querySelector(
                ".chat-title"
            ).textContent =
                groupName || "Conversa";


            document.querySelector(
                ".chat-subtitle"
            ).textContent =
                "Grupo de mensagens";

        }
    );

});


// =====================================================
// PESQUISA
// =====================================================

chatSearchButton.addEventListener(
    "click",
    function () {

        chatSearch.classList.toggle("d-none");

        if (!chatSearch.classList.contains("d-none")) {
            chatSearchInput.focus();
        }

    }
);


// =====================================================
// ENVIAR MENSAGEM
// =====================================================

messageForm.addEventListener(
    "submit",
    function (event) {

        event.preventDefault();

        const text =
            messageInput.value.trim();

        if (!text) {
            return;
        }


        const message =
            document.createElement("div");

        message.className =
            "message";


        message.innerHTML = `

            <div class="avatar message-avatar">
                S
            </div>

            <div class="message-content">

                <div class="message-author">

                    ShadowKing

                    <span>
                        agora
                    </span>

                </div>

                <p>
                    ${escapeHtml(text)}
                </p>

            </div>

        `;


        messages.appendChild(message);

        messageInput.value = "";

        messages.scrollTop =
            messages.scrollHeight;

    }
);


// =====================================================
// ESCAPAR HTML
// =====================================================

function escapeHtml(text) {

    const div =
        document.createElement("div");

    div.textContent =
        text;

    return div.innerHTML;

}


// =====================================================
// CLASSIFICAÇÃO
// =====================================================

const rankingModal =
    document.getElementById(
        "rankingModal"
    );

const rankingTitle =
    document.getElementById(
        "rankingTitle"
    );

const closeRankingButton =
    document.getElementById(
        "closeRankingButton"
    );


const tournamentRows =
    document.querySelectorAll(
        ".my-tournament-row"
    );


tournamentRows.forEach(function (row) {

    row.addEventListener(
        "click",
        function () {

            const tournament =
                row.dataset.tournament;

            openRanking(tournament);

        }
    );

});


function openRanking(tournament) {

    const tournamentNames = {

        "copa-reis":
            "Copa dos Reis",

        "mega-ludo":
            "Mega Ludo",

        "masters":
            "Ludo Masters"

    };


    rankingTitle.textContent =
        tournamentNames[tournament]
        || "Torneio";


    rankingModal.classList.remove(
        "d-none"
    );

}


closeRankingButton.addEventListener(
    "click",
    function () {

        rankingModal.classList.add(
            "d-none"
        );

    }
);


// Fechar clicando fora

rankingModal.addEventListener(
    "click",
    function (event) {

        if (
            event.target ===
            rankingModal
        ) {

            rankingModal.classList.add(
                "d-none"
            );

        }

    }
);


// =====================================================
// ENTRAR EM TORNEIO
// =====================================================

const joinButtons =
    document.querySelectorAll(
        ".join-button"
    );


joinButtons.forEach(function (button) {

    button.addEventListener(
        "click",
        function () {

            const tournament =
                button.dataset.tournament;


            button.disabled = true;

            button.textContent =
                "INSCRITO ✓";


            button.style.color =
                "#64e894";

            button.style.borderColor =
                "#3dbb6c";


            console.log(
                "Usuário inscrito:",
                tournament
            );

        }
    );

});


// =====================================================
// CRIAR GRUPO
// =====================================================

const createGroupButton =
    document.getElementById(
        "createGroupButton"
    );


createGroupButton.addEventListener(
    "click",
    function () {

        const groupName =
            prompt(
                "Nome do novo grupo:"
            );


        if (!groupName) {
            return;
        }


        const groupsList =
            document.getElementById(
                "groupsList"
            );


        const group =
            document.createElement("button");


        group.className =
            "group-item";


        group.dataset.group =
            groupName;


        group.innerHTML = `

            <span class="group-icon">
                💬
            </span>

            <span>
                ${escapeHtml(groupName)}
            </span>

        `;


        groupsList.appendChild(group);


        group.addEventListener(
            "click",
            function () {

                groupItems.forEach(
                    function (item) {
                        item.classList.remove(
                            "active"
                        );
                    }
                );

                group.classList.add(
                    "active"
                );

                document.querySelector(
                    ".chat-title"
                ).textContent =
                    groupName;

            }
        );

    }
);


// =====================================================
// CRIAR TORNEIO
// =====================================================

const createTournamentButton =
    document.getElementById(
        "createTournamentButton"
    );

const createTournamentModal =
    document.getElementById(
        "createTournamentModal"
    );

const closeCreateTournamentButton =
    document.getElementById(
        "closeCreateTournamentButton"
    );


createTournamentButton.addEventListener(
    "click",
    function () {

        createTournamentModal.classList
            .remove("d-none");

    }
);


closeCreateTournamentButton.addEventListener(
    "click",
    function () {

        createTournamentModal.classList
            .add("d-none");

    }
);


createTournamentModal.addEventListener(
    "click",
    function (event) {

        if (
            event.target ===
            createTournamentModal
        ) {

            createTournamentModal.classList
                .add("d-none");

        }

    }
);


// =====================================================
// PREÇO / PRÊMIO
// =====================================================

const slotsInput =
    document.getElementById(
        "tournamentSlotsInput"
    );

const feeInput =
    document.getElementById(
        "tournamentFeeInput"
    );

const prizePreview =
    document.getElementById(
        "prizePreview"
    );


function updatePrize() {

    const slots =
        Number(slotsInput.value);

    const fee =
        Number(feeInput.value);


    /*
     * Exemplo:
     * 80% do valor arrecadado
     * vai para o prêmio.
     */

    const prize =
        Math.floor(
            slots * fee * 0.8
        );


    prizePreview.textContent =
        "🪙 " +
        prize.toLocaleString("pt-BR");

}


slotsInput.addEventListener(
    "change",
    updatePrize
);

feeInput.addEventListener(
    "change",
    updatePrize
);

updatePrize();


// =====================================================
// FORM CRIAR TORNEIO
// =====================================================

const createTournamentForm =
    document.getElementById(
        "createTournamentForm"
    );


createTournamentForm.addEventListener(
    "submit",
    function (event) {

        event.preventDefault();


        const name =
            document.getElementById(
                "tournamentNameInput"
            ).value.trim();


        const slots =
            slotsInput.value;


        const fee =
            feeInput.value;


        const date =
            document.getElementById(
                "tournamentDateInput"
            ).value;


        if (!name || !date) {

            alert(
                "Preencha os campos obrigatórios."
            );

            return;
        }


        console.log({
            name: name,
            slots: slots,
            fee: fee,
            date: date
        });


        alert(
            "Torneio criado com sucesso! 🏆"
        );


        createTournamentModal.classList
            .add("d-none");


        createTournamentForm.reset();

        updatePrize();

    }
);