import { useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useWebSocket } from '@/context/WebSocketContext';
import { useToast } from '@/context/ToastContext';
import { useAuth } from '@/context/AuthContext';

interface OutbidMessage {
  uuid: string;  // auction UUID
  auctionName: string;  // auction name for display
  amount: number;
}

export function OutbidNotificationListener() {
  const { subscribe } = useWebSocket();
  const { addToast } = useToast();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const handleOutbid = useCallback(
    (message: unknown) => {
      const outbid = message as OutbidMessage;

      addToast({
        type: 'warning',
        message: `You've been outbid on "${outbid.auctionName}"! New highest bid: $${outbid.amount.toFixed(2)}`,
        onClick: () => navigate(`/auctions/${outbid.uuid}`),
        action: {
          label: 'View Auction',
          onClick: () => navigate(`/auctions/${outbid.uuid}`),
        },
      });
    },
    [addToast, navigate]
  );

  useEffect(() => {
    // Only subscribe when authenticated
    if (!isAuthenticated) return;

    const unsubscribe = subscribe('/user/queue/notifications', handleOutbid);
    return unsubscribe;
  }, [isAuthenticated, subscribe, handleOutbid]);

  // This component doesn't render anything
  return null;
}
