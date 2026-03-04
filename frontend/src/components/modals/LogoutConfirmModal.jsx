const LogoutConfirmModal = ({ isOpen, onClose, onConfirm }) => {
  if (!isOpen) return null; 

  return (
    <div className="fixed inset-0 z-[200] flex items-center justify-center p-4">
      {/* Arka Plan Bluru */}
      <div
        className="absolute inset-0 bg-slate-900/40 backdrop-blur-md animate-in fade-in duration-300"
        onClick={onClose}
      />

      {/* Modal İçeriği */}
      <div className="relative bg-white rounded-2xl shadow-2xl w-full max-w-sm overflow-hidden animate-in zoom-in duration-200">
        <div className="p-8 text-center">
          <div className="w-16 h-16 bg-red-50 text-red-500 rounded-full flex items-center justify-center mx-auto mb-4 text-2xl">
            👋
          </div>
          <h3 className="text-xl font-bold text-gray-900 mb-2">Oturumu Kapat?</h3>
          <p className="text-gray-500 text-sm mb-8">
            Çıkış yapmak istediğine emin misin? Mevcut oturumun sonlandırılacak.
          </p>

          <div className="flex space-x-3">
            <button
              onClick={onClose}
              className="flex-1 px-4 py-3 text-sm font-bold text-gray-500 hover:bg-gray-100 rounded-xl transition-all"
            >
              Vazgeç
            </button>
            <button
              onClick={onConfirm}
              className="flex-1 px-4 py-3 text-sm font-bold text-white bg-red-500 hover:bg-red-600 rounded-xl shadow-lg shadow-red-200 transition-all active:scale-95"
            >
              Evet, Çıkış Yap
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LogoutConfirmModal;