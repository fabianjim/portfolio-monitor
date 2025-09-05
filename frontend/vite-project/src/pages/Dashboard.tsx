import { useState, useEffect, useRef } from 'react'
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

export default function Dashboard() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string>('')
  const [results, setResults] = useState<Holding[]>([])
  const hasFetched = useRef(false)

  const handleLogout = async () => {
    setResults([])
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

  const fetchPortfolioInfo = async () => {
    setError('')
    setLoading(true)
    try {
      // fetch stock data
      const fetchRes = await fetch('/api/stock/fetch', {
        method: 'GET',
        credentials: "include",
      })
      if (!fetchRes.ok) throw new Error('Failed to fetch stock data')
      const stockData = (await fetchRes.json()) as Stock[]
      
      // fetch holding data
      const getRes = await fetch('/api/portfolio/holdings', {
        method: 'GET',
        credentials: "include",
      })
      if (!getRes.ok) throw new Error('Failed to fetch holdings')
      const holdingsData = (await getRes.json()) as Holding[]
      
      // combine to display
      const combinedData = holdingsData.map(holding => {
        const stock = stockData.find(s => s.ticker === holding.ticker)
        return {
          ...holding,
          stock: stock || null
        }
      })
      
      setResults(combinedData)
      
    } catch (e) {
      const message = e instanceof Error ? e.message : 'Unexpected error'
      setError(message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (!hasFetched.current) {
      hasFetched.current = true
      fetchPortfolioInfo()
    }
  }, [])

  return (
    <div style={{ maxWidth: 960, margin: '24px auto', padding: '0 12px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <h2 style={{ margin: 0 }}>Dashboard</h2>
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

      <div style={{ display: 'flex', gap: 8, marginBottom: 16 }}>
        <button type="button" onClick={fetchPortfolioInfo} disabled={loading}>
          {loading ? 'Loading…' : 'Refresh Portfolio Info'}
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