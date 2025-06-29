// src/components/ExamForm.jsx
import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import ExamService from "../services/exam-service";

const ExamForm = () => {
  const navigate = useNavigate();
  const { id } = useParams();

  // State for form fields (existing)
  const [title, setTitle] = useState('');
  const [examDate, setExamDate] = useState('');
  const [durationInMinutes, setDurationInMinutes] = useState(''); // Added 'In'
  const [registrationDeadline, setRegistrationDeadline] = useState('');
  const [active, setActive] = useState(true);

  // State for new fields (ADDED)
  const [description, setDescription] = useState('');
  const [maxMarks, setMaxMarks] = useState('');
  const [passingMarks, setPassingMarks] = useState('');
  const [courseName, setCourseName] = useState('');
  const [createdBy, setCreatedBy] = useState(''); // This might be auto-set by backend from JWT
  const [maxCapacity, setMaxCapacity] = useState('');
  const [name, setName] = useState(''); // 'name' seems like an overall exam name

  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);

  useEffect(() => {
    if (id) {
      setIsEditMode(true);
      setLoading(true);
      ExamService.getExamById(id)
        .then(response => {
          const exam = response.data;
          setTitle(exam.title);
          setDescription(exam.description || ''); // Handle potentially null description
          setExamDate(new Date(exam.examDate).toISOString().slice(0, 16));
          setDurationInMinutes(exam.durationInMinutes);
          setMaxMarks(exam.maxMarks); // Load existing value
          setPassingMarks(exam.passingMarks); // Load existing value
          setRegistrationDeadline(new Date(exam.registrationDeadline).toISOString().slice(0, 16));
          setActive(exam.active);
          setCourseName(exam.courseName || ''); // Load existing value
          setCreatedBy(exam.createdBy || ''); // Load existing value
          setMaxCapacity(exam.maxCapacity); // Load existing value
          setName(exam.name || ''); // Load existing value
          setLoading(false);
        })
        .catch(error => {
          const resMessage =
            (error.response && error.response.data && error.response.data.message) ||
            error.message ||
            error.toString();
          setMessage('Error loading exam for edit: ' + resMessage);
          setLoading(false);
          console.error('Error loading exam for edit:', error);
        });
    }
  }, [id]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    setLoading(true);

    // Basic validation - ADDED CHECKS FOR NEW FIELDS
    if (!title || !examDate || !durationInMinutes || !registrationDeadline ||
        !description || !maxMarks || !passingMarks || !courseName ||
        !createdBy || !maxCapacity || !name) {
      setMessage('All fields are required. Please fill in all details.');
      setLoading(false);
      return;
    }

    const examData = {
      title,
      description, // ADDED
      examDate: new Date(examDate).toISOString(),
      durationInMinutes: parseInt(durationInMinutes),
      maxMarks: parseInt(maxMarks), // ADDED, ensure parsed as int
      passingMarks: parseInt(passingMarks), // ADDED, ensure parsed as int
      active,
      courseName, // ADDED
      createdBy, // ADDED
      maxCapacity: parseInt(maxCapacity), // ADDED, ensure parsed as int
      name, // ADDED
      registrationDeadline: new Date(registrationDeadline).toISOString(),
    };

    try {
      if (isEditMode) {
        await ExamService.updateExam(id, examData);
        setMessage('Exam updated successfully!');
      } else {
        await ExamService.createExam(examData);
        setMessage('Exam created successfully!');
        // Clear form after successful creation
        setTitle('');
        setDescription('');
        setExamDate('');
        setDurationInMinutes('');
        setMaxMarks('');
        setPassingMarks('');
        setRegistrationDeadline('');
        setActive(true);
        setCourseName('');
        setCreatedBy('');
        setMaxCapacity('');
        setName('');
      }
      setLoading(false);
      navigate('/exams'); // Navigate back to exams list after success
    } catch (error) {
      const resMessage =
        (error.response && error.response.data && error.response.data.message) ||
        error.message ||
        error.toString();
      setMessage('Error: ' + resMessage);
      setLoading(false);
      console.error('Error submitting exam:', error.response || error);
    }
  };

  if (loading && isEditMode) {
    return <div>Loading exam details...</div>;
  }

  return (
    <div style={{ maxWidth: '600px', margin: '50px auto', padding: '20px', border: '1px solid #ccc', borderRadius: '8px', boxShadow: '2px 2px 10px rgba(0,0,0,0.1)' }}>
      <h2>{isEditMode ? 'Edit Exam' : 'Create New Exam'}</h2>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="name" style={{ display: 'block', marginBottom: '5px' }}>Exam Name:</label>
          <input
            type="text"
            id="name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
          />
        </div>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="title" style={{ display: 'block', marginBottom: '5px' }}>Title (e.g., Mathematics):</label>
          <input
            type="text"
            id="title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
          />
        </div>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="description" style={{ display: 'block', marginBottom: '5px' }}>Description:</label>
          <textarea
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            required
            rows="3"
            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
          ></textarea>
        </div>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="examDate" style={{ display: 'block', marginBottom: '5px' }}>Exam Date & Time:</label>
          <input
            type="datetime-local"
            id="examDate"
            value={examDate}
            onChange={(e) => setExamDate(e.target.value)}
            required
            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
          />
        </div>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="durationInMinutes" style={{ display: 'block', marginBottom: '5px' }}>Duration (minutes):</label>
          <input
            type="number"
            id="durationInMinutes"
            value={durationInMinutes}
            onChange={(e) => setDurationInMinutes(e.target.value)}
            required
            min="1"
            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
          />
        </div>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="maxMarks" style={{ display: 'block', marginBottom: '5px' }}>Max Marks:</label>
          <input
            type="number"
            id="maxMarks"
            value={maxMarks}
            onChange={(e) => setMaxMarks(e.target.value)}
            required
            min="1"
            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
          />
        </div>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="passingMarks" style={{ display: 'block', marginBottom: '5px' }}>Passing Marks:</label>
          <input
            type="number"
            id="passingMarks"
            value={passingMarks}
            onChange={(e) => setPassingMarks(e.target.value)}
            required
            min="0"
            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
          />
        </div>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="courseName" style={{ display: 'block', marginBottom: '5px' }}>Course Name:</label>
          <input
            type="text"
            id="courseName"
            value={courseName}
            onChange={(e) => setCourseName(e.target.value)}
            required
            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
          />
        </div>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="createdBy" style={{ display: 'block', marginBottom: '5px' }}>Created By:</label>
          <input
            type="text"
            id="createdBy"
            value={createdBy}
            onChange={(e) => setCreatedBy(e.target.value)}
            required
            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
          />
        </div>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="maxCapacity" style={{ display: 'block', marginBottom: '5px' }}>Max Capacity:</label>
          <input
            type="number"
            id="maxCapacity"
            value={maxCapacity}
            onChange={(e) => setMaxCapacity(e.target.value)}
            required
            min="1"
            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
          />
        </div>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="registrationDeadline" style={{ display: 'block', marginBottom: '5px' }}>Registration Deadline:</label>
          <input
            type="datetime-local"
            id="registrationDeadline"
            value={registrationDeadline}
            onChange={(e) => setRegistrationDeadline(e.target.value)}
            required
            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
          />
        </div>
        <div style={{ marginBottom: '15px', display: 'flex', alignItems: 'center' }}>
          <input
            type="checkbox"
            id="active"
            checked={active}
            onChange={(e) => setActive(e.target.checked)}
            style={{ marginRight: '10px' }}
          />
          <label htmlFor="active">Active</label>
        </div>
        <button
          type="submit"
          disabled={loading}
          style={{
            width: '100%',
            padding: '10px',
            backgroundColor: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            fontSize: '16px',
            marginTop: '10px'
          }}
        >
          {loading ? (isEditMode ? 'Updating...' : 'Creating...') : (isEditMode ? 'Update Exam' : 'Create Exam')}
        </button>
        <button
          type="button"
          onClick={() => navigate('/exams')}
          style={{
            width: '100%',
            padding: '10px',
            backgroundColor: '#6c757d', // Grey for cancel
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            fontSize: '16px',
            marginTop: '10px'
          }}
        >
          Cancel
        </button>
      </form>
      {message && (
        <div style={{ marginTop: '15px', padding: '10px', backgroundColor: message.startsWith('Error') ? '#f8d7da' : '#d4edda', color: message.startsWith('Error') ? '#721c24' : '#155724', border: '1px solid #f5c6cb', borderRadius: '4px' }}>
          {message}
        </div>
      )}
    </div>
  );
};

export default ExamForm;