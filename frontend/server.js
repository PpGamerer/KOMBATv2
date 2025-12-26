const WebSocket = require('ws');
const server = new WebSocket.Server({ port: 8080 });

server.on('connection', socket => {
  console.log('Client connected');
  socket.send(JSON.stringify({ message: 'Welcome to the WebSocket Game Server!' }));

  socket.on('message', data => {
    console.log('Received:', data);
    server.clients.forEach(client => {
      if (client.readyState === WebSocket.OPEN) {
        client.send(data);
      }
    });
  });

  socket.on('close', () => console.log('Client disconnected'));
});
