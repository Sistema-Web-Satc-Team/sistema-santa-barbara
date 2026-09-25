import React, { useState, useRef, useEffect } from 'react';
import '../styles/select.css';

export interface SelectOption {
  value: string;
  label: string;
}

export interface SelectProps {
  label?: string;
  value: string | string[];
  onChange: (value: string | string[]) => void;
  options: SelectOption[];
  multiple?: boolean;
  placeholder?: string;
  error?: string;
  disabled?: boolean;
  className?: string;
}

export const Select: React.FC<SelectProps> = ({
  label,
  value,
  onChange,
  options,
  multiple = false,
  placeholder = 'Selecione...',
  error,
  disabled = false,
  className = '',
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  const selectedValues: string[] = Array.isArray(value)
    ? value
    : value
    ? [value]
    : [];

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleSelect = (optionValue: string) => {
    if (multiple) {
      if (selectedValues.includes(optionValue)) {
        onChange(selectedValues.filter((v) => v !== optionValue));
      } else {
        onChange([...selectedValues, optionValue]);
      }
    } else {
      onChange(optionValue);
      setIsOpen(false);
    }
  };

  const removeTag = (e: React.MouseEvent, optionValue: string) => {
    e.stopPropagation();
    onChange(selectedValues.filter((v) => v !== optionValue));
  };

  const getOptionLabel = (val: string) => {
    return options.find((opt) => opt.value === val)?.label || val;
  };

  return (
    <div className={`select-container ${disabled ? 'select-disabled' : ''} ${className}`} ref={containerRef}>
      {label && <label className="select-label">{label}</label>}

      <div
        className={`select-trigger ${error ? 'select-error-border' : ''} ${isOpen ? 'select-focused' : ''}`}
        onClick={() => !disabled && setIsOpen(!isOpen)}
        role="button"
        tabIndex={0}
      >
        <div className="select-content">
          {selectedValues.length === 0 ? (
            <span className="select-placeholder">{placeholder}</span>
          ) : multiple ? (
            <div className="select-tags">
              {selectedValues.map((val) => (
                <span key={val} className="select-tag">
                  {getOptionLabel(val)}
                  <span
                    className="select-tag-remove"
                    onClick={(e) => removeTag(e, val)}
                  >
                    ×
                  </span>
                </span>
              ))}
            </div>
          ) : (
            <span className="select-single-value">{getOptionLabel(selectedValues[0])}</span>
          )}
        </div>

        <span className={`select-arrow ${isOpen ? 'open' : ''}`}>▲</span>
      </div>

      {error && <span className="select-error-message">{error}</span>}

      {isOpen && !disabled && (
        <ul className="select-dropdown">
          {options.map((option) => {
            const isSelected = selectedValues.includes(option.value);
            return (
              <li
                key={option.value}
                className={`select-dropdown-item ${isSelected ? 'selected' : ''}`}
                onClick={() => handleSelect(option.value)}
              >
                <span>{option.label}</span>
                {isSelected && <span className="select-check">✓</span>}
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
};

export default Select;