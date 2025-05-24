
import React from 'react';
import { Header } from '@/components/layout/Header';
import { TeachersList } from '@/components/teachers/TeachersList';
import  TeacherDetail  from '@/components/teachers/TeacherDetail';
import { Routes, Route } from 'react-router-dom';

const Teachers = () => {
  return (
    <>
      <Routes>
        <Route 
          index
          element={
            <>
              <Header title="Teachers" />
              <div className="p-6">
                <TeachersList />
              </div>
            </>
          } 
        />
              </Routes>
    </>
  );
};

export default Teachers;
