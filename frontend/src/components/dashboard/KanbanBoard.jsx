import TicketCard from './TicketCard';

const KanbanBoard = ({ tickets, columns, onTicketClick }) => {
  return (
    <div className="flex-1 flex space-x-4 overflow-x-auto pb-4 custom-scrollbar min-h-0">
      {columns.map((col) => (
        <div key={col.title} className={`flex-1 min-w-[280px] flex flex-col rounded-xl ${col.color} p-3 h-full`}>
          <div className="flex items-center justify-between mb-4 px-1">
            <h3 className="text-[11px] font-black text-gray-500 tracking-widest uppercase">{col.title}</h3>
            <span className="text-[10px] bg-white/50 px-2 py-0.5 rounded-full font-bold text-gray-500">
              {tickets.filter(t => col.statuses.includes(t.status)).length}
            </span>
          </div>

          <div className="flex-1 overflow-y-auto space-y-3 pr-1 custom-scrollbar">
            {tickets
              .filter(t => col.statuses.includes(t.status))
              .map(ticket => (
                <TicketCard key={ticket.id} ticket={ticket} onClick={() => onTicketClick(ticket)} />
              ))}
            {tickets.filter(t => col.statuses.includes(t.status)).length === 0 && (
              <div className="text-center py-8 text-gray-300 text-xs italic">Bilet bulunamadı</div>
            )}
          </div>
        </div>
      ))}
    </div>
  );
};

export default KanbanBoard;