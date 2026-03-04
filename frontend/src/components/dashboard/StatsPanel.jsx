const StatsPanel = ({ stats }) => {
  return (
    <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div className="bg-white p-4 rounded-xl border border-gray-100 shadow-sm">
        <p className="text-[10px] font-black text-gray-400 uppercase tracking-widest">Toplam Bilet</p>
        <p className="text-2xl font-bold">{stats.total}</p>
      </div>
      <div className="bg-white p-4 rounded-xl border-l-4 border-l-blue-500 shadow-sm">
        <p className="text-[10px] font-black text-gray-400 uppercase tracking-widest">Aktif İşler</p>
        <p className="text-2xl font-bold">{stats.active}</p>
      </div>
      <div className="bg-white p-4 rounded-xl border-l-4 border-l-red-500 shadow-sm">
        <p className="text-[10px] font-black text-gray-400 uppercase tracking-widest text-red-500">Kritik / Acil</p>
        <p className="text-2xl font-bold">{stats.urgent}</p>
      </div>
      <div className="bg-white p-4 rounded-xl border border-gray-100 shadow-sm flex items-center justify-between">
        <div>
          <p className="text-[10px] font-black text-gray-400 uppercase tracking-widest">İlerleme</p>
          <p className="text-2xl font-bold text-green-600">%{stats.completedPercent}</p>
        </div>
        <div className="w-8 h-8 rounded-full border-4 border-green-100 border-t-green-500 animate-spin"></div>
      </div>
    </div>
  );
};

export default StatsPanel;