# RAPPORT DE SÉCURISATION — PARTIE II

## Projet DIGITRANS-CM — CAMTECH SOLUTIONS S.A. / AGROCAM S.A.

---

## SOMMAIRE

**II.2 — STRATÉGIE DE SÉCURITÉ**
1. Identification de 4 risques de sécurité clés dans le Cloud
2. Responsabilités selon le modèle de responsabilité partagée
3. Politique IAM — Rôles et droits d'accès
4. Procédure de gestion des droits en cas de départ d'un développeur
5. Politique de rotation des clés pour la base de données
6. Plan de réponse aux incidents (IRP)
7. Chiffrement des données en transit et au repos
8. Guide de bonnes pratiques pour la sécurité dans le Cloud

**II.3 — SÉCURITÉ DES TRANSACTIONS ET DES DONNÉES (BLOCKCHAIN)**
1. Plateforme blockchain retenue : Hyperledger Fabric
2. Structure d'un bloc
3. Mécanisme de consensus : Raft
4. Réponse à la loi camerounaise n°2010/012
5. Smart contract développé
6. Interaction du smart contract avec les modules SI d'AGROCAM
7. Bonnes pratiques de sécurité et vulnérabilités classiques
8. Question situationnelle : clé privée d'un nœud compromise
9. Extension à des partenaires internationaux

---

## INTRODUCTION

Ce rapport présente la stratégie de sécurisation du SI DIGITRANS-CM déployé sur une infrastructure cloud hybride AWS/Azure, ainsi que la solution blockchain Hyperledger Fabric pour la traçabilité des transactions de la Supply Chain d'AGROCAM S.A. Il répond aux exigences de la loi camerounaise n°2010/012 sur la cybersécurité et la souveraineté des données, dans le cadre du budget de 480M FCFA alloué au projet.

---

## II.2 — STRATÉGIE DE SÉCURITÉ

### 1. Identification de 4 risques de sécurité clés dans le Cloud

| N° | Risque | Description | Niveau |
|----|--------|-------------|--------|
| R1 | **Fuite de données sensibles** | Données RH, financières et clients exposées via API mal configurées, bucket S3 public, ou RDS accessible depuis Internet | Critique |
| R2 | **Indisponibilité du service** | Coupures réseau à Douala rendant l'application inaccessible ; dépendance à un seul cloud sans basculement multi-région | Élevé |
| R3 | **Compromission des identités Azure AD** | Vol de tokens JWT, force brute sur comptes Azure AD, phishing administrateurs | Critique |
| R4 | **Non-conformité réglementaire** | Données souveraines (loi n°2010/012) hébergées hors Cameroun sans autorisation ; absence de traçabilité | Élevé |

### 2. Responsabilités selon le modèle de responsabilité partagée

| Risque | Responsabilité AGROCAM | Responsabilité Fournisseur Cloud |
|--------|----------------------|----------------------------------|
| **R1** | Configurer groupes sécurité, chiffrer données, gérer clés | AWS = sécurité du cloud (physique/réseau) ; Azure AD = infrastructure d'identité |
| **R2** | Architecture multi-AZ, offline-first, test basculement | AWS = SLA RDS 99,95% Multi-AZ ; Azure AD = 99,99% disponibilité |
| **R3** | Moindre privilège, MFA, monitoring Identity Protection | Microsoft = sécurité plateforme, anti-DDoS, rotation clés signature |
| **R4** | Classifier données, journaliser accès, ne pas exporter données souveraines | AWS/Azure = certifications ISO 27001, SOC 2, régions africaines |

### 3. Politique IAM — Rôles et droits d'accès

| Rôle | Périmètre | Droits |
|------|-----------|--------|
| **ADMIN** | Tous les modules | CRUD complet sur ERP, CRM, Supply, BI, utilisateurs |
| **SALES** | CRM | Lecture seule clients et restaurants |
| **LOGISTICS** | Supply Chain | Lecture seule produits et expéditions |
| **MANAGER** | BI | Lecture seule tableau de bord |
| **DEV** | Développement | Accès dev uniquement ; lecture seule en prod |
| **AUDITOR** | Conformité | Lecture seule logs CloudTrail et Azure Monitor |

**Matrice :**

```
                ERP    CRM    Supply    BI    Logs   Users
ADMIN           CRUD   CRUD   CRUD     CRUD   R      CRUD
SALES            -     R       -        -     -       -
LOGISTICS        -      -     R         -     -       -
MANAGER          -      -      -       R      -       -
DEV             R*     R*     R*       R*     R       -
AUDITOR          -      -      -        -     R       -
(* = dev uniquement)
```

