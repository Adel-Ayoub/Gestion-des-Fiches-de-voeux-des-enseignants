import React, { useState, useEffect } from 'react';
import { ArrowLeft, Download, Printer, Calendar, User, Briefcase, Hash } from 'lucide-react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
interface fichechoice {
    ficheId: number;
    moduleId: number;
    rank:number;
    wantsCours:boolean;
    wantsTd:number;
    wantsTp:number;
    targetSemester: string;
}
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

const ViewFichePage = ({  onBack }) => {
  const [fiche, setFiche] = useState(null);
  const [loading, setLoading] = useState(true);
  const { ficheId} = useParams();
  const [moduless1, setModulesS1] = useState([]);
  const [moduless2, setModulesS2] = useState([]);
  const [teacher, setTeacher] = useState<teacher | null>(null);
  const [exportDataDialogOpen, setExportDataDialogOpen] = useState(false);
const renderModuleTable = (choices: fichechoice[], semesterLabel: string) => {
    console.log(choices);
    console.log("modulesS1:",moduless1);
    const modules = semesterLabel === 'S1' ? moduless1 : moduless2;
    console.log("moduless1:",moduless1[0]);
    console.log("modules:",modules[1]);
    return(
    <div className="mb-8">
      <h4 className="text-lg font-semibold border-b-2 border-gray-300 pb-2 mb-4 flex items-center">
        <Briefcase className="h-5 w-5 mr-2" />
        MODULES SÉLECTIONNÉS - {semesterLabel}
      </h4>
      <table className="w-full border-collapse">
        <thead>
          <tr className="bg-gray-100 print:bg-transparent">
            <th className="border border-gray-300 px-4 py-3 text-left text-sm font-semibold">Priorité</th>
            <th className="border border-gray-300 px-4 py-3 text-left text-sm font-semibold">Code</th>
            <th className="border border-gray-300 px-4 py-3 text-left text-sm font-semibold">Intitulé du Module</th>
            <th className="border border-gray-300 px-4 py-3 text-center text-sm font-semibold">Niveau</th>
            <th className="border border-gray-300 px-4 py-3 text-center text-sm font-semibold">Spécialité</th>
            <th className="border border-gray-300 px-4 py-3 text-center text-sm font-semibold">Groupes</th>
          </tr>
        </thead>
        <tbody>
          {choices.map((choice, index) => {
            const [level, specialty] = modules[index].level.split('-');
          
            return (
              <tr key={choice.id} className={index % 2 === 0 ? 'bg-white' : 'bg-gray-50 print:bg-transparent'}>
                <td className="border border-gray-300 px-4 py-3 text-center font-semibold">
                  <span className="inline-flex items-center justify-center w-8 h-8 rounded-full bg-indigo-100 text-indigo-700 print:bg-transparent print:border print:border-indigo-700">
                    {choice.rank}
                  </span>
                </td>
                <td className="border border-gray-300 px-4 py-3 font-mono text-sm">{choice.module.code}</td>
                <td className="border border-gray-300 px-4 py-3">
                  <div>
                    <div className="font-medium">{module.name}</div>
                    <div className="text-sm text-gray-600 mt-1">
                      CM: {choice.wantsCours}h | TD: {choice.wantsTd}h | TP: {choice.wantsTp}h
                    </div>
                  </div>
                </td>
                <td className="border border-gray-300 px-4 py-3 text-center">{level}</td>
                <td className="border border-gray-300 px-4 py-3 text-center">{specialty}</td>
                              </tr>
            );
          })}
        </tbody>
        <tfoot>
          <tr className="bg-gray-100 print:bg-transparent font-semibold">
            <td colSpan={5} className="border border-gray-300 px-4 py-3 text-right">Total des groupes:</td>
            <td className="border border-gray-300 px-4 py-3 text-center">
              {choices.reduce((sum, choice) => sum + choice.groupCount, 0)}
            </td>
          </tr>
        </tfoot>
      </table>
    </div>
  );
};
  useEffect(() => {
    fetchFicheDetails();
  }, [ficheId]);

  const fetchFicheDetails = async () => {
    try {
      // Mock data - replace with actual API call
      const token = localStorage.getItem('jwt');
      const ress = await axios.get<fichedeveoux[]>(`http://localhost:8080/api/fiches-de-voeux/${ficheId}`, {
          headers: {
            'Authorization':localStorage.getItem('jwt'),
          }
        });
      

        const ressp = await fetch(`http://localhost:8080/api/teachers/${ress.data.teacherId}`, {
          headers: {
            'Authorization': token,
          }
        });
        const data = await ressp.json();
        const response= await axios.get(`http://localhost:8080/api/users/${data.userId}`, {
          headers: {
            'Authorization': token,
          }
        });
       setTeacher({...data, ...response.data});
      const res = await axios.get<ficheChoice[]>(`http://localhost:8080/ficheChoice/by-fiche/${ficheId}`, {
          headers: {
            'Authorization':localStorage.getItem('jwt'),
          }
        });
        const semester1Choices = [];
        const semester2Choices= [];
        const modules1= [];
        const modules2= [];
        console.log(res.data);
        res.data.forEach( async (choice: fichechoice) => {
          if (choice.targetSemester === 'S1') {
            semester1Choices.push(choice);
          } else if (choice.targetSemester === 'S2') {
            semester2Choices.push(choice);}
          const module = await axios.get(`http://localhost:8080/api/modules/${choice.moduleId}`, { headers: { 'Authorization': localStorage.getItem('jwt') } });
          console.log("module:!!!!",module.data);
          const [level, specialty,s] = module.data.level.split('-');
          if (s=="S1") {
            modules1.push(module.data);
          }else if (s=="S2") {
            modules2.push(module.data);
          }
                  });
        console.log(ress.data);
      console.log("modules1:",modules1);
  setModulesS1(modules1);
          setModulesS2(modules2);

        setFiche({...ress.data, semester1Choices:semester1Choices, semester2Choices:semester2Choices});
      console.log(fiche);
      const mockFiche = {
        id: ficheId,
        teacher: {
          firstName: 'Jean',
          lastName: 'DUPONT',
          grade: 'Maître de Conférences A',
          email: 'jean.dupont@university.edu',
          department: 'Informatique'
        },
        academicYear: '2024-2025',
        submissionDate: '2024-03-15T10:30:00',
        status: 'SUBMITTED',
        choices: [
          {
            id: 1,
            priority: 1,
            module: {
              code: 'INF101',
              name: 'Algorithmique et Structures de Données',
              level: 'L2',
              semester: 'S1',
              specialty: 'Informatique',
              credits: 6,
              courseHours: 24,
              tdHours: 24,
              tpHours: 24
            },
            groupCount: 3
          },
          {
            id: 2,
            priority: 2,
            module: {
              code: 'INF202',
              name: 'Bases de Données Avancées',
              level: 'L3',
              semester: 'S1',
              specialty: 'Informatique',
              credits: 5,
              courseHours: 24,
              tdHours: 18,
              tpHours: 18
            },
            groupCount: 2
          },
          {
            id: 3,
            priority: 3,
            module: {
              code: 'INF303',
              name: 'Réseaux et Protocoles',
              level: 'L3',
              semester: 'S2',
              specialty: 'Informatique',
              credits: 5,
              courseHours: 24,
              tdHours: 12,
              tpHours: 24
            },
            groupCount: 2
          }
        ]
      };
      
      setLoading(false);
    } catch (error) {
      console.error('Error fetching fiche details:', error);
      setLoading(false);
    }
  };

  const handlePrint = () => {
    window.print();
  };

  const handleDownloadPDF = async () => {
    // Implement PDF download
    try{
        const response = await axios.get("http://localhost:8080/api/admin/export/fiche/"+ficheId+"?format=pdf", { headers: { 'Authorization': localStorage.getItem('jwt') },responseType: 'blob' }).catch((error) => {
          console.error("Error fetching data:", error);
        });
        const blob = new Blob([response.data], { type: 'application/pdf' });
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

  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: 'long',
      year: 'numeric'
    });
  };

  const formatDateTime = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  if (!fiche) {
    return <div>Fiche non trouvée</div>;
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header with actions - hidden in print */}
      <div className="bg-white shadow-sm border-b print:hidden">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex justify-between items-center">
            <button
              onClick={onBack}
              className="flex items-center text-gray-600 hover:text-gray-900"
            >
              <ArrowLeft className="h-5 w-5 mr-2" />
              Retour
            </button>
            <div className="flex space-x-3">
                            <button
                onClick={handleDownloadPDF}
                className="flex items-center px-4 py-2 bg-indigo-600 text-white rounded-md text-sm font-medium hover:bg-indigo-700"
              >
                <Download className="h-4 w-4 mr-2" />
                Télécharger PDF
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Main content - styled like PDF */}
      <div className="max-w-4xl mx-auto p-8 print:p-0">
        <div className="bg-white shadow-lg print:shadow-none">
          {/* Document Header */}
          <div className="bg-indigo-600 text-white p-8 print:bg-white print:text-black print:border-b-4 print:border-black">
            <div className="text-center">
              <h1 className="text-3xl font-bold mb-2">UNIVERSITÉ DES SCIENCES ET DE LA TECHNOLOGIE</h1>
              <h2 className="text-xl mb-4">Faculté d'Informatique</h2>
              <div className="inline-block border-t-2 border-b-2 border-white print:border-black py-2 px-8 mt-4">
                <h3 className="text-2xl font-semibold">FICHE DE VŒUX</h3>
                <p className="text-lg mt-1">Année Universitaire {fiche.academicYear}</p>
              </div>
            </div>
          </div>

          {/* Teacher Information */}
          <div className="p-8">
            <div className="mb-8">
              <h4 className="text-lg font-semibold border-b-2 border-gray-300 pb-2 mb-4 flex items-center">
                <User className="h-5 w-5 mr-2" />
                INFORMATIONS DE L'ENSEIGNANT
              </h4>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <span className="font-medium text-gray-600">Nom:</span>
                  <span className="ml-2 font-semibold">{teacher.name}</span>
                </div>
                
                <div>
                  <span className="font-medium text-gray-600">Grade:</span>
                  <span className="ml-2 font-semibold">{teacher.grade}</span>
                </div>
                <div>
                  <span className="font-medium text-gray-600">Département:</span>
                  <span className="ml-2 font-semibold">{teacher.departmentName}</span>
                </div>
                <div className="col-span-2">
                  <span className="font-medium text-gray-600">Email:</span>
                  <span className="ml-2 font-semibold">{teacher.email}</span>
                </div>
              </div>
            </div>

            {/* Submission Information */}
            <div className="mb-8 p-4 bg-gray-50 rounded-lg print:bg-transparent print:border print:border-gray-300">
              <div className="flex items-center justify-between">
                <div className="flex items-center">
                  <Calendar className="h-5 w-5 mr-2 text-gray-600" />
                  <span className="font-medium text-gray-600">Date de soumission:</span>
                  <span className="ml-2 font-semibold">{formatDateTime(fiche.submissionDate)}</span>
                </div>
                <div className="flex items-center">
                  <Hash className="h-5 w-5 mr-2 text-gray-600" />
                  <span className="font-medium text-gray-600">Référence:</span>
                  <span className="ml-2 font-mono font-semibold">FV-{fiche.academicYear.replace('-', '')}-{String(fiche.id).padStart(4, '0')}</span>
                </div>
              </div>
            </div>

            {/* Modules Table s1*/}
{renderModuleTable(fiche.semester1Choices, 'S1')}
            {/* Modules Table s2*/}
{renderModuleTable(fiche.semester2Choices, 'S2')}

        

            
            {/* Footer */}
            <div className="mt-12 pt-8 border-t border-gray-300 text-center text-sm text-gray-600">
              <p>Document généré le {formatDate(new Date())} à {new Date().toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })}</p>
              <p className="mt-1">Ce document est confidentiel et destiné à l'usage exclusif de l'administration</p>
            </div>
          </div>
        </div>
      </div>

      {/* Print styles */}
      <style jsx>{`
        @media print {
          @page {
            size: A4;
            margin: 1cm;
          }
          
          body {
            print-color-adjust: exact;
            -webkit-print-color-adjust: exact;
          }
          
          .print\\:hidden {
            display: none !important;
          }
          
          .shadow-lg {
            box-shadow: none !important;
          }
        }
      `}</style>
    </div>
  );
};

export default ViewFichePage;
