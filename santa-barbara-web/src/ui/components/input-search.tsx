import React from 'react';
import '../styles/input-search.css';

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  icon?: React.ReactNode;
}

export const InputSearch: React.FC<InputProps> = ({
  label,
  error,
  icon,
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
        {icon && <span className="input-icon">{icon}</span>}
      </div>
      {error && <span className="input-error-message">{error}</span>}
    </div>
  );
};