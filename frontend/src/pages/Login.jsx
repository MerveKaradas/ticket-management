import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom'; 
import api from '../services/Api';

const Login = () => {
    const navigate = useNavigate(); 
    const [loading, setLoading] = useState(false); 
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const response = await api.post('/users/login', {
                email: formData.email,
                password: formData.password
            });

            const token = response.data.accessToken;
            const refreshToken = response.data.refreshToken;

            if (token) {
                console.log("Giriş başarılı!");
          
                localStorage.setItem('token', token);
                localStorage.setItem('refreshToken', refreshToken);

              
                navigate('/dashboard');
            }
        } catch (err) {
            console.error("Giriş hatası:", err);
            alert("Giriş başarısız! E-posta veya şifre hatalı olabilir.");
        } finally {
            setLoading(false); 
        }

    };

    return (
        <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
            <div className="max-w-md w-full bg-white rounded-2xl shadow-xl p-8">
                <div className="text-center mb-10">
                    <h2 className="text-3xl font-bold text-gray-800">Hoş Geldin!</h2>
                    <p className="text-gray-500 mt-2">Biletlerini yönetmek için giriş yap.</p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-6">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">E-posta</label>
                        <input
                            type="email"
                            required
                            className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
                            placeholder="merve@example.com"
                            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Şifre</label>
                        <input
                            type="password"
                            required
                            className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
                            placeholder="••••••••"
                            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className={`w-full py-3 rounded-xl font-semibold shadow-lg transition duration-300 ${loading
                                ? 'bg-blue-400 cursor-not-allowed opacity-70 shadow-none'
                                : 'bg-blue-600 text-white hover:bg-blue-700 shadow-blue-200'
                            }`}
                    >
                        {loading ? (
                            <div className="flex items-center justify-center space-x-2">
                                <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                                <span>Giriş Yapılıyor...</span>
                            </div>
                        ) : (
                            'Giriş Yap'
                        )}
                    </button>
                </form>

                <p className="text-center mt-8 text-gray-600">
                    Hesabın yok mu?{' '}
                    <Link to="/register" className="text-blue-600 font-bold hover:underline">
                        Kayıt Ol
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default Login;