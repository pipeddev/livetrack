import { io } from 'socket.io-client';

const shipmentId = process.argv[2];
console.log(`suscribiendo a ${shipmentId}...`);

const socket = io('http://localhost:3000/tracking');

socket.on('connect', () => {
  console.log(`conectado ${socket.id}`);
  socket.emit('subscribe', { shipmentId }, console.log);
});
socket.on('connect_error', (err) =>
  console.error(`error de conexión: ${err.message}`),
);
socket.on('subscribed', (id) => console.log(`suscrito a ${id}`));
socket.on('location', (p) =>
  console.log(`📍 seq=${p.sequence} ${p.lat.toFixed(4)}, ${p.lng.toFixed(4)}`),
);
socket.on('status', (s) => console.log(`🔄 ${s.status}`));
