const grid = document.getElementById("grid");
const scoreDisplay = document.getElementById("score");
const gameOverDisplay = document.getElementById("game-over");
const userNameDisplay = document.getElementById("user-name");
const formLogin = document.getElementById("form-login");
const profile = document.getElementById("profile");

let matrix = [];
let startCell = null;
let startX = 0;
let startY = 0;

function renderGrid() {
  grid.innerHTML = "";
  matrix.forEach((row, rowIndex) => {
    row.forEach((cell, colIndex) => {
      const div = document.createElement("div");
      div.className = "cell";
      if (cell > 0) {
        div.classList.add(`value-${cell}`);
        div.textContent = cell;
      } else {
        div.textContent = "";
      }

      div.dataset.row = rowIndex;
      div.dataset.col = colIndex;

      div.addEventListener("mousedown", (e) => {
        startCell = { row: rowIndex, col: colIndex };
        startX = e.clientX;
        startY = e.clientY;
      });

      grid.appendChild(div);
    });
  });
}

function updateUI(data) {
  matrix = data.board;
  scoreDisplay.textContent = data.score;
  gameOverDisplay.style.display = data.gameOver ? "block" : "none";
  renderGrid();
}

function sendMove(direction) {
  fetch("/app-web/api/move", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ direction })
  })
    .then(resp => {
      if (!resp.ok) throw new Error("Error making move");
      return resp.json();
    })
    .then(updateUI)
    .catch(err => console.error("Error sending move:", err));
}

document.addEventListener("DOMContentLoaded", () => {
  fetch("/app-web/api/state")
    .then(resp => {
      if (!resp.ok) throw new Error("Error fetching state");
      return resp.json();
    })
    .then(updateUI)
    .catch(err => console.error("Error loading initial state:", err));

  document.addEventListener("mouseup", (e) => {
    if (!startCell) return;

    const deltaX = e.clientX - startX;
    const deltaY = e.clientY - startY;

    let direction = null;
    if (Math.abs(deltaX) > Math.abs(deltaY)) {
      direction = deltaX > 0 ? "right" : "left";
    } else {
      direction = deltaY > 0 ? "down" : "up";
    }

    sendMove(direction);
    startCell = null;
  });

  document.addEventListener("keydown", (e) => {
    const keyMap = {
      ArrowUp: "up",
      ArrowDown: "down",
      ArrowLeft: "left",
      ArrowRight: "right"
    };
    if (keyMap[e.key]) {
      sendMove(keyMap[e.key]);
      e.preventDefault();
    }
  });

  document.getElementById("restart-btn").addEventListener("click", () => {
    fetch("/app-web/api/restart", {
      method: "POST"
    })
      .then(resp => {
        if (!resp.ok) throw new Error("Error restarting game");
        return resp.json();
      })
      .then(updateUI)
      .catch(err => console.error("Error restarting game:", err));
  });

  document.getElementById("submit-btn").addEventListener("click", () => {
    const name = document.getElementById("input-name").value;
    const email = document.getElementById("input-mail").value;
    const password1 = document.getElementById("input-password1").value;
    const password2 = document.getElementById("input-password2").value;

    const isSignup = password2 && password2.style.display !== "none" && password2.value !== "";

    if (isSignup) {
      if (password1 !== password2.value) {
        alert("Passwords do not match.");
        return;
      }

      fetch("/app-web/api/user/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, email, password: password1 })
      })
        .then(resp => resp.json())
        .then(data => {
          if (data.success) {
            showProfile(data.name || email);
          } else {
            alert("Signup failed: " + (data.message || "Unknown error"));
          }
        })
        .catch(err => {
          console.error("Signup error:", err);
          alert("Error during signup.");
        });

    } else {
      fetch("/app-web/api/user/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password: password1 })
      })
        .then(resp => resp.json())
        .then(data => {
          if (data.success) {
            fetch("/app-web/api/user/me")
              .then(resp => resp.json())
              .then(userData => {
                showProfile(userData.name);
              });
          } else {
            alert("Login failed: " + (data.message || "Unknown error"));
          }
        })
        .catch(err => {
          console.error("Login error:", err);
          alert("Error during login.");
        });
    }
});


  const loginBtn = document.getElementById("login-btn");
  const signupBtn = document.getElementById("signup-btn");
  const cancelBtn = document.getElementById("cancel-btn");
  const submitBtn = document.getElementById("submit-btn");

  const nameInput = document.getElementById("input-name");
  const namelabel = document.getElementById("label-name");
  const emailInput = document.getElementById("input-mail");
  const pass1Input = document.getElementById("input-password1");
  const pass2Input = document.getElementById("input-password2");
  const labelPass2 = document.getElementById("label-pass2");

  const loginForm = document.querySelector(".form-login");

  function showLoginForm() {
    loginForm.style.display = "flex";
    pass2Input.style.display = "none";
    labelPass2.style.display = "none";
    nameInput.style.display = "none";
    nameLabel.style.display = "none";
    loginBtn.style.display = "none";
    signupBtn.style.display = "none";
    cancelBtn.style.display = "inline-block";
  }

  function showSignupForm() {
    loginForm.style.display = "flex";
    nameInput.style.display = "inline-block";
    nameLabel.style.display = "inline-block";
    pass2Input.style.display = "inline-block";
    labelPass2.style.display = "inline-block";
    loginBtn.style.display = "none";
    signupBtn.style.display = "none";
    cancelBtn.style.display = "inline-block";
  }

  function hideForm() {
    loginForm.style.display = "none";
    emailInput.value = "";
    pass1Input.value = "";
    pass2Input.value = "";
    loginBtn.style.display = "inline-block";
    signupBtn.style.display = "inline-block";
    cancelBtn.style.display = "none";
    nameInput.style.display = "none";
    nameLabel.style.display = "none";
  }

  function showProfile(userName) {
    document.querySelector("#profile h3").textContent = userName;
    profile.style.display = "flex";
    formLogin.style.display = "none";
    loginBtn.style.display = "none";
    signupBtn.style.display = "none";
  }

  function hideProfile() {
    profile.style.display = "none";
    formLogin.style.display = "block";
    loginBtn.style.display = "inline-block";
    signupBtn.style.display = "inline-block";
  }

  loginBtn.addEventListener("click", showLoginForm);
  signupBtn.addEventListener("click", showSignupForm);
  cancelBtn.addEventListener("click", hideForm);
  document.getElementById("logout-btn").addEventListener("click", hideProfile);

  hideForm();
  hideProfile();
});
