import { useState, useEffect, useCallback } from 'react';
import Api from '../../services/Api';
import { toast } from 'react-toastify';
import { useNavigate, useOutletContext, useParams } from 'react-router-dom';
import { claimTicket } from '../../services/TicketService';

const TicketDetailModal = () => {
    const { ticketId } = useParams();
    const [ticketData, setTicketData] = useState(null);
    const [comments, setComments] = useState([]);
    const [isEditing, setIsEditing] = useState(false);
    const [users, setUsers] = useState([]);

    const [editedTitle, setEditedTitle] = useState("");
    const [editedDescription, setEditedDescription] = useState("");
    const [editedStatus, setEditedStatus] = useState("");
    const [editedPriority, setEditedPriority] = useState("");
    const [editedAssignedToId, setEditedAssignedToId] = useState("");

    const [deleteConfirmId, setDeleteConfirmId] = useState(null);
    const [newComment, setNewComment] = useState('');
    const { currentUser, refreshTickets } = useOutletContext();

    const [isClaiming, setIsClaiming] = useState(false);
    const [claimTitle, setClaimTitle] = useState("");

    const navigate = useNavigate();
    const onClose = () => navigate('/board');

    // URLdeki ID değiştikçe tetiklenir
    useEffect(() => {
        const fetchTicket = async () => {
            try {
                const response = await Api.get(`/tickets/getTicket/${ticketId}`);
                const data = response.data;
                setTicketData(data);

                setEditedTitle(data.title);
                setClaimTitle(data.title);
                setEditedDescription(data.description);
                setEditedStatus(data.status);
                setEditedPriority(data.priority);
                setEditedAssignedToId(data.assignedTo?.id || '');
            } catch (err) {
                toast.error("Bilet yüklenemedi.");
            }
        };
        fetchTicket();
    }, [ticketId]);



    // Yorumlar
    const fetchLatestData = useCallback(async () => {
        if (!ticketData?.id) return;
        try {
            const commentsRes = await Api.get(`/comments/${ticketData.id}`);
            setComments(commentsRes.data || []);
        } catch (err) {
            console.error("Yorum hatası:", err);
        }
    }, [ticketData?.id]);

    useEffect(() => {
        fetchLatestData();
    }, [fetchLatestData]);

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const response = await Api.get('/users/listForAssignment');
                setUsers(response.data || []);
            } catch (err) { console.error(err); }
        };
        if (isEditing && isCreator) fetchUsers();
    }, [isEditing]);


    const handleAddComment = async () => {
        if (!newComment.trim()) return;
        try {
            const response = await Api.post(`/comments/${ticketData.id}/comment`, {
                content: newComment
            });
            if (response.status === 200 || response.status === 201) {
                toast.success("Yorum eklendi! ✨");
                setNewComment('');
                fetchLatestData();
                if (refreshTickets) refreshTickets();
            }
        } catch (err) {
            toast.error("Yorum gönderilemedi.");
        }
    };

    const handleConfirmDelete = async (commentId) => {
        try {
            const response = await Api.delete(`/comments/${commentId}`);
            if (response.status === 200 || response.status === 204) {
                toast.success("Yorum silindi 🗑️");
                setDeleteConfirmId(null);
                fetchLatestData();
            }
        } catch (err) {
            toast.error("Silme yetkiniz yok.");
        }
    };

    if (!ticketData) {
        return (
            <div className="absolute inset-0 bg-white z-[200] flex items-center justify-center">
                <div className="flex flex-col items-center space-y-4">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#0747A6]"></div>
                    <p className="text-gray-500 font-medium">Bilet Detayları Yükleniyor...</p>
                    <p>Bilet {ticketId} Yükleniyor...</p>
                </div>
            </div>
        );
    }


    const isCreator = currentUser?.id === ticketData.createdBy?.id;
    const isAssignee = currentUser?.id === ticketData.assignedTo?.id;
    const canEdit = isCreator || isAssignee;

    const handleUpdate = async () => {
        if (!isEditing) {
            setIsEditing(true);
            return;
        }

        try {
            let response;
            if (isCreator) {
                const updatePayload = {
                    id: ticketData.id,
                    title: editedTitle,
                    description: editedDescription,
                    status: editedStatus,
                    priority: editedPriority,
                    assignedToId: editedAssignedToId
                };
                response = await Api.put(`/tickets/${ticketData.id}`, updatePayload);
            } else if (isAssignee) {
                response = await Api.patch(`/tickets/${ticketData.id}/status`, { status: editedStatus });
            }

            if (response.status === 200 || response.status === 204) {
                toast.success("Güncellendi! ✨");
                setIsEditing(false);

                setTicketData(response.data || ticketData);

                if (refreshTickets) refreshTickets();
            }
        } catch (err) {
            toast.error("Kaydedilemedi.");
        }
    };

    const handleCancel = () => {

        setEditedTitle(ticketData.title);
        setEditedDescription(ticketData.description);
        setEditedStatus(ticketData.status);
        setEditedPriority(ticketData.priority);
        setEditedAssignedToId(ticketData.assignedTo?.id || '');
        setIsEditing(false);
    };

    const formatDate = (dateString) => {
        if (!dateString) return "Belirtilmedi";
        return new Date(dateString).toLocaleString('tr-TR', {
            year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit'
        });
    };


    const isUnassigned = ticketData.assignedTo?.role === 'SYSTEM';

    const handleFinalClaim = async () => {
        if (!claimTitle.trim()) {
            toast.error("Lütfen bir başlık girin!");
            return;
        }
        try {
            const response = await claimTicket(ticketData.id, claimTitle);
            if (response) {
                toast.success("Bilet başarıyla üstlenildi! ✨");
                setIsEditing(false);
                setTicketData(response.data || ticketData);
                setIsClaiming(false);
                if (refreshTickets) refreshTickets();
            }

        } catch (err) {
            toast.error("Bilet üstlenilemedi!");
        }
    }


    return (
        <div className="absolute inset-0 bg-white z-[200] flex flex-col animate-in fade-in duration-200">

            <div className="flex-1 flex overflow-hidden">
                {/* SOL BLOK: Bilet Detayları */}
                <div className="flex-[1.5] overflow-y-auto p-8 border-r custom-scrollbar">
                    <div className="flex items-start justify-between mb-4">
                        <div className="flex-1">
                            {(isClaiming || (isEditing && isCreator)) ? (
                                <input
                                    className="text-3xl font-bold text-gray-900 border-b-2 border-blue-500 outline-none w-full bg-transparent animate-pulse"
                                    value={isClaiming ? claimTitle : editedTitle}
                                    onChange={(e) => isClaiming ? setClaimTitle(e.target.value) : setEditedTitle(e.target.value)}
                                    placeholder="Bilet için yeni bir başlık girin..."
                                    autoFocus
                                />
                            ) : (
                                <h1 className="text-3xl font-bold text-gray-900 tracking-tight leading-tight">
                                    {ticketData.title}
                                </h1>
                            )}
                        </div>

                        {isUnassigned && (
                            <div className="flex items-center space-x-2">
                                {isClaiming ? (
                                    <>
                                        <button
                                            onClick={() => setIsClaiming(false)}
                                            className="px-4 py-2 text-gray-500 hover:text-red-500 font-bold text-sm"
                                        >
                                            Vazgeç
                                        </button>
                                        <button
                                            onClick={() => handleFinalClaim()}
                                            className="px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 shadow-md font-bold text-sm"
                                        >
                                            Onayla ve Üstlen
                                        </button>
                                    </>
                                ) : (
                                    <button
                                        onClick={() => setIsClaiming(true)}
                                        className="px-6 py-2 bg-[#0747A6] text-white rounded-lg hover:bg-[#0052CC] shadow-md font-bold text-sm"
                                    >
                                        Bileti Üstlen 
                                    </button>
                                )}
                            </div>
                        )}

                        {canEdit && !isUnassigned && (
                            <div className="ml-4 flex items-center space-x-2">
                                {/* Sadece Düzenleme Modundayken Vazgeç Butonu Gözükür */}
                                {isEditing && (
                                    <button
                                        onClick={handleCancel}
                                        className="px-4 py-2 bg-white border border-red-200 rounded-xl text-red-500 hover:bg-red-50 hover:border-red-500 transition-all shadow-sm text-sm font-bold"
                                    >
                                        Vazgeç ❌
                                    </button>
                                )}

                                <button
                                    onClick={handleUpdate}
                                    className="flex items-center space-x-2 px-4 py-2 bg-white border border-gray-200 rounded-xl text-gray-600 hover:text-[#0747A6] hover:border-[#0747A6] transition-all shadow-sm"
                                >
                                    <span className="text-sm font-bold">{isEditing ? "Değişiklikleri Kaydet" : "Düzenle"}</span>
                                    <span>{isEditing ? "💾" : "✏️"}</span>
                                </button>
                            </div>
                        )}

                    </div>

                    <div className="space-y-10">
                        {/* Açıklama */}
                        <div>
                            <label className="block text-[11px] font-black text-gray-400 uppercase tracking-widest mb-3">Açıklama</label>
                            {isEditing && isCreator ? (
                                <textarea className="w-full p-6 rounded-2xl border border-blue-200 focus:border-blue-500 outline-none text-sm text-gray-700 min-h-[200px] resize-none"
                                    value={editedDescription} onChange={(e) => setEditedDescription(e.target.value)} />
                            ) : (
                                <div className="prose prose-sm max-w-none text-gray-700 leading-relaxed bg-gray-50/50 p-6 rounded-2xl border border-gray-100 min-h-[200px]">
                                    {ticketData.description || "Açıklama yok."}
                                </div>
                            )}
                        </div>

                        {/* Durum ve Öncelik */}
                        <div className="grid grid-cols-2 gap-8">
                            <div className="p-5 bg-blue-50/30 rounded-2xl border border-blue-100/50">
                                <label className="block text-[10px] font-black text-blue-400 uppercase mb-2">Durum</label>
                                {isEditing && canEdit ? (
                                    <select value={editedStatus} onChange={(e) => setEditedStatus(e.target.value)}
                                        className="bg-white border border-blue-200 rounded-lg px-3 py-1.5 text-xs font-bold text-blue-600 w-full outline-none">
                                        <option value="OPEN">OPEN</option>
                                        <option value="IN_PROGRESS">IN_PROGRESS</option>
                                        <option value="DONE">DONE</option>
                                        <option value="REOPENED">REOPENED</option>
                                    </select>
                                ) : (
                                    <span className="px-3 py-1.5 bg-white border border-blue-200 rounded-lg text-xs font-bold text-blue-600 uppercase italic">
                                        {ticketData.status}
                                    </span>
                                )}
                            </div>

                            <div className="p-5 bg-red-50/30 rounded-2xl border border-red-100/50">
                                <label className="block text-[10px] font-black text-red-400 uppercase mb-2">Öncelik</label>
                                {isEditing && isCreator ? (
                                    <select value={editedPriority} onChange={(e) => setEditedPriority(e.target.value)}
                                        className="bg-white border border-red-200 rounded-lg px-3 py-1.5 text-xs font-bold text-red-600 w-full outline-none">
                                        <option value="LOW">LOW</option>
                                        <option value="MEDIUM">MEDIUM</option>
                                        <option value="HIGH">HIGH</option>
                                    </select>
                                ) : (
                                    <span className="px-3 py-1.5 bg-white border border-red-200 rounded-lg text-xs font-bold text-red-600 uppercase italic">
                                        {ticketData.priority}
                                    </span>
                                )}
                            </div>

                            {/*  Alt Bilgiler */}
                            <div className="pt-6 border-t border-gray-100 space-y-4">
                                <label className="block text-[10px] font-black text-gray-400 uppercase tracking-widest mb-4">Kayıt Bilgileri</label>
                                <div className="flex items-start space-x-3">
                                    <div className="mt-1.5 w-2 h-2 rounded-full bg-green-400"></div>
                                    <div>
                                        <p className="text-[11px] text-gray-500 font-medium">Oluşturan: <span className="text-gray-700 font-bold">{ticketData.createdBy?.name + " " + ticketData.createdBy?.surname}</span></p>
                                        <p className="text-[10px] text-gray-400">{formatDate(ticketData.createdAtDate)}</p>
                                    </div>
                                </div>
                                {ticketData.updatedBy && (
                                    <div className="flex items-start space-x-3">
                                        <div className="mt-1.5 w-2 h-2 rounded-full bg-blue-400"></div>
                                        <div>
                                            <p className="text-[11px] text-gray-500 font-medium">Son Güncelleyen: <span className="text-gray-700 font-bold">{ticketData.updatedBy?.name + " " + ticketData.updatedBy?.surname}</span></p>
                                            <p className="text-[10px] text-gray-400">{formatDate(ticketData.updatedDate)}</p>
                                        </div>
                                    </div>
                                )}
                            </div>

                            <div className="flex items-center justify-between group">
                                <div className="flex items-center space-x-3">
                                    <div className="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-[#0747A6] font-bold ring-2 ring-white">
                                        {ticketData.assignedTo?.name?.charAt(0).toUpperCase()}
                                    </div>
                                    <div className="flex-1">
                                        <label className="block text-[10px] font-black text-gray-400 uppercase tracking-widest">Atanan Kişi</label>
                                        {isEditing && isCreator ? (
                                            <select
                                                value={editedAssignedToId}
                                                onChange={(e) => setEditedAssignedToId(e.target.value)}
                                                className="w-full mt-1 bg-white border border-gray-200 rounded-lg px-2 py-1 text-sm outline-none focus:ring-2 focus:ring-blue-500"
                                            >
                                                <option value="">Seçiniz...</option>
                                                {users.map(u => (
                                                    <option key={u.id} value={u.id}>{u.name} {u.surname}</option>
                                                ))}
                                            </select>
                                        ) : (
                                            <p className="text-sm font-semibold text-gray-700">
                                                {ticketData.assignedTo?.name + " " + ticketData.assignedTo?.surname || "Atanmamış"}
                                            </p>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>

                </div>



                {/* SAĞ BLOK: Yorumlar */}
                <div className="flex-1 bg-gray-50/30 flex flex-col overflow-hidden">
                    <div className="p-4 flex justify-end">
                        <button title="Geri dön" onClick={onClose} className="flex items-center space-x-2 text-sm text-gray-500 hover:text-blue-600 transition-colors py-1 px-3 rounded-lg bg-gray-200 hover:bg-blue-100">
                            <span>←</span>
                            <span className="font-medium"> </span>
                        </button></div>
                    <div className="p-6 border-b bg-white/50">
                        <h3 className="font-bold text-gray-800">Aktivite <span className="text-[10px] bg-gray-200 px-2 py-0.5 rounded-full text-gray-500">Yorumlar</span></h3>
                    </div>

                    <div className="flex-1 overflow-y-auto p-8 space-y-8 custom-scrollbar">
                        <div className="flex space-x-4">
                            <div className="w-10 h-10 rounded-full bg-[#0747A6] flex items-center justify-center text-white font-bold ring-4 ring-white shadow-sm">
                                {currentUser?.name?.charAt(0)}{currentUser?.surname?.charAt(0)}
                            </div>
                            <div className="flex-1">
                                <textarea placeholder="Bir yorum bırakın..." className="w-full p-4 bg-white border border-gray-200 rounded-2xl text-sm outline-none focus:ring-2 focus:ring-blue-500 shadow-sm resize-none"
                                    rows="3" value={newComment} onChange={(e) => setNewComment(e.target.value)} />
                                <div className="mt-3 flex justify-end">
                                    <button className="bg-[#0052CC] text-white px-6 py-2 rounded-lg text-xs font-bold hover:shadow-lg transition-all" onClick={handleAddComment}>Gönder</button>
                                </div>
                            </div>
                        </div>

                        <div className="space-y-6">
                            {comments && [...comments]
                                .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
                                .map((comment, index) => (
                                    <div key={index} className="flex space-x-4 group relative">
                                        <div className="w-9 h-9 rounded-full bg-orange-100 text-orange-600 flex items-center justify-center font-bold text-xs">
                                            {comment.fullName?.charAt(0)}
                                        </div>
                                        <div className="flex-1 bg-white p-5 rounded-2xl border border-gray-100 shadow-sm relative">
                                            <div className="flex justify-between items-center mb-2">
                                                <span className="text-[13px] font-bold text-gray-700">{comment.fullName}</span>
                                                <div className="flex items-center space-x-3">
                                                    <span className="text-[11px] text-gray-400 italic">{formatDate(comment.createdAt)}</span>
                                                    {currentUser?.id === comment.authorId && (
                                                        <div>
                                                            {deleteConfirmId === comment.id ? (
                                                                <div className="flex space-x-2">
                                                                    <button onClick={() => handleConfirmDelete(comment.id)} className="text-[10px] text-red-600 font-bold">Sil</button>
                                                                    <button onClick={() => setDeleteConfirmId(null)} className="text-[10px] text-gray-500 font-bold">Vazgeç</button>
                                                                </div>
                                                            ) : (
                                                                <button onClick={() => setDeleteConfirmId(comment.id)} className="opacity-0 group-hover:opacity-100 transition-all">🗑️</button>
                                                            )}
                                                        </div>
                                                    )}
                                                </div>
                                            </div>
                                            <p className="text-sm text-gray-600">"{comment.content}"</p>
                                        </div>
                                    </div>
                                ))}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default TicketDetailModal;