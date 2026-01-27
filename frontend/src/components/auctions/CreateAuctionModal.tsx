import { useState, type FormEvent, useEffect } from 'react';
import { auctionsApi } from '@/api/auctions';
import { useToast } from '@/context/ToastContext';
import { ApiError } from '@/api/client';

interface CreateAuctionModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

interface FormData {
  name: string;
  description: string;
  startTime: string;
  endTime: string;
  startingPrice: string;
}

interface FormErrors {
  name?: string;
  description?: string;
  startTime?: string;
  endTime?: string;
  startingPrice?: string;
}

export function CreateAuctionModal({
  isOpen,
  onClose,
  onSuccess,
}: CreateAuctionModalProps) {
  const { addToast } = useToast();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [startNow, setStartNow] = useState(false);
  const [formData, setFormData] = useState<FormData>({
    name: '',
    description: '',
    startTime: '',
    endTime: '',
    startingPrice: '',
  });
  const [errors, setErrors] = useState<FormErrors>({});

  // Reset form when modal opens
  useEffect(() => {
    if (isOpen) {
      setFormData({
        name: '',
        description: '',
        startTime: '',
        endTime: '',
        startingPrice: '',
      });
      setErrors({});
      setStartNow(false);
    }
  }, [isOpen]);

  // Handle escape key
  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };
    document.addEventListener('keydown', handleEscape);
    return () => document.removeEventListener('keydown', handleEscape);
  }, [isOpen, onClose]);

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};

    if (!formData.name.trim()) {
      newErrors.name = 'Name is required';
    }

    if (!formData.description.trim()) {
      newErrors.description = 'Description is required';
    }

    const now = new Date();
    const startTime = startNow ? now : new Date(formData.startTime);
    const endTime = new Date(formData.endTime);

    if (!startNow && !formData.startTime) {
      newErrors.startTime = 'Start time is required';
    } else if (!startNow && startTime < now) {
      newErrors.startTime = 'Start time must be in the future';
    }

    if (!formData.endTime) {
      newErrors.endTime = 'End time is required';
    } else if (endTime <= startTime) {
      newErrors.endTime = 'End time must be after start time';
    }

    const price = parseFloat(formData.startingPrice);
    if (!formData.startingPrice) {
      newErrors.startingPrice = 'Starting price is required';
    } else if (isNaN(price) || price < 0) {
      newErrors.startingPrice = 'Starting price must be a valid number >= 0';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);

    try {
      const startTime = startNow
        ? new Date().toISOString()
        : new Date(formData.startTime).toISOString();
      const endTime = new Date(formData.endTime).toISOString();

      await auctionsApi.create({
        name: formData.name.trim(),
        description: formData.description.trim(),
        startTime,
        endTime,
        startingPrice: parseFloat(formData.startingPrice),
      });

      addToast({
        type: 'success',
        message: 'Auction created successfully!',
      });

      onSuccess();
      onClose();
    } catch (err) {
      if (err instanceof ApiError) {
        addToast({
          type: 'error',
          message: err.message || 'Failed to create auction',
        });
      } else {
        addToast({
          type: 'error',
          message: 'An unexpected error occurred',
        });
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleInputChange = (
    field: keyof FormData,
    value: string
  ) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    // Clear error when user starts typing
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: undefined }));
    }
  };

  if (!isOpen) return null;

  // Get minimum datetime for inputs (current time)
  const now = new Date();
  const minDateTime = now.toISOString().slice(0, 16);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      {/* Backdrop */}
      <div
        className="absolute inset-0 bg-black/60 backdrop-blur-sm"
        onClick={onClose}
      />

      {/* Modal */}
      <div className="relative z-10 w-full max-w-lg rounded-lg bg-surface p-6 shadow-xl">
        <div className="flex items-center justify-between">
          <h2 className="text-xl font-bold text-text-primary">
            Create New Auction
          </h2>
          <button
            onClick={onClose}
            className="text-text-disabled transition-colors hover:text-text-secondary"
            aria-label="Close"
          >
            <svg
              className="h-6 w-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>

        <form onSubmit={handleSubmit} className="mt-6 space-y-4">
          {/* Name */}
          <div>
            <label
              htmlFor="name"
              className="mb-1 block text-sm font-medium text-text-secondary"
            >
              Name
            </label>
            <input
              id="name"
              type="text"
              value={formData.name}
              onChange={(e) => handleInputChange('name', e.target.value)}
              disabled={isSubmitting}
              className="w-full rounded border border-border bg-card px-3 py-2 text-text-primary placeholder-text-disabled outline-none transition-colors focus:border-accent focus:ring-1 focus:ring-accent disabled:opacity-50"
              placeholder="Enter auction name"
            />
            {errors.name && (
              <p className="mt-1 text-sm text-error">{errors.name}</p>
            )}
          </div>

          {/* Description */}
          <div>
            <label
              htmlFor="description"
              className="mb-1 block text-sm font-medium text-text-secondary"
            >
              Description
            </label>
            <textarea
              id="description"
              value={formData.description}
              onChange={(e) => handleInputChange('description', e.target.value)}
              disabled={isSubmitting}
              rows={3}
              className="w-full resize-none rounded border border-border bg-card px-3 py-2 text-text-primary placeholder-text-disabled outline-none transition-colors focus:border-accent focus:ring-1 focus:ring-accent disabled:opacity-50"
              placeholder="Enter auction description"
            />
            {errors.description && (
              <p className="mt-1 text-sm text-error">{errors.description}</p>
            )}
          </div>

          {/* Start Now checkbox */}
          <div className="flex items-center gap-2">
            <input
              id="startNow"
              type="checkbox"
              checked={startNow}
              onChange={(e) => setStartNow(e.target.checked)}
              disabled={isSubmitting}
              className="h-4 w-4 rounded border-border bg-card text-accent focus:ring-accent"
            />
            <label
              htmlFor="startNow"
              className="text-sm text-text-secondary"
            >
              Start auction immediately
            </label>
          </div>

          {/* Start Time */}
          {!startNow && (
            <div>
              <label
                htmlFor="startTime"
                className="mb-1 block text-sm font-medium text-text-secondary"
              >
                Start Time
              </label>
              <input
                id="startTime"
                type="datetime-local"
                value={formData.startTime}
                onChange={(e) => handleInputChange('startTime', e.target.value)}
                disabled={isSubmitting}
                min={minDateTime}
                className="w-full rounded border border-border bg-card px-3 py-2 text-text-primary outline-none transition-colors focus:border-accent focus:ring-1 focus:ring-accent disabled:opacity-50"
              />
              {errors.startTime && (
                <p className="mt-1 text-sm text-error">{errors.startTime}</p>
              )}
            </div>
          )}

          {/* End Time */}
          <div>
            <label
              htmlFor="endTime"
              className="mb-1 block text-sm font-medium text-text-secondary"
            >
              End Time
            </label>
            <input
              id="endTime"
              type="datetime-local"
              value={formData.endTime}
              onChange={(e) => handleInputChange('endTime', e.target.value)}
              disabled={isSubmitting}
              min={minDateTime}
              className="w-full rounded border border-border bg-card px-3 py-2 text-text-primary outline-none transition-colors focus:border-accent focus:ring-1 focus:ring-accent disabled:opacity-50"
            />
            {errors.endTime && (
              <p className="mt-1 text-sm text-error">{errors.endTime}</p>
            )}
          </div>

          {/* Starting Price */}
          <div>
            <label
              htmlFor="startingPrice"
              className="mb-1 block text-sm font-medium text-text-secondary"
            >
              Starting Price
            </label>
            <div className="relative">
              <span className="absolute left-3 top-1/2 -translate-y-1/2 text-text-disabled">
                $
              </span>
              <input
                id="startingPrice"
                type="number"
                value={formData.startingPrice}
                onChange={(e) => handleInputChange('startingPrice', e.target.value)}
                disabled={isSubmitting}
                min="0"
                step="0.01"
                className="w-full rounded border border-border bg-card py-2 pl-7 pr-3 text-text-primary placeholder-text-disabled outline-none transition-colors focus:border-accent focus:ring-1 focus:ring-accent disabled:opacity-50"
                placeholder="0.00"
              />
            </div>
            {errors.startingPrice && (
              <p className="mt-1 text-sm text-error">{errors.startingPrice}</p>
            )}
          </div>

          {/* Actions */}
          <div className="flex justify-end gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              disabled={isSubmitting}
              className="rounded px-4 py-2 text-text-secondary transition-colors hover:text-text-primary disabled:opacity-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="rounded bg-accent px-4 py-2 font-medium text-background transition-colors hover:bg-accent-hover disabled:cursor-not-allowed disabled:opacity-50"
            >
              {isSubmitting ? 'Creating...' : 'Create Auction'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
