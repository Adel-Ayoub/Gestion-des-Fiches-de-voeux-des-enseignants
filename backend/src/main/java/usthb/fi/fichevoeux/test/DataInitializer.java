package usthb.fi.fichevoeux.test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import usthb.fi.fichevoeux.module.Module;
import usthb.fi.fichevoeux.module.ModuleRepository;
import usthb.fi.fichevoeux.user.Role;
import usthb.fi.fichevoeux.user.User;
import usthb.fi.fichevoeux.user.UserRepository;

@Configuration
public class DataInitializer {

    private int getConceptualSemesterType(String levelString, String moduleNameForLog) {
        if (levelString == null) {
            System.err.println("!!!! Level string is null for module '" + moduleNameForLog + "'. Cannot determine semester type. Defaulting to 0 (ERROR).");
            return 0;
        }
        String upperLevel = levelString.toUpperCase();
        if (upperLevel.contains("-S1") || upperLevel.contains("-S3") || upperLevel.contains("-S5") || upperLevel.contains("-S7") || upperLevel.contains("-S9")) {
            return 1;
        } else if (upperLevel.contains("-S2") || upperLevel.contains("-S4") || upperLevel.contains("-S6") || upperLevel.contains("-S8")) {
            return 2;
        } else {
            System.err.println("!!!! Could not determine conceptual semester type (1 or 2) from level '" + levelString + "' for module '" + moduleNameForLog + "'. Defaulting to 0 (ERROR). Please check level format for S<number> pattern.");
            return 0;
        }
    }

