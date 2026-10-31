# Guide de démonstration et captures TP02

## Prérequis
- Java 25 et Maven (ou Maven installé par l'IDE).
- Ouvrir le dossier `springcoredemo`.
- Pour changer d'étape, arrêter l'application avant la commande Git.
- Commande de démarrage : `mvn spring-boot:run`.
- Arrêter proprement : Ctrl+C.
- Retourner à la version finale : `git switch main`.
- Si Git indique des modifications locales, ne pas les écraser sans vérification.

## Captures dans l'ordre

1. `git switch --detach ex51` puis lancer et `curl http://localhost:8080/dailyworkout` : CricketCoach.
2. `git switch --detach ex52` puis lancer : échec intentionnel, capturer `NoUniqueBeanDefinitionException` et les quatre noms des beans.
3. `git switch --detach ex53` puis lancer et appeler `/dailyworkout` : `Practice fast bowling for 15 minutes` avec @Qualifier.
4. `git switch --detach ex54` puis lancer et appeler `/dailyworkout` : `Run a hard 5k!` avec @Primary.
5. `git switch --detach ex54-priority` puis lancer et appeler `/dailyworkout` : cricket malgré @Primary sur TrackCoach.
6. `git switch --detach ex55` puis lancer : TennisCoach ne doit pas être construit au démarrage; les autres sont construits.
7. `git switch --detach ex55-global` puis lancer : aucun constructeur Coach au démarrage; appeler `/dailyworkout` et observer l'instanciation.
8. `git switch --detach ex56-singleton` puis lancer et appeler `/check` : `true`.
9. `git switch --detach ex56-prototype` puis lancer et appeler `/check` : `false`.
10. `git switch --detach ex57` puis lancer et arrêter avec Ctrl+C : capturer `In constructor`, `In doMyStartupStuff()` et `In doMyCleanupStuff()`.
11. `git switch main` puis lancer et appeler `/dailyworkout` : `Swim 1000 meters as a warm up`.

## Commandes de test
`curl http://localhost:8080/dailyworkout`
`curl http://localhost:8080/check` (uniquement étapes ex56)

## Important
Les captures doivent être prises sur l'exécution réelle. Aucun test Maven ni exécution de l'application n'a été réalisé lors de la génération de cette archive. Les erreurs intentionnelles sont normales aux étapes ex52. Pour ex55-global, des beans Spring autres que Coach peuvent être initialisés au démarrage.
