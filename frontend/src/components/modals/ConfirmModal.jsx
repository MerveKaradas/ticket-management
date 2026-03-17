import React from 'react';

const ConfirmModal = ({ 
  isOpen, 
  onClose, 
  onConfirm, 
  title = "Emin misiniz?", 
  message = "Bu işlemi yapmak istediğinize emin misiniz?", 
  confirmText = "Evet, Onayla", 
  cancelText = "Vazgeç",
  type = "danger", 
  icon = "❓" 
}) => {
  if (!isOpen) return null;

  const confirmBtnClass = type === "danger" 
    ? "bg-red-500 hover:bg-red-600 shadow-red-200" 
    : "bg-blue-600 hover:bg-blue-700 shadow-blue-200";

  const iconBgClass = type === "danger" ? "bg-red-50 text-red-500" : "bg-blue-50 text-blue-500";

  return (
    <div className="fixed inset-0 z-[999] flex items-center justify-center p-4">
      {/* Arka Plan Bluru */}
      <div
        className="absolute inset-0 bg-slate-900/40 backdrop-blur-md animate-in fade-in duration-300"
        onClick={onClose}
      />

      {/* Modal İçeriği */}
      <div className="relative bg-white rounded-2xl shadow-2xl w-full max-w-sm overflow-hidden animate-in zoom-in duration-200">
        <div className="p-8 text-center">
          <div className={`w-16 h-16 ${iconBgClass} rounded-full flex items-center justify-center mx-auto mb-4 text-2xl`}>
            {icon}
          </div>
          <h3 className="text-xl font-bold text-gray-900 mb-2">{title}</h3>
          <p className="text-gray-500 text-sm mb-8">{message}</p>

          <div className="flex space-x-3">
            <button
              onClick={onClose}
              className="flex-1 px-4 py-3 text-sm font-bold text-gray-500 hover:bg-gray-100 rounded-xl transition-all"
            >
              {cancelText}
            </button>
            <button
              onClick={onConfirm}
              className={`flex-1 px-4 py-3 text-sm font-bold text-white rounded-xl shadow-lg transition-all active:scale-95 ${confirmBtnClass}`}
            >
              {confirmText}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ConfirmModal;