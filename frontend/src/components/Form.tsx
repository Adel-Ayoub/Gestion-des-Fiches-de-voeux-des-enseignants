import React from 'react';

import axios from 'axios';

import { useForm } from 'react-hook-form';

import styles from '@/assets/form.module.css';

import * as yup from 'yup';

import { yupResolver } from '@hookform/resolvers/yup';

import { useState, useEffect } from 'react';

import { motion, AnimatePresence } from 'framer-motion';

import { Header } from './layout/Header';

import { useNavigate, useSearchParams } from 'react-router-dom';

import { jwtDecode } from 'jwt-decode';


type course = {

    id: number;

    moduleName: string;

    semester: number;

    level: string;

    hasTd: boolean;

    hasTp: boolean;

    specialty?: string;

}


interface fichechoice {

    moduleId: number;

    rank: number;

    wantsCours: boolean;

    wantsTd: number;

    wantsTp: number;

    targetSemester: string;

}


interface fichedeveoux {

    academicYear: string;

    wantsSupplementaryHoursS1: number;

    wantsSupplementaryHoursS2: number;

    proposedPfeL: number;

    proposedPfeM: number;

    comments: string;

    semester1Choices?: [fichechoice, fichechoice, fichechoice];

    semester2Choices?: [fichechoice, fichechoice, fichechoice];

}


const schema = yup.object({

    extra_s1: yup.number().typeError("veuillez entrer un numéro").positive("numéro des heures doit ètre positive").integer("veuillez entrer un entier"),

    extra_s2: yup.number().typeError("veuillez entrer un numéro").positive("numéro des heures doit ètre positive").integer("veuillez entrer un entier"),

    proposedPfeL: yup.number().typeError("veuillez entrer un numéro").positive("numéro des heures doit ètre positive").integer("veuillez entrer un entier"),

    proposedPfeM: yup.number().typeError("veuillez entrer un numéro").positive("numéro des heures doit ètre positive").integer("veuillez entrer un entier"),

    comments: yup.string().required("veuillez entrer un commentaire")

}).required();


