import { Client, type IFrame, type IMessage, type StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { env } from '@/config/env';

export type ConnectionState = 'connecting' | 'connected' | 'disconnected';

type ConnectionStateListener = (state: ConnectionState) => void;
type MessageCallback = (message: unknown) => void;

interface Subscription {
  destination: string;
  callback: MessageCallback;
  stompSubscription?: StompSubscription;
}

class WebSocketService {
  private client: Client | null = null;
  private token: string | null = null;
  private connectionState: ConnectionState = 'disconnected';
  private stateListeners: Set<ConnectionStateListener> = new Set();
  private subscriptions: Map<string, Subscription> = new Map();
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 10;
  private reconnectTimeout: ReturnType<typeof setTimeout> | null = null;

  connect(token: string): void {
    if (this.client?.active) {
      return;
    }

    this.token = token;
    this.reconnectAttempts = 0;
    this.doConnect();
  }

  private doConnect(): void {
    if (!this.token) return;

    this.setConnectionState('connecting');

    this.client = new Client({
      webSocketFactory: () => {
        return new SockJS(`${env.apiBaseUrl}/ws`);
      },
      connectHeaders: {
        Authorization: `Bearer ${this.token}`,
      },
      debug: (str) => {
        if (import.meta.env.DEV) {
          console.log('[STOMP]', str);
        }
      },
      reconnectDelay: 0, // We handle reconnection manually
      onConnect: this.handleConnect.bind(this),
      onDisconnect: this.handleDisconnect.bind(this),
      onStompError: this.handleError.bind(this),
      onWebSocketClose: this.handleWebSocketClose.bind(this),
    });

    this.client.activate();
  }

  private handleConnect(_frame: IFrame): void {
    this.setConnectionState('connected');
    this.reconnectAttempts = 0;

    // Resubscribe to all pending subscriptions
    this.subscriptions.forEach((sub, id) => {
      this.doSubscribe(id, sub);
    });
  }

  private handleDisconnect(_frame: IFrame): void {
    this.setConnectionState('disconnected');
  }

  private handleError(frame: IFrame): void {
    console.error('[WebSocket] STOMP error:', frame.headers.message);
    this.setConnectionState('disconnected');
  }

  private handleWebSocketClose(): void {
    if (this.connectionState !== 'disconnected') {
      this.setConnectionState('disconnected');
      this.attemptReconnect();
    }
  }

  private attemptReconnect(): void {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('[WebSocket] Max reconnection attempts reached');
      return;
    }

    if (this.reconnectTimeout) {
      clearTimeout(this.reconnectTimeout);
    }

    // Exponential backoff: 1s, 2s, 4s, 8s, 16s, max 30s
    const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts), 30000);
    this.reconnectAttempts++;

    console.log(`[WebSocket] Reconnecting in ${delay}ms (attempt ${this.reconnectAttempts})`);

    this.reconnectTimeout = setTimeout(() => {
      if (this.token) {
        this.doConnect();
      }
    }, delay);
  }

  disconnect(): void {
    if (this.reconnectTimeout) {
      clearTimeout(this.reconnectTimeout);
      this.reconnectTimeout = null;
    }

    if (this.client?.active) {
      this.client.deactivate();
    }

    this.client = null;
    this.token = null;
    this.subscriptions.clear();
    this.setConnectionState('disconnected');
  }

  subscribe(destination: string, callback: MessageCallback): () => void {
    const id = `${destination}-${Date.now()}`;
    const subscription: Subscription = { destination, callback };

    this.subscriptions.set(id, subscription);

    if (this.client?.connected) {
      this.doSubscribe(id, subscription);
    }

    // Return unsubscribe function
    return () => {
      const sub = this.subscriptions.get(id);
      if (sub?.stompSubscription) {
        sub.stompSubscription.unsubscribe();
      }
      this.subscriptions.delete(id);
    };
  }

  private doSubscribe(id: string, subscription: Subscription): void {
    if (!this.client?.connected) return;

    const stompSubscription = this.client.subscribe(
      subscription.destination,
      (message: IMessage) => {
        try {
          const body = JSON.parse(message.body);
          subscription.callback(body);
        } catch (e) {
          console.error('[WebSocket] Failed to parse message:', e);
        }
      }
    );

    subscription.stompSubscription = stompSubscription;
    this.subscriptions.set(id, subscription);
  }

  send(destination: string, body: object): void {
    if (!this.client?.connected) {
      console.warn('[WebSocket] Cannot send message: not connected');
      return;
    }

    this.client.publish({
      destination,
      body: JSON.stringify(body),
    });
  }

  getConnectionState(): ConnectionState {
    return this.connectionState;
  }

  onConnectionStateChange(listener: ConnectionStateListener): () => void {
    this.stateListeners.add(listener);
    // Immediately call with current state
    listener(this.connectionState);
    return () => {
      this.stateListeners.delete(listener);
    };
  }

  private setConnectionState(state: ConnectionState): void {
    if (this.connectionState !== state) {
      this.connectionState = state;
      this.stateListeners.forEach((listener) => listener(state));
    }
  }
}

// Singleton instance
export const webSocketService = new WebSocketService();
