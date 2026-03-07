import { useState, useRef, useEffect } from 'react';

const FilterBar = ({ filters, setFilters, users, currentUser, onOpenCreateModal }) => {
  const [isAdvancedOpen, setIsAdvancedOpen] = useState(false);
  const advancedRef = useRef(null);

  useEffect(() => {
    const handleClick = (e) => {
      if (advancedRef.current && !advancedRef.current.contains(e.target)) setIsAdvancedOpen(false);
    };
    document.addEventListener("mousedown", handleClick);
    return () => document.removeEventListener("mousedown", handleClick);
  }, []);

  const toggleFilter = (key, value) => {
    setFilters(prev => ({
      ...prev,
      [key]: prev[key] === value ? '' : value, // Aynı butona basınca filtreyi iptal eder

    }));
  };

  return (
    <div className="flex items-center justify-between mb-8">
      <div className="flex items-center flex-1 space-x-4">

        {/* Arama Barı */}
        <div className="relative w-64">
          <span className="absolute inset-y-0 left-3 flex items-center text-gray-400">🔍</span>
          <input
            type="text"
            placeholder="Karakterle ara..."
            value={filters.title}
            onChange={(e) => setFilters({ ...filters, title: e.target.value })}
            className="w-full pl-10 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-md text-sm focus:ring-2 focus:ring-blue-500 outline-none"
          />
        </div>

        {/* Filtreleme Paneli */}
        <div className="relative" ref={advancedRef}>
          <button
            onClick={() => setIsAdvancedOpen(!isAdvancedOpen)}
            className={`flex items-center space-x-2 px-4 py-2 bg-white border rounded-lg text-xs font-bold transition-all ${isAdvancedOpen ? 'border-blue-500 text-blue-600 shadow-sm' : 'border-gray-200 text-gray-600 hover:bg-gray-50'
              }`}
          >
            <span>⚡ Filtrele</span>
            {/* Aktif filtre işareti */}
            {(filters.priority || filters.status || filters.assignedToId) && (
              <span className="flex h-2 w-2 rounded-full bg-blue-600"></span>
            )}
          </button>

          {isAdvancedOpen && (
            <div className="absolute top-full left-0 mt-2 w-72 bg-white border border-gray-100 shadow-2xl rounded-xl p-4 z-[100] animate-in fade-in zoom-in duration-200">
              <div className="space-y-4">
                {/* Öncelik Filtresi */}
                <div>
                  <label className="text-[10px] font-black text-gray-400 uppercase tracking-wider">Öncelik Seviyesi</label>
                  <select
                    value={filters.priority}
                    onChange={(e) => setFilters({ ...filters, priority: e.target.value })}
                    className="w-full mt-1.5 text-sm bg-gray-50 border-none rounded-lg p-2.5 outline-none focus:ring-2 focus:ring-blue-100"
                  >
                    <option value="">Tümü</option>
                    <option value="LOW">Düşük (Low)</option>
                    <option value="MEDIUM">Orta (Medium)</option>
                    <option value="HIGH">Yüksek (High)</option>
                  </select>
                </div>

                {/* Status Filtresi */}
                <div>
                  <label className="text-[10px] font-black text-gray-400 uppercase tracking-wider">Durum</label>
                  <select
                    value={filters.status}
                    onChange={(e) => setFilters({ ...filters, status: e.target.value })}
                    className="w-full mt-1.5 text-sm bg-gray-50 border-none rounded-lg p-2.5 outline-none focus:ring-2 focus:ring-blue-100"
                  >
                    <option value="">Tümü</option>
                    <option value="REOPENED">REOPENED</option>
                    <option value="OPEN">OPEN</option>
                    <option value="IN_PROGRESS">IN_PROGRESS</option>
                    <option value="DONE">DONE</option>
                  </select>
                </div>

                {/* Atanan Kişi Filtresi */}
                <div>
                  <label className="text-[10px] font-black text-gray-400 uppercase tracking-wider">Atanan Kişi</label>
                  <select
                    value={filters.assignedToId}
                    onChange={(e) => setFilters({ ...filters, assignedToId: e.target.value })}
                    className="w-full mt-1.5 text-sm bg-gray-50 border-none rounded-lg p-2.5 outline-none focus:ring-2 focus:ring-blue-100"
                  >
                    <option value="">Herkes</option>
                    {users?.map(user => (
                      <option key={user.id} value={user.id}>{user.name + " " + user.surname}</option>
                    ))}
                  </select>
                </div>

                {/* Temizleme Butonu */}
                <button
                  onClick={() => setFilters({ title: '', priority: '', status: '', assignedToId: '', page: 0, size: 50 })}
                  className="w-full py-2 text-[10px] font-bold text-red-500 hover:bg-red-50 rounded-lg transition-colors border border-transparent hover:border-red-100"
                >
                  Filtreleri Sıfırla
                </button>
              </div>
            </div>
          )}
        </div>
        <div>
        </div>

        <div className="h-6 w-[1px] bg-gray-300 "></div>

        <div className="flex items-center space-x-1">

          <button
            onClick={() => setFilters({ ...filters, priority: '', assignedToId: '', status: '' })}
            className={`px-4 py-1.5 rounded-md text-sm font-semibold transition-all ${!filters.priority && !filters.assignedToId
                ? "bg-gray-100 text-blue-800 shadow-sm"
                : "text-slate-600 hover:bg-gray-50"
              }`}
          >
            Hepsi
          </button>

          {/* Bana Atananlar */}
          <button
            onClick={() => toggleFilter('assignedToId', currentUser?.id)} 
            className={`px-4 py-1.5 rounded-md text-sm font-semibold transition-all ${filters.assignedToId === currentUser?.id
                ? "bg-gray-100 text-blue-800 shadow-sm"
                : "text-slate-600 hover:bg-gray-50"
              }`}
          >
            Bana Atananlar
          </button>

          {/* Acil Olanlar */}
          <button
            onClick={() => toggleFilter('priority', 'HIGH')} 
            className={`px-4 py-1.5 rounded-md text-sm font-semibold transition-all ${filters.priority === 'HIGH'
                ? "bg-gray-100 text-blue-800 shadow-sm"
                : "text-slate-600 hover:bg-gray-50"
              }`}
          >
            Acil Olanlar
          </button>
        </div>
      </div>

      <button onClick={onOpenCreateModal} className="bg-[#0052CC] hover:bg-[#0747A6] text-white px-6 py-2 rounded-lg font-bold text-sm shadow-md transition-all active:scale-95">
        + Oluştur
      </button>
    </div>
  );
};

export default FilterBar;