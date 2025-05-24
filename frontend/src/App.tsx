
import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Index from "./pages/Index";
import Dashboard from "./pages/Dashboard";
import Teachers from "./pages/Teachers";
import Wishes from "./pages/Wishes";
import Courses from "./pages/Courses";
import NotFound from "./pages/NotFound";
import { DashboardLayout } from "./components/layout/Dashboard";
import Form from '@/components/Form';
import ProfilePage from '@/pages/Profile';
import Login from "./pages/Login";
import TeacherDetail from "./components/teachers/TeacherDetail";
import {WishDetail} from "./components/wishes/WishDetail";
const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster />
      <Sonner />
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Index />} />
          
          {/* Dashboard Layout Routes */}
          <Route path="/admin" element={<DashboardLayout />}>
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="teachers" element={<Teachers />} />
            <Route path="teachers/:id" element={<TeacherDetail />} />
            <Route path="wishes" element={<Wishes />} />
            <Route path="wishes/:id" element={<WishDetail />} />
            <Route path="courses" element={<Courses />} />
          </Route>
          <Route path="/login" element={<Login />} />
          <Route path="/form" element={<Form />}/>
          <Route path="/profile" element={<ProfilePage/>} />
          {/* Catch-all Route */}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