**Implémentation :** Azure AD (identité) → rôles dans claims JWT → `@PreAuthorize` Spring Security.

### 4. Procédure de gestion des droits en cas de départ d'un développeur

1. **Signalement** RH + IT dans les 24h
2. **Désactivation immédiate** du compte Azure AD (< 15 min)
3. **Révocation tokens** : `Revoke-AzureADUserAllRefreshToken`
4. **Rotation secrets** : mot de passe RDS dev, clés SSH, tokens GitHub
5. **Vérification** CloudTrail/Azure Monitor pour accès frauduleux
6. **Rétention** 90 jours avant suppression définitive
7. **Notification** équipe

### 5. Politique de rotation des clés pour la base de données

| Environnement | Fréquence | Déclencheur |
|--------------|-----------|-------------|
| Développement | 90 jours | Calendaire |
| Test | 90 jours | Calendaire |
| Production | 30 jours | Calendaire + incident |

**Processus :** génération mot de passe 32 car. → `aws rds modify-db-instance` → mise à jour AWS Secrets Manager → `kubectl rollout restart` → vérification → journalisation SIEM.

### 6. Plan de réponse aux incidents (IRP)

| Sévérité | Délai | Exemple |
|----------|-------|---------|
| **P1** Critique | 15 min | Fuite données, intrusion, indispo totale |
| **P2** Élevé | 1 h | Dégradation significative |
| **P3** Moyen | 4 h | Incident performance |
| **P4** Faible | 24 h | Demande modification |

**Procédure P1 :**
- **0-15 min** : Détection CloudWatch/Azure Monitor → notification PagerDuty → ticket Jira
- **15-45 min** : Isoler ressource compromise, couper accès réseau, révoquer clés, snapshot forensique
- **45 min-4 h** : Restaurer snapshot RDS, redéployer image Docker saine, scanner vulnérabilités
- **48 h** : Post-mortem (RCA), mise à jour runbooks, rapport DPO

### 7. Chiffrement des données en transit et au repos

**En transit :** Client → ALB (TLS 1.3) → Application (TLS 1.3) → RDS/Redis (TLS 1.2) → Azure AD (HTTPS). Interne K8s : mTLS.

**Au repos :** RDS MySQL (AES-256 KMS), ElastiCache Redis (AES-256), EBS (AES-256 KMS), CloudWatch/Azure Monitor (AES-256), Secrets Manager (KMS).

### 8. Guide de bonnes pratiques

**Gouvernance :** moindre privilège, environnements isolés, rotation automatique secrets, Zero Trust.
**Opérations :** MFA obligatoire, patches hebdomadaires Docker, snapshots RDS quotidiens (rétention 30j), monitoring CloudWatch + Azure Monitor.
**Conformité :** CloudTrail + Azure Monitor logs (conservation 1 an), audit externe annuel, test intrusion semestriel, classification données, formation employés tous les 6 mois.

---

## II.3 — SÉCURITÉ DES TRANSACTIONS ET DES DONNÉES (BLOCKCHAIN)

### 1. Plateforme blockchain retenue : Hyperledger Fabric 2.5

**Pourquoi Fabric et pas Ethereum :**

| Contrainte | Hyperledger Fabric | Ethereum |
|-----------|-------------------|----------|
| Latence Douala (150-250 ms) | 1-2 secondes | 12-15 secondes + gas fees |
| Hébergement Cameroun | On-premise ou AWS EKS | Ethereum mainnet seulement |
| Budget 480M FCFA | Gratuit (open source) | Gas fees volatils en ETH |
| Données sensibles (loi 2010/012) | Blockchain privée, données confidentielles | Données publiques par défaut |
| Confidentialité transactions | Channels + Private Data Collections | Tout visible |
| Identités | Certificats X.509 (MSP) | Adresses pseudonymes |

### 2. Structure d'un bloc

```
BLOCK N
├── HEADER
│   ├── Block Number: 1537
│   ├── Previous Hash: a3f2c8e1... (SHA-256 du bloc N-1)
│   ├── Current Hash: b7d4e9f2...
│   ├── Timestamp: 2026-05-21T14:30:00+01:00
│   └── Data Hash: 9e1c3a5b... (Merkle Root)
├── DATA (Transactions)
│   ├── Tx 1: Shipment SH-2026-0421 créé
│   │   ├── Payload: { productId, quantity, origin, destination }
│   │   ├── Creator: cert(CN=logistics01, OU=Supply, O=AGROCAM)
│   │   └── Signature ECDSA
│   ├── Tx 2: Mise à jour statut SH-2026-0421
│   └── Tx 3: Transfert propriété LOT-2026-0315
└── METADATA
    ├── Creator: cert(CN=orderer01, O=AGROCAM)
    ├── Signature orderer
    └── Last Config Block: 1500
```

