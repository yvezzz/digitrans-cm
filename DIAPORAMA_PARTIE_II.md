# DIAPORAMA — PARTIE II
## Sécurisation de l'application DIGITRANS-CM
### CAMTECH SOLUTIONS S.A. — Mai 2026

---

## Slide 1 — Page de garde

**Titre :** Rapport de Sécurisation — Projet DIGITRANS-CM
**Sous-titre :** Sécurisation des transactions et des données par la Blockchain
**Client :** AGROCAM S.A.
**Société :** CAMTECH SOLUTIONS S.A., Douala, Cameroun
**Date :** Mai 2026

---

## Slide 2 — Contexte et objectifs

**Rappel du projet :**
- Modernisation du SI d'AGROCAM S.A. (groupe agroalimentaire, 1 200 employés)
- 4 modules : ERP, CRM, Supply Chain, BI
- Budget : 480M FCFA — 18 mois

**Objectifs de la Partie II :**
- Sécuriser l'infrastructure cloud hybride AWS + Azure
- Assurer la traçabilité via la blockchain
- Répondre à la loi camerounaise n°2010/012
- Garantir la résilience face aux coupures réseau

---

## Slide 3 — Risques de sécurité cloud (1/2)

| N° | Risque | Impact |
|----|--------|--------|
| R1 | Fuite de données sensibles | Critique : données clients, RH, financières exposées |
| R2 | Indisponibilité du service | Élevé : coupures réseau à Douala, dépendance AWS |
| R3 | Compromission Azure AD | Critique : vol de tokens JWT, accès non autorisés |
| R4 | Non-conformité réglementaire | Élevé : loi n°2010/012, souveraineté des données |

---

## Slide 4 — Risques de sécurité cloud (2/2) — Responsabilité partagée

**Modèle de responsabilité partagée AWS/Azure :**

| Risque | AGROCAM (Client) | Fournisseur Cloud |
|--------|-----------------|-------------------|
| Fuite données | Configurer les groupes de sécurité, chiffrer les données, gérer les clés KMS | Sécurité physique, réseau, hyperviseur (AWS) |
| Indisponibilité | Architecture multi-AZ, offline-first, tests de basculement | SLA RDS 99,95%, EC2 99,99% |
| Compromission identités | MFA, moindre privilège, Azure AD Identity Protection | Protection de la plateforme Azure AD |
| Non-conformité | Classifier les données, journaliser les accès | Certifications ISO 27001, SOC 2 |

---

## Slide 5 — Politique IAM

**6 rôles identifiés :**

| Rôle | Périmètre | Droits |
|------|-----------|--------|
| ADMIN | Technique | CRUD complet sur tous les modules |
| SALES | CRM | Lecture clients + restaurants |
| LOGISTICS | Supply Chain | Lecture produits + expéditions |
| MANAGER | BI | Lecture tableau de bord |
| DEV | Développement | Dev uniquement, lecture logs prod |
| AUDITOR | Conformité | Lecture logs d'audit |

**Mise en œuvre :** Azure AD + Spring Security `@PreAuthorize`

---

## Slide 6 — Gestion des accès — Départ d'un développeur

**Procédure en 7 étapes :**
1. Notification RH + IT (24h)
2. Désactivation immédiate du compte Azure AD (< 15 min)
3. Révocation des sessions : `Revoke-AzureADUserAllRefreshToken`
4. Rotation des secrets impactés (DB dev, clés SSH, tokens GitHub)
5. Vérification rétrospective dans CloudTrail
6. Conservation 90 jours avant suppression définitive
7. Notification à l'équipe

---

## Slide 7 — Rotation des clés base de données

**Fréquence :** Prod = 30 jours, Dev/Test = 90 jours

**Processus automatisé (Secrets Manager) :**
1. Générer mot de passe 32 caractères
2. `aws rds modify-db-instance --master-user-password "$NEW"`
3. `aws secretsmanager put-secret-value --secret-id digitrans/db/prod`
4. `kubectl rollout restart deployment/digitrans-cm`
5. Vérification /actuator/health
6. Suppression ancien secret
7. Journalisation SIEM

---

## Slide 8 — Plan de réponse aux incidents

**4 niveaux de sévérité :**

| Niveau | Délai | Exemple |
|--------|-------|---------|
| P1 — Critique | 15 min | Fuite de données, intrusion |
| P2 — Élevé | 1 h | Dégradation significative |
| P3 — Moyen | 4 h | Erreur applicative mineure |
| P4 — Faible | 24 h | Demande de modification |

**Procédure P1 :**
1. Détection (CloudWatch/Azure Monitor) → Notification PagerDuty
2. Contenance : isolement du pod, blocage NACL, snapshot forensique
3. Rétablissement : restauration snapshot RDS, redéploiement image Docker
4. Post-mortem : RCA, mise à jour runbooks, rapport DPO

