import { Search } from 'lucide-react';
import React from 'react';
import '../styles/input-search.css';

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
}

export const InputSearch: React.FC<InputProps> = ({
  label,
  error,
  id,
  className = '',
  disabled = false,
  ...props
}) => {
    const inputId = id || (label ? label.toLowerCase().replace(/\s+/g, '-') : undefined);

    return ( <div className={`input-container ${disabled ? 'input-disabled' : ''} ${className}`}>
      {label && (
        <label htmlFor={inputId} className="input-label">
          {label}
        </label>
      )}
      <div className={`input-wrapper ${error ? 'input-error-border' : ''}`}>
        <input
          id={inputId}
          className="input-field"
          disabled={disabled}
          {...props}
        />
        <span className="input-icon">{<Search/>}</span>
      </div>
      {error && <span className="input-error-message">{error}</span>}
    </div>
  );
};