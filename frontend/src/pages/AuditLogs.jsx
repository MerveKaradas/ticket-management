
import React, { useState, useEffect } from 'react';
import { getAuditLogs } from '../services/AdminService'; 

const AuditLogPage = () => {
  const [logs, setLogs] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadLogs();
  }, [page, searchTerm]);

  const loadLogs = async () => {
    setLoading(true);
    try {
      const response = await getAuditLogs(page, 10, searchTerm);
      setLogs(response.data.content || []);
      setTotalPages(response.data.page?.totalPages || 0);
    } catch (error) {
      console.error("Loglar yüklenemedi", error);
    }
    setLoading(false);
  };

  return (
    <div className="h-full max-h-screen flex flex-col overflow-hidden">
      
      {/* HEADER */}
      <div className="p-8 pb-0 shrink-0 space-y-6">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <h1 className="text-2xl font-semibold text-[#172B4D]">Sistem Günlükleri</h1>
            <p className="text-sm text-gray-500">Güvenlik ve işlem geçmişini buradan takip edebilirsiniz.</p>
          </div>

          <div className="relative">
            <span className="absolute inset-y-0 left-0 flex items-center pl-3">
              <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
              </svg>
            </span>
            <input 
              type="text"
              placeholder="Kullanıcı veya işlem ara..."
              className="pl-10 pr-4 py-2 border border-gray-200 rounded-2xl w-full md:w-80 outline-none focus:ring-2 focus:ring-[#0747A6] transition-all bg-white shadow-sm"
              value={searchTerm}
              onChange={(e) => {
                 setSearchTerm(e.target.value);
                 setPage(0);
              }}
            />
          </div>
        </div>
      </div>

      {/* TABLO KART */}
      <div className="flex-1 flex flex-col m-8 overflow-hidden bg-white rounded-3xl shadow-xl border border-gray-100 min-h-0">
        
        {/* TABLO BAŞLIĞI */}
        <div className="shrink-0 bg-gray-100/50 border-b border-gray-100">
          <table className="w-full text-left table-fixed">
            <thead>
              <tr>
                <th className="px-6 py-4 text-[11px] font-black uppercase tracking-wider text-gray-400 w-1/5">Kullanıcı</th>
                <th className="px-6 py-4 text-[11px] font-black uppercase tracking-wider text-gray-400 w-1/5">İşlem</th>
                <th className="px-6 py-4 text-[11px] font-black uppercase tracking-wider text-gray-400 w-2/5">Detaylar</th>
                <th className="px-6 py-4 text-[11px] font-black uppercase tracking-wider text-gray-400 text-center w-1/6">Durum</th>
                <th className="px-6 py-4 text-[11px] font-black uppercase tracking-wider text-gray-400 w-1/6">Tarih</th>
              </tr>
            </thead>
          </table>
        </div>

        {/* TABLO İÇERİĞİ */}
        <div className="flex-1 overflow-y-auto min-h-0 custom-scrollbar">
          <table className="w-full text-left table-fixed">
            <tbody className="divide-y divide-gray-50">
              {loading && (
                <div className="absolute inset-0 bg-white/60 flex items-center justify-center z-20">
                  <span className="text-[#0747A6] font-bold animate-bounce">Loglar Getiriliyor...</span>
                </div>
              )}
              
              {logs.length > 0 ? (
                logs.map((log) => (
                  <tr key={log.id} className="hover:bg-blue-100/30 transition-colors group">
                    <td className="px-6 py-4 w-1/5">
                      <div className="flex items-center space-x-3">
                        <div className="w-8 h-8 rounded-full bg-blue-100 text-[#0747A6] flex items-center justify-center text-xs font-bold shrink-0">
                          {log.performedBy.charAt(0).toUpperCase()}
                        </div>
                        <span className="text-sm font-semibold text-gray-700 truncate">{log.performedBy}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 w-1/5">
                      <span className="px-3 py-1 bg-gray-100 text-gray-600 rounded-lg text-[10px] font-bold tracking-tight uppercase inline-block truncate">
                        {log.operation}
                      </span>
                    </td>
                    <td className="px-6 py-4 w-2/5">
                      <p className="text-xs text-gray-500 truncate" title={log.details}>
                        {log.details}
                      </p>
                    </td>
                    <td className="px-6 py-4 text-center w-1/6">
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-[10px] font-bold ${
                        log.status === 'SUCCESS' ? 'bg-green-50 text-green-600' : 'bg-red-50 text-red-600'
                      }`}>
                        {log.status === 'SUCCESS' ? '✓ BAŞARILI' : '✕ HATALI'}
                      </span>
                    </td>
                    <td className="px-6 py-4 w-1/6">
                      <div className="text-[11px] text-gray-500 font-medium whitespace-nowrap">
                        {new Date(log.createdAtDate).toLocaleDateString()}
                        <span className="block text-gray-300">
                          {new Date(log.createdAtDate).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}
                        </span>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                !loading && (
                  <tr>
                    <td colSpan="5" className="text-center py-20 text-gray-400 font-medium">Kayıt bulunamadı.</td>
                  </tr>
                )
              )}
            </tbody>
          </table>
        </div>

        {/* PAGINATION */}
         <div className="shrink-0 flex items-center justify-between p-4 bg-white border-t border-gray-100">
          <div className="text-sm text-gray-500 font-medium">
            Toplam <span className="text-[#0747A6] font-bold">{totalPages}</span> sayfa / <span className="text-[#0747A6] font-bold">{page + 1}</span>. sayfa
          </div>
          
          <div className="flex space-x-2">
            <button 
              disabled={page === 0} 
              onClick={() => setPage(p => p - 1)}
              className="px-4 py-2 border border-gray-200 rounded-xl hover:bg-gray-50 disabled:opacity-30 font-bold text-[10px] tracking-widest transition-all"
            >
              ← GERİ
            </button>
            <button 
              disabled={page + 1 >= totalPages} 
              onClick={() => setPage(p => p + 1)}
              className="px-4 py-2 bg-[#0747A6] text-white rounded-xl hover:bg-[#0052CC] disabled:opacity-30 font-bold text-[10px] tracking-widest transition-all shadow-md shadow-blue-100"
            >
              İLERİ →
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AuditLogPage;