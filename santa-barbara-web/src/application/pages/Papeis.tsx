import React from 'react';
import { Card } from '../../ui/components/card';
import { BAND_ROLES } from '../hooks/useInvites';

export const Papeis: React.FC = () => {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      <h1 style={{ fontSize: '18px', fontWeight: 700, color: 'var(--text-main)' }}>
        Papéis e Permissões
      </h1>
      <Card>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
          {BAND_ROLES.map((role) => (
            <span
              key={role.value}
              style={{
                backgroundColor: 'var(--primary-light)',
                color: 'var(--primary)',
                padding: '6px 12px',
                borderRadius: '16px',
                fontSize: '13px',
                fontWeight: 600,
              }}
            >
              {role.label}
            </span>
          ))}
        </div>
      </Card>
    </div>
  );
};

export default Papeis;