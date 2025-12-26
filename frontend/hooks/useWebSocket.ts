import { useEffect, useState } from 'react';

const useWebSocket = (url: string) => {
  const [socket, setSocket] = useState<WebSocket | null>(null);
  const [messages, setMessages] = useState<string[]>([]);

  useEffect(() => {
    const ws = new WebSocket(url);
    ws.onopen = () => console.log('Connected to WebSocket');
    ws.onmessage = (event) => {
      setMessages((prev) => [...prev, event.data]);
    };
    ws.onclose = () => console.log('Disconnected from WebSocket');
    setSocket(ws);

    return () => {
      ws.close();
    };
  }, [url]);

  return { socket, messages };
};

export default useWebSocket;
