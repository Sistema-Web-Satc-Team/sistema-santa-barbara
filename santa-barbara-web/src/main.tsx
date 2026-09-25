import React from 'react';
import ReactDOM from 'react-dom/client';
import { AppRoutes } from './application/routes/AppRoutes';
import './ui/styles/global.css';

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <AppRoutes />
  </React.StrictMode>
);