import { useEffect, useState } from 'react';
import { deleteAllTickets, deleteTicket, filterTickets, getAllTickets } from '../services/TicketService';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import ConfirmModal from '../components/modals/ConfirmModal';

const TicketManagement = () => {
    const [tickets, setTickets] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(false);

    // FİLTRE STATE'LERİ
    const [searchTerm, setSearchTerm] = useState("");
    const [statusFilter, setStatusFilter] = useState("");
    const [priorityFilter, setPriorityFilter] = useState("");

    // MODAL 
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedTicketId, setSelectedTicketId] = useState(null);
    const [modalType, setModalType] = useState("single"); 

    const navigate = useNavigate();

    useEffect(() => {
        fetchTickets();
    }, [page, searchTerm, statusFilter, priorityFilter]);

    const fetchTickets = async () => {
        setLoading(true);
        try {
            const response = await filterTickets(searchTerm, statusFilter, priorityFilter, page, 10);
            setTickets(response.data.content || []);
            setTotalPages(response.data.page.totalPages || 0);
        } catch (error) {
            console.error("Filtreleme hatası:", error);
        }
        setLoading(false);
    };

    const openDeleteModal = async (id) => {
        setSelectedTicketId(id);
        setModalType("single");
        setIsModalOpen(true);

    };



    const openDeleteAllModal = async () => {

        setModalType("all");
        setIsModalOpen(true);
    };

    const handleConfirmDelete = async () => {
        setIsModalOpen(false); 
        try {
            if (modalType === "single") {
                const response = await deleteTicket(selectedTicketId);
                if (response.status === 200 || response.status === 200) {
                    toast.success('Bilet başarıyla silindi!');
                }
            } else {
                await deleteAllTickets();
                toast.success('Tüm biletler temizlendi!');
            }
            fetchTickets(); 
        } catch (err) {
            toast.error("İşlem başarısız!");
        }
    };

    return (
        <div className="h-full flex flex-col overflow-hidden p-8 space-y-6">

            {/* HEADER VE FİLTRELER */}
            <div className="shrink-0 space-y-4">
                <div className="flex justify-between items-center">
                    <div>
                        <h1 className="text-2xl font-semibold text-[#172B4D]">Bilet Yönetimi</h1>
                        <p className="text-sm text-gray-500">Sistemdeki biletleri detaylı filtreleyin.</p>
                    </div>
                    <button onClick={openDeleteAllModal} className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-all semi-bold text-xs shadow-lg shadow-red-100">
                        ⚠️ Tüm Biletleri Sil
                    </button>
                </div>

                {/* FİLTRE ÇUBUĞU */}
                <div className="flex flex-wrap gap-4 bg-white p-4 rounded-2xl shadow-sm border border-gray-100">
                    <input
                        type="text"
                        placeholder="Başlık ara..."
                        className="flex-1 min-w-[200px] pl-4 pr-4 py-2 border border-gray-100 rounded-xl outline-none focus:ring-2 focus:ring-[#0747A6] bg-gray-50/50"
                        value={searchTerm}
                        onChange={(e) => { setSearchTerm(e.target.value); setPage(0); }}
                    />

                    <select
                        className="px-4 py-2 border border-gray-100 rounded-xl outline-none bg-gray-50/50 text-sm font-semibold text-gray-700 truncate"
                        value={statusFilter}
                        onChange={(e) => { setStatusFilter(e.target.value); setPage(0); }}
                    >
                        <option value="" >Tüm Durumlar</option>
                        <option value="REOPENED">REOPENED</option>
                        <option value="OPEN">OPEN</option>
                        <option value="IN_PROGRESS">IN PROGRESS</option>
                        <option value="DONE">DONE</option>
                    </select>

                    <select
                        className="px-4 py-2 border border-gray-100 rounded-xl outline-none bg-gray-50/50 text-sm font-semibold text-gray-700 truncate"
                        value={priorityFilter}
                        onChange={(e) => { setPriorityFilter(e.target.value); setPage(0); }}
                    >
                        <option value="">Tüm Öncelikler</option>
                        <option value="HIGH">High</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="LOW">Low</option>
                    </select>
                </div>
            </div>

            {/* TABLO KART */}
            <div className="flex-1 flex flex-col bg-white rounded-3xl shadow-xl border border-gray-100 overflow-hidden min-h-0">
                <div className="shrink-0 bg-gray-100/50 border-b border-gray-100">
                    <table className="w-full text-left table-fixed border-collapse">
                        <thead>
                            <tr>
                                <th className="px-6 py-4 text-[11px] font-black uppercase tracking-wider text-gray-400 w-[20%]">Bilet / Başlık</th>
                                <th className="px-6 py-4 text-[11px] font-black uppercase tracking-wider text-gray-400 text-left w-[20%]">Oluşturan</th>
                                <th className="px-6 py-4 text-[11px] font-black uppercase tracking-wider text-gray-400 text-left w-[20%]">Atanan</th>
                                <th className="px-6 py-4 text-[11px] font-black uppercase tracking-wider text-gray-400 text-center w-[15%]">Durum</th>
                                <th className="px-6 py-4 text-[11px] font-black uppercase tracking-wider text-gray-400 text-center w-[15%]">Öncelik</th>
                                <th className="px-6 py-4 text-[11px] font-black uppercase tracking-wider text-gray-400 text-right w-[10%]">İşlemler</th>
                            </tr>
                        </thead>
                    </table>
                </div>

                <div className="flex-1 overflow-y-auto min-h-0 custom-scrollbar relative">
                    <table className="w-full text-left table-fixed border-collapse">
                        <tbody className="divide-y divide-gray-50">
                            {loading && <div className="absolute inset-0 bg-white/60 flex items-center justify-center z-10">Yükleniyor...</div>}
                            {tickets.map((ticket) => (
                                <tr
                                    key={ticket.id}
                                    onClick={() => navigate(`/board/ticket/${ticket.id}`)}
                                    className="hover:bg-blue-100/30 transition-colors group">
                                    <td className="px-6 py-4 w-[20%]">
                                        <div className="text-sm font-semibold text-gray-700 truncate">{ticket.title}</div>
                                        <div className="text-[11px] text-gray-400 truncate">#{ticket.id.toString().substring(0, 8)}</div>
                                    </td>
                                    <td className="px-6 py-4 w-[20%]">
                                        <div className="text-sm font-semibold text-gray-700/80 truncate">{ticket.createdBy?.name} {ticket.createdBy?.surname}</div>
                                    </td>
                                    <td className="px-6 py-4 w-[20%]">
                                        <div className="text-sm font-semibold text-gray-700/80 truncate">{ticket.assignedTo?.name} {ticket.assignedTo?.surname}</div>
                                    </td>
                                    <td className="px-6 py-4 text-center w-[15%]">
                                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-[10px] font-bold ${ticket.status === 'OPEN'
                                            ? 'bg-red-50 text-red-600 border border-red-100' :
                                            ticket.status === 'REOPENED' ? 'bg-orange-50 text-orange-600 border border-orange-100' :
                                                ticket.status === 'IN_PROGRESS' ? 'bg-blue-50 text-blue-600 border border-blue-100' :
                                                    ticket.status === 'DONE' ? 'bg-green-50 text-green-600 border border-green-100' : 'bg-gray-50 text-gray-600 border border-gray-100'}`}>
                                            {ticket.status}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4 text-center w-[15%]">
                                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-[10px] font-bold ${ticket.priority === 'HIGH'
                                            ? 'bg-red-50 text-red-600 border border-red-100' :
                                            ticket.priority === 'MEDIUM' ? 'bg-yellow-50 text-yellow-600 border border-yellow-100' :
                                                ticket.priority === 'LOW' ? 'bg-green-50 text-green-600 border border-green-100' : 'bg-gray-50 text-gray-600 border border-gray-100'}`}>
                                            {ticket.priority}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4 text-right w-[10%]">
                                        <button onClick={(e) => {
                                            e.stopPropagation();
                                            openDeleteModal(ticket.id);
                                        }}

                                            className="p-2 hover:bg-red-50 rounded-xl text-gray-400 hover:text-red-600 transition-all">
                                            🗑️
                                        </button>
                                    </td>
                                </tr>
                            ))}
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

            {/* MODAL */}
            <ConfirmModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onConfirm={handleConfirmDelete}
                title={modalType === "single" ? "Bileti Sil" : "Tüm Biletleri Sil"}
                message={modalType === "single"
                    ? "Bu bileti silmek istediğinize emin misiniz? Bu işlem geri alınamaz."
                    : "DİKKAT! Sistemdeki TÜM biletler silinecek. Bu işlem kesinlikle geri alınamaz!"}
                confirmText={modalType === "single" ? "Evet, Onayla" : "Evet, Hepsini Sil"}
                type="danger"
                icon={modalType === "single" ? "🗑️" : "⚠️"}
            />
        </div>
    );
};

export default TicketManagement;
