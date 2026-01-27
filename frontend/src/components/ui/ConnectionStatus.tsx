import { useState, useEffect } from 'react';
import { useWebSocket } from '@/context/WebSocketContext';

export function ConnectionStatus() {
  const { connectionState } = useWebSocket();
  const [showIndicator, setShowIndicator] = useState(false);

  // Only show indicator if disconnected for more than 5 seconds
  useEffect(() => {
    let timeout: ReturnType<typeof setTimeout>;

    if (connectionState === 'disconnected') {
      timeout = setTimeout(() => {
        setShowIndicator(true);
      }, 5000);
    } else {
      setShowIndicator(false);
    }

    return () => {
      if (timeout) clearTimeout(timeout);
    };
  }, [connectionState]);

  if (!showIndicator && connectionState !== 'connecting') {
    return null;
  }

  return (
    <div className="fixed bottom-4 left-4 z-50">
      <div className="flex items-center gap-2 rounded bg-card px-3 py-2 text-sm shadow-lg">
        {connectionState === 'connecting' ? (
          <>
            <span className="h-2 w-2 animate-pulse rounded-full bg-warning" />
            <span className="text-text-secondary">Connecting...</span>
          </>
        ) : (
          <>
            <span className="h-2 w-2 rounded-full bg-error" />
            <span className="text-text-secondary">Disconnected</span>
          </>
        )}
      </div>
    </div>
  );
}
