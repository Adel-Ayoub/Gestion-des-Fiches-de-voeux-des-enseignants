
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Bell, Search } from 'lucide-react';
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

export const Header = ({ title,showsidebar=true,showsearch=true }: { title: string,showsidebar?:boolean,showsearch?:boolean }) => {
  const navigate = useNavigate();
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
        
        <div className="relative">
          <Bell className="text-gray-500 w-5 h-5 cursor-pointer" />
          <span className="absolute -top-1 -right-1 w-4 h-4 rounded-full bg-red-500 text-white text-xs flex items-center justify-center">3</span>
        </div>
        
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <div className="flex items-center gap-2 cursor-pointer">
              <Avatar className="h-8 w-8">
                <AvatarImage src="/placeholder.svg" alt="Admin User" />
                <AvatarFallback>AU</AvatarFallback>
              </Avatar>
              <span className="hidden md:inline text-sm font-medium">Admin User</span>
            </div>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuLabel>My Account</DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuItem><Link to="/profile">Profile</Link></DropdownMenuItem>
            <DropdownMenuItem>Settings</DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem><button onClick={handleLogout}>Logout</button></DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  );
};
