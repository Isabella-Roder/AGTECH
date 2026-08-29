import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { LoginPage } from './pages/LoginPage.tsx'
import { PropriedadesPage } from './pages/PropriedadesPage.tsx';
import { RotaProtegida } from './RotaProtegida.tsx'
import './index.css'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path='/' element={<LoginPage/>} />
        <Route
          path='/propriedades'
          element={
            <RotaProtegida>
              <PropriedadesPage/>
            </RotaProtegida>
          }/>
      </Routes>
    </BrowserRouter>
  </StrictMode>,
)
