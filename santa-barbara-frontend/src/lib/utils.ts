import type { ClassValue } from 'clsx';
import { clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export const capitalize = <T extends string>(s: T) => (s[0].toUpperCase() + s.slice(1)) as Capitalize<typeof s>;

export const capitalizeAll = (s: string): string => {
  if (!s) return "";
  
  let result = "";
  let capitalizeNext = true;
  
  for (let i = 0; i < s.length; i++) {
    const char = s[i];
    
    if (char === " ") {
      result += " ";
      capitalizeNext = true;
    } else {
      result += capitalizeNext ? char.toUpperCase() : char.toLowerCase();
      capitalizeNext = false;
    }
  }
  
  return result;
};