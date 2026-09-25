import React from 'react';
import { Card } from '../../ui/components/card';

export const Membros: React.FC = () => {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      <h1 style={{ fontSize: '18px', fontWeight: 700, color: 'var(--text-main)' }}>
        Membros da Banda
      </h1>
      <Card>
        <p style={{ color: 'var(--text-muted)' }}>
          Lista de membros ativos da Banda Musical Santa Bárbara.
        </p>
      </Card>
    </div>
  );
};

export default Membros;