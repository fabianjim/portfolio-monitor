import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom'
import './App.css'
import Portfolio from './pages/Portfolio'
import Login from './pages/Login'

export default function App() {
  return (
    <Router>
      <div>
        <nav style={{ padding: 16, borderBottom: '1px solid #ddd' }}>
          <Link to="/login" style={{ marginRight: 16, textDecoration: 'none' }}>
            <button>Login</button>
          </Link>
          <Link to="/portfolio" style={{ textDecoration: 'none' }}>
            <button>Portfolio</button>
          </Link>
        </nav>

        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/portfolio" element={<Portfolio />} />
          <Route path="/" element={<Navigate to="/login" replace />} />
        </Routes>
      </div>
    </Router>
  )
}
