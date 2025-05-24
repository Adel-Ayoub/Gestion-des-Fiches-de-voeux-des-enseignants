import React, { useState } from 'react';
import { X, FileSpreadsheet, FileText, Check, X as ClearIcon, ChevronDown, Download } from 'lucide-react';

interface ExportDataDialogProps {
  isOpen: boolean;
  onClose: () => void;
onExport: (format:'pdf'|'excel',fileName:string) => void;
}

interface ExportConfig {
  format: 'excel' | 'pdf';
  fields: string[];
  includeHeaders: boolean;
  includeFooter: boolean;
  orientation: 'portrait' | 'landscape';
  fileName: string;
}

const ExportDataComponent: React.FC<ExportDataDialogProps> = ({ isOpen, onClose, onExport }) => {
  const [format, setFormat] = useState<'excel' | 'pdf'>('pdf');
  const [selectedFields, setSelectedFields] = useState<string[]>([]);
  const [includeHeaders, setIncludeHeaders] = useState(true);
  const [includeFooter, setIncludeFooter] = useState(true);
  const [orientation, setOrientation] = useState<'portrait' | 'landscape'>('landscape');
  const [fileName, setFileName] = useState('data-export-2025-05-23');
  const [isFieldsDropdownOpen, setIsFieldsDropdownOpen] = useState(false);

  const availableFields = ['Name', 'Email', 'Phone', 'Address', 'Date Created', 'Status', 'Category'];

  const handleSelectAll = () => {
    setSelectedFields([...availableFields]);
  };

  const handleClearAll = () => {
    setSelectedFields([]);
  };

  const handleFieldToggle = (field: string) => {
    setSelectedFields(prev => 
      prev.includes(field) 
        ? prev.filter(f => f !== field)
        : [...prev, field]
    );
  };

  const handleExport = () => {
    onExport(
      format,
      fileName
    );
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center max-h-[80vg] overflow-y-auto justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-md mx-4 border border-gray-200">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <h2 className="text-xl font-semibold text-black">Export Data</h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700 transition-colors"
          >
            <X size={24} />
          </button>
        </div>

        <div className="p-6 space-y-6">
          {/* Export Format */}
          <div>
            <h3 className="text-sm font-medium text-black mb-3">Export Format</h3>
            <div className="grid grid-cols-2 gap-3">
              <button
                onClick={() => setFormat('excel')}
                height={10}
                width={10}
                className={` rounded-lg border border-black  py-0 text-sm transition-all ${
                  format === 'excel'
                    ? 'border-green-500 bg-green-50'
                    : 'border-gray-200 bg-gray-50 hover:bg-gray-100'
                }`}
              >
                <FileSpreadsheet className="mx-auto mb-2 text-black" size={24} />
                <span className="text-black font-medium">Excel</span>
              </button>
              <button
                onClick={() => setFormat('pdf')}
                className={` rounded-lg border-2 hover:bg-red-100 border border-black  py-0 text-sm transition-all ${
                  format === 'pdf'
                    ? 'border-red-500 bg-red-50'
                    : 'border-gray-200 bg-gray-50 hover:bg-gray-100'
                }`}
              >
                <FileText className="mx-auto mb-2 text-black" size={24} />
                <span className="text-black font-medium">PDF</span>
              </button>
            </div>
          </div>

          {/* Fields to Include */}
          
          {/* Options */}
          
          {/* Page Orientation */}
          <div>
            <h3 className="text-sm font-medium text-black mb-3">Page Orientation</h3>
            <div className="grid grid-cols-2 gap-3">
              <button
                onClick={() => setOrientation('portrait')}
                className={`p-3 rounded-lg border-2 transition-all ${
                  orientation === 'portrait'
                    ? 'border-blue-500 bg-blue-50'
                    : 'border-gray-200 bg-gray-50 hover:bg-gray-100'
                }`}
              >
                <div className="w-6 h-8 mx-auto mb-2 border-2 border-black rounded"></div>
                <span className="text-black font-medium">Portrait</span>
              </button>
              <button
                onClick={() => setOrientation('landscape')}
                className={`p-3 rounded-lg border-2 transition-all ${
                  orientation === 'landscape'
                    ? 'border-blue-500 bg-blue-50'
                    : 'border-gray-200 bg-gray-50 hover:bg-gray-100'
                }`}
              >
                <div className="w-8 h-6 mx-auto mb-2 border-2 border-black rounded"></div>
                <span className="text-black font-medium">Landscape</span>
              </button>
            </div>
          </div>

          {/* File Name */}
          <div>
            <h3 className="text-sm font-medium text-black mb-3">File Name</h3>
            <input
              type="text"
              value={fileName}
              onChange={(e) => setFileName(e.target.value)}
              className="w-full p-3 bg-gray-50 border border-gray-200 rounded-lg text-black placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Enter file name"
            />
          </div>
        </div>

        {/* Footer */}
        <div className="flex gap-3 p-6 border-t border-gray-200">
          <button
            onClick={onClose}
            className="flex-1 px-4 py-2 bg-gray-100 hover:bg-gray-200 text-black rounded-lg transition-colors flex items-center justify-center gap-2"
          >
            <X size={16} />
            Cancel
          </button>
          <button
            onClick={() => onExport(format,fileName)}
            className="flex-1 px-4 py-2 bg-[rgb(85,102,102)] hover:bg-[rgb(85,102,102)] text-black rounded-lg transition-colors flex items-center justify-center gap-2"
          >
            <Download size={16} />
            Export
          </button>
        </div>
      </div>
    </div>
  );
};

export default ExportDataComponent;
