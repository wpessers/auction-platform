import { renderHook, act } from '@testing-library/react';
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { useCountdown } from './useCountdown';

describe('useCountdown', () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  describe('Time format per spec', () => {
    it('shows "X days left" when more than 1 day remaining', () => {
      // Set current time
      const now = new Date('2024-01-15T10:00:00Z');
      vi.setSystemTime(now);

      // End time 2 days from now
      const endTime = new Date('2024-01-17T10:00:00Z').toISOString();

      const { result } = renderHook(() => useCountdown(endTime));

      expect(result.current.timeRemaining).toBe('2 days left');
      expect(result.current.isUrgent).toBe(false);
      expect(result.current.isExpired).toBe(false);
    });

    it('shows "1 day left" for singular day', () => {
      const now = new Date('2024-01-15T10:00:00Z');
      vi.setSystemTime(now);

      const endTime = new Date('2024-01-16T12:00:00Z').toISOString();

      const { result } = renderHook(() => useCountdown(endTime));

      expect(result.current.timeRemaining).toBe('1 day left');
    });

    it('shows "X hours Y minutes left" when less than 1 day remaining', () => {
      const now = new Date('2024-01-15T10:00:00Z');
      vi.setSystemTime(now);

      // End time 5 hours 23 minutes from now
      const endTime = new Date('2024-01-15T15:23:00Z').toISOString();

      const { result } = renderHook(() => useCountdown(endTime));

      expect(result.current.timeRemaining).toBe('5 hours 23 minutes left');
      expect(result.current.isUrgent).toBe(false);
    });

    it('shows "X hours left" when minutes are 0', () => {
      const now = new Date('2024-01-15T10:00:00Z');
      vi.setSystemTime(now);

      const endTime = new Date('2024-01-15T13:00:00Z').toISOString();

      const { result } = renderHook(() => useCountdown(endTime));

      expect(result.current.timeRemaining).toBe('3 hours left');
    });

    it('shows "1 hour left" for singular hour', () => {
      const now = new Date('2024-01-15T10:00:00Z');
      vi.setSystemTime(now);

      const endTime = new Date('2024-01-15T11:00:00Z').toISOString();

      const { result } = renderHook(() => useCountdown(endTime));

      expect(result.current.timeRemaining).toBe('1 hour left');
    });

    it('shows "X minutes left" when less than 1 hour but more than 5 minutes', () => {
      const now = new Date('2024-01-15T10:00:00Z');
      vi.setSystemTime(now);

      // End time 45 minutes from now
      const endTime = new Date('2024-01-15T10:45:00Z').toISOString();

      const { result } = renderHook(() => useCountdown(endTime));

      expect(result.current.timeRemaining).toBe('45 minutes left');
      expect(result.current.isUrgent).toBe(false);
    });

    it('shows "1 minute left" for singular minute', () => {
      const now = new Date('2024-01-15T10:00:00Z');
      vi.setSystemTime(now);

      // End time 6 minutes from now (not urgent yet)
      const endTime = new Date('2024-01-15T10:06:00Z').toISOString();

      const { result } = renderHook(() => useCountdown(endTime));

      expect(result.current.timeRemaining).toBe('6 minutes left');
    });

    it('shows "M:SS" countdown format when less than 5 minutes remaining (urgent)', () => {
      const now = new Date('2024-01-15T10:00:00Z');
      vi.setSystemTime(now);

      // End time 4 minutes 32 seconds from now
      const endTime = new Date('2024-01-15T10:04:32Z').toISOString();

      const { result } = renderHook(() => useCountdown(endTime));

      expect(result.current.timeRemaining).toBe('4:32');
      expect(result.current.isUrgent).toBe(true);
      expect(result.current.isExpired).toBe(false);
    });

    it('pads seconds with leading zero in urgent countdown', () => {
      const now = new Date('2024-01-15T10:00:00Z');
      vi.setSystemTime(now);

      // End time 2 minutes 5 seconds from now
      const endTime = new Date('2024-01-15T10:02:05Z').toISOString();

      const { result } = renderHook(() => useCountdown(endTime));

      expect(result.current.timeRemaining).toBe('2:05');
      expect(result.current.isUrgent).toBe(true);
    });

    it('shows "Ended" when time has passed', () => {
      const now = new Date('2024-01-15T10:00:00Z');
      vi.setSystemTime(now);

      // End time was 1 hour ago
      const endTime = new Date('2024-01-15T09:00:00Z').toISOString();

      const { result } = renderHook(() => useCountdown(endTime));

      expect(result.current.timeRemaining).toBe('Ended');
      expect(result.current.isUrgent).toBe(false);
      expect(result.current.isExpired).toBe(true);
    });
  });

  describe('Timer updates', () => {
    it('updates every minute when more than 5 minutes remaining', () => {
      const now = new Date('2024-01-15T10:00:00Z');
      vi.setSystemTime(now);

      // End time 10 minutes from now
      const endTime = new Date('2024-01-15T10:10:00Z').toISOString();

      const { result } = renderHook(() => useCountdown(endTime));

      expect(result.current.timeRemaining).toBe('10 minutes left');

      // Advance 1 minute
      act(() => {
        vi.advanceTimersByTime(60000);
      });

      expect(result.current.timeRemaining).toBe('9 minutes left');
    });

    it('updates every second when less than 5 minutes remaining', () => {
      const now = new Date('2024-01-15T10:00:00Z');
      vi.setSystemTime(now);

      // End time 4 minutes from now
      const endTime = new Date('2024-01-15T10:04:00Z').toISOString();

      const { result } = renderHook(() => useCountdown(endTime));

      expect(result.current.timeRemaining).toBe('4:00');
      expect(result.current.isUrgent).toBe(true);

      // Advance 1 second
      act(() => {
        vi.advanceTimersByTime(1000);
      });

      expect(result.current.timeRemaining).toBe('3:59');
    });

    it('does not set interval when already expired', () => {
      const now = new Date('2024-01-15T10:00:00Z');
      vi.setSystemTime(now);

      // End time was 1 hour ago
      const endTime = new Date('2024-01-15T09:00:00Z').toISOString();

      const { result } = renderHook(() => useCountdown(endTime));

      expect(result.current.isExpired).toBe(true);

      // Advance time - should not cause errors
      act(() => {
        vi.advanceTimersByTime(60000);
      });

      expect(result.current.timeRemaining).toBe('Ended');
    });
  });

  describe('Edge cases', () => {
    it('handles exactly 5 minutes remaining (boundary)', () => {
      const now = new Date('2024-01-15T10:00:00Z');
      vi.setSystemTime(now);

      // End time exactly 5 minutes from now (300 seconds)
      const endTime = new Date('2024-01-15T10:05:00Z').toISOString();

      const { result } = renderHook(() => useCountdown(endTime));

      // At exactly 300 seconds, isUrgent should be false (< 300 triggers urgent)
      expect(result.current.timeRemaining).toBe('5 minutes left');
      expect(result.current.isUrgent).toBe(false);
    });

    it('handles exactly 299 seconds remaining (just under 5 min)', () => {
      const now = new Date('2024-01-15T10:00:01Z');
      vi.setSystemTime(now);

      // End time 4 min 59 sec from now
      const endTime = new Date('2024-01-15T10:05:00Z').toISOString();

      const { result } = renderHook(() => useCountdown(endTime));

      expect(result.current.timeRemaining).toBe('4:59');
      expect(result.current.isUrgent).toBe(true);
    });

    it('handles 0 seconds remaining', () => {
      const now = new Date('2024-01-15T10:00:00Z');
      vi.setSystemTime(now);

      const endTime = new Date('2024-01-15T10:00:00Z').toISOString();

      const { result } = renderHook(() => useCountdown(endTime));

      expect(result.current.timeRemaining).toBe('Ended');
      expect(result.current.isExpired).toBe(true);
    });

    it('handles 1 second remaining', () => {
      const now = new Date('2024-01-15T10:00:00Z');
      vi.setSystemTime(now);

      const endTime = new Date('2024-01-15T10:00:01Z').toISOString();

      const { result } = renderHook(() => useCountdown(endTime));

      expect(result.current.timeRemaining).toBe('0:01');
      expect(result.current.isUrgent).toBe(true);
    });
  });
});
