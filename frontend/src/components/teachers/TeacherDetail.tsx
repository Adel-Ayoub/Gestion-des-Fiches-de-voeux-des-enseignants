
import React from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, Mail, Phone, Calendar, Check, X, Edit, Trash2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import {useState} from 'react';
import {useLocation} from 'react-router-dom';
import { useEffect } from 'react';
import {jwtDecode} from 'jwt-decode';
import axios from 'axios';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { useToast } from '@/components/ui/use-toast';
import { FileText, User } from 'lucide-react';

interface Teacher {
  id?: number;
  userId: number;
  name?: string;
  grade: string;
  departmentName: string;
  officeNumber: string;
  email?: string;
  emailPersonel?:string;
  role?: string;
  grade?: string;
  }

const TeacherDetail = () => {
  const { id } = useParams();
  const [user, setUser] = useState<Teacher>(null);
   const [loading, setLoading] = useState(true);
   useEffect(() => {
    try{
      const token= localStorage.getItem('jwt');
      const {exp} =jwtDecode< {exp: number}>(token);
      const currentTime = Date.now() / 1000;
      if (!token || exp < currentTime) {
        navigate('/login');
        return;
      }}catch (error) {
      console.error("Error decoding token:", error);
    }

    const fetchTeachers = async () => {
    try{
      const token= localStorage.getItem('jwt');
      const response= await axios.get<Teacher>('http://localhost:8080/api/teachers/'+id,{ headers:{Authorization: token}});
      if (response.status ==401){
          navigate('/login');
      };
      console.log(response.data);
      const userres=await axios.get('http://localhost:8080/api/users/'+response.data.userId,{headers:{Authorization: token}});
      console.log("after get user request");
      setUser({...response.data, email: userres.data.email,name:userres.data.name,role:userres.data.role, emailPersonel:userres.data.emailPersonel});
      console.log(user);
    }catch (error) {
      console.log("error fetching for teachers");
      console.error(error);
    }
    };
    fetchTeachers();
    console.log(user);
    setLoading(false);
},[]);
// Check if teacher has submitted wishes
  const handleProfileUpdate = async (e) => {
    e.preventDefault();
    const res= await axios.put("http://localhost:8080/api/users/"+user.id,  { "id":user.id,"email":user.email,"name":user.name, role:"TEACHER" },{ headers :{ Authorization: localStorage.getItem("jwt")} });
    const response = await axios.put("http://localhost:8080/api/teachers/"+user.id, {"grade":user.grade,"officeNumber":user.officeNumber,"departmentName":user.departmentName}, { headers:{ Authorization: localStorage.getItem("jwt")} });
    console.log(res.data);
    
};

  return (<>
    {loading ? (<div>Loading...</div>) : (
    <div className="container mx-auto p-6">
        <Tabs defaultValue="profile" className="space-y-4">
          <TabsList className="grid w-full grid-cols-4">
            <TabsTrigger value="profile" className="flex items-center gap-2">
              <User className="w-4 h-4" />
              Informations
            </TabsTrigger>
            <TabsTrigger value="forms" className="flex items-center gap-2">
              <FileText className="w-4 h-4" />
              Messages
            </TabsTrigger>
            
          </TabsList>

          <TabsContent value="profile">
            <Card>
              <CardHeader>
                <CardTitle>Informations du Profil</CardTitle>
              </CardHeader>
              <CardContent>
                <form  className="space-y-4">
                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="fullName">Nom Complet</Label>
                      <Input id="name" value={user?.name} onChange={(e) => setUser({ ...user, name: e.target.value })} />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="email">Email</Label>
                      <Input id="email" type="email" value={user?.email} onChange={(e) => setUser({ ...user, email: e.target.value })} />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="grade">Grade</Label>
                      <Input id="grade" value={user?.grade} onChange={(e) => setUser({ ...user, grade: e.target.value })} />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="department">Département</Label>
                      <Input id="department" value={user?.departmentName} onChange={(e) => setUser({ ...user, department: e.target.value })} />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="officeNumber">Bureau</Label>
                      <Input id="officeNumber" value={user?.officeNumber} onChange={(e) => setUser({ ...user, officeNumber: e.target.value })} />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="role">Role</Label>
                      <Input id="role" value={user?.role} onChange={(e) => setUser({ ...user, role: e.target.value })} />
                    </div>
                  </div>
                  
                  <Button onClick={handleProfileUpdate} className="space-y-3">Mettre à jour le profil</Button>
                </form>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
    </div>)};
  </>
  );
};
export default TeacherDetail;
