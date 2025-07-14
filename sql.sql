-- Table profil_formule (inchangée)
CREATE TABLE profil_formule (
    id SERIAL PRIMARY KEY,
    profil VARCHAR(255) NOT NULL,
    nombre_de_mois INTEGER NOT NULL,
    nblivre_port INTEGER NOT NULL
);

-- Table users (inchangée)
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    numero BIGINT,
    profil_id INTEGER REFERENCES profil_formule(id)
);

-- Table livre (simplifiée, ne contient plus les exemplaires)
CREATE TABLE livre (
    id SERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    auteur VARCHAR(255) NOT NULL
);

-- Nouvelle table exemplaire
CREATE TABLE exemplaire (
    id SERIAL PRIMARY KEY,
    livre_id INTEGER NOT NULL REFERENCES livre(id),
    num_exemplaire VARCHAR(255) NOT NULL UNIQUE,
    disponible BOOLEAN NOT NULL DEFAULT true,
    date_acquisition DATE,
    emplacement VARCHAR(100),
    CONSTRAINT unique_num_exemplaire UNIQUE (num_exemplaire)
);

-- Table emprunt (mise à jour pour référencer exemplaire au lieu de livre)
CREATE TABLE emprunt (
    id SERIAL PRIMARY KEY,
    exemplaire_id INTEGER NOT NULL REFERENCES exemplaire(id),
    id_nom_emprunteur INTEGER NOT NULL REFERENCES users(id),
    date_debut_emprunt TIMESTAMP NOT NULL,
    date_fin_emprunt TIMESTAMP NOT NULL,
    date_fin_proposee TIMESTAMP,
    type_de_lecture VARCHAR(255),
    is_prolongement BOOLEAN NOT NULL DEFAULT false,
    nombre_prolongement INTEGER NOT NULL DEFAULT 0,
    prolongement_demande BOOLEAN NOT NULL DEFAULT false,
    prolongement_valide BOOLEAN,
    motif_refus_prolongement TEXT
);

-- Table prolongement (inchangée)
CREATE TABLE prolongement (
    id SERIAL PRIMARY KEY,
    emprunt_id INTEGER REFERENCES emprunt(id) NOT NULL,
    date_demande TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_fin_originale TIMESTAMP NOT NULL,
    date_fin_proposee TIMESTAMP NOT NULL,
    valide_par_admin INTEGER REFERENCES users(id),
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    date_validation TIMESTAMP,
    motif_refus TEXT,
    CONSTRAINT statut_valide CHECK (statut IN ('EN_ATTENTE', 'VALIDE', 'REFUSE'))
);

-- Tables reservation, parametres_penalite et penalite (mises à jour si nécessaire)
CREATE TABLE reservation (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    livre_id INTEGER REFERENCES livre(id), -- On réserve un livre, pas un exemplaire spécifique
    date_reservation DATE NOT NULL,
    statut VARCHAR(50) NOT NULL,
    exemplaire_attribue INTEGER REFERENCES exemplaire(id) -- NULL tant qu'aucun exemplaire n'est attribué
);

CREATE TABLE parametres_penalite (
    id SERIAL PRIMARY KEY,
    nom_parametre VARCHAR(255) UNIQUE NOT NULL,
    valeur INTEGER,
    description TEXT
);

CREATE TABLE penalite (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    emprunt_id INTEGER REFERENCES emprunt(id),
    date_debut_penalite TIMESTAMP NOT NULL,
    date_fin_penalite TIMESTAMP NOT NULL,
    motif TEXT,
    statut VARCHAR(50) NOT NULL,
    jours_retard INTEGER NOT NULL,
    duree_penalite_jours INTEGER NOT NULL
);

-- Index
CREATE INDEX idx_exemplaire_livre ON exemplaire(livre_id);
CREATE INDEX idx_emprunt_exemplaire ON emprunt(exemplaire_id);
CREATE INDEX idx_emprunt_emprunteur ON emprunt(id_nom_emprunteur);
CREATE INDEX idx_reservation_user ON reservation(user_id);
CREATE INDEX idx_reservation_livre ON reservation(livre_id);
CREATE INDEX idx_reservation_exemplaire ON reservation(exemplaire_attribue);
CREATE INDEX idx_penalite_user ON penalite(user_id);
CREATE INDEX idx_penalite_emprunt ON penalite(emprunt_id);
CREATE INDEX idx_users_profil ON users(profil_id);