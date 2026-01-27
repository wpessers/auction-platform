import {
  createContext,
  useContext,
  useEffect,
  useState,
  useCallback,
  type ReactNode,
} from 'react';
import { useAuth } from './AuthContext';
import {
  webSocketService,
  type ConnectionState,
} from '@/services/websocket';

interface WebSocketContextType {
  connectionState: ConnectionState;
  subscribe: (destination: string, callback: (message: unknown) => void) => () => void;
  send: (destination: string, body: object) => void;
}

const WebSocketContext = createContext<WebSocketContextType | undefined>(undefined);

interface WebSocketProviderProps {
  children: ReactNode;
}

export function WebSocketProvider({ children }: WebSocketProviderProps) {
  const { token, isAuthenticated } = useAuth();
  const [connectionState, setConnectionState] = useState<ConnectionState>('disconnected');

  // Connect when authenticated, disconnect when not
  useEffect(() => {
    if (isAuthenticated && token) {
      webSocketService.connect(token);
    } else {
      webSocketService.disconnect();
    }

    return () => {
      webSocketService.disconnect();
    };
  }, [isAuthenticated, token]);

  // Subscribe to connection state changes
  useEffect(() => {
    const unsubscribe = webSocketService.onConnectionStateChange(setConnectionState);
    return unsubscribe;
  }, []);

  const subscribe = useCallback(
    (destination: string, callback: (message: unknown) => void) => {
      return webSocketService.subscribe(destination, callback);
    },
    []
  );

  const send = useCallback((destination: string, body: object) => {
    webSocketService.send(destination, body);
  }, []);

  const value: WebSocketContextType = {
    connectionState,
    subscribe,
    send,
  };

  return (
    <WebSocketContext.Provider value={value}>
      {children}
    </WebSocketContext.Provider>
  );
}

export function useWebSocket(): WebSocketContextType {
  const context = useContext(WebSocketContext);
  if (context === undefined) {
    throw new Error('useWebSocket must be used within a WebSocketProvider');
  }
  return context;
}
