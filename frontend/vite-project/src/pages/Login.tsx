import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

export default function Login() {
  const navigate = useNavigate()
  const [isLogin, setIsLogin] = useState(true)
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const handleSubmit = async () => {
    setError('')
    setSuccess('')
    
    if (!username || !password) {
      setError('Please fill in all fields')
      return
    }

    setLoading(true)
    try {
      const endpoint = isLogin ? '/api/auth/login' : '/api/auth/register'
      const body = { username, password }

      const response = await fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      })

      const data = await response.json()

      if (response.ok) {
        setSuccess(data.message)
        if (isLogin) {
          // Navigate to portfolio page
          navigate('/portfolio')
        } else {
          // Switch to login after successful registration
          setIsLogin(true)
          setUsername('')
          setPassword('')
        }
      } else {
        setError(data.error || 'Something went wrong')
      }
    } catch (e) {
      setError('Network error. Make sure the backend is running.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{ maxWidth: 400, margin: '50px auto', padding: '0 20px' }}>
      <h2>{isLogin ? 'Login' : 'Register'}</h2>

      <div style={{ marginBottom: 16 }}>
        <input
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          style={{ width: '100%', padding: 12, marginBottom: 8 }}
        />
        
        <input
          placeholder="Password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          style={{ width: '100%', padding: 12, marginBottom: 8 }}
        />


      </div>

      <button
        onClick={handleSubmit}
        disabled={loading}
        style={{
          width: '100%',
          padding: 12,
          backgroundColor: '#007bff',
          color: 'white',
          border: 'none',
          borderRadius: 4,
          cursor: loading ? 'not-allowed' : 'pointer',
          marginBottom: 16
        }}
      >
        {loading ? 'Loading...' : (isLogin ? 'Login' : 'Register')}
      </button>

      <button
        onClick={() => {
          setIsLogin(!isLogin)
          setError('')
          setSuccess('')
        }}
        style={{
          width: '100%',
          padding: 8,
          backgroundColor: 'transparent',
          color: '#007bff',
          border: '1px solid #007bff',
          borderRadius: 4,
          cursor: 'pointer'
        }}
      >
        {isLogin ? 'Register' : 'Login'}
      </button>

      {error && (
        <div style={{ color: '#dc3545', marginTop: 16, padding: 8 }}>
          {error}
        </div>
      )}

      {success && (
        <div style={{ color: '#28a745', marginTop: 16, padding: 8 }}>
          {success}
        </div>
      )}
    </div>
  )
}
