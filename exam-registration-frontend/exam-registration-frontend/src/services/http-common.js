// src/services/http-common.js
import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080/api/', // Your backend API base URL
  headers: {
    'Content-type': 'application/json',
  },
});

// Request interceptor to add the JWT token to every outgoing request
instance.interceptors.request.use(
  (config) => {
    const user = JSON.parse(localStorage.getItem('user'));
    if (user && user.token) {
      config.headers.Authorization = 'Bearer ' + user.token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default instance;