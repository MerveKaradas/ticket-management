import { useEffect, useState } from 'react';
import { getAllUsers } from '../services/AdminService';
import { deleteUser } from '../services/UserService';
import ConfirmModal from '../components/modals/ConfirmModal';
import { toast } from 'react-toastify';


const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");


  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState(null);

  useEffect(() => {
    fetchUsers();
  }, [page, searchTerm]);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await getAllUsers(page, 10, searchTerm);
      setUsers(response.data.content || []);
      setTotalPages(response.data.page?.totalPages || 0);
    } catch (error) {
      console.error("Kullanıcılar yüklenirken hata oluştu!", error);
    }
    setLoading(false);
  };

  const openDeleteModal = (id) => {
    setSelectedUserId(id);
    setIsModalOpen(true);
  };

  const handleConfirmDelete = async () => {
    setIsModalOpen(false);
    try {
      const response = await deleteUser(selectedUserId);
      if (response.status === 200 || response.status === 200) {
        fetchUsers();
        toast.success('Kullanıcı başarıyla silindi!');
      }
    } catch (err) {
      alert("Silme başarısız!");
      console.log(err.message);
    }
  };

  return (
    <div className="h-full flex flex-col  overflow-hidden p-8 space-y-6">

      {/* HEADER */}
      <div className="shrink-0 flex justify-between items-end">
        <div>
          <h1 className="text-2xl font-semibold text-[#172B4D]">Kullanıcı Yönetimi</h1>
          <p className="text-sm text-gray-500">Sistemdeki kullanıcıları ve yetkilerini yönetin.</p>
        </div>
        <div className="relative">
          <span className="absolute inset-y-0 left-0 flex items-center pl-3">
            <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
            </svg>
          </span>
          <input
            type="text"
            placeholder="İsim, email veya rol ara..."
            className="pl-10 pr-4 py-2 border border-gray-200 rounded-2xl w-full md:w-80 outline-none focus:ring-2 focus:ring-[#0747A6] transition-all bg-white shadow-sm"
            value={null}
            onChange={(e) => {
              setSearchTerm(e.target.value);
              setPage(0);
            }}
          />
        </div>
      </div>

      {/* TABLO KART */}
      <div className="flex-1 flex flex-col bg-white rounded-3xl shadow-xl border border-gray-100 overflow-hidden min-h-0">

        {/* TABLO HEADER */}
        <div className="shrink-0 bg-gray-100/50 border-b border-gray-100">
          <table className="w-full text-left table-fixed border-collapse">
            <thead>
              <tr>
                <th className="px-6 py-4 text-[11px] font-black uppercase tracking-wider text-gray-400 w-2/5">Kullanıcı</th>
                <th className="px-6 py-4 text-[11px] font-black uppercase tracking-wider text-gray-400 text-center w-1/5">Rol</th>
                <th className="px-6 py-4 text-[11px] font-black uppercase tracking-wider text-gray-400 text-center w-1/5">Hesap Durumu</th>
                <th className="px-6 py-4 text-[11px] font-black uppercase tracking-wider text-gray-400 text-right w-1/5">İşlemler</th>
              </tr>
            </thead>
          </table>
        </div>

        {/* TABLO BODY */}
        <div className="flex-1 overflow-y-auto min-h-0 custom-scrollbar relative">
          <table className="w-full text-left table-fixed border-collapse">
            <tbody className="divide-y divide-gray-50">
              {loading && (
                <div className="absolute inset-0 bg-white/60 flex items-center justify-center z-10">
                  <span className="text-[#0747A6] font-bold animate-pulse">Yükleniyor...</span>
                </div>
              )}

              {users.length > 0 ? (
                users.map((user) => (
                  <tr key={user.id} className="hover:bg-blue-100/30 transition-colors group">
                    {/* Kullanıcı Bilgisi */}
                    <td className="px-6 py-4 w-2/5">
                      <div className="flex items-center gap-3">
                        <div className="w-9 h-9 rounded-full bg-blue-100 text-[#0747A6] flex items-center justify-center font-bold text-xs shrink-0">
                          {user.name?.charAt(0).toUpperCase()}
                        </div>
                        <div className="truncate">
                          <div className="text-sm font-semibold text-gray-700 truncate">{user.name} {user.surname}</div>
                          <div className="text-[11px] text-gray-400 truncate">{user.email}</div>
                        </div>
                      </div>
                    </td>

                    {/* Rol */}
                    <td className="px-6 py-4 text-center w-1/5">
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-[10px] font-bold ${
                        user.role === 'ADMIN' ? 'bg-violet-50 text-violet-600 border border-violet-100' :
                        user.role === 'SYSTEM' ? 'bg-slate-50 text-slate-600 border border-slate-100' :
                        'bg-blue-50 text-blue-600 border border-blue-100'
                        }`}>
                        {user.role}
                      </span>

                    </td>

                    {/* Durum */}
                    <td className="px-6 py-4 text-center w-1/5">
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-[10px] font-bold  ${user.active === true ? 'bg-green-50 text-green-600' : 'bg-red-50 text-red-600'
                      }`}>
                        {user.active === true ? '✓ AKTİF' : '✕ AKTİF DEĞİL'}
                      </span>
                    </td>

                    {/* Aksiyonlar */}
                    <td className="px-6 py-4 text-right w-1/5">
                      <div className="flex justify-end gap-2">
                        {user.active && user.role !== "SYSTEM" && user.email.toLowerCase() !== "admin@kafein.com" && (
                        <button className="p-2 hover:bg-gray-100 rounded-xl transition-all text-gray-400 hover:text-blue-600" title="Düzenle">
                          ⚙️
                        </button>
                        )}
                        {user.active && user.email.toLowerCase() !== "admin@kafein.com" && (
                        <button
                          onClick={(e) => {
                            e.stopPropagation();
                            openDeleteModal(user.id);
                          }}
                          className="p-2 hover:bg-red-50 rounded-xl transition-all text-gray-400 hover:text-red-600" title="Sil">
                          🗑️
                          
                        </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                !loading && (
                  <tr>
                    <td colSpan="4" className="text-center py-20 text-gray-400 font-medium italic">
                      Henüz kayıtlı kullanıcı bulunamadı.
                    </td>
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

      {/* MODAL */}
      <ConfirmModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onConfirm={handleConfirmDelete}
        title={"Kullanıcıyı Sil"}
        message={
          "Bu kullanıcıyı silmek istediğinize emin misiniz?"}
        confirmText={"Evet, Onayla"}
        type="danger"
        icon={"🗑️"}
      />
    </div>
  );
};

export default UserManagement;