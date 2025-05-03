const grid = document.getElementById("grid");

let matrix = [
  [0, 2, 0, 2],
  [4, 0, 4, 0],
  [0, 2, 0, 2],
  [2, 0, 2, 0],
];

let startCell = null;
let startX = 0;
let startY = 0;

function renderGrid() {
  grid.innerHTML = "";
  matrix.forEach((row, rowIndex) => {
    row.forEach((cell, colIndex) => {
      const div = document.createElement("div");
      div.className = "cell";
      div.textContent = cell === 0 ? "" : cell;

      div.dataset.row = rowIndex;
      div.dataset.col = colIndex;

      div.addEventListener("mousedown", (e) => {
        startCell = { row: rowIndex, col: colIndex };
        startX = e.clientX;
        startY = e.clientY;
      });

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

        console.log("Start cell:", startCell);
        console.log("Direction:", direction);

        // fetch("/api/move", {
        //   method: "POST",
        //   headers: { "Content-Type": "application/json" },
        //   body: JSON.stringify({
        //     startRow: startCell.row,
        //     startCol: startCell.col,
        //     direction
        //   })
        // }).then(resp => resp.json()).then(updatedMatrix => {
        //   matrix = updatedMatrix;
        //   renderGrid();
        // });

        alert(`Arrastre desde (${startCell.row}, ${startCell.col}) hacia ${direction}`);

        startCell = null;
      });

      grid.appendChild(div);
    });
  });
}

renderGrid();
