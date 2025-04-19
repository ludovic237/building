export class User {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  role: 'locataire' | 'visiteur';
  motDePasse: string;
}

export class Tenant {
  id: number;
  userId: number;
  dateEntree: Date;
  dateSortie: Date;
  logementId: number;
  depotGarantie: number;
}

export class HousingUnit {
  id: number;
  number: string;
  floor: number;
  area: number;
  address: string;
  type: 'studio' | 'T2' | 'T3' | 'T4';
}

export class Rent {
  id: number;
  logementId: number;
  locataireId: number;
  mois: number;
  annee: number;
  montant: number;
  statut: 'payé' | 'non payé' | 'en retard';
  datePaiement?: Date;
}

export class Invoice {
  id: number;
  locataireId: number;
  type: 'eau' | 'électricité' | 'gaz' | 'autre';
  mois: number;
  montant: number;
  statut: 'payée' | 'impayée';
  datePaiement?: Date;
}

export class Issue {
  id: number;
  locataireId: number;
  titre: string;
  description: string;
  dateDeclaration: Date;
  statut: 'ouvert' | 'en cours' | 'résolu';
}

export class Service {
  id: number;
  nom: string;
  description: string;
  prixMensuel: number;
}

export class Subscription {
  id: number;
  locataireId?: number;
  utilisateurId?: number;
  serviceId: number;
  dateDebut: Date;
  dateFin?: Date;
  statut: string;
}
