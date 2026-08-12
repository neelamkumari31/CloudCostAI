import React, { useState, useEffect } from 'react';
import axios from 'axios';

function App() {
  const [servers, setServers] = useState([]);
  const [loading, setLoading] = useState(true);

  // Function to pull live rows straight out of your PostgreSQL database through Spring Boot
  const fetchServers = async () => {
    try {
      setLoading(true);
      const response = await axios.get('http://localhost:8080/api/servers');
      setServers(response.data);
    } catch (error) {
      console.error("Error fetching data from backend api:", error);
    } finally {
      setLoading(false);
    }
  };

  // Pull data automatically when the browser tab first opens up
  useEffect(() => {
    fetchServers();
    // Refresh the table feed automatically every 5 seconds to match the Kafka stream!
    const interval = setInterval(fetchServers, 5000);
    return () => clearInterval(interval);
  }, []);

  // Quick Action Handler: Approves optimization directly from your webpage buttons!
  // const handleApprove = async (token) => {
  //   try {
  //     alert("Dispatched approval command token to infrastructure engine!");
  //     await axios.get(`http://localhost:8080/api/approve?token=${token}`);
  //     fetchServers(); // Reload table data to show updated green status badges
  //   } catch (error) {
  //     alert("Approval network transmission failed.");
  //   }
  // };
  const handleApprove = async (token) => {
    if (!token) {
      alert("❌ Frontend Error: No secure token found for this server row asset!");
      return;
    }
    try {
      console.log("🚀 [FRONTEND ACTION] Firing web parameter call for token string: " + token);
      
      // Execute network transaction call
      const response = await axios.get(`http://localhost:8080/api/approve?token=${token}`);
      
      alert("⚡ Optimization command executed successfully!");
      fetchServers(); // Reload table logs to switch badge layout state colors live
    } catch (error) {
      console.error("Backend response failure:", error);
      alert("❌ Approval network transmission failed.");
    }
  };


  // Live analytics counters adding up your data rows automatically
  const pendingCount = servers.filter(s => s.status === 'PENDING_APPROVAL').length;
  const totalSavedCost = servers
    .filter(s => s.status === 'APPROVED')
    .reduce((sum, s) => sum + s.monthlyCostINR, 0);

  return (
    <div style={{ fontFamily: 'Arial, sans-serif', padding: '30px', backgroundColor: '#f5f7fb', minHeight: '100vh' }}>
      
      {/* 🚀 DASHBOARD HEADER LAYOUT */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' }}>
        <div>
          <h1 style={{ margin: 0, color: '#1e293b', fontSize: '28px' }}>🤖 CloudCost.AI Control Center</h1>
          <p style={{ margin: '5px 0 0 0', color: '#64748b' }}>Automated Live FinOps Infrastructure Stream Monitor</p>
        </div>
        <button onClick={fetchServers} style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '10px 20px', backgroundColor: '#fff', border: '1px solid #cbd5e1', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', color: '#334155' }}>
          🔄 Sync Table
        </button>
      </div>

      {/* 📊 SUMMARY ANALYTICS TRACKING METRIC CARDS */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginBottom: '35px' }}>
        <div style={{ backgroundColor: '#fff', padding: '20px', borderRadius: '12px', boxShadow: '0 1px 3px rgba(0,0,0,0.05)', display: 'flex', alignItems: 'center', gap: '20px' }}>
          <div style={{ backgroundColor: '#fef3c7', padding: '15px', borderRadius: '10px', fontSize: '24px' }}>⚠️</div>
          <div>
            <h3 style={{ margin: 0, color: '#64748b', fontSize: '14px', textTransform: 'uppercase' }}>Active Wasted Servers</h3>
            <p style={{ margin: '5px 0 0 0', fontSize: '24px', fontWeight: 'bold', color: '#1e293b' }}>{pendingCount} Assets</p>
          </div>
        </div>

        <div style={{ backgroundColor: '#fff', padding: '20px', borderRadius: '12px', boxShadow: '0 1px 3px rgba(0,0,0,0.05)', display: 'flex', alignItems: 'center', gap: '20px' }}>
          <div style={{ backgroundColor: '#dcfce7', padding: '15px', borderRadius: '10px', fontSize: '24px' }}>✅</div>
          <div>
            <h3 style={{ margin: 0, color: '#64748b', fontSize: '14px', textTransform: 'uppercase' }}>Total Savings Realized</h3>
            <p style={{ margin: '5px 0 0 0', fontSize: '24px', fontWeight: 'bold', color: '#16a34a' }}>Itemized Cost Saved: ₹{totalSavedCost.toLocaleString('en-IN')} / mo</p>
          </div>
        </div>
      </div>

      {/* 📋 THE MAIN LIVE STATUS DATA FEED TABLE */}
      <div style={{ backgroundColor: '#fff', borderRadius: '12px', boxShadow: '0 1px 3px rgba(0,0,0,0.05)', overflow: 'hidden' }}>
        <div style={{ padding: '20px', borderBottom: '1px solid #f1f5f9', fontWeight: 'bold', fontSize: '16px', color: '#1e293b' }}>📊 Real-Time Infrastructure Logs</div>
        
        {loading && servers.length === 0 ? (
          <p style={{ padding: '30px', textAlign: 'center', color: '#64748b' }}>Connecting to data pipeline channels...</p>
        ) : (
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
            <thead>
              <tr style={{ backgroundColor: '#f8fafc', borderBottom: '1px solid #cbd5e1', color: '#475569', fontSize: '14px' }}>
                <th style={{ padding: '15px' }}>SERVER INSTANCE</th>
                <th style={{ padding: '15px' }}>CPU USED</th>
                <th style={{ padding: '15px' }}>COST (INR)</th>
                <th style={{ padding: '15px' }}>AI OPTIMIZATION ANALYSIS</th>
                <th style={{ padding: '15px' }}>STATUS</th>
                <th style={{ padding: '15px', textAlign: 'right' }}>ACTION</th>
              </tr>
            </thead>
            <tbody>
              {servers.map((server) => (
                <tr key={server.id} style={{ borderBottom: '1px solid #f1f5f9', fontSize: '15px', color: '#334155' }}>
                  <td style={{ padding: '15px', fontWeight: '500' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>🖥️ {server.serverId}</div>
                  </td>
                  <td style={{ padding: '15px', color: '#dc2626', fontWeight: 'bold' }}>{server.cpuUtilization}%</td>
                  <td style={{ padding: '15px' }}>₹{server.monthlyCostINR.toLocaleString('en-IN')}</td>
                  <td style={{ padding: '15px', fontSize: '13px', color: '#475569', maxWidth: '300px' }}>
                    <b>{server.aiSummary || 'Analyzing metrics...'}</b>
                    <code style={{ display: 'block', marginTop: '4px', color: '#0f172a', backgroundColor: '#f1f5f9', padding: '4px', borderRadius: '4px', fontSize: '11px' }}>{server.aiScript}</code>
                  </td>
                  <td style={{ padding: '15px' }}>
                    <span style={{ padding: '4px 10px', borderRadius: '20px', fontSize: '12px', fontWeight: '600', backgroundColor: server.status === 'APPROVED' ? '#dcfce7' : '#fef3c7', color: server.status === 'APPROVED' ? '#16a34a' : '#d97706' }}>
                      <i>{server.status === 'PENDING_APPROVAL' ? 'pending approval' : server.status}</i>

                    </span>
                  </td>
                  <td style={{ padding: '15px', textAlign: 'right' }}>
                    {server.status === 'PENDING_APPROVAL' ? (
                      <button onClick={() => handleApprove(server.approvalToken)} style={{ backgroundColor: '#1a73e8', color: '#fff', border: 'none', padding: '6px 14px', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold', fontSize: '13px' }}>
                        Approve Fix
                      </button>
                    ) : (
                      <span style={{ color: '#94a3b8', fontSize: '13px' }}>Executed ✓</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

    </div>
  );
}

export default App;
