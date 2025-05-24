import {useState} from 'react';
import axios from 'axios';
import {jwtDecode} from 'jwt-decode';
import {useNavigate} from 'react-router-dom';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Eye, EyeOff, Lock, LogIn, User } from "lucide-react";
import { useToast } from "@/components/ui/use-toast";
import bgImage from "../../public/bgImage.png";
function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const { toast } = useToast();
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', {
        email,
        password,
      });
      // Assuming the API returns a token or user data
      const token = `${response.data.tokenType} ${response.data.accessToken}`;
      const decodedToken = jwtDecode(response.data.accessToken);
      console.log(decodedToken);
      const role = decodedToken.roles;
      if(role=='ROLE_ADMIN'){
        navigate('/admin/dashboard');
      }else if(role=='ROLE_TEACHER'){
        navigate('/form');
      };
      localStorage.setItem('jwt', token); // Store the token in local storage
      // Store the token in local storage
      //onLogin(token); // Call the onLogin function passed as a prop
      console.log(response.data);
      // Handle successful login (e.g., redirect to dashboard)
    } catch (err) {
      console.log(err.message);
      setError('Invalid email or password');
    }
  };

const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };
  return (
    
<div style={{backgroundImage : `url(${bgImage})`}} className="min-h-screen w-full flex items-center justify-center bg-login-bg bg-cover bg-center bg-no-repeat relative">
      <div className="absolute inset-0 bg-teal-DEFAULT/30 backdrop-blur-sm"></div>
      
      <div className="relative z-10 w-full max-w-md px-4 py-12 mx-auto">
        <div className="mb-8 text-center">
          <h1 className="text-4xl font-bold text-white">Workspace Login</h1>
          <p className="mt-2 text-teal-light text-white/80">Access your professional dashboard</p>
        </div>
    <Card className="w-full max-w-md shadow-lg border-teal-light/20 bg-white/90 backdrop-blur-sm">
      <CardHeader className="space-y-1">
        <CardTitle className="text-2xl font-bold text-center text-teal-DEFAULT">Welcome Back</CardTitle>
        <CardDescription className="text-center text-teal-light">
          Enter your credentials to access your account
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="email" className="text-teal-DEFAULT">Email</Label>
            <div className="relative">
              <div className="absolute left-3 top-1/2 -translate-y-1/2 text-teal-light">
                <User size={18} />
              </div>
              <Input
                id="email"
                type="email"
                placeholder="name@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="pl-10 bg-white border-teal-light/30 focus-visible:ring-teal-DEFAULT/50"
                required
              />
            </div>
          </div>
          <div className="space-y-2">
            <Label htmlFor="password" className="text-teal-DEFAULT">Password</Label>
            <div className="relative">
              <div className="absolute left-3 top-1/2 -translate-y-1/2 text-teal-light">
                <Lock size={18} />
              </div>
              <Input
                id="password"
                type={showPassword ? "text" : "password"}
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="pl-10 pr-10 bg-white border-teal-light/30 focus-visible:ring-teal-DEFAULT/50"
                required
              />
              <button
                type="button"
                onClick={togglePasswordVisibility}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-teal-light hover:text-teal-DEFAULT transition-colors"
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
          </div>
          <Button 
            type="submit" 
            className="w-full bg-[#566a6c] hover:bg-[#566a6c]/90 text-white flex items-center justify-center gap-2"
            disabled={isLoading}
          >
            {isLoading ? "Logging in..." : (
              <>
                <LogIn size={18} /> Login
              </>
            )}
          </Button>
        </form>
      </CardContent>
      <CardFooter className="flex justify-center mt-2">
        {/* Sign up link removed */}
      </CardFooter>
    </Card> 
    <p className="mt-8 text-center text-white/70 text-sm">
          &copy; 2025 Workspace. All rights reserved.
        </p>
      </div>
    </div>
);
}
export default Login;
