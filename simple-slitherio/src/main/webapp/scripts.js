var socket =  io.connect('http://localhost:3000');

socket.on('connect', () => {
  console.log('> Connected to server');
});

const screen = document.getElementById('screen');
const context = screen.getContext('2d');

let game;
let currentPlayer;

socket.on('bootstrap', gameInitialState => {
  game = gameInitialState;
  console.log(gameInitialState);
});

