var socket =  io.connect('http://localhost:3000');

socket.on('connect', () => {
  console.log('> Connected to server');
});

const screen = document.getElementById('screen');
const context = screen.getContext('2d');

let game;
let currentPlayer;



socket.on('bootstrap', gameInitialState => {
  game = JSON.parse(gameInitialState);
  currentPlayer = game.players[game.players.findIndex(player => player.id === socket.id)]

  requestAnimationFrame(renderGame);

  function renderGame() {
    context.globalAlpha = 1;
    context.clearRect(0, 0, screen.width, screen.height);

    for (const i in game.players) {
      const player = game.players[i];
      for (let j = 0; j < player.body.length; j++) {
        context.fillStyle = '#ffffff';
        context.globalAlpha = 0.20;
        context.fillRect(player.body[j].x, player.body[j].y, 1, 1);
        if (player.id === socket.id) {
          context.fillStyle = '#80e040';
          context.globalAlpha = 0.40;
          context.fillRect(player.body[j].x, player.body[j].y, 1, 1);
        }
      }
      context.fillStyle = '#ffffff';
      context.globalAlpha = 0.40;
      context.fillRect(player.body[0].x, player.body[0].y, 1, 1);
      if (player.id === socket.id) {
          context.fillStyle = '#80e040';
          context.globalAlpha = 1;
          context.fillRect(player.body[0].x, player.body[0].y, 1, 1);
      }
    }
    for (const i in game.fruits) {
      const fruit = game.fruits[i];
      context.fillStyle = '#d01050';
      context.globalAlpha = 1;
      context.fillRect(fruit.x, fruit.y, 1, 1);
    }
    requestAnimationFrame(renderGame);
  }

  document.addEventListener('keydown', (event) => {
    const keyPressed = event.key;
    currentPlayer.lastMovement = keyPressed;
    const JSONCurrentPlayer = JSON.stringify(currentPlayer); 
    socket.emit('playerMove', JSONCurrentPlayer);
  });

  socket.on('newGameState', gameState => {
    game = JSON.parse(gameState);
    currentPlayer = game.players[game.players.findIndex(player => player.id === socket.id)];
  });
});

