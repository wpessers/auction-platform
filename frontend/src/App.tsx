import { RouterProvider } from 'react-router-dom';
import { AuthProvider } from '@/context/AuthContext';
import { WebSocketProvider } from '@/context/WebSocketContext';
import { ToastProvider } from '@/context/ToastContext';
import { ToastContainer } from '@/components/ui/ToastContainer';
import { ConnectionStatus } from '@/components/ui/ConnectionStatus';
import { OutbidNotificationListener } from '@/components/notifications/OutbidNotificationListener';
import { router } from './router';

function App() {
  return (
    <AuthProvider>
      <WebSocketProvider>
        <ToastProvider>
          <RouterProvider router={router} />
          <ToastContainer />
          <ConnectionStatus />
          <OutbidNotificationListener />
        </ToastProvider>
      </WebSocketProvider>
    </AuthProvider>
  );
}

export default App;
