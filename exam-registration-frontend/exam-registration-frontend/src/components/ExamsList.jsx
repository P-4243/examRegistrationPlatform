// src/components/ExamsList.jsx
import React, { useState, useEffect } from 'react';
import ExamService from "../services/exam-service";
import AuthService from '../services/auth.service'; // To get current user for role check
import { Link } from 'react-router-dom'; // For future navigation to individual exam details or creation page

const ExamsList = () => {
  const [exams, setExams] = useState([]);
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(true); // Added loading state
  const currentUser = AuthService.getCurrentUser(); // Get current user
  const isAdmin = currentUser && currentUser.roles && currentUser.roles.includes('ROLE_ADMIN');

  useEffect(() => {
    // Fetch exams when the component mounts
    const fetchExams = async () => {
      try {
        const response = await ExamService.getAllExams(); // Use the service method
        setExams(response.data); // Update state with fetched exams
        setLoading(false); // Set loading to false after data is fetched
      } catch (error) {
        const resMessage =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();
        setMessage('Error fetching exams: ' + resMessage);
        setLoading(false); // Set loading to false even if there's an error
        console.error('Error fetching exams:', error.response || error);
      }
    };

    fetchExams();
  }, []); // Empty dependency array means this runs once on mount

  const handleDelete = async (examId) => {
    if (window.confirm('Are you sure you want to delete this exam?')) {
      try {
        await ExamService.deleteExam(examId);
        setMessage('Exam deleted successfully!');
        // Remove the deleted exam from the state
        setExams(exams.filter(exam => exam.id !== examId));
      } catch (error) {
        const resMessage =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();
        setMessage('Error deleting exam: ' + resMessage);
        console.error('Error deleting exam:', error.response || error);
      }
    }
  };


  if (loading) {
    return <div>Loading exams...</div>;
  }

  return (
    <div style={{ padding: '20px' }}>
      <h2>Available Exams</h2>

      {isAdmin && (
        <div style={{ marginBottom: '20px' }}>
          <Link to="/exams/create" style={{ padding: '10px 20px', backgroundColor: 'white', color: 'black', textDecoration: 'none', borderRadius: '5px' }}>
            Add New Exam
          </Link>
        </div>
      )}

      {message && <div style={{ color: message.startsWith('Error') ? 'red' : 'green', marginBottom: '15px' }}>{message}</div>}

      {exams.length === 0 ? (
        <p>No exams available at the moment.</p>
      ) : (
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {exams.map((exam) => (
            <li key={exam.id} style={{
              border: '1px solid #eee',
              borderRadius: '8px',
              padding: '15px',
              marginBottom: '10px',
              backgroundColor: 'black',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center'
            }}>
              <div>
                <h3>{exam.title}</h3>
                <p><strong>Date:</strong> {new Date(exam.examDate).toLocaleString()}</p>
                <p><strong>Duration:</strong> {exam.durationMinutes} minutes</p>
                <p><strong>Deadline:</strong> {new Date(exam.registrationDeadline).toLocaleString()}</p>
                <p><strong>Active:</strong> {exam.active ? 'Yes' : 'No'}</p>
                {/* You might want to display more details here */}
              </div>
              {isAdmin && (
                <div style={{ marginLeft: '20px' }}>
                  <Link to={`/exams/edit/${exam.id}`} style={{ marginRight: '10px', padding: '8px 12px', backgroundColor: '#ffc107', color: 'black', textDecoration: 'none', borderRadius: '4px' }}>
                    Edit
                  </Link>
                  <button onClick={() => handleDelete(exam.id)} style={{ padding: '8px 12px', backgroundColor: '#dc3545', color: 'black', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                    Delete
                  </button>
                </div>
              )}
              {/* Add Registration Button for Students later */}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default ExamsList;