function Form() {

    const currentYear = new Date().getFullYear();

    const navigate = useNavigate();

    const [searchParams] = useSearchParams();

    const isEditMode = searchParams.get('edit') == 'true';

    const ficheId = searchParams.get('ficheid');

    console.log(ficheId);

    console.log("isEditMode:", isEditMode);

    

    const {

        register,

        handleSubmit,

        formState: { errors },

        getValues,

        setValue,

        watch,

    } = useForm({

        resolver: yupResolver(schema),

        mode: 'onChange',

    });

    

    const [extra_s1, setSelected] = useState("");

    const [extra_s2, setSelected2] = useState("");

    const [valid, setIsValid] = useState("");


    const [courses, setCourses] = useState<course[]>([]);

    const [courseName, setCourseName] = useState("");

    const [level, setLevel] = useState("L1");

    const [specialty, setSpecialty] = useState("cyber security");

    const [semester, setSemester] = useState(1);

    const [hasTd, setHasTd] = useState(true);

    const [hasTp, setHasTp] = useState(true);

    const [comments, setComments] = useState("");

    const [selectedCourses, setSelectedCourses] = useState<course[]>([]);

    const [semester1choices, setSemester1choices] = useState<fichechoice[]>([

        { moduleId: 0, rank: 1, wantsCours: false, wantsTd: 0, wantsTp: 0, targetSemester: 'S1' },

        { moduleId: 0, rank: 2, wantsCours: false, wantsTd: 0, wantsTp: 0, targetSemester: 'S1' },

        { moduleId: 0, rank: 3, wantsCours: false, wantsTd: 0, wantsTp: 0, targetSemester: 'S1' },

    ]);

    const [semester2choices, setSemester2choices] = useState<fichechoice[]>([

        { moduleId: 0, rank: 1, wantsCours: false, wantsTd: 0, wantsTp: 0, targetSemester: 'S2' },

        { moduleId: 0, rank: 2, wantsCours: false, wantsTd: 0, wantsTp: 0, targetSemester: 'S2' },

        { moduleId: 0, rank: 3, wantsCours: false, wantsTd: 0, wantsTp: 0, targetSemester: 'S2' },

    ]);


    useEffect(() => {

        const token = localStorage.getItem("jwt");

        console.log("fetching courses");

        const fetchCourses = async () => {

            const res = await axios.get('http://localhost:8080/api/modules', { headers: { authorization: token } })

            const data: course[] = await res.data;

            const parsed = data.map(course => {

                const [level, speciality, semesterstr] = course.level.split("-");

                return {

                    id: course.id,

                    moduleName: course.moduleName,

                    level: level,

                    specialty: speciality,

                    semester: course.semester,

                    hasTd: course.hasTd,

                    hasTp: course.hasTp,

                };

            });

            setCourses(parsed);

            setSelectedCourses(parsed);

        };

        fetchCourses();

    }, []);


    useEffect(() => {

        if (isEditMode && ficheId) {

            const fetchFicheData = async () => {

                try {

                    const token = localStorage.getItem("jwt");

                    const response = await axios.get(`http://localhost:8080/api/fiches-de-voeux/${ficheId}`, {

                        headers: { Authorization: token }

                    });

                    const ficheData = response.data as fichedeveoux;

                    console.log("Fetched fiche data:", ficheData);

                    const res = await axios.get<fichedeveoux[]>(`http://localhost:8080/ficheChoice/by-fiche/${ficheId}`, {

                        headers: {

                            'Authorization': localStorage.getItem('jwt'),

                        }

                    });

                    const semester1Choices = [];

                    const semester2Choices = [];

                    console.log("Fetched choices data:", ficheData);

                    res.data.forEach((choice: fichechoice) => {

                        if (choice.targetSemester === 'S1') {

                            semester1Choices.push(choice);

                        } else if (choice.targetSemester === 'S2') {

                            semester2Choices.push(choice);

                        }

                    });

                    ficheData.semester1Choices = semester1Choices;

                    ficheData.semester2Choices = semester2Choices;

                    console.log("Fetched choices:", ficheData.semester1Choices, ficheData.semester2Choices);


                    setSemester1choices(ficheData.semester1Choices || semester1choices);

                    setSemester2choices(ficheData.semester2Choices || semester2choices);


                    console.log("Fetched choices:", res.data);


                    setValue('extra_s1', ficheData.wantsSupplementaryHoursS1);

                    setValue('extra_s2', ficheData.wantsSupplementaryHoursS2);

                    setValue('proposedPfeL', ficheData.proposedPfeL);

                    setValue('proposedPfeM', ficheData.proposedPfeM);

                    setValue('comments', ficheData.comments);


                    if (ficheData.wantsSupplementaryHoursS1 > 0) {

                        setSelected('extra_s1');

                    }

                    if (ficheData.wantsSupplementaryHoursS2 > 0) {

                        setSelected2('extra_s2');

                    }


                    if (ficheData.semester1Choices) {

                        setSemester1choices(ficheData.semester1Choices);

                    }

                    if (ficheData.semester2Choices) {

                        setSemester2choices(ficheData.semester2Choices);

                    }

                } catch (error) {

                    console.error("Error fetching fiche data:", error);

                }

            };

            fetchFicheData();

        }

    }, [isEditMode, ficheId, setValue]);


    const filtercourses = () => {

        const key = `${level}-${specialty}-${semester}`;

        return selectedCourses.filter(c => c.moduleName === key);

    };


    const filterbyLevel = () => {

        return courses.filter(c => c.level === level);

    }


    const filterbySpecialty = () => {

        return courses.filter(c => c.specialty === specialty);

    }


    const handleSelect = (index: number, moduleId: number) => {

        const selectedCourse = courses.find(c => c.id === moduleId);

        if (selectedCourse) {

            setSelectedCourses(prevCourses => {

                const newCourses = [...selectedCourses];

                newCourses[index] = selectedCourse;

                return newCourses;

            });

        }

    };


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

        'ING1',

        'ING2',

        'ING3',

        'ING4',

        'ING5',

    ];


    const specialties = [

        'cyber security',

        'GTR',

        'acad',

        'isil',

        'ing',

        'bioinfo',

    ];


    const groups = [0, 1, 2, 3, 4];

    const coursesS1 = courses.filter(c => c.semester === 1);

    const coursesS2 = courses.filter(c => c.semester === 2);


    const handleChoiceChange = (

        semester: 'S1' | 'S2',

        index: number,

        field: keyof fichechoice,

        value: any

    ) => {

        const update = (prev: fichechoice[]) => {

            const newChoices = [...prev];

            newChoices[index] = {

                ...newChoices[index],

                [field]: value,

            };

            return newChoices;

        };

        if (semester === 'S1') setSemester1choices((prev) => update(prev));

        else setSemester2choices((prev) => update(prev));

    };


    const handleCancel = () => {

        navigate('/profile');

    }


    const handleFinalSubmit = () => {

        const token = localStorage.getItem("jwt");

        const decodedToken = jwtDecode(token);

        console.log("Decoded token:", decodedToken);

        const academicYear = `${currentYear}/${currentYear + 1}`;

        const payload: fichedeveoux = {

            academicYear,

            wantsSupplementaryHoursS1: parseInt(getValues("extra_s1") || "0"),

            wantsSupplementaryHoursS2: parseInt(getValues("extra_s2") || "0"),

            proposedPfeL: parseInt(getValues("proposedPfeL") || "0"),

            proposedPfeM: parseInt(getValues("proposedPfeM") || "0"),

            comments: getValues("comments") || "123",

            semester1Choices: semester1choices,

            semester2Choices: semester2choices,

        };

        axios.post("http://localhost:8080/api/fiches-de-voeux/submissions/yearly", payload, {

            headers: { authorization: localStorage.getItem("jwt") }

        }).catch(err => console.error("Error", err));

    };


    const renderChoiceTable = (semester: 'S1' | 'S2', choices: fichechoice[], setFn: typeof handleChoiceChange, filteredCourses: course[]) => (

        <div className={styles.tableContainer}>

            <table className={styles.choiseTable}>

                <thead>

                    <tr>

                        <th>Choix</th>

                        <th>Module</th>

                        <th>Cours</th>

                        <th>TD</th>

                        <th>TP</th>

                    </tr>

                </thead>

                <tbody>

                    {choices.map((choice, index) => (

                        <tr key={index}>

                            <td>{`Choix ${index + 1}`}</td>

                            <td>

                                <select

                                    value={choice.moduleId}

                                    onChange={(e) => setFn(semester, index, 'moduleId', parseInt(e.target.value))}

                                >

                                    <option value={0}>--Sélectionner--</option>

                                    {filteredCourses.map((c) => (

                                        <option key={c.id} value={c.id}>

                                            {c.moduleName}

                                        </option>

                                    ))}

                                </select>

                            </td>

                            <td>

                                <input

                                    type="checkbox"

                                    checked={choice.wantsCours}

                                    onChange={(e) => setFn(semester, index, 'wantsCours', e.target.checked)}

                                />

                            </td>

                            <td>

                                <select

                                    value={choice.wantsTd}

                                    disabled={courses.length !== 0 && choice.moduleId !== 0 && !courses[choice.moduleId - 1].hasTd}

                                    onChange={(e) => setFn(semester, index, 'wantsTd', parseInt(e.target.value))}

                                >

                                    {groups.map((group) => (

                                        <option key={group} value={group}>

                                            {group}

                                        </option>

                                    ))}

                                </select>

                            </td>

                            <td>

                                <select

                                    value={choice.wantsTp}

                                    disabled={courses.length !== 0 && choice.moduleId !== 0 && !courses[choice.moduleId - 1].hasTp}

                                    onChange={(e) => setFn(semester, index, 'wantsTp', parseInt(e.target.value))}

                                >

                                    {groups.map((group) => (

                                        <option key={group} value={group}>

                                            {group}

                                        </option>

                                    ))}

                                </select>

                            </td>

                        </tr>

                    ))}

                </tbody>

            </table>

        </div>

    );


    return (

        <>

            <Header title="Form" showsidebar={false} showsearch={false} />

            <div className={styles.main}>

                <form className={styles.formContainer}>

                    <div className={styles.section}>

                        <label>Choix d'enseignements du Semestre 1</label>

                        {renderChoiceTable('S1', semester1choices, handleChoiceChange, coursesS1)}

                    </div>

                    <div className={styles.section}>

                        <label>Choix d'enseignements du Semestre 2</label>

                        {renderChoiceTable('S2', semester2choices, handleChoiceChange, coursesS2)}

                    </div>

                    <div className={styles.section}>

                        <label htmlFor="extra">Voulez vous assurer des enseignements en heurs supp en S1?</label>

                        <div className={`${styles.extraRadio} flex items-center flex-col gap-4`}>

                            <label className="flex"><input type="radio" name="extra-s1" value="extra_s1" className="w-[24px] mr-2" onChange={(e) => setSelected(e.target.value)} /><span>Oui</span></label>

                            <label className="flex"><input type="radio" name="extra-s1" value="no" className="w-[24px] mr-2" onChange={(e) => setSelected(e.target.value)} /><span>Non</span></label>

                        </div>

                    </div>

                    <AnimatePresence initial={false} >

                        {extra_s1 == "extra_s1" &&

                            (<motion.div layout initial={{ y: -10, opacity: 0 }} animate={{ y: 0, opacity: 1 }} exit={{ y: -10, opacity: 0 }} transition={{ duration: 0.2 }} className={`${styles.section} shadow rounded-xl`}>

                                <label htmlFor="extra-choix">precisez combien d'heures suplimentaires pour le S1</label>

                                <input {...register("extra_s1")} type="text" name="extra_s1" className="extra-input border p-1 rounded" placeholder="veuillez entrer un numéro" />

                                {errors.extra_s1 && <p className="text-red-500 transition-opacity duration-200 mt-5">{errors.extra_s1.message}</p>}

                            </motion.div>)}

                    </AnimatePresence>

                    <div className={styles.section}>

                        <label htmlFor="extra">Voulez vous assurer des enseignements en heurs supp en S2?</label>

                        <div className={`${styles.extraRadio} flex items-center flex-col gap-4`}>

                            <label className="flex"><input type="radio" name="extra-s2" value="extra_s2" className="w-[24px] mr-2" onChange={(e) => setSelected2(e.target.value)} /><span>Oui</span></label>

                            <label className="flex"><input type="radio" name="extra-s2" value="no" className="w-[24px] mr-2" onChange={(e) => setSelected2(e.target.value)} /><span>Non</span></label>

                        </div>

                    </div>

                    <AnimatePresence >

                        {extra_s2 == "extra_s2" &&

                            (<motion.div layout initial={{ y: -10, opacity: 0 }} animate={{ y: 0, opacity: 1 }} exit={{ y: -10, opacity: 0 }} transition={{ duration: 0.2, ease: "easeInOut" }} className={`${styles.section} rounded-xl p-4 shadow overflow-hidden`}>

                                <label htmlFor="extra-choix">precisez combien d'heures suplimentaires pour le S2</label>

                                <input {...register("extra_s2")} type="text" name="extra_s2" className="extra-input border p-1 rounded" placeholder="veuillez entrer un numéro" />

                                {errors.extra_s2 && <p className="text-red-500">{errors.extra_s2.message}</p>}

                            </motion.div>)}

                    </AnimatePresence>

                    <div className={styles.section}>

                        <label> Nombre de PFE Licence prévus pour l'année {currentYear}/{currentYear + 1}</label>

                        <input {...register("proposedPfeL")} type="text" name="proposedPfeL" className="pfe-licence border p-1 rounded" placeholder="veuillez entrer un numéro" />

                    </div>

                    <div className={styles.section}>

                        <label> Nombre de PFE Master prévus pour l'année {currentYear}/{currentYear + 1}</label>

                        <input {...register("proposedPfeM")} type="text" name="proposedPfeM" className="pfe-master border p-1 rounded" placeholder="veuillez entrer un numéro" />

                    </div>

                    <div className={styles.section}>

                        <label className={styles.optional}> enter a comment</label>

                        <input {...register("comments")} type="text" name="comments" className="comments border p-1 rounded" placeholder="veuillez entrer votre commentaire" />

                    </div>

                    <div className="flex flex-row gap-6">{isEditMode && (

                        <button

                            type="button"

                            className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"

                            onClick={handleCancel}

                        >

                            Annuler

                        </button>)}

                        <button

                            type="button"

                            className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"

                            onClick={handleFinalSubmit}>

                            {isEditMode ? "Modifier" : "Envoyer"}</button>

                    </div>

                </form>

            </div>

        </>

    );

}


export default Form;