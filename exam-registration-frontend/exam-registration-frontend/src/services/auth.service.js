// src/services/auth.service.js
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth/';

const register = (username, email, password, role) => {
  return axios.post(API_URL + 'register', {
    username,
    email,
    password,
    role, // Assuming backend expects a single role string like "ADMIN" or "STUDENT" during registration
  })
  .then(response => {
    // Backend might return role as a string for registration too.
    // Ensure consistency by always storing roles as an array with 'ROLE_' prefix if needed.
    const userToStore = {
      ...response.data,
      // If backend sends 'role' as a string, convert it to 'roles' array with 'ROLE_' prefix
      roles: response.data.role ? [`ROLE_${response.data.role.toUpperCase()}`] : []
    };
    // No token on registration usually, but if it does, store it.
    if (userToStore.token) { // Check if a token is present (unlikely for pure registration)
        localStorage.setItem('user', JSON.stringify(userToStore));
    }
    return response.data;
  });
};

const login = (username, password) => {
  return axios.post(API_URL + 'login', {
    username,
    password,
  })
  .then((response) => {
    if (response.data.token) {
      const backendRole = response.data.role; // Get the role string from backend
      const userToStore = {
        ...response.data,
        // Convert the single role string into a 'roles' array with 'ROLE_' prefix
        roles: backendRole ? [`ROLE_${backendRole.toUpperCase()}`] : [],
      };
      // Important: Remove the original single 'role' property if you only want 'roles' array
      delete userToStore.role; // Clean up the object before storing

      localStorage.setItem('user', JSON.stringify(userToStore));
    }
    return response.data; // Return original response if needed, or userToStore
  });
};

const logout = () => {
  localStorage.removeItem('user');
};

const getCurrentUser = () => {
  return JSON.parse(localStorage.getItem('user'));
};

const AuthService = {
  register,
  login,
  logout,
  getCurrentUser,
};

export default AuthService;