    @Bean
    @Order(1)
    CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail("admin@etu.usthb.com")) {
                User adminUser = new User();
                adminUser.setEmail("admin@etu.usthb.com");
                adminUser.setPassword(passwordEncoder.encode("password"));
                adminUser.setName("Admin User");
                adminUser.setRole(Role.ADMIN);
                userRepository.save(adminUser);
                System.out.println(">>>> Created ADMIN user: admin@etu.usthb.com / password");
            }

            if (!userRepository.existsByEmail("teacher@etu.usthb.com")) {
                User teacherUser = new User();
                teacherUser.setEmail("teacher@etu.usthb.com");
                teacherUser.setPassword(passwordEncoder.encode("password"));
                teacherUser.setName("Teacher User");
                teacherUser.setRole(Role.TEACHER);
                userRepository.save(teacherUser);
                System.out.println(">>>> Created TEACHER user: teacher@etu.usthb.com / password");
            }
        };
    }

    @Bean
    @Order(2)
    CommandLineRunner initCatalog(ModuleRepository moduleRepository) {
        return args -> {
            if (moduleRepository.count() == 0) {
                System.out.println(">>>> Initializing Course Catalog with TD/TP flags and derived semester...");

                @FunctionalInterface
                interface ModuleCreator {
                    void create(ModuleRepository repo, String name, String level, boolean hasTd, boolean hasTp);
                }

                ModuleCreator createAndSaveModule = (repo, name, level, hasTd, hasTp) -> { // Changed boolean hasTp to hasTp for clarity
                    int conceptualSemester = getConceptualSemesterType(level, name);
                    if (conceptualSemester == 0) {
                        System.err.println("SKIPPING module '" + name + "' (level: " + level + ") due to conceptual semester parsing error. PLEASE CHECK LEVEL FORMAT.");
                        return;
                    }
                    repo.save(new Module(null, name, level, conceptualSemester, hasTd, hasTp));
                };

                createAndSaveModule.create(moduleRepository, "Algorithmique et structure de données 1", "L1-TC-S1", true, true);
                createAndSaveModule.create(moduleRepository, "Analyse mathématique 1", "L1-TC-S1", true, false);
                createAndSaveModule.create(moduleRepository, "Architecture des ordinateurs 1", "L1-TC-S1", true, false);
                createAndSaveModule.create(moduleRepository, "Introduction aux systèmes d'exploitation 1", "L1-TC-S1", true, true);
                createAndSaveModule.create(moduleRepository, "Jabr 1", "L1-TC-S1", true, false);
                createAndSaveModule.create(moduleRepository, "Technique d'expression écrite", "L1-TC-S1", false, false);
                createAndSaveModule.create(moduleRepository, "Bureautique et Web", "L1-TC-S1", false, false);

                createAndSaveModule.create(moduleRepository, "Algorithmique et structure de données 2", "L1-TC-S2", true, true);
                createAndSaveModule.create(moduleRepository, "Analyse mathématique 2", "L1-TC-S2", true, false);
                createAndSaveModule.create(moduleRepository, "Introduction aux systèmes d'exploitation 2", "L1-TC-S2", true, true);
                createAndSaveModule.create(moduleRepository, "Algèbre 2", "L1-TC-S2", true, false);
                createAndSaveModule.create(moduleRepository, "Logique mathématique", "L1-TC-S2", true, false);
                createAndSaveModule.create(moduleRepository, "Probabilités et statistique 1", "L1-TC-S2", true, false);
                createAndSaveModule.create(moduleRepository, "Technique d'expression orale", "L1-TC-S2", false, false);

                createAndSaveModule.create(moduleRepository, "Structures de fichiers et structure de données", "L2-TC-S3", true, true);
                createAndSaveModule.create(moduleRepository, " Algèbre 3", "L2-TC-S3", true, true);
                createAndSaveModule.create(moduleRepository, "Programmation orientée objet 1", "L2-TC-S3", false, true);
                createAndSaveModule.create(moduleRepository, "Algorithmique et complexité", "L2-TC-S3", true, false);
                createAndSaveModule.create(moduleRepository, "Architecture des ordinateurs 2", "L2-TC-S3", true, false);
                createAndSaveModule.create(moduleRepository, "Analyse mathématique 3", "L2-TC-S3", true, false);
                createAndSaveModule.create(moduleRepository, "Probabilités et statistiques 2", "L2-TC-S3", true, false);
                createAndSaveModule.create(moduleRepository, "Al-Moqawalatya", "L2-TC-S3", false, false);

                createAndSaveModule.create(moduleRepository, "Programmation orientée objet 2", "L2-TC-S4", false, true);
                createAndSaveModule.create(moduleRepository, "Introduction aux systèmes d'information", "L2-TC-S4", true, false);
                createAndSaveModule.create(moduleRepository, "Introduction aux réseaux informatiques", "L2-TC-S4", true, true);
                createAndSaveModule.create(moduleRepository, "Introduction aux bases de données", "L2-TC-S4", true, false);
                createAndSaveModule.create(moduleRepository, "Théorie des langages", "L2-TC-S4", true, false);
                createAndSaveModule.create(moduleRepository, "Théorie des graphes", "L2-TC-S4", true, false);
                createAndSaveModule.create(moduleRepository, "Projet pluridisciplinaire", "L2-TC-S4", false, true);
                createAndSaveModule.create(moduleRepository, "Anglais", "L2-TC-S4", false, false);

                createAndSaveModule.create(moduleRepository, "Algorithmique et Complexité Avancées", "ING3-GL-S5", true, true);
                createAndSaveModule.create(moduleRepository, "Génie logiciel (GL)", "ING3-GL-S5", true, true);
                createAndSaveModule.create(moduleRepository, "BDD: Administration et Architecture", "ING3-GL-S5", false, true);
                createAndSaveModule.create(moduleRepository, "Techniques d'Optimisation (TOP)", "ING3-GL-S5", true, false);
                createAndSaveModule.create(moduleRepository, "Fondements de l'IA (FIA)", "ING3-GL-S5", false, true);

                createAndSaveModule.create(moduleRepository, "Conception de logiciels", "ING3-GL-S6", true, true);
                createAndSaveModule.create(moduleRepository, "Programmation WEB (PWEB)", "ING3-GL-S6", false, true);
                createAndSaveModule.create(moduleRepository, "BDD: Optimisation et gestion des accès concurrents", "ING3-GL-S6", true, true);
                createAndSaveModule.create(moduleRepository, "Compilation 1 (COMPIL1)", "ING3-GL-S6", true, true);
                createAndSaveModule.create(moduleRepository, "Analyse numérique", "ING3-GL-S6", true, true);
                createAndSaveModule.create(moduleRepository, "Introduction à la sécurité Informatique (ISEC)", "ING3-GL-S6", true, true);

                createAndSaveModule.create(moduleRepository, "Concepts avancés de BD", "ING4-GL-S7", true, true);
                createAndSaveModule.create(moduleRepository, "Gestion de projets (GP)", "ING4-GL-S7", true, true);
                createAndSaveModule.create(moduleRepository, "Web avancé et Micro-services", "ING4-GL-S7", false, true);
                createAndSaveModule.create(moduleRepository, "Data-Mining", "ING4-GL-S7", true, true);
                createAndSaveModule.create(moduleRepository, "Compilation 2", "ING4-GL-S7", true, true);
                createAndSaveModule.create(moduleRepository, "Méthodes de management agiles", "ING4-GL-S7", false, true);
                createAndSaveModule.create(moduleRepository, "Réseaux et protocoles", "ING4-GL-S7", false, true);
                createAndSaveModule.create(moduleRepository, "IHM: Conception et évaluation des interfaces", "ING4-GL-S7", false, true);

                createAndSaveModule.create(moduleRepository, "Architecture et Gestion des Systèmes d'Information Avancés", "ING4-GL-S8", true, true);
                createAndSaveModule.create(moduleRepository, "Big-Data et Base de données NoSQL", "ING4-GL-S8", true, true);
                createAndSaveModule.create(moduleRepository, "Architectures Logicielles", "ING4-GL-S8", false, true);
                createAndSaveModule.create(moduleRepository, "Modèles et Gestion de Procédés Logiciels", "ING4-GL-S8", true, true);
                createAndSaveModule.create(moduleRepository, "Test Logiciel et Assurance qualité", "ING4-GL-S8", false, true);
                createAndSaveModule.create(moduleRepository, "Modélisation et Evaluation des Performances", "ING4-GL-S8", true, false);
                createAndSaveModule.create(moduleRepository, "Systèmes d'exploitation Mobiles", "ING4-GL-S8", false, true);
                createAndSaveModule.create(moduleRepository, "Projet Pluridisciplinaire GL S8", "ING4-GL-S8", false, true);

                createAndSaveModule.create(moduleRepository, "Méthodes formelles pour GL", "ING5-GL-S9", true, true);
                createAndSaveModule.create(moduleRepository, "Développement de logiciels embarqués", "ING5-GL-S9", false, true);
                createAndSaveModule.create(moduleRepository, "Conception de jeux vidéo: Théorie et Pratique", "ING5-GL-S9", false, true);
                createAndSaveModule.create(moduleRepository, "Internet of Things (IoT): Concepts et développement", "ING5-GL-S9", true, true);
                createAndSaveModule.create(moduleRepository, "DevOPs & Cloud Computing", "ING5-GL-S9", false, true);
                createAndSaveModule.create(moduleRepository, "Sécurité Logicielle", "ING5-GL-S9", false, true);
                createAndSaveModule.create(moduleRepository, "Développement mobile GL S9", "ING5-GL-S9", false, true);
                createAndSaveModule.create(moduleRepository, "Aspects juridiques", "ING5-GL-S9", false, false);

                createAndSaveModule.create(moduleRepository, "Architecture et administration des BDD IA", "ING3-IA-S5", false, true);
                createAndSaveModule.create(moduleRepository, "Compilation IA", "ING3-IA-S5", true, true);
                createAndSaveModule.create(moduleRepository, "Programmation Linéaire et Dynamique", "ING3-IA-S5", true, true);
                createAndSaveModule.create(moduleRepository, "Analyse numérique 1 IA", "ING3-IA-S5", true, true);
                createAndSaveModule.create(moduleRepository, "Génie logiciel IA", "ING3-IA-S5", true, true);
                createAndSaveModule.create(moduleRepository, "Fondements de l'IA IA", "ING3-IA-S5", false, true);
                createAndSaveModule.create(moduleRepository, "Développement mobile IA S5", "ING3-IA-S5", false, true);
                createAndSaveModule.create(moduleRepository, "Interface Homme Machine IA S5", "ING3-IA-S5", false, true);
                createAndSaveModule.create(moduleRepository, "Conception de jeux vidéo IA S5", "ING3-IA-S5", false, true);

                createAndSaveModule.create(moduleRepository, "Gestion des bases de données réparties", "ING3-IA-S6", false, true);
                createAndSaveModule.create(moduleRepository, "Système d'Exploitation: Synchronisation et Communication IA", "ING3-IA-S6", true, true);
                createAndSaveModule.create(moduleRepository, "Gestion de projets IA", "ING3-IA-S6", true, true);
                createAndSaveModule.create(moduleRepository, "Programmation WEB IA", "ING3-IA-S6", false, true);
                createAndSaveModule.create(moduleRepository, "Analyse numérique 2 IA", "ING3-IA-S6", true, true);
                createAndSaveModule.create(moduleRepository, "Introduction à la sécurité Informatique IA", "ING3-IA-S6", true, true);
                createAndSaveModule.create(moduleRepository, "Ethique de l'IA", "ING3-IA-S6", false, false);

                createAndSaveModule.create(moduleRepository, "Représentation des connaissances et raisonnement", "ING4-IA-S7", true, true);
                createAndSaveModule.create(moduleRepository, "Calcul haute performance", "ING4-IA-S7", true, true);
                createAndSaveModule.create(moduleRepository, "Machine Learning", "ING4-IA-S7", false, true);
                createAndSaveModule.create(moduleRepository, "Modélisation et simulation IA", "ING4-IA-S7", true, true);
                createAndSaveModule.create(moduleRepository, "Business Intelligence IA", "ING4-IA-S7", true, true);
                createAndSaveModule.create(moduleRepository, "Recherche opérationnelle IA", "ING4-IA-S7", true, true);
                createAndSaveModule.create(moduleRepository, "Techniques de rédaction IA", "ING4-IA-S7", false, true);

                createAndSaveModule.create(moduleRepository, "Sécurité des données IA", "ING4-IA-S8", true, true);
                createAndSaveModule.create(moduleRepository, "Deep learning", "ING4-IA-S8", false, true);
                createAndSaveModule.create(moduleRepository, "Traitement Automatique du Langage Naturel", "ING4-IA-S8", false, true);
                createAndSaveModule.create(moduleRepository, "Traitement de données massives", "ING4-IA-S8", false, true);
                createAndSaveModule.create(moduleRepository, "Technologies de Calcul Distribué et Intelligence Artificielle", "ING4-IA-S8", false, true);
                createAndSaveModule.create(moduleRepository, "Analyse et Traitement d'image", "ING4-IA-S8", false, true);
                createAndSaveModule.create(moduleRepository, "Projet pluridisciplinaire IA S8", "ING4-IA-S8", false, true);

                createAndSaveModule.create(moduleRepository, "Visualisation de données", "ING5-IA-S9", false, true);
                createAndSaveModule.create(moduleRepository, "Vision par ordinateur", "ING5-IA-S9", false, true);
                createAndSaveModule.create(moduleRepository, "Generative Al", "ING5-IA-S9", false, true);
                createAndSaveModule.create(moduleRepository, "Méthodes bio-inspirées", "ING5-IA-S9", false, true);
                createAndSaveModule.create(moduleRepository, "Architectures et technologies blockchain IA", "ING5-IA-S9", false, true);
                createAndSaveModule.create(moduleRepository, "Recherche d'information", "ING5-IA-S9", false, true);
                createAndSaveModule.create(moduleRepository, "Séminaire & workshops IA", "ING5-IA-S9", false, false);

                createAndSaveModule.create(moduleRepository, "Mathematical Tools for Cryptography", "ING3-SECU-S5", true, true);
                createAndSaveModule.create(moduleRepository, "Operational Research SECU", "ING3-SECU-S5", true, true);
                createAndSaveModule.create(moduleRepository, "Software Engineering SECU", "ING3-SECU-S5", true, true);
                createAndSaveModule.create(moduleRepository, "Python Programming", "ING3-SECU-S5", false, true);
                createAndSaveModule.create(moduleRepository, "Web Development SECU", "ING3-SECU-S5", false, true);
                createAndSaveModule.create(moduleRepository, "Theory of Information and Coding", "ING3-SECU-S5", true, true);
                createAndSaveModule.create(moduleRepository, "Business Intelligence SECU", "ING3-SECU-S5", true, true);

                createAndSaveModule.create(moduleRepository, "Advanced Cryptography", "ING3-SECU-S6", true, true);
                createAndSaveModule.create(moduleRepository, "Modeling and Simulation SECU", "ING3-SECU-S6", true, true);
                createAndSaveModule.create(moduleRepository, "Cloud Computing SECU", "ING3-SECU-S6", false, true);
                createAndSaveModule.create(moduleRepository, "Advanced Databases", "ING3-SECU-S6", true, true);
                createAndSaveModule.create(moduleRepository, "Mobile Development SECU S6", "ING3-SECU-S6", false, true);
                createAndSaveModule.create(moduleRepository, "Digital Signal Processing", "ING3-SECU-S6", true, true);
                createAndSaveModule.create(moduleRepository, "Al Notions and Principles", "ING3-SECU-S6", true, true);
                createAndSaveModule.create(moduleRepository, "Startup and Professional Development", "ING3-SECU-S6", true, false);

                createAndSaveModule.create(moduleRepository, "Advanced Operating Systems", "ING4-SECU-S7", false, true);
                createAndSaveModule.create(moduleRepository, "Advanced Networks", "ING4-SECU-S7", true, true);
                createAndSaveModule.create(moduleRepository, "Computer Systems Security", "ING4-SECU-S7", false, true);
                createAndSaveModule.create(moduleRepository, "Information and Data Security", "ING4-SECU-S7", false, true);
                createAndSaveModule.create(moduleRepository, "Programming by Constraint", "ING4-SECU-S7", true, true);
                createAndSaveModule.create(moduleRepository, "Machine Learning, Deep Learning, and Security", "ING4-SECU-S7", false, true);
                createAndSaveModule.create(moduleRepository, "Malwares Analysis", "ING4-SECU-S7", true, true);
                createAndSaveModule.create(moduleRepository, "Critical Thinking and Creativity Skills", "ING4-SECU-S7", false, false);

                createAndSaveModule.create(moduleRepository, "Operating Systems Security", "ING4-SECU-S8", false, true);
                createAndSaveModule.create(moduleRepository, "Cybersecurity", "ING4-SECU-S8", false, true);
                createAndSaveModule.create(moduleRepository, "Network Security", "ING4-SECU-S8", true, true);
                createAndSaveModule.create(moduleRepository, "Wireless and Mobile Network Security", "ING4-SECU-S8", false, true);
                createAndSaveModule.create(moduleRepository, "Identity & Access Management", "ING4-SECU-S8", true, true);
                createAndSaveModule.create(moduleRepository, "Secure Software Development", "ING4-SECU-S8", true, true);
                createAndSaveModule.create(moduleRepository, "Innovation and Entrepreneurship SECU S8", "ING4-SECU-S8", false, false);
                createAndSaveModule.create(moduleRepository, "Multidisciplinary Project SECU S8", "ING4-SECU-S8", false, true);

                createAndSaveModule.create(moduleRepository, "Web and mobile application security", "ING5-SECU-S9", false, true);
                createAndSaveModule.create(moduleRepository, "Embedded Systems Security", "ING5-SECU-S9", false, true);
                createAndSaveModule.create(moduleRepository, "Digital Forensics", "ING5-SECU-S9", false, true);
                createAndSaveModule.create(moduleRepository, "DevOps SECU", "ING5-SECU-S9", false, true);
                createAndSaveModule.create(moduleRepository, "Ethical Hacking", "ING5-SECU-S9", false, true);
                createAndSaveModule.create(moduleRepository, "Project Management SECU", "ING5-SECU-S9", false, false);
                createAndSaveModule.create(moduleRepository, "Emerging Security Technologies", "ING5-SECU-S9", false, false);
                createAndSaveModule.create(moduleRepository, "Academic Communication and Research", "ING5-SECU-S9", false, false);

                System.out.println(">>>> Finished Initializing Course Catalog with TD/TP flags for all specialties.");
            } else {
                System.out.println(">>>> Course Catalog already initialized.");
            }
        };
    }

    @FunctionalInterface
    interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }
}