
import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { CoursesList } from '@/components/courses/CoursesList';
import  Mailbox  from '@/components/Mailbox';
import {useEffect} from 'react';
import axios from 'axios';
import {jwtDecode} from 'jwt-decode';
import { useState } from 'react';
type user = {
  id:number;
  name:string;
  email:string;
  role:string;
}
const Courses = () => {
const [user, setUser] = useState<teacher>("");
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    // Simulate fetching user data from an API
    
    const fetchUserData = async () => {
      const token = localStorage.getItem("jwt");
      const decodedToken = jwtDecode(token);
      console.log(decodedToken);
      const userdto = await axios.get("http://localhost:8080/api/users/by-email" ,{headers: { Authorization: token}, params: { email: decodedToken.sub }});
      console.log(userdto.data);
           setUser({
        id: userdto.data.id,
        name: userdto.data.name,
        email: userdto.data.email,
        role:userdto.data.role,      });    
};

    fetchUserData();
    setLoading(false);
  }, [user.id]);
    
 
  return (<>
{loading ? <div>Loading...</div> :
    <Mailbox userId={user.id}/>}</>
  );
};

export default Courses;