**Intégrité :** chaque bloc contient le hash du précédent → modification détectée instantanément. Validation requiert signature ≥ 2/3 des validateurs.

### 3. Mécanisme de consensus : Raft

| Critère | Raft (Fabric) | PoW (Bitcoin) | PoS (Ethereum) |
|---------|---------------|---------------|----------------|
| Débit | ~10 000 tx/s | ~7 tx/s | ~30 tx/s |
| Latence | < 1s | ~10 min | ~12s |
| Consommation | Négligeable | Très élevée | Faible |
| Nb validateurs | 3-5 nœuds | Des milliers | Des milliers |
| Réseau privé | Conçu pour | Non | Non |

**Réseau AGROCAM :**
- Validateur 1 — AGROCAM Siège (Douala) — on-premise
- Validateur 2 — AGROCAM Usine (Yaoundé) — on-premise
- Validateur 3 — AWS af-south-1 (Cape Town) — cloud basculement
- Orderer — AWS af-south-1
- Pair Supply Chain — agents terrain (offline-first)
- Pair ERP — comptabilité

### 4. Réponse à la loi camerounaise n°2010/012

La loi exige la **traçabilité des accès aux SI**. La blockchain y répond par :
1. **Signature X.509** de chaque transaction (identité vérifiée par MSP)
2. **Historique immuable** : création, modification statut, transfert propriété
3. **Audit trail complet** : qui, quand, quoi, signature
4. **Requête d'audit** : `stub.GetHistoryForKey("SH-2026-0421")` → historique complet
5. **Conservation indéfinie** des logs (non-répudiation)

### 5. Smart contract développé

**Langage :** Go (chaincode Hyperledger Fabric)

**Fonctions :**

| Fonction | Déclencheur | Effet |
|----------|-------------|-------|
| `CreateShipment()` | Nouvelle expédition ERP | Enregistre expédition + hash |
| `UpdateStatus()` | Scan terrain | Met à jour statut + timestamp |
| `TransferOwnership()` | Changement responsable | Transfert avec signature |
| `VerifyShipment()` | Contrôle qualité | Ajoute certificat conformité |
| `GetShipmentHistory()` | Demande audit | Retourne historique complet |
| `GetShipmentByID()` | Consultation | Retourne état courant |
| `GetAllShipments()` | Listing | Toutes les expéditions |

**Fichiers livrés :**
- `chaincode/supplychain/supplychain.go` (9 KB, 200+ lignes)
- `chaincode/supplychain/go.mod`
- `chaincode/supplychain/supplychain_test.go` (7 tests : create, duplicate, invalid quantity, transitions, not found, history)

### 6. Interaction du smart contract avec les modules SI

**Flux :**
1. **Création expédition** : Agent Supply Chain → POST `/api/v1/supply/shipments` → Spring Boot valide → Fabric SDK → `SubmitTransaction('CreateShipment')` → Endorsing Peers (x3) → Orderer → Commit → Event → Webhook ERP
2. **Scan terrain** : Agent scan QR code (offline-first Redis) → sync → `UpdateStatus()` → historique immuable
3. **Vérification acheteur** : Restaurant → GET `/api/v1/supply/shipments` → `GetShipmentHistory()` → provenance garantie

### 7. Bonnes pratiques de sécurité et vulnérabilités

**Bonnes pratiques :** validation entrées, moindre privilège (namespace chaincode), authentification MSP, Private Data Collections, Checks-Effects-Interactions.

**Vulnérabilités traitées :**

| Vulnérabilité | Prévention |
|--------------|------------|
| **Reentrancy Attack** | Pattern Checks-Effects-Interactions : vérifier → mettre à jour état → puis émettre événement |
| **Integer Overflow** | Vérification bornes : `if quantity <= 0 { return error }` |

### 8. Question situationnelle : clé privée d'un nœud compromise

**Procédure :**
1. **Contenance (0-30 min)** : Révoquer certificat (CRL Fabric), isoler nœud (Network Policies K8s), vérifier intégrité blocs, couper accès physique
2. **Maintien service (30 min-2 h)** : Activer nœud secours AWS avec nouveau certificat, Raft continue avec 2/3 nœuds sains, offline-first gère les coupures
3. **Rétablissement** : Déployer nouveau nœud Douala avec nouvelles clés, synchroniser ledger, post-mortem
4. **Recommandation** : HSM (Hardware Security Module) pour protéger les clés privées

