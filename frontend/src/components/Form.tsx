import React from 'react';
import axios from 'axios';
import { useForm } from 'react-hook-form';
import '@/assets/form.css';
import * as yup from 'yup';
import {yupResolver} from '@hookform/resolvers/yup';
import { useState,useEffect } from 'react';
import { motion,AnimatePresence } from 'framer-motion';
import {Header} from './layout/Header';
const schema= yup.object({
        extra_s1: yup.number().typeError("veuillez entrer un numéro").positive("numéro des heures doit ètre positive").integer("veuillez entrer un entier"),
        extra_s2: yup.number().typeError("veuillez entrer un numéro").positive("numéro des heures doit ètre positive").integer("veuillez entrer un entier"),
    }).required();
function Form() {
    const currentYear = new Date().getFullYear();
    
    const { register, handleSubmit, formState: { errors } } = useForm({
        resolver: yupResolver(schema),
        mode: "onChange",
    });
    const [extra_s1, setSelected] = useState("");
    const [extra_s2, setSelected2] = useState("");
    const [valid, setIsValid] = useState("");
    const [value, setValue] = useState("");
    useEffect(() => {
            window.extra_s1 = extra_s1;
            window.extra_s2 = extra_s2;
            window.errors = errors;
        },[extra_s1,extra_s2,errors]);
    const choices = [
        'choix\n1',
        'choix\n2',
        'choix\n3',
    ];
    const levels = [
        'L1',
        'L2',
        'L3',
        'M1',
        'M2',
    ];
    const specialties = [
        'cyber security',
        'GTR',
        'acad',
        'isil',
        'ing',
        'bioinfo',
    ];
    const modules = [
        'Module 1',
        'Module 2',
        'Module 3',
        'Module 4',
        'Module 5',
    ];
    return (
        <>
        
        <Header title="Form" showsidebar={false} showsearch={false}/>
                <form className="bg-white p-4 flex justify-center items-center flex-col gap-4" onSubmit={handleSubmit((data) => console.log(data))}>
{/*<div className="section">
                <label htmlFor="name text-lg font-bold">Full Name</label>
                    <input type="text" placeholder='Full Name' className='' />
            </div>
            <div className="section">
                <label htmlFor="email">Email</label>
                <input type="email" placeholder='Email' />
            </div>
            <div className="section">
                <label htmlFor="Grade">Grade</label>
                <div className="grade-radio flex flex-col gap-2">
                    {["MAA", "MAB", "MCB", "MCA", "PROF"].map((grade, index) => (
                        <label key={index} className="inline-flex items-center w-32">
                            <input type="radio" name="grade" className="mr-2"/>
                            <span>{grade}</span>
                        </label>
                    ))}
                </div>
            </div>*/}
            <div className="section">
                <label>Choix d'enseignements du Semestre1</label>
                <table className="choise-table">
                    <thead>
                        <tr>
                            <th className="text-center">Choix</th>
                            <th className="text-center">Niveau</th>
                            <th className="text-center">Spécialité</th>
                            <th className="text-center">Module</th>
                            <th className="text-center">Cours</th>
                            <th className="text-center">TD</th>
                            <th className="text-center">TP</th>
                    </tr>
                    </thead>
                    <tbody>
                        {choices.map((choix, index) => (
                            <tr key={index} className="">
                                <td>{choix}</td>
                                <td>
                                    <select className="bg-gray-200 m-3 p-1 rounded">
                                        {levels.map((level, index) => (
                                            <option key={index} value={level}>{level}</option>
                                        ))}
                                    </select>
                                </td>
                                <td>
                                    <select className="bg-gray-200 m-3 p-1 rounded">
                                        {specialties.map((specialty, index) => (
                                            <option key={index} value={specialty}>{specialty}</option>
                                        ))}
                                    </select>
                                </td>
                                <td>
                                    <select className="bg-gray-200 m-3 p-1 rounded">
                                        {modules.map((module, index) => (
                                            <option key={index} value={module}>{module}</option>
                                        ))}
                                    </select>
                                </td>
                                <td className="text-center align-middle"><input type="checkbox" /></td>
                                <td className="text-center align-middle"><input type="checkbox" /></td>
                                <td className="text-center align-middle"><input type="checkbox" /></td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
            <div className="section">
                <label htmlFor="extra">Voulez vous assurer des enseignements en heurs supp en S1?</label>
                <div className="extra-radio flex items-center flex-col gap-4">
                   <label className="flex"><input type="radio" name="extra-s1" value="extra_s1"  className="w-[24px] mr-2" onChange={(e) => setSelected(e.target.value)} /><span>Oui</span></label>
                   <label className="flex"><input type="radio" name="extra-s1" value="no" className="w-[24px] mr-2" onChange={(e) => setSelected(e.target.value)} /><span>Non</span></label>
                </div>
            </div>
            <AnimatePresence initial={false} >
                {extra_s1 =="extra_s1" &&
                (<motion.div layout initial={{ y:-10,opacity:0}} animate={{y:0,opacity:1}} exit={{ y:-10,opacity:0}} transition={{duration: 0.2}} className="section shadow rounded-xl  "> 
                    <label htmlFor="extra-choix">precisez combien d'heures suplimentaires pour le S1</label>
                    <input {...register("extra_s1")} type="text" name="extra_s1" className="extra-input border p-1 rounded" placeholder="veuillez entrer un numéro" />
                    {errors.extra_s1 && <p className="text-red-500 transition-opacity duration-200 mt-5">{errors.extra_s1.message}</p>}
                </motion.div>)}
            </AnimatePresence>
            <div className="section">
                <label htmlFor="extra">Voulez vous assurer des enseignements en heurs supp en S2?</label>
                <div className="extra-radio flex items-center flex-col gap-4">
                   <label className="flex"><input type="radio" name="extra-s2" value="extra_s2" className="w-[24px] mr-2" onChange={(e) => setSelected2(e.target.value)} /><span>Oui</span></label>
                   <label className="flex"><input type="radio" name="extra-s2" value="no" className="w-[24px] mr-2" onChange={(e) => setSelected2(e.target.value)} /><span>Non</span></label>
                </div>
            </div>
            <AnimatePresence >
                {extra_s2 =="extra_s2" &&
                    (<motion.div layout initial={{ y:-10,opacity:0}} animate={{y:0,opacity:1}} exit={{ y:-10,opacity:0}} transition={{duration: 0.2,ease:"easeInOut"}} className="section rounded-xl p-4 shadow overflow-hidden">
                    <label htmlFor="extra-choix">precisez combien d'heures suplimentaires pour le S2</label>
                    <input {...register("extra_s2")} type="text" name="extra_s2" className="extra-input border p-1 rounded" placeholder="veuillez entrer un numéro" />
                    {errors.extra_s2 && <p className="text-red-500">{errors.extra_s2.message}</p>}
                </motion.div>)}
</AnimatePresence>
            <div className="section">
                <label> Nombre de PFE Licence prévus pour l'année {currentYear}/{currentYear+1}</label>
                <input type="text" name="pfe-licence" className="pfe-licence border p-1 rounded" placeholder="veuillez entrer un numéro" />
            </div>
            <div className="section">
                <label> Nombre de PFE Master prévus pour l'année {currentYear}/{currentYear+1}</label>
                <input type="text" name="pfe-master" className="pfe-master border p-1 rounded" placeholder="veuillez entrer un numéro" />
            </div>

        </form>
        </>
    );
    }
export default Form;
