import React, { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Textarea } from "@/components/ui/textarea";
import { Header } from '@/components/layout/Header';
import { Badge } from "@/components/ui/badge";
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

function ProfilePage() {
  const [user, setUser] = useState({
    fullName: "John Doe",
    email: "johndoe@example.com",
    rank: "Enseignant Senior",
    department: "Informatique",
    officeNumber: "CS-101",
    phone: "+1234567890"
  });

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

  const [messages, setMessages] = useState([
    {
      id: 1,
      text: "J'ai besoin de clarifications sur l'allocation des heures d'enseignement.",
      timestamp: "2024-03-15 10:30",
      sender: "user"
    },
    {
      id: 2,
      text: "Votre demande a été reçue. Un administrateur vous répondra bientôt.",
      timestamp: "2024-03-15 10:35",
      sender: "admin"
    }
  ]);

  const handleProfileUpdate = (e: React.FormEvent) => {
    e.preventDefault();
    alert("Profil mis à jour avec succès !");
  };

  const handleSettingsUpdate = (e: React.FormEvent) => {
    e.preventDefault();
    alert("Paramètres enregistrés avec succès !");
  };

  const handleMessageSend = () => {
    if (message.trim()) {
      setMessages([
        ...messages,
        {
          id: messages.length + 1,
          text: message,
          timestamp: new Date().toLocaleString(),
          sender: "user"
        }
      ]);
      setMessage("");
    }
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
                <form onSubmit={handleProfileUpdate} className="space-y-4">
                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="fullName">Nom Complet</Label>
                      <Input id="fullName" value={user.fullName} onChange={(e) => setUser({ ...user, fullName: e.target.value })} />
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
                      <Label htmlFor="phone">Téléphone</Label>
                      <Input id="phone" value={user.phone} onChange={(e) => setUser({ ...user, phone: e.target.value })} />
                    </div>
                  </div>
                  <Button type="submit" className="w-full">Mettre à jour le profil</Button>
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
                <div className="space-y-4">
                  {previousForms.map((form) => (
                    <div key={form.id} className="flex items-center justify-between p-4 border rounded-lg hover:bg-gray-50">
                      <div className="space-y-1">
                        <div className="font-medium">{form.semester}</div>
                        <div className="flex items-center gap-4 text-sm text-gray-500">
                          <span className="flex items-center gap-1">
                            <Calendar className="w-4 h-4" />
                            {form.submitDate}
                          </span>
                          <span className="flex items-center gap-1">
                            <Clock className="w-4 h-4" />
                            {form.totalHours} heures
                          </span>
                        </div>
                      </div>
                      <Badge className={getStatusColor(form.status)}>{form.status}</Badge>
                    </div>
                  ))}
                </div>
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
                <div className="h-[400px] flex flex-col">
                  <ScrollArea className="flex-1 pr-4 mb-4">
                    <div className="space-y-4">
                      {messages.map((msg) => (
                        <div key={msg.id} className={`flex ${msg.sender === "user" ? "justify-end" : "justify-start"}`}>
                          <div className={`max-w-[80%] rounded-lg p-3 ${msg.sender === "user" ? "bg-primary text-white" : "bg-gray-100"}`}>
                            <p>{msg.text}</p>
                            <p className="text-xs mt-1 opacity-70">{msg.timestamp}</p>
                          </div>
                        </div>
                      ))}
                    </div>
                  </ScrollArea>
                  <div className="flex gap-2">
                    <Textarea
                      placeholder="Écrivez votre message..."
                      value={message}
                      onChange={(e) => setMessage(e.target.value)}
                      className="flex-1"
                    />
                    <Button onClick={handleMessageSend} className="flex-shrink-0">
                      <Send className="w-4 h-4" />
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </>
  );
}

export default ProfilePage;
