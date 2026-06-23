# Rapport d'analyse de performance (C24) - DIGITRANS-CM

## Résumé exécutif

- Date :
- Environnement testé :
- Objectif du test : vérifier latence, débit, stabilité

## Configuration du test

- Outil : K6
- Script : `loadtests/k6-load-test.js`
- Scénario : montée progressive 20→100 VUs
- URL testée : `BASE_URL`

## KPIs mesurés

- Uptime
- Latence P95 / P99
- Débit (req/s)
- Taux d'erreur
- Utilisation CPU / Mémoire

## Résultats

- Durée du test :
- VUs max :
- Requêtes totales :
- Erreurs :
- P95 latency :
- P99 latency :

## Analyse des goulots

- Observations :
- Hypothèses :
- Recommandations :

## Actions correctives proposées

1. Optimiser les requêtes SQL lentes identifiées
2. Ajouter cache TTL plus long pour endpoints read-heavy
3. Augmenter l'auto-scaling threshold si nécessaire

## Annexes

- Commandes exécutées

```bash
k6 run --vus 100 --duration 10m loadtests/k6-load-test.js --env BASE_URL=https://<endpoint>
```

- Logs K6 (export) :

