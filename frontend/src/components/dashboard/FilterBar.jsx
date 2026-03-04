const FilterBar = ({ searchTerm, setSearchTerm, activeFilter, setActiveFilter, onOpenCreateModal }) => {
  return (
    <div className="flex items-center justify-between mb-8">
      <div className="flex items-center flex-1 max-w-4xl space-x-4">
        {/* Arama Barı */}
        <div className="relative w-64">
          <span className="absolute inset-y-0 left-3 flex items-center text-gray-400">🔍</span>
          <input
            type="text"
            placeholder="Ticket ara..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-10 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all"
          />
        </div>

        {/* Hızlı Filtreler */}
        <div className="flex items-center space-x-2 border-l pl-4 border-gray-200">
          <button
            onClick={() => setActiveFilter('ALL')}
            className={`px-3 py-1.5 rounded text-xs font-bold transition-all ${activeFilter === 'ALL' ? 'bg-[#EBECF0] text-[#42526E]' : 'text-gray-500 hover:bg-gray-100'}`}
          >
            Hepsi
          </button>
          <button
            onClick={() => setActiveFilter('MY_TICKETS')}
            className={`px-3 py-1.5 rounded text-xs font-bold transition-all ${activeFilter === 'MY_TICKETS' ? 'bg-[#EBECF0] text-[#42526E]' : 'text-gray-500 hover:bg-gray-100'}`}
          >
            Bana Atananlar
          </button>
          <button
            onClick={() => setActiveFilter('URGENT')}
            className={`px-3 py-1.5 rounded text-xs font-bold transition-all ${activeFilter === 'URGENT' ? 'bg-red-50 text-red-600' : 'text-gray-500 hover:bg-gray-100'}`}
          >
            Acil Olanlar
          </button>
        </div>
      </div>

      {/* Yeni Ticket Butonu */}
      <button onClick={onOpenCreateModal} className="bg-[#0052CC] hover:bg-[#0747A6] text-white px-5 py-2 rounded font-bold text-sm shadow-lg flex items-center space-x-2 transition-all shrink-0">
        <span className="text-lg">+</span>
        <span>Oluştur</span>
      </button>
    </div>
  );
};

export default FilterBar;