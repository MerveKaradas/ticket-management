import { useEffect, useState } from 'react';
import { getAuditLogs, logoutAllUsers } from '../services/AdminService';

const AuditLogs = () => {
    const [logs, setLogs] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalLogs, setTotalLogs] = useState(0);

    useEffect(() => {
        fetchLogs();
    }, [page]);

    const fetchLogs = async () => {
        try {
            const response = await getAuditLogs(page, 10);
            const data = response.data;
            setLogs(data.content);
            setTotalPages(data.page.totalPages);
            setTotalLogs(data.page.totalElements);
        } catch (error) {
            console.error("Loglar yüklenirken hata oluştu!", error);
        }
    };

    const handleLogoutAll = async () => {
        if (window.confirm("Tüm kullanıcıların oturumunu kapatmak istediğinize emin misiniz?")) {
            try {
                await logoutAllUsers();
                alert("Tüm oturumlar sonlandırıldı.");
            } catch (error) {
                alert("İşlem sırasında bir hata oluştu.");
            }
        }
    };

    return (
        <div className="p-8 bg-white min-h-screen">
           
            <div className="flex justify-between items-end mb-10">
                <div>
                   
                    <h1 className="text-2xl font-semibold text-[#172B4D]">Sistem Günlükleri</h1>
                    <p className="text-sm text-[#5E6C84] mt-1">Son aktiviteler ve sistem hareketleri.</p>
                </div>
              
                <button 
                    onClick={handleLogoutAll} 
                    className="bg-[#0052CC] hover:bg-[#0747A6] text-white px-6 py-2 rounded-lg font-bold text-sm shadow-md transition-all active:scale-95"
                >
                    <span className="text-base">🔐</span> Tüm Oturumları Kapat
                </button>
            </div>

            

            {/* Tablo Konteynırı  */}
            <div className="bg-white rounded-lg border border-[#DFE1E6] overflow-hidden shadow-sm">
                <table className="w-full text-left border-collapse">
                    <thead>
                        <tr className="bg-[#F4F5F7] border-b border-[#DFE1E6]">
                            <th className="p-4 text-[11px] font-bold text-[#5E6C84] uppercase tracking-wider">Kullanıcı</th>
                            <th className="p-4 text-[11px] font-bold text-[#5E6C84] uppercase tracking-wider">İşlem</th>
                            <th className="p-4 text-[11px] font-bold text-[#5E6C84] uppercase tracking-wider">Detaylar</th>
                            <th className="p-4 text-[11px] font-bold text-[#5E6C84] uppercase tracking-wider text-center">Durum</th>
                            <th className="p-4 text-[11px] font-bold text-[#5E6C84] uppercase tracking-wider">Tarih</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-[#DFE1E6]">
                        {logs.map((log) => (
                            <tr key={log.id} className="hover:bg-[#F4F5F7]/50 transition-colors group">
                                {/* Kullanıcı */}
                                <td className="p-4">
                                    <div className="flex items-center gap-3">
                                        <div className="w-8 h-8 rounded-full bg-[#0747A6] text-white flex items-center justify-center font-bold text-[10px] shadow-sm">
                                            {log.performedBy?.substring(0, 1).toUpperCase() || 'S'}
                                        </div>
                                        <span className="text-[13px] font-medium text-[#172B4D]">{log.performedBy}</span>
                                    </div>
                                </td>

                                {/* İşlem  */}
                                <td className="p-4">
                                    <span className="text-[10px] bg-[#EBECF0] text-[#42526E] px-2 py-1 rounded font-bold uppercase tracking-tight">
                                        {log.operation}
                                    </span>
                                </td>

                                {/* Detaylar  */}
                                <td className="p-4">
                                    <div className="max-w-md">
                                        <p className="text-[13px] text-[#42526E] leading-relaxed">
                                            {log.details}
                                        </p>
                                        {log.errorMessage && (
                                            <p className="text-[11px] text-[#DE350B] mt-1 font-medium italic">
                                                ⚠ {log.errorMessage}
                                            </p>
                                        )}
                                    </div>
                                </td>

                                {/* Durum */}
                                <td className="p-4 text-center">
                                    <span className={`inline-flex items-center px-3 py-1 rounded-full text-[10px] font-bold ${
                                        log.status === 'SUCCESS' 
                                            ? 'text-[#006644] bg-[#E3FCEF]' 
                                            : 'text-[#BF2600] bg-[#FFEBE6]'
                                    }`}>
                                        {log.status === 'SUCCESS' ? '✓ BAŞARILI' : '✕ HATA'}
                                    </span>
                                </td>

                                {/* Tarih */}
                                <td className="p-4">
                                    <div className="text-[12px] text-[#5E6C84]">
                                        <span className="block font-medium text-[#172B4D]">{new Date(log.createdAtDate).toLocaleDateString('tr-TR')}</span>
                                        <span className="opacity-70">{new Date(log.createdAtDate).toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' })}</span>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>

                {/* Sayfalama */}
                <div className="p-4 flex justify-between items-center bg-[#F4F5F7] border-t border-[#DFE1E6]">
                    <span className="text-xs font-medium text-[#5E6C84]">
                        Gösterilen: <span className="text-[#172B4D]">{logs.length}</span> / Toplam: {totalLogs}
                    </span>
                    <div className="flex gap-2">
                        <button 
                            disabled={page === 0} 
                            onClick={() => setPage(p => p - 1)}
                            className="px-4 py-1.5 text-xs font-bold border border-[#DFE1E6] rounded bg-white text-[#42526E] hover:bg-[#EBECF0] disabled:opacity-40 transition-all shadow-sm"
                        >
                            ← Önceki
                        </button>
                        <button 
                            disabled={page + 1 === totalPages} 
                            onClick={() => setPage(p => p + 1)}
                            className="px-4 py-1.5 text-xs font-bold border border-[#DFE1E6] rounded bg-white text-[#42526E] hover:bg-[#EBECF0] disabled:opacity-40 transition-all shadow-sm"
                        >
                            Sonraki →
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AuditLogs;