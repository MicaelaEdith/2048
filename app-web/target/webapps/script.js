function showCustomAlert(message, callback = null) {
  const modal = document.getElementById("custom-alert");
  const messageEl = document.getElementById("alert-message");
  const okBtn = document.getElementById("alert-ok-btn");

  messageEl.textContent = message;
  modal.classList.remove("hidden");

  okBtn.onclick = () => {
    modal.classList.add("hidden");
    if (callback) callback();
  };
}

function showDoubleConfirmDialog(message1, message2, onFinalConfirm) {
  const modal = document.getElementById("confirm-delete-alert");
  const messageEl = document.getElementById("confirm-delete-message");
  const yesBtn = document.getElementById("confirm-delete-yes");
  const noBtn = document.getElementById("confirm-delete-no");

  function firstStep() {
    messageEl.textContent = message1;
    modal.classList.remove("hidden");

    yesBtn.onclick = () => {
      messageEl.textContent = message2;

      yesBtn.onclick = () => {
        modal.classList.add("hidden");
        onFinalConfirm();
      };

      noBtn.onclick = () => {
        modal.classList.add("hidden");
      };
    };

    noBtn.onclick = () => {
      modal.classList.add("hidden");
    };
  }

  firstStep();
}


document.addEventListener("DOMContentLoaded", () => {
  const grid = document.getElementById("grid");
  const scoreDisplay = document.getElementById("score");
  const gameOverDisplay = document.getElementById("game-over");
  const userNameDisplay = document.getElementById("user-name");
  const editBtn = document.getElementById("edit-btn");
  const formLogin = document.getElementById("form-login");
  const profile = document.getElementById("profile");

  const loginBtn = document.getElementById("login-btn");
  const signupBtn = document.getElementById("signup-btn");
  const cancelBtn = document.getElementById("cancel-btn");
  const submitBtn = document.getElementById("submit-btn");

  const nameInput = document.getElementById("input-name");
  const nameLabel = document.getElementById("label-name");
  const emailInput = document.getElementById("input-mail");
  const pass1Input = document.getElementById("input-password1");
  const pass2Input = document.getElementById("input-password2");
  const labelPass2 = document.getElementById("label-pass2");

  const loginForm = document.querySelector(".form-login");

  const contactSearchInput = document.getElementById("contact-search");
  const contactsDropdown = document.getElementById("contacts-dropdown");
  const sendDuelBtn = document.getElementById("btn-send-duel");

  const restartBtn = document.getElementById("restart-btn");
  
  const formEditProfile = document.getElementById("form-edit-profile");
  const editNameInput = document.getElementById("edit-name");
  const editCurrentPassInput = document.getElementById("edit-current-password");
  const editNewPassInput = document.getElementById("edit-new-password");
  const submitEditBtn = document.getElementById("submit-edit-btn");
  const cancelEditBtn = document.getElementById("cancel-edit-btn");

  let contacts = [];
  let selectedContact = null;

  let matrix = Array.from({ length: 4 }, () => Array(4).fill(0));
  let startCell = null;
  let startX = 0;
  let startY = 0;
  let gameStarted = false;
  
  let duelActive = false;
  let duelOpponentName = null;
  let duelOpponentScore = null;

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
    renderGrid();
    gameOverDisplay.style.display = data.gameOver ? "block" : "none";

    let has2048 = false;
    matrix.forEach(row => {
      row.forEach(cell => {
        if (cell === 2048) {
          has2048 = true;
        }
      });
    });

    const finalScore = has2048 ? data.score + 2048 : data.score;
    scoreDisplay.textContent = finalScore;

    if (data.gameOver) {
      showCustomAlert("Game Over");

      fetch("/app-web/api/duel/submit", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ score: finalScore }),
        credentials: "include"
      })
        .then(resp => resp.json())
        .then(resp => {
          if (resp.success) {
            console.log("Puntaje de duelo registrado.");
          } else {
            console.error("Error al registrar puntaje de duelo:", resp.message);
          }
        })
        .catch(err => console.error("Error al enviar puntuación del duelo:", err));

      fetch("/app-web/api/user/update-points", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ score: finalScore }),
        credentials: "include"
      })
        .then(resp => resp.json())
        .then(resp => {
          if (resp.success) {
            console.log("Puntaje total actualizado si correspondía.");
          } else {
            console.error("Error al actualizar puntaje total:", resp.message);
          }
        })
        .catch(err => console.error("Error al enviar total_points:", err));
		
		if (duelActive) {
		  if (finalScore > duelOpponentScore) {
		    showCustomAlert(`¡Ganaste el duelo contra ${duelOpponentName} con ${finalScore} puntos!`);
		  } else if (finalScore < duelOpponentScore) {
		    showCustomAlert(`Perdiste el duelo contra ${duelOpponentName}. Tu puntaje: ${finalScore}, oponente: ${duelOpponentScore}.`);
		  } else {
		    showCustomAlert(`Empate en el duelo con ${duelOpponentName}. Ambos hicieron ${finalScore} puntos.`);
		  }
		  duelActive = false;
		}
		restartBtn.click();
    }
	
	
  }

  function sendMove(direction) {
    fetch("/app-web/api/move", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ direction }),
      credentials: "include"
    })
      .then(resp => {
        if (!resp.ok) throw new Error("Error making move");
        return resp.json();
      })
      .then(updateUI)
      .catch(err => console.error("Error sending move:", err));
  }

  document.addEventListener("mouseup", (e) => {
    if (!gameStarted || !startCell) return;

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
    if (!gameStarted) return;

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

  function updateRanking() {
    fetch('/app-web/api/ranking')
      .then(res => res.json())
      .then(data => {
        if (!data.success || !data.ranking) return;

        const rankingList = document.querySelector('.ranking');
        rankingList.innerHTML = '';

        data.ranking.forEach(player => {
          const tag = document.createElement('div');
          tag.className = 'ranking-tag';

          const username = document.createElement('h4');
          username.className = 'username-rank';
          username.textContent = player.username;

          const points = document.createElement('span');
          points.className = 'points';
          points.textContent = player.score;

          tag.appendChild(username);
          tag.appendChild(points);
          rankingList.appendChild(tag);
        });
      })
      .catch(err => {
        console.error('Error cargando ranking:', err);
      });
  }

  restartBtn.addEventListener("click", () => {
    const url = gameStarted ? "/app-web/api/restart" : "/app-web/api/state";
    const method = gameStarted ? "POST" : "GET";

    fetch(url, { method, credentials: "include" })
      .then(resp => {
        if (!resp.ok) throw new Error("Error reiniciando o cargando juego");
        return resp.json();
      })
      .then(data => {
        updateUI(data);
        gameStarted = true;
        restartBtn.textContent = "RESTART";
        updateRanking();
      })
      .catch(err => console.error("Error:", err));
  });

  submitBtn.addEventListener("click", () => {
    const name = nameInput.value;
    const email = emailInput.value;
    const password1 = pass1Input.value;
    const password2 = pass2Input.value;

    const isSignup = pass2Input.style.display !== "none" && pass2Input.value !== "";

    if (isSignup) {
      if (password1 !== password2) {
        showCustomAlert("Passwords do not match.");
        return;
      }

      fetch("/app-web/api/user/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, email, password: password1 }),
        credentials: "include"
      })
        .then(resp => resp.json())
        .then(data => {
          if (data.success) {
            showProfile(data.name || email);
          } else {
            showCustomAlert("Signup failed: " + (data.message || "Unknown error"));
          }
        })
        .catch(err => {
          console.error("Signup error:", err);
          showCustomAlert("Error during signup.");
        });

    } else {
      fetch("/app-web/api/user/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password: password1 }),
        credentials: "include"
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
            showCustomAlert("Login failed: " + (data.message || "Unknown error"));
          }
        })
        .catch(err => {
          console.error("Login error:", err);
          showCustomAlert("Error during login.");
        });
    }
  });

  function loadContacts() {
    fetch("/app-web/api/user/contacts", {
      credentials: "include"
    })
      .then(resp => {
        if (!resp.ok) throw new Error("No autorizado o error en servidor");
        return resp.json();
      })
      .then(data => {
        if (data.success && Array.isArray(data.contacts)) {
          contacts = data.contacts;
          renderDropdown(contacts);
        }
      })
      .catch(err => console.error("Error cargando contactos:", err));
  }

  function renderDropdown(list) {
    contactsDropdown.innerHTML = "";
    if (list.length === 0) {
      contactsDropdown.style.display = "none";
      return;
    }

    list.forEach(contact => {
      const div = document.createElement("div");
      div.textContent = contact.name;
      div.dataset.id = contact.id;
      div.addEventListener("click", () => {
        selectedContact = contact;
        contactSearchInput.value = contact.name;
        sendDuelBtn.disabled = false;
      });
      contactsDropdown.appendChild(div);
    });

    contactsDropdown.style.display = "block";
  }

  contactSearchInput.addEventListener("input", () => {
    selectedContact = null;
    sendDuelBtn.disabled = true;
    const term = contactSearchInput.value.toLowerCase();
    const filtered = contacts.filter(c => c.name.toLowerCase().includes(term));
    renderDropdown(filtered);
  });

  sendDuelBtn.addEventListener("click", () => {
    if (!selectedContact) return;

    fetch("/app-web/api/user/send-duel", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ opponentId: selectedContact.id }),
      credentials: "include"
    })
      .then(resp => resp.json())
      .then(data => {
        if (data.success) {
		  showCustomAlert(`Duelo iniciado con ${selectedContact.name}. La próxima partida corresponde a este duelo.`);
          contactSearchInput.value = "";
          selectedContact = null;
          sendDuelBtn.disabled = true;
          renderDropdown(contacts);
          restartBtn.click();
        } else {
          showCustomAlert("Error enviando solicitud: " + (data.message || ""));
        }
      })
      .catch(err => {
        console.error("Error enviando solicitud de duelo:", err);
        showCustomAlert("Error enviando solicitud.");
      });
  });

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
    editBtn.style.display = "inline-block";

    loadContacts();

    fetch("/app-web/api/user/duel/pending", {
      credentials: "include"
    })
      .then(resp => resp.json())
      .then(data => {
        if (data.success && data.duelPending && data.opponentName) {
          showCustomAlert(`Duelo pendiente con ${data.opponentName}. La próxima partida corresponde a ese duelo.`);
          duelActive = true;
          duelOpponentName = data.opponentName;
          duelOpponentScore = data.opponentScore || 0;
          restartBtn.click();
        } else {
          fetch("/app-web/api/user/duel/last", {
            credentials: "include"
          })
            .then(resp => resp.json())
            .then(data => {
              if (data.success && data.duelFound) {
                showCustomAlert(data.message);
              }
            })
            .catch(err => console.error("Error verificando último duelo:", err));
        }
      })
      .catch(err => console.error("Error verificando duelo pendiente:", err));
  }

  function hideProfile() {
    profile.style.display = "none";
    formLogin.style.display = "block";
    loginBtn.style.display = "inline-block";
    signupBtn.style.display = "inline-block";
    editBtn.style.display = "none";
  }
  
  function showEditProfile(userName) {
    formEditProfile.style.display = "block";
    profile.style.display = "none";
    
    editNameInput.value = userName || "";
    editCurrentPassInput.value = "";
    editNewPassInput.value = "";
  }

  function hideEditProfile() {
    formEditProfile.style.display = "none";
    profile.style.display = "flex";
  }
  
  editBtn.addEventListener("click", () => {
    showEditProfile(userNameDisplay.textContent);
  });

  cancelEditBtn.addEventListener("click", () => {
    hideEditProfile();
  });

  submitEditBtn.addEventListener("click", () => {
    const newName = editNameInput.value.trim();
    const currentPass = editCurrentPassInput.value;
    const newPass = editNewPassInput.value;

    if (!newName) {
      showCustomAlert("El nombre no puede estar vacío.");
      return;
    }

    if (newPass && !currentPass) {
      showCustomAlert("Para cambiar la contraseña, ingrese la contraseña actual.");
      return;
    }

    const payload = { name: newName };
    if (newPass) payload.password = newPass;
    if (newPass) payload.currentPassword = currentPass;

    fetch("/app-web/api/user/update-profile", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify(payload)
    })
      .then(resp => resp.json())
      .then(data => {
        if (data.success) {
          showCustomAlert("Perfil actualizado correctamente.", () => {
            userNameDisplay.textContent = newName;
            hideEditProfile();
          });
        } else {
          showCustomAlert("Error al actualizar: " + (data.message || "Error desconocido."));
        }
      })
      .catch(err => {
        console.error("Error al actualizar perfil:", err);
        showCustomAlert("Error en la comunicación con el servidor.");
      });
  });



  loginBtn.addEventListener("click", showLoginForm);
  signupBtn.addEventListener("click", showSignupForm);
  cancelBtn.addEventListener("click", hideForm);

  
  document.getElementById("logout-btn").addEventListener("click", () => {
    fetch("/app-web/api/user/logout", {
      method: "GET",
      credentials: "include"
    })
      .then(resp => resp.json())
      .then(data => {
        if (data.success) {
          hideProfile();
          hideForm();
        } else {
          showCustomAlert("Logout failed.");
        }
      })
      .catch(err => {
        console.error("Logout error:", err);
        showCustomAlert("Error during logout.");
      });
  });

  document.getElementById("delete-account-btn").addEventListener("click", () => {
    showDoubleConfirmDialog(
      "¿Estás seguro que deseas eliminar tu cuenta? Esta acción es permanente.",
      "Una vez eliminada, no podrás recuperar tus datos. ¿Eliminar cuenta definitivamente?",
      () => {
        fetch("/app-web/api/user/delete-account", {
          method: "POST",
          credentials: "include",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify({})
        })
          .then(resp => resp.json())
          .then(data => {
            if (data.success) {
              showCustomAlert("Cuenta eliminada. Se cerrará la sesión.", () => {
                location.reload();
              });
            } else {
              showCustomAlert("Error al eliminar la cuenta: " + (data.message || "Error desconocido."));
            }
          })
          .catch(err => {
            console.error("Error al eliminar cuenta:", err);
            showCustomAlert("Error en la comunicación con el servidor.");
          });
      }
    );
  });

  
  
  hideForm();
  hideProfile();
  renderGrid();
  updateRanking();
});
