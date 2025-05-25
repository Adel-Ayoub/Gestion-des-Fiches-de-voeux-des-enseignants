import React, { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {ChatContainer} from "@/components/messaging/ChatContainer";
import { Textarea } from "@/components/ui/textarea";
import { Header } from '@/components/layout/Header';
import { Badge } from "@/components/ui/badge";
import Mailbox from '@/components/Mailbox';
import WishsList from '@/components/wishes/WishesList';
import { ScrollArea } from "@/components/ui/scroll-area";
import { 
  User, 
  Settings, 
  Mail, 
  Phone, 
  Building, 
  Send,
  FileText,
  Calendar,
  Clock
} from "lucide-react";
import { useEffect } from "react";
import axios from "axios";
import {jwtDecode} from 'jwt-decode';
import ChatComponent from "@/components/Chat";
type teacher = {
  id?:number;
  userId:number;
  name?:string;
  grade:string;
  officeNumber:string;
  departmentName:string;
  email?:string;
  role?:string;
}

  
function ProfilePage() {
  const [user, setUser] = useState<teacher>(""); 

  const [message, setMessage] = useState("");

  const previousForms = [

    {
      id: 1,
      submitDate: "2024-03-01",
      semester: "Printemps 2024",
      status: "Approuvé",
      totalHours: 12
    },
    {
      id: 2,
      submitDate: "2023-09-15",
      semester: "Automne 2023",
      status: "Complété",
      totalHours: 15
    },
    {
      id: 3,
      submitDate: "2023-03-10",
      semester: "Printemps 2023",
      status: "Complété",
      totalHours: 14
    }
  ];
  const [wishes, setWishes] = useState([]);
  useEffect(() => {
    // Simulate fetching user data from an API
    const fetchUserData = async () => {
      const token = localStorage.getItem("jwt");
      const decodedToken = jwtDecode(token);
      console.log(decodedToken);
      const userdto = await axios.get("http://localhost:8080/api/users/by-email" ,{headers: { Authorization: token}, params: { email: decodedToken.sub }});
      console.log(userdto.data);
      const response = await axios.get("http://localhost:8080/api/teachers/user/"+parseInt(userdto.data.id), {headers: { Authorization: token}});
      console.log(response.data);
      const res = await axios.get("http://localhost:8080/api/teachers/teacher/submitted-fiches/19", { headers: { Authorization: token } });
      setWishes(res.data);
      console.log(wishes);

      setUser({
        id: userdto.id,
        userId: response.data.id,
        
        name: userdto.data.name,
        email: userdto.data.email,
        rank: response.data.grade,
        department: response.data.departmentName,
        officeNumber: response.data.officeNumber,
        role:userdto.data.role,      });    
};
    
    // fetch submitted forms from the API
    fetchUserData();
  }, []);
    const handleProfileUpdate = async (e) => {
    e.preventDefault();
    const res= await axios.put("http://localhost:8080/api/users/"+user.id,  { "id":user.id,"email":user.email,"name":user.name, role:"TEACHER" },{ headers :{ Authorization: localStorage.getItem("jwt")} });
    const response = await axios.put("http://localhost:8080/api/teachers/"+user.id, {"grade":user.rank,"officeNumber":user.officeNumber,"departmentName":user.departmentName}, { headers:{ Authorization: localStorage.getItem("jwt")} });
    console.log(res.data);
    
};


  const handleSettingsUpdate = (e: React.FormEvent) => {
    e.preventDefault();
    alert("Paramètres enregistrés avec succès !");
  };

  
  const getStatusColor = (status: string) => {
    switch (status.toLowerCase()) {
      case "approuvé": return "bg-green-100 text-green-800";
      case "en attente": return "bg-yellow-100 text-yellow-800";
      case "complété": return "bg-blue-100 text-blue-800";
      default: return "bg-gray-100 text-gray-800";
    }
  };

  return (
    <>
      <Header title="Profil" showsidebar={false} showsearch={false} />
      <div className="container mx-auto p-6">
        <Tabs defaultValue="profile" className="space-y-4">
          <TabsList className="grid w-full grid-cols-4">
            <TabsTrigger value="profile" className="flex items-center gap-2">
              <User className="w-4 h-4" />
              Informations
            </TabsTrigger>
            <TabsTrigger value="forms" className="flex items-center gap-2">
              <FileText className="w-4 h-4" />
              Formulaires Précédents
            </TabsTrigger>
            <TabsTrigger value="settings" className="flex items-center gap-2">
              <Settings className="w-4 h-4" />
              Paramètres
            </TabsTrigger>
            <TabsTrigger value="support" className="flex items-center gap-2">
              <Mail className="w-4 h-4" />
              Support
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
                      <Input id="name" value={user.name} onChange={(e) => setUser({ ...user, name: e.target.value })} />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="email">Email</Label>
                      <Input id="email" type="email" value={user.email} onChange={(e) => setUser({ ...user, email: e.target.value })} />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="rank">Grade</Label>
                      <Input id="rank" value={user.rank} onChange={(e) => setUser({ ...user, rank: e.target.value })} />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="department">Département</Label>
                      <Input id="department" value={user.department} onChange={(e) => setUser({ ...user, department: e.target.value })} />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="officeNumber">Bureau</Label>
                      <Input id="officeNumber" value={user.officeNumber} onChange={(e) => setUser({ ...user, officeNumber: e.target.value })} />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="role">Role</Label>
                      <Input id="role" value={user.role} onChange={(e) => setUser({ ...user, role: e.target.value })} />
                    </div>
                  </div>
                  
                  <Button onClick={handleProfileUpdate} className="space-y-3">Mettre à jour le profil</Button>
                </form>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="forms">
            <Card>
              <CardHeader>
                <CardTitle>Soumissions Précédentes</CardTitle>
              </CardHeader>
              <CardContent>
                { wishes.length > 0 ? (
                  <WishesList wishes={wishes}/>                ) : (
                  <p>Aucune soumission précédente trouvée.</p>
                )}          

</CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="settings">
            <Card>
              <CardHeader>
                <CardTitle>Paramètres du Compte</CardTitle>
              </CardHeader>
              <CardContent>
                <form onSubmit={handleSettingsUpdate} className="space-y-4">
                  <div className="space-y-4">
                    <div className="space-y-2">
                      <Label>Notifications par Email</Label>
                      <div className="space-y-2">
                        <label className="flex items-center space-x-2">
                          <input type="checkbox" className="rounded" />
                          <span>Mises à jour du statut des formulaires</span>
                        </label>
                        <label className="flex items-center space-x-2">
                          <input type="checkbox" className="rounded" />
                          <span>Annonces administratives</span>
                        </label>
                        <label className="flex items-center space-x-2">
                          <input type="checkbox" className="rounded" />
                          <span>Messages de support</span>
                        </label>
                      </div>
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="currentPassword">Mot de passe actuel</Label>
                      <Input id="currentPassword" type="password" />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="newPassword">Nouveau mot de passe</Label>
                      <Input id="newPassword" type="password" />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="confirmPassword">Confirmer le nouveau mot de passe</Label>
                      <Input id="confirmPassword" type="password" />
                    </div>
                  </div>
                  <Button type="submit">Enregistrer les paramètres</Button>
                </form>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="support">
            <Card>
              <CardHeader>
                <CardTitle>Chat de Support</CardTitle>
              </CardHeader>
              <CardContent>
                <Mailbox userId={user.userId}/>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </>
  );
}

export default ProfilePage;
