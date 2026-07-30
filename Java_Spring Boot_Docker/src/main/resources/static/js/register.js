const registerForm =
    document.getElementById("registerForm");

const usernameInput =
    document.getElementById("usernameInput");

const emailInput =
    document.getElementById("emailInput");

const passwordInput =
    document.getElementById("passwordInput");

const confirmPasswordInput =
    document.getElementById("confirmPasswordInput");

const termsCheckbox =
    document.getElementById("termsCheckbox");

const registerButton =
    document.getElementById("registerButton");

const registerMessage =
    document.getElementById("registerMessage");

const togglePasswordButton =
    document.getElementById(
        "togglePasswordButton"
    );

const toggleConfirmPasswordButton =
    document.getElementById(
        "toggleConfirmPasswordButton"
    );

const passwordIcon =
    document.getElementById("passwordIcon");

const confirmPasswordIcon =
    document.getElementById(
        "confirmPasswordIcon"
    );

const strengthBar =
    document.getElementById("strengthBar");

const strengthText =
    document.getElementById("strengthText");


// ========================================
// MOSTRAR / ESCONDER SENHA
// ========================================

togglePasswordButton.addEventListener(
    "click",
    function () {

        togglePassword(
            passwordInput,
            passwordIcon
        );

    }
);


toggleConfirmPasswordButton.addEventListener(
    "click",
    function () {

        togglePassword(
            confirmPasswordInput,
            confirmPasswordIcon
        );

    }
);


function togglePassword(input, icon) {

    if (input.type === "password") {

        input.type = "text";

        icon.classList.remove(
            "bi-eye-fill"
        );

        icon.classList.add(
            "bi-eye-slash-fill"
        );

    } else {

        input.type = "password";

        icon.classList.remove(
            "bi-eye-slash-fill"
        );

        icon.classList.add(
            "bi-eye-fill"
        );
    }
}


// ========================================
// FORÇA DA SENHA
// ========================================

passwordInput.addEventListener(
    "input",
    function () {

        const password =
            passwordInput.value;

        updatePasswordStrength(password);

    }
);


function updatePasswordStrength(password) {

    let strength = 0;

    if (password.length >= 6) {
        strength++;
    }

    if (password.length >= 10) {
        strength++;
    }

    if (/[A-Z]/.test(password)) {
        strength++;
    }

    if (/[0-9]/.test(password)) {
        strength++;
    }

    if (/[^A-Za-z0-9]/.test(password)) {
        strength++;
    }


    const percentage =
        (strength / 5) * 100;

    strengthBar.style.width =
        percentage + "%";


    if (!password) {

        strengthText.textContent =
            "Digite uma senha";

        strengthBar.style.background =
            "transparent";

        return;
    }


    if (strength <= 2) {

        strengthText.textContent =
            "Senha fraca";

        strengthText.style.color =
            "#ff7373";

        strengthBar.style.background =
            "#ef4c5d";

    } else if (strength <= 4) {

        strengthText.textContent =
            "Senha média";

        strengthText.style.color =
            "#ffd34d";

        strengthBar.style.background =
            "#ffc928";

    } else {

        strengthText.textContent =
            "Senha forte!";

        strengthText.style.color =
            "#62e995";

        strengthBar.style.background =
            "#42dc7d";
    }
}


// ========================================
// SUBMIT
// ========================================

registerForm.addEventListener(
    "submit",
    function (event) {

        event.preventDefault();

        clearMessage();


        const username =
            usernameInput.value.trim();

        const email =
            emailInput.value.trim();

        const password =
            passwordInput.value;

        const confirmPassword =
            confirmPasswordInput.value;


        // Nome

        if (username.length < 3) {

            showMessage(
                "O nome de jogador precisa ter pelo menos 3 caracteres.",
                "error"
            );

            usernameInput.focus();

            return;
        }


        // E-mail

        if (!isValidEmail(email)) {

            showMessage(
                "Digite um endereço de e-mail válido.",
                "error"
            );

            emailInput.focus();

            return;
        }


        // Senha

        if (password.length < 6) {

            showMessage(
                "Sua senha precisa ter pelo menos 6 caracteres.",
                "error"
            );

            passwordInput.focus();

            return;
        }


        // Confirmação

        if (password !== confirmPassword) {

            showMessage(
                "As senhas não coincidem.",
                "error"
            );

            confirmPasswordInput.focus();

            return;
        }


        // Termos

        if (!termsCheckbox.checked) {

            showMessage(
                "Você precisa aceitar os Termos de Uso para continuar.",
                "error"
            );

            return;
        }


        // Loading

        setLoading(true);


        /*
         * Aqui você poderá conectar ao backend:
         *
         * fetch("/api/register", {
         *
         *     method: "POST",
         *
         *     headers: {
         *         "Content-Type":
         *             "application/json"
         *     },
         *
         *     body: JSON.stringify({
         *         username,
         *         email,
         *         password
         *     })
         *
         * });
         */


        // Simulação

        setTimeout(function () {

            setLoading(false);

            showMessage(
                "Conta criada com sucesso! Bem-vindo à arena! 🏆",
                "success"
            );

            console.log({
                username: username,
                email: email
            });

        }, 1500);

    }
);


// ========================================
// VALIDAR E-MAIL
// ========================================

function isValidEmail(email) {

    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        .test(email);

}


// ========================================
// MENSAGEM
// ========================================

function showMessage(message, type) {

    registerMessage.textContent =
        message;

    registerMessage.classList.remove(
        "d-none",
        "error",
        "success"
    );

    registerMessage.classList.add(
        type
    );
}


function clearMessage() {

    registerMessage.textContent = "";

    registerMessage.classList.add(
        "d-none"
    );

    registerMessage.classList.remove(
        "error",
        "success"
    );
}


// ========================================
// LOADING
// ========================================

function setLoading(loading) {

    registerButton.disabled =
        loading;

    if (loading) {

        registerButton.innerHTML = `
            <span
                class="spinner-border spinner-border-sm"
                role="status"
            ></span>

            CRIANDO SUA CONTA...
        `;

    } else {

        registerButton.innerHTML = `
            <i class="bi bi-stars"></i>

            <span>
                CRIAR MINHA CONTA
            </span>

            <i class="bi bi-chevron-right"></i>
        `;
    }
}