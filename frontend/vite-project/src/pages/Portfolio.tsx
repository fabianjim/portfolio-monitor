import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

export default function Portfolio() {
  const navigate = useNavigate()
  const [holdings, setHoldings] = useState<Array<{ ticker: string; shares: string }>>([
    { ticker: '', shares: '' },
  ])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string>('')

  const addHolding = () => setHoldings([...holdings, { ticker: '', shares: '' }])
  const removeHolding = (idx: number) => setHoldings(holdings.filter((_, i) => i !== idx))
  const updateHolding = (idx: number, updated: { ticker: string; shares: string }) =>
    setHoldings(holdings.map((h, i) => (i === idx ? updated : h)))

  const handleLogout = async () => {
    setHoldings([{ ticker: '', shares: '' }])
    setError('')
    try {
      await fetch('/api/auth/logout', {
        method: 'POST',
        credentials: 'include'
      })      
    } catch (error) {
      console.error('Logout error:', error)
    }
    navigate('/login')
  }

  const validate = (): string => {
    if (holdings.length === 0) return 'Add at least one holding'
    for (const h of holdings) {
      if (!h.ticker || h.ticker.trim() === '') return 'Ticker is required for all holdings'
      if (h.shares === '' || isNaN(Number(h.shares))) return 'Shares must be a number for all holdings'
      if (Number(h.shares) < 0) return 'Shares cannot be negative'
    }
    return ''
  }

  const createPortfolio = async () => {
    setError('')
    const validationError = validate()
    if (validationError) {
      setError(validationError)
      return
    }
    
    setLoading(true)
    try {
      const payload = {
        holdings: holdings.map((h) => ({
          ticker: h.ticker.trim().toUpperCase(),
          shares: Number(h.shares),
        })),
      }
      
      // create portfolio with holdings
      const response = await fetch('/api/portfolio/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
        credentials: "include",
      })
      
      if (!response.ok) throw new Error('Failed to create portfolio, backend must be running')

      navigate('/dashboard')
      
    } catch (e) {
      const message = e instanceof Error ? e.message : 'Unexpected error'
      setError(message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{ maxWidth: 960, margin: '24px auto', padding: '0 12px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <h2 style={{ margin: 0 }}>Portfolio</h2>
        <button 
          onClick={handleLogout}
          style={{
            padding: '8px 16px',
            backgroundColor: '#dc3545',
            color: 'white',
            border: 'none',
            borderRadius: 4,
            cursor: 'pointer',
            fontSize: '14px'
          }}
        >
          Logout
        </button>
      </div>
      

      {holdings.map((h, idx) => (
        <div key={idx} style={{ display: 'flex', gap: 12, alignItems: 'center', marginBottom: 8 }}>
          <input
            placeholder="Ticker (e.g., AAPL)"
            value={h.ticker}
            onChange={(e) => updateHolding(idx, { ...h, ticker: e.target.value.toUpperCase() })}
            maxLength={10}
            style={{ padding: 8, width: 160 }}
          />
          <input
            placeholder="Shares"
            type="number"
            value={h.shares}
            onChange={(e) => updateHolding(idx, { ...h, shares: e.target.value })}
            style={{ padding: 8, width: 120 }}
          />
          <button type="button" onClick={() => removeHolding(idx)}>Remove</button>
        </div>
      ))}

      <div style={{ display: 'flex', gap: 8, marginTop: 12 }}>
        <button type="button" onClick={addHolding}>Add Holding</button>
        <button type="button" onClick={createPortfolio} disabled={loading}>
          {loading ? 'Creating…' : 'Create Portfolio'}
        </button>
      </div>

      {error && <div style={{ color: '#b00020', marginTop: 8 }}>{error}</div>}
    </div>
  )
}


