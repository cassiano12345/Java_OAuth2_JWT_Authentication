const loginForm = document.getElementById("loginForm");

const usernameInput = document.getElementById("usernameInput");

const passwordInput =
    document.getElementById("passwordInput");

const rememberCheckbox =
    document.getElementById("rememberCheckbox");

const loginButton =
    document.getElementById("loginButton");

const togglePasswordButton =
    document.getElementById("togglePasswordButton");

const togglePasswordIcon =
    document.getElementById("togglePasswordIcon");

const loginMessage =
    document.getElementById("loginMessage");

const googleLoginButton =
    document.getElementById("googleLoginButton");

const registerLink =
    document.getElementById("registerLink");

const forgotPasswordLink =
    document.getElementById("forgotPasswordLink");


// ========================================
// MOSTRAR / ESCONDER SENHA
// ========================================

togglePasswordButton.addEventListener(
    "click",
    function () {

        const isPassword =
            passwordInput.type === "password";

        if (isPassword) {

            passwordInput.type = "text";

            togglePasswordIcon.classList.remove(
                "bi-eye-fill"
            );

            togglePasswordIcon.classList.add(
                "bi-eye-slash-fill"
            );

            togglePasswordButton.setAttribute(
                "aria-label",
                "Esconder senha"
            );

        } else {

            passwordInput.type = "password";

            togglePasswordIcon.classList.remove(
                "bi-eye-slash-fill"
            );

            togglePasswordIcon.classList.add(
                "bi-eye-fill"
            );

            togglePasswordButton.setAttribute(
                "aria-label",
                "Mostrar senha"
            );
        }
    }
);


// ========================================
// LOGIN
// ========================================

 loginForm.addEventListener(
    "submit",

    async function (event) {

        event.preventDefault();

        const username =
            usernameInput.value.trim();

        const password =
            passwordInput.value;

        clearMessage();


        // Validação
        if (!username || !password) {

            showMessage(
                "Preencha seu e-mail e sua senha.",
                "error"
            );

            return;
        }


        // Loading
        setLoading(true);

        console.log({
            email: username,
            password: password,
            remember: rememberCheckbox.checked
        });

        const response = await fetch("/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        let dados = {};

        try {
            dados = await response.json();
        } catch (error) {
            console.log("Resposta não contém JSON");
        }

        console.log("Status:", response.status);
        console.log("Dados:", dados);

        if (response.ok && dados.accessToken) {

            localStorage.setItem("accessToken", dados.accessToken);

            setTimeout(() => {
                setLoading(false);
                showMessage(
                    "Login realizado! Boa partida! 🎲",
                    "success"
                );
                window.location.href = "home.html";
            }, 1500);

        } else {

            setTimeout(() => {
                setLoading(false);
                showMessage(
                    "Erro ao efetuar Login, verifique o username ou a password!",
                    "error"
                );
            }, 1500);
        }

    }
);


// ========================================
// GOOGLE
// ========================================

googleLoginButton.addEventListener(
    "click",
    function () {

        console.log(
            "Login com Google selecionado."
        );

        showMessage(
            "A autenticação com Google será iniciada.",
            "success"
        );
    }
);


// ========================================
// CRIAR CONTA
// ========================================

registerLink.addEventListener(
    "click",

    function (event) {
        window.location.href = "register.html";

        event.preventDefault();


    }
);


// ========================================
// ESQUECI A SENHA
// ========================================

forgotPasswordLink.addEventListener(
    "click",
    function (event) {

        event.preventDefault();

        const email =
            usernameInput.value.trim();

        if (!email) {

            showMessage(
                "Digite seu e-mail primeiro.",
                "error"
            );

            usernameInput.focus();

            return;
        }

        showMessage(
            "Enviaremos as instruções para seu e-mail.",
            "success"
        );
    }
);


// ========================================
// FUNÇÕES AUXILIARES
// ========================================


function showMessage(message, type) {

    loginMessage.textContent = message;

    loginMessage.classList.remove(
        "d-none",
        "success",
        "error"
    );

    loginMessage.classList.add(type);
}


function clearMessage() {

    loginMessage.textContent = "";

    loginMessage.classList.add(
        "d-none"
    );

    loginMessage.classList.remove(
        "success",
        "error"
    );
}


function setLoading(loading) {


    loginButton.disabled = loading;

    if (loading) {

        loginButton.innerHTML = `
            <span
                class="spinner-border spinner-border-sm"
                role="status"
            ></span>

            Entrando...
        `;

    } else {

        loginButton.innerHTML = `
            <span class="button-icon">
                <i class="bi bi-controller"></i>
            </span>

            <span>ENTRAR NO JOGO</span>

            <i class="bi bi-chevron-right"></i>
        `;
    }
}

const viewTournamentsButton =
    document.getElementById("viewTournamentsButton");


viewTournamentsButton.addEventListener(
    "click",
    function () {

        console.log(
            "Abrir lista completa de torneios..."
        );

        alert(
            "Aqui será aberta a página com todos os torneios."
        );
    }
);