import { useEffect, useState } from 'react';
import Api from '../services/Api';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer, Cell
} from 'recharts';
import StatsPanel from '../components/dashboard/StatsPanel';
import { useNavigate } from 'react-router-dom';


const Dashboard = () => {
  const [stats, setStats] = useState({
    total: 0,
    statusDistribution: [],
    lastTickets: []
  });

  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);
        const response = await Api.get('/dashboard/summary');
        const data = response.data;
        console.log()

        const chartData = Object.entries(data.eachStatusTotalTicketsCount || {}).map(([name, value]) => ({
          name,
          value
        }));

        const statusData = data.eachStatusTotalTicketsCount || {};
        const priorityData = data.totalPriority || {};

        const activeTickets =
          (statusData.IN_PROGRESS || 0) +
          (statusData.REOPENED || 0);


        setStats({
          total: data.totalTicketCount || 0,
          statusDistribution: chartData,
          lastTickets: data.last5Tickets || [],
          active: activeTickets,
          high: priorityData.HIGH || 0
        });


      } catch (err) {
        console.error("Dashboard yüklenirken hata:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  const STATUS_COLORS = {
    'DONE': '#00C49F',        
    'IN_PROGRESS': '#0088FE', 
    'OPEN': '#FFBB28',    
    'REOPENED': '#FF8042',
    'BACKLOG' :'#8884d8'
  };

  if (loading) return <div className="p-8 text-gray-500 font-medium">Veriler yükleniyor...</div>;

  return (
    <div className="flex-1 flex flex-col min-w-0 h-full overflow-hidden bg-white p-6">

      <div className="flex-1 flex flex-col min-h-0 space-y-6">

        {/* İstatistik Paneli */}
        <div className="shrink-0">
          <StatsPanel stats={stats} />
        </div>

        {/* Grafik ve Liste Alanı*/}
        <div className="flex-1 grid grid-cols-1 lg:grid-cols-2 gap-8 min-h-0 pb-4">

          {/* Grafik Kartı */}
          <div className="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm flex flex-col min-h-0">
            <h3 className="text-lg font-bold text-gray-800 mb-4 shrink-0">Bilet Durum Dağılımı</h3>

            <div className="flex-1 w-full min-h-0">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={stats.statusDistribution}>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
                  <XAxis dataKey="name" axisLine={false} tickLine={false} tick={{ fontSize: 12, fontWeight: 600 }} />
                  <YAxis axisLine={false} tickLine={false} />
                  <Tooltip
                    cursor={{ fill: '#f8fafc' }}
                    contentStyle={{ borderRadius: '12px', border: 'none', boxShadow: '0 10px 15px -3px rgb(0 0 0 / 0.1)' }}
                  />
                  <Bar dataKey="value" radius={[6, 6, 0, 0]}>
                    {stats.statusDistribution.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={STATUS_COLORS[entry.name] || '#8884d8'} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>

          {/* Son Hareketler Listesi Kartı */}
          <div className="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm flex flex-col min-h-0">
            <div className="flex items-center justify-between mb-4 shrink-0">
              <h3 className="text-lg font-bold text-gray-800">Son Hareketler</h3>
              <span className="text-[10px] bg-blue-50 text-blue-600 px-2 py-1 rounded-full font-bold">SON 5</span>
            </div>

            {/* Liste Alanı*/}
            <div className="flex-1 overflow-y-auto pr-2 custom-scrollbar space-y-3">
              {stats.lastTickets.map((ticket) => (
                <div
                  key={ticket.id}
                  onClick={() => navigate(`/board/ticket/${ticket.id}`)}
                  className="flex items-center justify-between p-4 hover:bg-gray-50 rounded-2xl border border-transparent hover:border-gray-100 transition-all group cursor-pointer"
                >
                  <div className="flex items-center space-x-4">
                    <div className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center group-hover:bg-white transition-colors">
                      📁
                    </div>
                    <div>
                      <h4 className="text-sm font-bold text-gray-700 leading-none">{ticket.title}</h4>
                      <p className="text-[11px] text-gray-400 mt-1">#{ticket.id.toString().substring(0, 8)}</p>
                    </div>
                  </div>
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-[10px] font-bold ${ticket.status === 'DONE' ? 'bg-green-50 text-green-600' :
                      ticket.status === 'IN_PROGRESS' ? 'bg-blue-50 text-blue-600' :
                        ticket.status === 'OPEN' ? 'bg-orange-100 text-orange-600' :
                          'bg-violet-50 text-violet-600'
                    }`}>
                    {ticket.status}
                  </span>
                </div>
              ))}
            </div>
          </div>

        </div>
      </div>
    </div>
  );

};

export default Dashboard;