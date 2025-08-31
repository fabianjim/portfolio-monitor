import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

type Stock = {
  ticker: string
  timestamp: string
  currentPrice: number
  open: number
  prevClose: number
  high: number
  low: number
}

type Holding = {
  ticker: string
  shares: number
  stock?: Stock | null
}

export default function Portfolio() {
  const navigate = useNavigate()
  const [holdings, setHoldings] = useState<Array<{ ticker: string; shares: string }>>([
    { ticker: '', shares: '' },
  ])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string>('')
  const [results, setResults] = useState<Holding[]>([])

  const addHolding = () => setHoldings([...holdings, { ticker: '', shares: '' }])
  const removeHolding = (idx: number) => setHoldings(holdings.filter((_, i) => i !== idx))
  const updateHolding = (idx: number, updated: { ticker: string; shares: string }) =>
    setHoldings(holdings.map((h, i) => (i === idx ? updated : h)))

  const handleLogout = () => {
    // Clear any stored authentication data (if you're using localStorage/sessionStorage)
    // localStorage.removeItem('authToken')
    // sessionStorage.clear()
    
    // Reset component state
    setHoldings([{ ticker: '', shares: '' }])
    setResults([])
    setError('')
    
    // Navigate back to login
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

  const fetchPortfolioInfo = async () => {
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
      const postRes = await fetch('/api/portfolio', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      })
      if (!postRes.ok) throw new Error('Failed to submit portfolio, backend must be running')

      const getRes = await fetch('/api/portfolio/holdings')
      if (!getRes.ok) throw new Error('Failed to fetch holdings')
      const data = (await getRes.json()) as Holding[]
      setResults(data)
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
        <button type="button" onClick={fetchPortfolioInfo} disabled={loading}>
          {loading ? 'Loading…' : 'Fetch Portfolio Info'}
        </button>
      </div>

      {error && <div style={{ color: '#b00020', marginTop: 8 }}>{error}</div>}

      {results.length > 0 && (
        <div style={{ marginTop: 16 }}>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr>
                {['Ticker', 'Shares', 'Timestamp', 'Current Price', 'Open', 'Prev Close', 'High', 'Low', 'Market Value'].map(
                  (h) => (
                    <th key={h} style={{ border: '1px solid #ddd', padding: 8, textAlign: 'left', background: '#808080' }}>
                      {h}
                    </th>
                  ),
                )}
              </tr>
            </thead>
            <tbody>
              {results.map((r, i) => (
                <tr key={i}>
                  <td style={{ border: '1px solid #ddd', padding: 8 }}>{r.ticker}</td>
                  <td style={{ border: '1px solid #ddd', padding: 8 }}>{r.shares}</td>
                  <td style={{ border: '1px solid #ddd', padding: 8 }}>{r.stock?.timestamp ?? '-'}</td>
                  <td style={{ border: '1px solid #ddd', padding: 8 }}>{r.stock?.currentPrice ?? '-'}</td>
                  <td style={{ border: '1px solid #ddd', padding: 8 }}>{r.stock?.open ?? '-'}</td>
                  <td style={{ border: '1px solid #ddd', padding: 8 }}>{r.stock?.prevClose ?? '-'}</td>
                  <td style={{ border: '1px solid #ddd', padding: 8 }}>{r.stock?.high ?? '-'}</td>
                  <td style={{ border: '1px solid #ddd', padding: 8 }}>{r.stock?.low ?? '-'}</td>
                  <td style={{ border: '1px solid #ddd', padding: 8 }}>
                    {r.stock ? (Number(r.shares) * Number(r.stock.currentPrice)).toFixed(2) : '-'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}


