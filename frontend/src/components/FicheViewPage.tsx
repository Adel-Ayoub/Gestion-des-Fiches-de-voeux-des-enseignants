import React, { useState, useEffect } from 'react';
import { ArrowLeft, Download, Printer, Calendar, User, Briefcase, Hash } from 'lucide-react';

const ViewFichePage = ({ ficheId, onBack }) => {
  const [fiche, setFiche] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchFicheDetails();
  }, [ficheId]);

  const fetchFicheDetails = async () => {
    try {
      // Mock data - replace with actual API call
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
      
      setFiche(mockFiche);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching fiche details:', error);
      setLoading(false);
    }
  };

  const handlePrint = () => {
    window.print();
  };

  const handleDownloadPDF = () => {
    // Implement PDF download
    console.log('Downloading PDF...');
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
                onClick={handlePrint}
                className="flex items-center px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
              >
                <Printer className="h-4 w-4 mr-2" />
                Imprimer
              </button>
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
                  <span className="ml-2 font-semibold">{fiche.teacher.lastName}</span>
                </div>
                <div>
                  <span className="font-medium text-gray-600">Prénom:</span>
                  <span className="ml-2 font-semibold">{fiche.teacher.firstName}</span>
                </div>
                <div>
                  <span className="font-medium text-gray-600">Grade:</span>
                  <span className="ml-2 font-semibold">{fiche.teacher.grade}</span>
                </div>
                <div>
                  <span className="font-medium text-gray-600">Département:</span>
                  <span className="ml-2 font-semibold">{fiche.teacher.department}</span>
                </div>
                <div className="col-span-2">
                  <span className="font-medium text-gray-600">Email:</span>
                  <span className="ml-2 font-semibold">{fiche.teacher.email}</span>
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

            {/* Modules Table */}
            <div className="mb-8">
              <h4 className="text-lg font-semibold border-b-2 border-gray-300 pb-2 mb-4 flex items-center">
                <Briefcase className="h-5 w-5 mr-2" />
                MODULES SÉLECTIONNÉS
              </h4>
              
              <table className="w-full border-collapse">
                <thead>
                  <tr className="bg-gray-100 print:bg-transparent">
                    <th className="border border-gray-300 px-4 py-3 text-left text-sm font-semibold">Priorité</th>
                    <th className="border border-gray-300 px-4 py-3 text-left text-sm font-semibold">Code</th>
                    <th className="border border-gray-300 px-4 py-3 text-left text-sm font-semibold">Intitulé du Module</th>
                    <th className="border border-gray-300 px-4 py-3 text-center text-sm font-semibold">Niveau</th>
                    <th className="border border-gray-300 px-4 py-3 text-center text-sm font-semibold">Semestre</th>
                    <th className="border border-gray-300 px-4 py-3 text-center text-sm font-semibold">Groupes</th>
                  </tr>
                </thead>
                <tbody>
                  {fiche.choices.map((choice, index) => (
                    <tr key={choice.id} className={index % 2 === 0 ? 'bg-white' : 'bg-gray-50 print:bg-transparent'}>
                      <td className="border border-gray-300 px-4 py-3 text-center font-semibold">
                        <span className="inline-flex items-center justify-center w-8 h-8 rounded-full bg-indigo-100 text-indigo-700 print:bg-transparent print:border print:border-indigo-700">
                          {choice.priority}
                        </span>
                      </td>
                      <td className="border border-gray-300 px-4 py-3 font-mono text-sm">{choice.module.code}</td>
                      <td className="border border-gray-300 px-4 py-3">
                        <div>
                          <div className="font-medium">{choice.module.name}</div>
                          <div className="text-sm text-gray-600 mt-1">
                            Spécialité: {choice.module.specialty} | 
                            CM: {choice.module.courseHours}h | 
                            TD: {choice.module.tdHours}h | 
                            TP: {choice.module.tpHours}h
                          </div>
                        </div>
                      </td>
                      <td className="border border-gray-300 px-4 py-3 text-center">{choice.module.level}</td>
                      <td className="border border-gray-300 px-4 py-3 text-center">{choice.module.semester}</td>
                      <td className="border border-gray-300 px-4 py-3 text-center font-semibold">{choice.groupCount}</td>
                    </tr>
                  ))}
                </tbody>
                <tfoot>
                  <tr className="bg-gray-100 print:bg-transparent font-semibold">
                    <td colSpan="5" className="border border-gray-300 px-4 py-3 text-right">Total des groupes:</td>
                    <td className="border border-gray-300 px-4 py-3 text-center">
                      {fiche.choices.reduce((sum, choice) => sum + choice.groupCount, 0)}
                    </td>
                  </tr>
                </tfoot>
              </table>
            </div>

            {/* Signature Section */}
            <div className="mt-12 grid grid-cols-2 gap-8">
              <div className="text-center">
                <div className="border-t-2 border-gray-400 pt-2 mt-16">
                  <p className="font-medium">Signature de l'enseignant</p>
                  <p className="text-sm text-gray-600 mt-1">Date: {formatDate(fiche.submissionDate)}</p>
                </div>
              </div>
              <div className="text-center">
                <div className="border-t-2 border-gray-400 pt-2 mt-16">
                  <p className="font-medium">Visa du Chef de Département</p>
                  <p className="text-sm text-gray-600 mt-1">Date: _______________</p>
                </div>
              </div>
            </div>

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
