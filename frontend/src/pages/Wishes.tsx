
import React from 'react';
import { Header } from '@/components/layout/Header';
import { WishesList } from '@/components/wishes/WishesList';
import { WishDetail } from '@/components/wishes/WishDetail';
import { Routes, Route } from 'react-router-dom';
import {useEffect} from 'react';
import {useState} from 'react';
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
interface fichedeveoux{
    id: number;
    teacherId: number;
    academicYear: string;
    wantsSupplementaryHoursS1: number;
    wantsSupplementaryHoursS2: number;
    proposedPfeL: number;
    proposedPfeM: number;
    comments: string;
    semester1Choices?: [fichechoice,fichechoice,fichechoice];
    semester2Choices?: [fichechoice,fichechoice,fichechoice];
    createdAt: string;
}
const Wishes = () => {
  const [wishes, setWishes] = useState<fichedeveoux[]>([]);
  const [loading, setLoading] = useState(true);
      
    useEffect(() => {
    const token = localStorage.getItem('jwt');
    const fetchData = async () => {
    const res = await axios.get('http://localhost:8080/api/fiches-de-voeux', {
      headers: {
        'Authorization':token,
      }
    });
    await setWishes(res.data);
    }
    fetchData();

const fetchchoices = async () => {
    const updatedWishes = await Promise.all(
      wishes.map(async (wish) => {
        const res = await axios.get<fichedeveoux[]>(`http://localhost:8080/ficheChoice/by-fiche/${wish.id}`, {
          headers: {
            'Authorization':localStorage.getItem('jwt'),
          }
        });
        const semester1Choices = [];
        const semester2Choices= [];
        
        res.data.forEach((choice: fichechoice) => {
          if (choice.targetSemester === 'S1') {
            semester1Choices.push(choice);
          } else if (choice.targetSemester === 'S2') {
            semester2Choices.push(choice);}
        });
        
        return {...wish, semester1Choices, semester2Choices};
      }
    )
    )
    
    setWishes(updatedWishes);
    setLoading(false);
}
  

    fetchchoices();
  }, []);
     return (
    <>
      <Routes>
        <Route 
          index
          element={
            <>
              <Header title="Wishes" />
              <div className="p-6">
                {loading ? (
                  <div className="flex items-center justify-center h-full">
                    <p>Loading...</p>
                  </div>
                ) :<WishesList wishes={wishes}/>
 }
              </div>
            </>
          } 
        />
        <Route 
          path=":id" 
          element={
            <>
              <Header title="Wish Details" />
              <div className="p-6">
                <WishDetail />
              </div>
            </>
          } 
        />
      </Routes>
    </>
  );
};

export default Wishes;
