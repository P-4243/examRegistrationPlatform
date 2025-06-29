// src/services/exam.service.js
import http from './http-common'; // Import our Axios instance with JWT interceptor

const getAllExams = () => {
  return http.get('/exams'); // Calls http://localhost:8080/api/exams
};

const getExamById = (id) => {
  return http.get(`/exams/${id}`); // Calls http://localhost:8080/api/exams/{id}
};

const createExam = (examData) => {
  return http.post('/exams', examData); // Calls http://localhost:8080/api/exams
};

const updateExam = (id, examData) => {
  return http.put(`/exams/${id}`, examData); // Calls http://localhost:8080/api/exams/{id}
};

const deleteExam = (id) => {
  return http.delete(`/exams/${id}`); // Calls http://localhost:8080/api/exams/{id}
};

// You can add registration related functions here later, or create a separate registration.service.js
// const registerForExam = (examId, userId) => { ... } etc.

const ExamService = {
  getAllExams,
  getExamById,
  createExam,
  updateExam,
  deleteExam,
};

export default ExamService;