---

## Slide 9 — Chiffrement des données

**En transit :**
- Client ↔ ALB : TLS 1.3 (certificat ACM)
- Application ↔ RDS : TLS 1.2 (sslmode=require)
- Application ↔ Redis : TLS + AUTH
- Internes K8s : Network Policies restrictives

**Au repos :**
- RDS MySQL : AES-256 (KMS)
- ElastiCache Redis : AES-256
- EBS (EC2) : AES-256 (KMS)
- Logs CloudWatch : AES-256
- Secrets : AWS KMS + Secrets Manager

---

## Slide 10 — Guide de bonnes pratiques (12 règles)

**Gouvernance :**
1. Moindre privilège — révision trimestrielle
2. Environnements isolés (comptes AWS distincts)
3. Rotation automatique des secrets
4. Zero Trust

**Opérations :**
5. MFA obligatoire pour tous les administrateurs
6. Images Docker rebuildées hebdomadairement
7. Snapshots RDS quotidiens, test restauration mensuel
8. Monitoring + alertes (CloudWatch + Azure Monitor)

**Conformité :**
9. CloudTrail + logs applicatifs (conservation 1 an)
10. Audit sécurité annuel, test d'intrusion semestriel
11. Classification des données
12. Formation sécurité tous les 6 mois

---

## Slide 11 — Blockchain — Choix de la plateforme

**Hyperledger Fabric 2.5** (Linux Foundation)

| Critère | Fabric | Ethereum |
|---------|--------|----------|
| Latence | 1-2 secondes | 12-15 secondes |
| Débit | ~10 000 tx/s | ~30 tx/s |
| Confidentialité | Totale (réseau privé) | Données publiques |
| Hébergement | On-premise ou cloud | Ethereum mainnet |
| Coût | Infrastructure seule | Gas ETH volatil |
| Identités | Certificats X.509 | Adresses pseudonymes |

**Conclusion :** Ethereum inadapté. Fabric seul permet confidentialité, rapidité et souveraineté.

---

## Slide 12 — Structure d'un bloc

```
BLOCK N
├── HEADER
│   ├── Block Number: 1537
│   ├── Previous Hash: a3f2c8e1... (SHA-256 du bloc N-1)
│   ├── Current Hash: b7d4e9f2... (SHA-256 header+data+metadata)
│   └── Data Hash: 9e1c3a5b... (Merkle Root)
├── DATA
│   ├── Tx 1: Shipment #SH-2026-0421 créé
│   ├── Tx 2: Statut expédition mis à jour
│   └── Tx 3: Transfert de propriété
└── METADATA
    ├── Creator: cert(orderer01, AGROCAM)
    └── Signature: [ECDSA signature]
```

**Intégrité :** Chaque bloc contient le hash du précédent → chaîne inviolable.

---

## Slide 13 — Consensus : Raft

**Pourquoi Raft et pas PoW/PoS ?**

| Critère | Raft | PoW | PoS |
|---------|------|-----|-----|
| Débit | 10 000 tx/s | 7 tx/s | 30 tx/s |
| Latence | < 1s | 10 min | 12s |
| Énergie | Négligeable | Très élevée | Faible |
| Réseau privé | Conçu pour |  |  |
| Coupures réseau (Douala) | Majorité 2/3 |  |  |

**Réseau proposé :**
- Nœud Validateur 1 — AGROCAM Douala (on-premise)
- Nœud Validateur 2 — AGROCAM Yaoundé (on-premise)
- Nœud Validateur 3 — AWS af-south-1 (cloud basculement)
- Nœud Orderer — AWS af-south-1
- Pairs : Supply Chain (mobile), ERP (API)

---

## Slide 14 — Conformité loi n°2010/012

**Exigence :** Traçabilité des accès aux systèmes d'information

**Notre réponse blockchain :**
1. Chaque transaction signée avec certificat X.509 (identité vérifiée)
2. Historique immuable de toutes les opérations
3. Audit trail complet : QUI + QUAND + QUOI + SIGNATURE
4. Fonction `GetShipmentHistory()` pour audits réglementaires
5. Conservation indéfinie (non-répudiation)

**Exemple audit :**
```
TxID: tx1537 | Valeur: SH-2026-0421 | Timestamp: 2026-05-21T14:30:00
| Validé par: cert(CN=logistics01, O=AGROCAM)
| Opération: UPDATE_STATUS → IN_TRANSIT
```

---

## Slide 15 — Smart Contract SupplyChain (Go)

**Langage :** Go (chaincode Hyperledger Fabric)

**Fonctions principales :**

| Fonction | Déclencheur | Effet |
|----------|-------------|-------|
| `CreateShipment` | Nouvelle expédition | Enregistrement immuable |
| `UpdateStatus` | Scan terrain | Mise à jour tracée |
| `TransferOwnership` | Changement responsable | Transfert signé |
| `VerifyShipment` | Contrôle qualité | Certificat de conformité |
| `GetShipment` | Consultation | État courant |
| `GetShipmentHistory` | Audit | Historique complet |
| `GetAllShipments` | BI Dashboard | Toutes les expéditions |

