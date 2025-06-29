// src/components/Register.jsx
import React, { useState } from 'react';
import AuthService from '../services/auth.service';

const Register = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('STUDENT'); // Default role
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [successful, setSuccessful] = useState(false);

  const handleRegister = (e) => {
    e.preventDefault();
    setMessage('');
    setLoading(true);
    setSuccessful(false);

    AuthService.register(username, email, password, role)
      .then(
        (response) => {
          setMessage(response.data.message || 'Registration successful!');
          setSuccessful(true);
          setLoading(false);
          // Optionally clear form
          setUsername('');
          setEmail('');
          setPassword('');
        },
        (error) => {
          const resMessage =
            (error.response &&
              error.response.data &&
              error.response.data.message) ||
            error.message ||
            error.toString();

          setMessage(resMessage);
          setSuccessful(false);
          setLoading(false);
          console.error('Registration error:', error.response || error);
        }
      );
  };

  return (
    <div style={{ maxWidth: '400px', margin: '50px auto', padding: '20px', border: '1px solid #ccc', borderRadius: '8px', boxShadow: '2px 2px 10px rgba(0,0,0,0.1)' }}>
      <h2>Register</h2>
      <form onSubmit={handleRegister}>
        <div>
          <label htmlFor="username">Username:</label>
          <input
            type="text"
            id="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            style={{ width: '100%', padding: '8px', margin: '5px 0', borderRadius: '4px', border: '1px solid #ddd' }}
          />
        </div>
        <div>
          <label htmlFor="email">Email:</label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            style={{ width: '100%', padding: '8px', margin: '5px 0', borderRadius: '4px', border: '1px solid #ddd' }}
          />
        </div>
        <div>
          <label htmlFor="password">Password:</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            style={{ width: '100%', padding: '8px', margin: '5px 0', borderRadius: '4px', border: '1px solid #ddd' }}
          />
        </div>
        <div>
          <label htmlFor="role">Role:</label>
          <select
            id="role"
            value={role}
            onChange={(e) => setRole(e.target.value)}
            style={{ width: '100%', padding: '8px', margin: '5px 0', borderRadius: '4px', border: '1px solid #ddd' }}
          >
            <option value="STUDENT">STUDENT</option>
            <option value="ADMIN">ADMIN</option>
          </select>
        </div>
        <button
          type="submit"
          disabled={loading}
          style={{
            width: '100%',
            padding: '10px',
            backgroundColor: '#28a745', // Green for register
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            fontSize: '16px',
            marginTop: '10px'
          }}
        >
          {loading ? 'Registering...' : 'Register'}
        </button>
      </form>
      {message && (
        <div style={{
          marginTop: '15px',
          padding: '10px',
          backgroundColor: successful ? '#d4edda' : '#f8d7da',
          color: successful ? '#155724' : '#721c24',
          border: successful ? '1px solid #c3e6cb' : '1px solid #f5c6cb',
          borderRadius: '4px'
        }}>
          {message}
        </div>
      )}
    </div>
  );
};

export default Register;