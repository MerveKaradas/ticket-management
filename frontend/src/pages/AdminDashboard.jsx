import { useEffect, useState } from 'react';
import { PieChart, Pie, Legend, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import { getSummary } from '../services/AdminService'; 
import { useNavigate } from 'react-router-dom'; 

const AdminDashboard = () => {

  const navigate = useNavigate();
  const [stats, setStats] = useState({
    waiting: 0,
    inProgress: 0,
    completed: 0,
    failRate: "%0",
    statusDistribution: [],
    recentActivities: [],
    userWorkload: []
  });

  useEffect(() => {
    fetchDashboardData();
  }, []);

  
  const fetchDashboardData = async () => {
    try {
      const response = await getSummary();
      const data = response.data;

      const formattedStatus = Object.entries(data.dailyTrendAnalysis).map(([key, value]) => ({
        name: key,
        value: value
      }));

      const formattedWorkload = Object.entries(data.userWorkloadDistribution).map(([key, value]) => ({
        name: key,
        value: value
      }));

    
      setStats({
        waiting: data.totalUsers,          // Toplam Kullanıcı
        inProgress: data.activeSessions,   // Aktif Oturumlar
        completed: data.averageResolveTime, // Ortalama Çözüm Süresi
        failRate: data.failRate,      // Hatalı İşlem Oranı
        statusDistribution: formattedStatus,
        userWorkload: formattedWorkload,
        
        recentActivities: data.recentSecurityActivities.map(log => ({
          id: log.id,
          user: log.performedBy,
          action: log.operation,
          // Zaman formatını okunur hale getirmek için
          time: new Date(log.createdAtDate).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          status: log.status
        }))
      });
    } catch (error) {
      console.error("Dashboard verileri yüklenirken hata oluştu:", error);
    }
  };

  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8'];

  const CHART_COLORS = ['#0052CC','#4C9AFF', '#e0ddf0', '#00B8D9', '#0747A6'  ];

  const STATUS_COLORS = {
    'DONE': '#00C49F',        
    'IN_PROGRESS': '#0088FE', 
    'OPEN': '#FFBB28',    
    'REOPENED': '#FF8042'         
  };

  return (
    <div className="flex-1 flex flex-col min-w-0 h-full overflow-hidden bg-white p-6 space-y-6">

      <div className="shrink-0">
        <h1 className="text-2xl font-semibold text-[#172B4D]">Kullanıcı & Sistem Odağı</h1>
        <p className="text-sm text-gray-500">Sistemdeki güncel duruma genel bir bakış.</p>
      </div>

      {/* Özet İstatistik Kartları */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 shrink-0">
        <StatCard title="Toplam Kullanıcı" count={stats.waiting} border="border-gray-100" textColor="text-gray-800" />
        <StatCard title="Aktif Oturumlar" count={stats.inProgress} border="border-gray-100 border-l-4 border-l-blue-500" textColor="text-gray-800" />
        <StatCard title="Ortalama Çözüm Süresi" count={`${stats.completed} sa`} border="border-gray-100 border-l-4 border-l-green-500" textColor="text-gray-800" />
        <StatCard title="Hatalı İşlem Oranı" count={`%${stats.failRate}`} border="border-gray-100 border-l-4 border-l-red-500" textColor="text-gray-800" />
      </div>


      <div className="shrink-0 mt-5">
        <h1 className="text-2xl font-semibold text-[#172B4D]">Derinlemesine İş Dağılımı ve Analiz</h1>
      </div>


      {/* Operasyonel Görünüm */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 min-h-[350px]">
        {/* İş Yükü Dağılımı */}
        <div className="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm flex flex-col min-h-0">
          <h3 className="text-lg font-bold text-gray-800 mb-4 shrink-0 font-black tracking-tight">Kullanıcı Bazlı Aktif İş Dağılımı</h3>
          <div className="flex-1 w-full min-h-0">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={stats.userWorkload}
                  cx="50%"
                  cy="50%"
                  innerRadius={60}
                  outerRadius={80}
                  paddingAngle={5}
                  dataKey="value"
                >
                  {stats.userWorkload.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={CHART_COLORS[index % CHART_COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip
                  contentStyle={{ borderRadius: '16px', border: 'none', boxShadow: '0 10px 15px -3px rgb(0 0 0 / 0.1)' }}
                />
                <Legend verticalAlign="bottom" height={36} iconType="circle" wrapperStyle={{ fontSize: '11px', fontWeight: 'bold' }} />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm flex flex-col min-h-0">
          <h3 className="text-lg font-bold text-gray-800 mb-4 shrink-0">Zaman Odaklı Aktivite Grafiği (Günlük)</h3>

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
                    <Cell key={`cell-${index}`} fill={STATUS_COLORS[entry.name] || '#8884d8'}  />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Son Aktiviteler Listesi  */}
        <div className="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm flex flex-col min-h-0">
          <div className="flex items-center justify-between mb-4 shrink-0">
            <h3 className="text-lg font-bold text-gray-800 tracking-tight">Son Güvenlik Aktiviteleri</h3>
            <span className="text-[10px] bg-blue-50 text-blue-600 px-2 py-1 rounded-full font-bold uppercase tracking-widest">Canlı</span>
          </div>

          <div className="flex-1 overflow-y-auto pr-2 custom-scrollbar space-y-3">
            {stats.recentActivities.map(activity => (
              <div key={activity.id} className="flex items-center justify-between p-4 hover:bg-gray-50 rounded-2xl border border-transparent hover:border-gray-100 transition-all group cursor-default">
                <div className="flex items-center space-x-4">
                  <div className="w-10 h-10 rounded-full bg-gray-100 text-[#0747A6] flex items-center justify-center font-bold text-xs group-hover:bg-white transition-colors">
                    {activity.user.substring(0, 1)}
                  </div>
                  <div>
                    <h4 className="text-sm font-bold text-gray-700 leading-tight truncate max-w-[150px]">{activity.action}</h4>
                    <p className="text-[11px] text-gray-400 mt-1 font-medium">{activity.user} • {activity.time}</p>
                  </div>
                </div>
                <div className="w-2 h-2 rounded-full bg-blue-400 animate-pulse"></div>
              </div>
            ))}
          </div>
          <button 
          onClick={() => navigate('/admin/logs')}
          className="w-full mt-4 py-3 text-[11px] font-black text-[#0747A6] bg-blue-50 hover:bg-blue-100 rounded-xl transition-all uppercase tracking-widest">
            Tüm Logları Gör
          </button>
        </div>

      </div>

    </div>
  );
};

const StatCard = ({ title, count, border, textColor }) => (
  <div className={`bg-white p-4 rounded-xl shadow-sm border ${border}`}>
    <p className={`text-[10px] font-black uppercase tracking-widest ${textColor === 'text-red-500' ? 'text-red-500' : 'text-gray-400'}`}>
      {title}
    </p>
    <p className={`text-2xl font-bold mt-1 ${textColor}`}>
      {count}
    </p>
  </div>
);

export default AdminDashboard;
