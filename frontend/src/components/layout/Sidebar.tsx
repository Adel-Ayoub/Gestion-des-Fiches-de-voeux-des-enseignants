
import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { 
  BarChart3, 
  Users, 
  GiftIcon, 
  Settings, 
  Menu,
  LogOut,
  BookOpen
} from 'lucide-react';
import { cn } from '@/lib/utils';
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarTrigger
} from '@/components/ui/sidebar';

export const AppSidebar = () => {
  const location = useLocation();
  const onLogout = () => {
    localStorage.removeItem('jwt'); // Remove the token from local storage
    window.location.href = '/login'; // Redirect to the login page
  };
  const menuItems = [
    { 
      icon: BarChart3, 
      label: 'Dashboard', 
      path: '/admin/dashboard' 
    },
    { 
      icon: Users, 
      label: 'Teachers', 
      path: '/admin/teachers' 
    },
    { 
      icon: GiftIcon, 
      label: 'Wishes', 
      path: '/admin/wishes' 
    },
    { 
      icon: BookOpen, 
      label: 'Courses', 
      path: '/admin/courses' 
    },
    { 
      icon: Settings, 
      label: 'Settings', 
      path: '/admin/settings' 
    },
  ];
  
  return (
    <Sidebar className="border-r border-gray-200">
      <SidebarHeader className="px-3 py-4">
        <div className="flex items-center">
          <div className="w-8 h-8 rounded-full bg-primary flex items-center justify-center text-white font-bold">
            WA
          </div>
          <span className="ml-2 text-lg font-semibold">WishAdmin</span>
        </div>
      </SidebarHeader>
      
      <SidebarContent className="px-3 py-2">
        <nav className="space-y-1">
          {menuItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className={cn(
                "sidebar-item",
                location.pathname === item.path ? "sidebar-item-active" : "text-gray-200 hover:bg-sidebar-accent/50"
              )}
            >
              <item.icon className="w-5 h-5" />
              <span>{item.label}</span>
            </Link>
          ))}
        </nav>
      </SidebarContent>
      
      <SidebarFooter className="px-3 py-4">
        <div className="sidebar-item text-gray-200 hover:bg-sidebar-accent/50 cursor-pointer">
          <LogOut className="w-5 h-5" />
          <span onClick={onLogout} >Logout</span>
        </div>
      </SidebarFooter>
    </Sidebar>
  );
};

export const MobileSidebarTrigger = () => {
  return (
    <SidebarTrigger >
      <Menu className="w-6 h-6" />
    </SidebarTrigger>
  );
};
