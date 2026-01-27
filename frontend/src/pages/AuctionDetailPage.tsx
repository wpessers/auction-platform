import { useParams } from 'react-router-dom';

export function AuctionDetailPage() {
  const { id } = useParams<{ id: string }>();

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-text-primary">Auction Details</h1>
      <p className="text-text-secondary">Auction ID: {id}</p>
    </div>
  );
}