### 9. Extension à des partenaires internationaux (consortium)

**Architecture :**
- Multi-channels : "Prix" (AGROCAM + Exportateur), "Traçabilité Publique" (tous), "RH/Financier" (AGROCAM seul)
- Private Data Collections : données souveraines AGROCAM jamais transférées aux autres nœuds
- Gouvernance : comité multi-parties
- 5 validateurs au lieu de 3 (tolérance 2 pannes)

---

## SCHÉMA D'ARCHITECTURE — GUIDE EDRawMax

Voici la description détaillée du schéma à reproduire dans EdrawMax :

### Éléments du schéma

**Titre :** "DIGITRANS-CM — Architecture de Sécurisation Cloud + Blockchain"

**Bloc 1 — Cloud AWS (af-south-1)**
- Forme : rectangle bleu foncé
- Contenu :
  - VPC (10.0.0.0/16)
  - ALB (Application Load Balancer)
  - Auto Scaling Group (2-6 EC2)
  - RDS MySQL Multi-AZ (chiffré AES-256 KMS)
  - ElastiCache Redis 7 (TLS + AUTH)
  - CloudWatch (métriques, alarmes, logs)

**Bloc 2 — Azure**
- Forme : rectangle bleu clair
- Contenu :
  - Azure AD (identités, OAuth2/JWT)
  - Azure Monitor (logs centralisés)
  - Azure AD Identity Protection

**Bloc 3 — On-premise Cameroun**
- Forme : rectangle vert
- Contenu :
  - Siège Douala (Validateur 1 Hyperledger)
  - Usine Yaoundé (Validateur 2)
  - Données souveraines (loi 2010/012)

**Bloc 4 — Application (Spring Boot)**
- Forme : rectangle gris au centre
- Contenu :
  - 4 modules : ERP, CRM, Supply Chain, BI
  - Sécurité : Spring Security + JWT
  - Offline-first : Redis + sync @Scheduled 5s
  - Swagger : /swagger-ui.html

**Bloc 5 — Blockchain Hyperledger Fabric**
- Forme : losange orange
- Contenu :
  - 3 Validateurs (Douala, Yaoundé, AWS)
  - 1 Orderer (AWS)
  - 2 Pairs (Supply Chain, ERP)
  - Smart Contract Go : 7 fonctions
  - Channels : Prix, Traçabilité, RH
  - Private Data Collections

### Connecteurs

1. **Clients → ALB** : flèche HTTPS (TLS 1.3) — légende : "Chiffrement en transit"
2. **ALB → Application** : flèche HTTP (chiffré interne)
3. **Application → RDS** : flèche avec cadenas — légende : "TLS 1.2 + AES-256"
4. **Application → Redis** : flèche avec cadenas — légende : "TLS + AUTH"
5. **Application → Azure AD** : flèche OAuth2 — légende : "JWT + OAuth2"
6. **Application → Hyperledger** : flèche Fabric SDK — légende : "SubmitTransaction()"
7. **Azure AD → Application** : flèche — légende : "JWT Token"
8. **CloudWatch + Azure Monitor → Application** : flèche pointillée — légende : "Monitoring"
9. **Validateurs entre eux** : flèche double — légende : "Consensus Raft 2/3"
10. **Private Data → On-premise** : flèche avec cadenas — légende : "Données souveraines"

### Notes visuelles
- Ajouter des icônes : cadenas (chiffrement), bouclier (sécurité), chaîne (blockchain)
- Code couleur : rouge (critique/P1), orange (élevé), vert (sécurisé), bleu (cloud)
- Légende en bas à droite expliquant les symboles

---

## CONCLUSION

Le projet DIGITRANS-CM intègre une stratégie de sécurité complète couvrant l'identification des risques cloud, la gouvernance IAM, la réponse aux incidents, le chiffrement de bout en bout, et un smart contract Hyperledger Fabric pour la traçabilité immuable de la Supply Chain. L'architecture respecte la loi camerounaise n°2010/012 via le mécanisme de Private Data Collections garantissant la souveraineté des données sensibles. Les 17 questions de la Partie II sont traitées avec un niveau de détail opérationnel, incluant procédures concrètes, code, et schémas d'architecture.

---

*Rapport rédigé par CAMTECH SOLUTIONS S.A. — Mai 2026*
*Projet DIGITRANS-CM pour AGROCAM S.A.*
