import { useState, useEffect, useCallback } from 'react';
import {
  differenceInSeconds,
  differenceInMinutes,
  differenceInHours,
  differenceInDays,
} from 'date-fns';

interface CountdownResult {
  timeRemaining: string;
  isUrgent: boolean;
  isExpired: boolean;
}

export function useCountdown(endTime: string): CountdownResult {
  const [now, setNow] = useState(() => new Date());

  const calculateTimeRemaining = useCallback((): CountdownResult => {
    const end = new Date(endTime);
    const totalSeconds = differenceInSeconds(end, now);

    if (totalSeconds <= 0) {
      return { timeRemaining: 'Ended', isUrgent: false, isExpired: true };
    }

    const days = differenceInDays(end, now);
    const hours = differenceInHours(end, now) % 24;
    const minutes = differenceInMinutes(end, now) % 60;
    const seconds = totalSeconds % 60;

    const isUrgent = totalSeconds < 300; // less than 5 minutes

    let timeRemaining: string;

    if (days > 0) {
      // "> 1 day": "2 days left" (spec format)
      timeRemaining = `${days} ${days === 1 ? 'day' : 'days'} left`;
    } else if (hours > 0) {
      // "< 1 day": "5 hours 23 minutes left" (spec format)
      if (minutes > 0) {
        timeRemaining = `${hours} ${hours === 1 ? 'hour' : 'hours'} ${minutes} ${minutes === 1 ? 'minute' : 'minutes'} left`;
      } else {
        timeRemaining = `${hours} ${hours === 1 ? 'hour' : 'hours'} left`;
      }
    } else if (minutes > 0 && !isUrgent) {
      // "< 1 hour": "45 minutes left" (spec format)
      timeRemaining = `${minutes} ${minutes === 1 ? 'minute' : 'minutes'} left`;
    } else {
      // "< 5 minutes": "4:32" - urgent countdown timer (spec format)
      const totalMinutes = Math.floor(totalSeconds / 60);
      const displaySeconds = seconds.toString().padStart(2, '0');
      timeRemaining = `${totalMinutes}:${displaySeconds}`;
    }

    return { timeRemaining, isUrgent, isExpired: false };
  }, [endTime, now]);

  useEffect(() => {
    const end = new Date(endTime);
    const totalSeconds = differenceInSeconds(end, new Date());

    if (totalSeconds <= 0) return;

    // Update every second if < 5 minutes, otherwise every minute
    const interval = totalSeconds < 300 ? 1000 : 60000;

    const timer = setInterval(() => {
      setNow(new Date());
    }, interval);

    return () => clearInterval(timer);
  }, [endTime]);

  return calculateTimeRemaining();
}