---

## Slide 16 — Flux technique : application ↔ blockchain

```
1. Agent logistique crée expédition dans l'interface Supply Chain
   ↓ POST /api/v1/supply/shipments
2. Spring Boot (SupplyChainService) → Fabric SDK
   ↓ SubmitTransaction("CreateShipment", args)
3. Endorsing Peers (×3) : vérifient signature + exécutent chaincode
   ↓ Signent le résultat
4. Orderer : ordonne la transaction, crée le bloc, diffuse
   ↓ Commit sur tous les pairs
5. Événement "ShipmentCreated" → Webhook → ERP notifié
```

**Mode offline terrain :**
Agent scan QR code → stocke dans Redis local → synchronisation automatique dès reconnexion (@Scheduled 5s)

---

## Slide 17 — Sécurisation du smart contract

**Bonnes pratiques appliquées :**
- Validation des entrées (quantité > 0, statuts autorisés)
- Vérification de l'identité via `ctx.GetClientIdentity().GetID()`
- Pattern Checks-Effects-Interactions (protection Reentrancy)
- Private Data Collections pour données sensibles (prix, marges)

**Vulnérabilités prévenues :**
| Vulnérabilité | Notre protection |
|--------------|-----------------|
| Reentrancy Attack | Mise à jour de l'état AVANT tout appel externe (événement) |
| Integer Overflow | Vérification `quantity > 0`, rejet des valeurs négatives |
| Accès non autorisé | Vérification du certificat appelant avant chaque opération |

---

## Slide 18 — Code Reentrancy Protection

```go
func (s *SupplyChainContract) UpdateStatus(ctx, shipmentID, newStatus) error {
    // 1. CHECKS
    shipment := getShipment(shipmentID)
    if !isValidTransition(shipment.Status, newStatus) { return error }
    if !isAuthorized(caller, shipment) { return error }

    // 2. EFFECTS (état mis à jour AVANT tout appel externe)
    shipment.Status = newStatus
    shipment.UpdatedAt = time.Now()
    ctx.GetStub().PutState(shipmentID, shipment)

    // 3. INTERACTIONS (après mise à jour)
    ctx.GetStub().SetEvent("ShipmentUpdated", shipmentJSON)
    return nil
}
```

**Principe :** Checks → Effects → Interactions. Jamais d'appel externe avant la mise à jour d'état.

---

## Slide 19 — Question situationnelle : clé compromise

**Scénario :** Clé privée du nœud validateur Douala compromise. Coupures réseau fréquentes.

**Procédure :**
1. Révoquer le certificat → CRL Fabric
2. Isoler le nœud → Network Policies K8s
3. Vérifier l'intégrité → comparer hashs entre nœuds sains
4. Activer le nœud de secours AWS af-south-1
5. Raft continue si 2/3 nœuds sont sains
6. Offline-first gère les transactions pendant la coupure
7. Déployer un nouveau nœud Douala (nouvelles clés, nouveau certificat)
8. Recommandation : HSM (Hardware Security Module) pour les clés

---

## Slide 20 — Extension au consortium international

**Architecture cible :**

```
AGROCAM (Cameroun)   Exportateur (France)   Client (Allemagne)   Auditeur (Suisse)
     │ Validateur           │ Pair                │ Pair              │ Pair
─────┴──────────────────────┴──────────────────────┴──────────────────┴─────
  Channel "Traçabilité" (tous)
  Channel "Prix" (AGROCAM + Exportateur)
  Channel "Souveraineté" (AGROCAM seul, on-premise)
```

**Private Data Collections :** données sensibles jamais transférées aux autres nœuds (seul le hash est partagé)

**Gouvernance :** Comité multi-parties (AGROCAM + 2 membres)

**Souveraineté :** Les données RH et financières restent sur le sol camerounais via PDC (loi n°2010/012)

---

## Slide 21 — Conclusion

**Ce qui a été réalisé :**
 - Infrastructure cloud hybride sécurisée (AWS + Azure)
 - Politique IAM complète (6 rôles)
 - Procédures de sécurité (incidents, rotation clés, départ)
 - Chiffrement bout en bout
 - Blockchain Hyperledger Fabric pour la traçabilité
 - Smart contract Go avec 7 fonctions
 - Mécanisme offline-first pour les coupures réseau

**Prochaines étapes :**
- Déploiement du réseau Fabric sur les 3 nœuds
- Tests d'intrusion et audit de sécurité
- Évolution vers le consortium avec partenaires internationaux

---

*CAMTECH SOLUTIONS S.A. — Douala, Cameroun — Mai 2026*
