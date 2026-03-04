import { Link } from 'react-router-dom';

const Home = () => {
  return (
   <div className="min-h-screen bg-gray-50 flex flex-col">

      <nav className="bg-white shadow-sm px-6 py-4 flex justify-between items-center">
        <h1 className="text-2xl font-bold text-blue-600">TicketMaster</h1>
        <div className="space-x-4">
          <Link to="/login">Giriş Yap</Link>
          <Link to="/register">
            Kayıt Ol
          </Link>
        </div>
      </nav>

      <main className="flex-grow flex items-center justify-center px-4">
        <div className="text-center max-w-3xl">
          <h2 className="text-5xl font-extrabold text-gray-900 mb-6">
            Destek Taleplerinizi <span className="text-blue-600">Akıllıca</span> Yönetin
          </h2>
          <p className="text-xl text-gray-600 mb-10 leading-relaxed">
            Müşteri taleplerini takip etmek, önceliklendirmek ve ekibinizle koordineli çalışmak için en modern çözüm. Backend'i Spring Boot ile güçlendirilmiş güvenli bilet yönetim sistemi.
          </p>
          <div className="flex flex-col sm:flex-row justify-center gap-4">
            <Link to="/register" className="bg-blue-600 text-white px-8 py-4 rounded-xl text-lg font-semibold hover:bg-blue-700 shadow-lg shadow-blue-200">
              Hemen Başlayın
            </Link>
            <Link to="/login" className="bg-white text-blue-600 border-2 border-blue-600 px-8 py-4 rounded-xl text-lg font-semibold hover:bg-blue-50">
              Demo İzle
            </Link>
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="py-6 text-center text-gray-400 text-sm border-t">
        © 2026 Ticket Management Project - Merve Karadaş
      </footer>
    </div>
  );
};

export default Home;
