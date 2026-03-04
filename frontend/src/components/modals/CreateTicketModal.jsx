import { useState, useEffect } from 'react';
import { toast } from 'react-toastify'; 
import Api from '../../services/Api';

const CreateTicketModal = ({ isOpen, onClose, refreshTickets }) => {
    
    const [users, setUsers] = useState([]);
    
    const [newTicket, setNewTicket] = useState({
        title: '',
        description: '',
        priority: 'LOW',
        assignedToId: '' 
    });

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const response = await Api.get('/users/listForAssignment');
                setUsers(response.data || []);
            } catch (err) {
                console.error("Kullanıcı listesi çekilemedi:", err);
            }
        };

        if (isOpen) {
            fetchUsers();
        }
    }, [isOpen]);

    const handleCreateTicket = async (e) => {
        e.preventDefault();
        try {
            const response = await Api.post('/tickets/createTicket', newTicket);
            
            if (response.status === 200 || response.status === 201) {
           
                toast.success('Bilet başarıyla oluşturuldu! 🚀', {
                    position: "bottom-right",
                    autoClose: 3000,
                });

                onClose(); 
                setNewTicket({ title: '', description: '', priority: 'LOW', assignedToId: '' }); 
                
                if (refreshTickets) refreshTickets(); // TODO : KONTROL ET
            }
        } catch (err) {
            console.error("Bilet oluşturma hatası:", err);
        }
    };

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-[100] flex items-center justify-center p-4">
            <div className="bg-white rounded-xl shadow-2xl w-full max-w-lg overflow-hidden animate-in zoom-in duration-200">
                <div className="px-6 py-4 border-b flex justify-between items-center bg-gray-50">
                    <h3 className="font-bold text-gray-800">Yeni Ticket Oluştur</h3>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600 text-2xl">&times;</button>
                </div>

                <form onSubmit={handleCreateTicket} className="p-6 space-y-4">
                    {/* Başlık ve Açıklama alanı */}
                    <div>
                        <label className="block text-xs font-black text-gray-400 uppercase mb-1">Başlık</label>
                        <input
                            type="text" required value={newTicket.title}
                            onChange={(e) => setNewTicket({ ...newTicket, title: e.target.value })}
                            placeholder="Ne üzerinde çalışılacak?"
                            className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                        />
                    </div>

                    <div>
                        <label className="block text-xs font-black text-gray-400 uppercase mb-1">Açıklama</label>
                        <textarea
                            rows="4" required value={newTicket.description}
                            onChange={(e) => setNewTicket({ ...newTicket, description: e.target.value })}
                            placeholder="Detayları buraya yazabilirsin..."
                            className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none transition-all resize-none"
                        ></textarea>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-xs font-black text-gray-400 uppercase mb-1">Öncelik</label>
                            <select
                                value={newTicket.priority}
                                onChange={(e) => setNewTicket({ ...newTicket, priority: e.target.value })}
                                className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none appearance-none bg-white cursor-pointer"
                            >
                                <option value="LOW">LOW</option>
                                <option value="MEDIUM">MEDIUM</option>
                                <option value="HIGH">HIGH</option>
                            </select>
                        </div>

                        {/* DİNAMİK KULLANICI LİSTESİ */}
                        <div>
                            <label className="block text-xs font-black text-gray-400 uppercase mb-1">Atanacak Kişi</label>
                            <select
                                required
                                value={newTicket.assignedToId}
                                onChange={(e) => setNewTicket({ ...newTicket, assignedToId: e.target.value })}
                                className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none appearance-none bg-white cursor-pointer"
                            >
                                <option value="">Bir kişi seçin...</option>
                                {users.map((u) => (
                                    <option key={u.id} value={u.id}>
                                        {u.name + " " + u.surname}
                                    </option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div className="pt-4 flex justify-end space-x-3">
                        <button type="button" onClick={onClose} className="px-4 py-2 text-sm font-bold text-gray-500 hover:bg-gray-100 rounded-lg">İptal</button>
                        <button type="submit" className="px-6 py-2 text-sm font-bold text-white bg-[#0052CC] hover:bg-[#0747A6] rounded-lg shadow-lg">Bileti Oluştur</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CreateTicketModal;