interface SkeletonProps {
  className?: string;
}

export function Skeleton({ className = '' }: SkeletonProps) {
  return (
    <div
      className={`animate-pulse rounded bg-card ${className}`}
      aria-hidden="true"
    />
  );
}

export function AuctionCardSkeleton() {
  return (
    <div className="rounded-lg border border-border bg-card p-4">
      <div className="flex items-start justify-between gap-2">
        <Skeleton className="h-6 w-3/4" />
        <Skeleton className="h-5 w-16" />
      </div>
      <Skeleton className="mt-3 h-4 w-full" />
      <Skeleton className="mt-2 h-4 w-2/3" />
      <div className="mt-4 flex items-end justify-between">
        <div>
          <Skeleton className="h-3 w-16" />
          <Skeleton className="mt-1 h-7 w-24" />
        </div>
        <div className="text-right">
          <Skeleton className="h-3 w-20" />
          <Skeleton className="mt-1 h-4 w-16" />
        </div>
      </div>
    </div>
  );
}

export function AuctionDetailSkeleton() {
  return (
    <div className="mx-auto max-w-4xl">
      <Skeleton className="mb-6 h-5 w-32" />
      <div className="rounded-lg border border-border bg-surface p-6">
        <div className="flex items-start justify-between">
          <Skeleton className="h-8 w-2/3" />
          <Skeleton className="h-6 w-20" />
        </div>
        <Skeleton className="mt-4 h-4 w-full" />
        <Skeleton className="mt-2 h-4 w-full" />
        <Skeleton className="mt-2 h-4 w-3/4" />
        <div className="mt-6 grid gap-4 sm:grid-cols-2">
          <div>
            <Skeleton className="h-3 w-20" />
            <Skeleton className="mt-1 h-5 w-32" />
          </div>
          <div>
            <Skeleton className="h-3 w-20" />
            <Skeleton className="mt-1 h-5 w-32" />
          </div>
        </div>
        <div className="mt-6 border-t border-border pt-6">
          <Skeleton className="h-6 w-32" />
          <Skeleton className="mt-4 h-12 w-full" />
        </div>
      </div>
    </div>
  );
}
