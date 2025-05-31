import React, { useState } from 'react';
import { Link , useNavigate } from 'react-router-dom';
import { Search, Filter, Plus, ChevronRight, BookOpen, Clock, Layout, ListOrdered, User, Briefcase,Trash2,Share,Eye,Download,Edit } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent } from '@/components/ui/card';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { saveAs } from 'file-saver';
import { 
  DropdownMenu, 
  DropdownMenuContent, 
  DropdownMenuItem, 
  DropdownMenuLabel, 
  DropdownMenuSeparator, 
  DropdownMenuTrigger 
} from '@/components/ui/dropdown-menu';
import {
  Dialog,
  DialogContent,
  DialogTrigger,
} from "@/components/ui/dialog";
import { useEffect } from 'react';
import axios from 'axios';
import {jwtDecode} from 'jwt-decode';
import ExportDataComponent from '@/components/ExportDataComponent';
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
interface fichedeveoux{
    id: number;
    teacherId: number;
    academicYear: string;
    wantsSuplementaryHoursS1: number;
    wantsSuplementaryHoursS2: number;
    proposedPfeL: number;
    proposedPfeM: number;
    comments: string;
    semester1Choices?: [fichechoice,fichechoice,fichechoice];
    semester2Choices?: [fichechoice,fichechoice,fichechoice];
    createdAt: string;
}

