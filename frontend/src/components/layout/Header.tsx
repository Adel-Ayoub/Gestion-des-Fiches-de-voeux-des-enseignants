
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Bell, Search,User } from 'lucide-react';
import { MobileSidebarTrigger } from './Sidebar';
import { Input } from '@/components/ui/input';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { 
  DropdownMenu, 
  DropdownMenuContent, 
  DropdownMenuItem, 
  DropdownMenuLabel, 
  DropdownMenuSeparator, 
  DropdownMenuTrigger 
} from '@/components/ui/dropdown-menu';
import { Link } from 'react-router-dom';
import {useEffect,useState} from 'react';
import {jwtDecode} from 'jwt-decode';
import axios from 'axios';
export const Header = ({ title,showsidebar=true,showsearch=true }: { title: string,showsidebar?:boolean,showsearch?:boolean }) => {
  const navigate = useNavigate();
  const [userName, setUserName] = useState<string>("");
  const [isTeacher, setIsTeacher] = useState(false);
  useEffect(()=>{
      const token= localStorage.getItem('jwt');
      const decoded= jwtDecode(token);
      const user = axios.get('http://localhost:8080/api/users/by-email', {headers: {
        'Authorization': token,
      },params: {email: decoded.sub}}).then((response) => { setUserName(response.data.name); });
      if (decoded.roles === 'ROLE_TEACHER') {
        setIsTeacher(true);
      }
}, []);
  const handleLogout = () => {
    localStorage.removeItem('jwt'); // Remove the token from local storage
    navigate('/login'); // Redirect to the login page
  };
  return (
    <header className="bg-white border-b p-4 flex justify-between items-center">
      <div className="flex items-center gap-3">
        {showsidebar && 
        <div className="lg:hidden">
          <MobileSidebarTrigger  />
        </div>}
        <h1 className="text-xl font-semibold">{title}</h1>
      </div>
      
      <div className="flex items-center gap-4">
        { showsearch &&
        <div className="hidden md:block relative w-64">
          
          <Search className="absolute left-2 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
          <Input 
            placeholder="Search..." 
            className="pl-8 bg-gray-50 border-gray-200 focus:bg-white"
          />
        </div>}
        
                
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <div className="flex items-center gap-2 cursor-pointer">
              <Avatar className="h-8 w-8">
                <User className="w-6 h-6 text-gray-500" />
              </Avatar>
              <span className="hidden md:inline text-sm font-medium">{userName}</span>
            </div>
          </DropdownMenuTrigger>
          {isTeacher &&(
          <DropdownMenuContent align="end">
            <DropdownMenuLabel>My Account</DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuItem><Link to="/profile">Profile</Link></DropdownMenuItem>
            
            <DropdownMenuSeparator />
            <DropdownMenuItem><button onClick={handleLogout}>Logout</button></DropdownMenuItem>
          </DropdownMenuContent>)}
        </DropdownMenu>
      </div>
    </header>
  );
};
