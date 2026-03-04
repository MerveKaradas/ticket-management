const TicketCard = ({ ticket ,onClick }) => {
  return (
    <div 
    onClick={onClick} 
    className="bg-white p-4 rounded-lg shadow-sm border border-gray-200 hover:border-blue-300 hover:shadow-md transition-all cursor-pointer group">
      <p className="text-sm text-gray-700 font-medium leading-relaxed mb-4 group-hover:text-blue-700">
        {ticket.title} 
      </p>
     
      <div className="flex justify-between items-center mt-auto text-[10px] font-bold">
        <span className="text-gray-400 uppercase tracking-tighter">
          TKT-{ticket.id.toString().substring(0, 4)}
        </span>
        <div className="w-6 h-6 rounded-full bg-[#0747A6] flex items-center justify-center text-white ring-2 ring-white">
          {ticket.assignedTo?.name?.charAt(0).toUpperCase() || 'M'}
        </div>
      </div>
    </div>
  );
};

export default TicketCard;