export const WishItem = ({ wish, showTeacher = true }: { wish: fichedeveoux, showTeacher?: boolean }) => {
  const [teacher, setTeacher] = useState<teacher | null>(null);
  const [exportDataDialogOpen, setExportDataDialogOpen] = useState(false);
  const [isTeacher, setIsTeacher] = useState(false);
  const navigate = useNavigate();
  const currentYear = new Date().getFullYear();
    useEffect(() => {
      const token = localStorage.getItem('jwt');
      if (token) {
          try{
              const decoded =jwtDecode(token) as any;
              console.log("Decoded token:", decoded);
              setIsTeacher(decoded.roles === 'ROLE_TEACHER');
          }catch (error) {
            console.error("Error decoding token:", error);
          }
      }
  }, []);
    useEffect(() => {
      const fetchteacher = async () => {
        const token = localStorage.getItem('jwt');
        const res = await fetch(`http://localhost:8080/api/teachers/${wish.teacherId}`, {
          headers: {
            'Authorization': token,
          }
        });
        const data = await res.json();
        const response= await axios.get(`http://localhost:8080/api/users/${data.userId}`, {
          headers: {
            'Authorization': token,
          }
        });
                setTeacher({...data, ...response.data});
      }
      fetchteacher();
      
    }, [wish.teacherId]);
    console.log(teacher);
  // Get preferred courses names
  /*const preferredCourses = wish.preferredCourses.map(courseId => {
    const course = courses.find(c => c.id === courseId);
    return course ? course.name : 'Unknown Course';
  });
  
  // Get preferred classes names
  const preferredClasses = wish.preferredClasses.map(classId => {
    const cls = classes.find(c => c.id === classId);
    return cls ? cls.name : 'Unknown Class';
  });*/
  // Get preferred classes names
  
  console.log(exportDataDialogOpen)
const viewFiche = () => {
      navigate(`/fiche/${wish.id}`);
}
const editFiche = () => {
    navigate(`/form?edit=true&ficheid=${wish.id}`);
}
const exportFiche = async (format: 'pdf' |'excel',filename:string) => {
       console.log("export fiche");
        try{
        console.log(format);
        const response = await axios.get("http://localhost:8080/api/admin/export/fiche/"+wish.id+"?format="+format, { headers: { 'Authorization': localStorage.getItem('jwt') },responseType: 'blob' }).catch((error) => {
          console.error("Error fetching data:", error);
        });
        const blob = new Blob([response.data], { type: format ==='pdf' ? 'application/pdf' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        const disposition = response.headers.get('Content-Disposition');
        a.download = filename;
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);
        }catch (error) {
          console.error("Error exporting fiche:", error);
        }
  }

const deleteFiche = () => {
    const token = localStorage.getItem('jwt');
    const response = axios.delete(`http://localhost:8080/api/fiches-de-voeux/${wish.id}`, { headers: { 'Authorization': token } }).catch((error) => {
      console.error("Error deleting fiche:", error);
    });
  }
  return (
 <Card className="hover:shadow-md transition duration-300 ease-in-out">
      <CardContent className="p-4">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div className="flex items-center gap-2">
          <ExportDataComponent isOpen={exportDataDialogOpen} onClose={() => setExportDataDialogOpen(false)} onExport={exportFiche} />
          </div>
          {/* Left: Teacher Info */}
          <div className="flex items-start gap-3">
            <div>
              <h3 className="font-medium text-lg">{teacher?.name}</h3>
              <div className="flex items-center text-sm text-gray-500">
                <Briefcase className="w-4 h-4 mr-1" /> 
                {teacher?.departmentName}
              </div>
              <div className="flex items-center text-sm text-gray-500">
                <User className="w-4 h-4 mr-1" /> 
                {teacher?.email}
              </div>
            </div>
          </div>

          {/* Middle: Comments and CreatedAt */}
          <div className="flex-1">
            <p className="text-sm text-gray-700 mb-1"><span className="font-semibold">Comment:</span> {wish.comments}</p>
            <p className="text-xs text-gray-500"><span className="font-semibold">Created:</span> {new Date(wish.createdAt).toLocaleDateString()}</p>
          </div>
          <div className="flex items-center  mx-auto"> 
            <Badge className={`text-xs font-medium px-2 py-1 rounded ${wish.academicYear === `${currentYear}/${currentYear + 1}` ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'}`}>
              {wish.academicYear}
            </Badge>
          </div>
          
          {/* Middle: grade*/}
          <div className="flex items-center gap-2 mr-8 max-h-[80vg] overflow-y-auto ">
            <Badge className="bg-blue-100 text-blue-800 text-xs font-medium px-2 py-1 rounded">
              {teacher?.grade}
            </Badge>
          </div>

          {/* Right: Action Buttons */}
          <div className="flex gap-2 ml-auto">
            <Eye
              title="view fiche"
              className="w-8 h-8 cursor-pointer border rounded-md p-1 bg-gray-100 hover:bg-blue-100 text-blue-600 transition"
              onClick={viewFiche}
            />
            {isTeacher && wish.academicYear==`${currentYear}/${currentYear+1}`&& (
              <Edit
                                  title="edit fiche"
                  className="w-8 h-8 cursor-pointer border rounded-md p-1 bg-gray-100 hover:bg-yellow-100 text-yellow-600 transition"
                  onClick={editFiche}/>
                                          )}
            <Download
              title="export fiche"
              className="w-8 h-8 cursor-pointer border rounded-md p-1 bg-gray-100 hover:bg-gray-200 text-black transition"
              onClick={() => setExportDataDialogOpen(true)}
            />
            <Trash2
              title="delete fiche"
              className="w-8 h-8 cursor-pointer border rounded-md p-1 bg-gray-100 hover:bg-red-100 text-red-500 transition"
              onClick={deleteFiche}
            />
          </div>
        </div>
      </CardContent>
    </Card>

  );
};

export const WishesList = ({wishes}:{fichedeveoux}) => {
  const [isTeacher, setIsTeacher] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
    const [categoryFilter, setCategoryFilter] = useState('all');
    useEffect(()=>{

      const token = localStorage.getItem("jwt");
      const decoded = jwtDecode(token);
      if(decoded.roles=="ROLE_TEACHER"){
        setIsTeacher(true);
      }




    }, []);
  const filteredWishes = wishes;  
const handleExportAll = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/admin/export/2025/2026', { headers:{ Authorization: localStorage.getItem("jwt")},
      responseType: 'blob', // Important!
    });

    const blob = new Blob([response.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    });

    saveAs(blob, 'data.xlsx');
  } catch (error) {
    console.error("Error downloading Excel file", error);
  }
};

  return (
    <div className="space-y-4">
      <div className="flex justify-between flex-col md:flex-row gap-4">
                
        <div className="flex gap-2">
          { !isTeacher &&<Button type="button" className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600" onClick={handleExportAll}> export tout les fiches </Button>}
          
{/* <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" className="flex items-center gap-1">
                <Filter className="w-4 h-4" />
                <span>Category</span>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuLabel>Filter by Category</DropdownMenuLabel>
              <DropdownMenuSeparator />
              <DropdownMenuItem onClick={() => setCategoryFilter('all')}>
                All Categories
              </DropdownMenuItem>
              {categories.map(category => (
                <DropdownMenuItem key={category} onClick={() => setCategoryFilter(category)}>
                  {category}
                </DropdownMenuItem>
              ))}
</DropdownMenuContent>
</DropdownMenu>*/}

</div>
</div>

            <div className="grid grid-cols-1 gap-4">
        {filteredWishes.length > 0 ? (
          filteredWishes.map((wish) => (
            <WishItem key={wish.id} wish={wish} />
          ))
        ) : (
          <div className="text-center py-8">
            <p className="text-gray-500 mb-2">No wishes found matching your filters</p>
            <Button onClick={() => {
              setSearchTerm('');
              
              setCategoryFilter('all');
            }}>Clear Filters</Button>
          </div>
        )}
      </div>
    </div>
  );
};
