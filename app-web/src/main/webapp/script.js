const grid = document.getElementById("grid");

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
    .then(data => {
      matrix = data.board;
      renderGrid();
    })
    .catch(err => console.error("Error sending move:", err));
}

document.addEventListener("DOMContentLoaded", () => {
  fetch("/app-web/api/state")
    .then(resp => {
      if (!resp.ok) throw new Error("Error fetching state");
      return resp.json();
    })
    .then(data => {
      matrix = data.board;
      renderGrid();
    })
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
});
