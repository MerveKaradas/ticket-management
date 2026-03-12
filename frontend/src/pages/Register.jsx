import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../services/Api';
import { toast } from 'react-toastify';


const Register = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: '',
    surname: '',
    email: '',
    password: '',
    confirmPassword: ''
  });

  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (formData.name.length < 2 || formData.name.length > 50) {
      toast.error('İsim 2-50 karakter arasında olmalıdır.');
      return;
    }

    if (formData.surname.length < 2 || formData.surname.length > 50) {
      toast.error('Soyad 2-50 karakter arasında olmalıdır.');
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      toast.error('Geçerli bir email giriniz.');
      return;
    }

    if (formData.password.length > 100 || formData.password.length < 8) {
      toast.error('Parola 8-100 karakter arasında olmalıdır!.');
      return;
    }

    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z\d]).{8,}$/;
    if (!passwordRegex.test(formData.password)) {
      const msg = "Parola en az bir büyük harf, bir küçük harf, bir sayı ve bir özel karakter içermeli";
      toast.error(msg);
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      toast.error('Parolalar birbiriyle eşleşmiyor!');
      return;
    }

    try {
      const response = await api.post('/users/createUser', {
        name: formData.name,
        surname: formData.surname,
        email: formData.email,
        password: formData.password

      });

      if (response.status === 200 || response.status === 201) {
        toast.success('Kayıt başarıyla oluşturuldu! 🚀', {
          position: "bottom-right",
          autoClose: 3000,
        });

        navigate('/login');

      }

    } catch (err) {

      const messages = err.response?.data?.message;
      let toastMessage = "";

      if (messages && typeof messages === "object") {
        toastMessage = Object.values(messages).join(" | ");
      } else {
        toastMessage = messages || "Kayıt başarısız.";
      }

      toast.error(toastMessage);
    }

  };

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
      <div className="max-w-md w-full bg-white rounded-2xl shadow-xl p-8">
        <div className="text-center mb-8">
          <h2 className="text-3xl font-bold text-gray-800">Yeni Hesap Oluştur</h2>
          <p className="text-gray-500 mt-2">Ticket Management sistemine katıl.</p>
        </div>

        {error && (
          <div className="bg-red-50 border-l-4 border-red-500 text-red-700 p-3 mb-6 rounded">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Ad</label>
            <input
              type="text"
              required
              className="w-full px-4 py-2 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition"
              placeholder="Merve"
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Soyad</label>
            <input
              type="text"
              required
              className="w-full px-4 py-2 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition"
              placeholder="Karadaş"
              onChange={(e) => setFormData({ ...formData, surname: e.target.value })}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">E-posta</label>
            <input
              type="email"
              required
              className="w-full px-4 py-2 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition"
              placeholder="merve@example.com"
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Şifre</label>
            <input
              type="password"
              required
              className="w-full px-4 py-2 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition"
              placeholder="••••••••"
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Şifre Tekrar</label>
            <input
              type="password"
              required
              className="w-full px-4 py-2 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition"
              placeholder="••••••••"
              onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
            />
          </div>

          <button
            type="submit"
            className="w-full bg-blue-600 text-white py-3 rounded-xl font-semibold hover:bg-blue-700 shadow-lg shadow-blue-200 transition duration-300 mt-4"
          >
            Kayıt Ol
          </button>
        </form>

        <p className="text-center mt-6 text-gray-600">
          Zaten hesabın var mı?{' '}
          <Link to="/login" className="text-blue-600 font-bold hover:underline">
            Giriş Yap
          </Link>
        </p>
      </div>
    </div>
  );
};

export default Register;