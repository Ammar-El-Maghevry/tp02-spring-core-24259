# TP02 — IoC et injection de dépendances (Spring Core)

**Étudiant : Ammar — Matricule : 24259**

Application de coaching sportif réalisée dans le cadre du module IRT — Spring Boot 4. Les exercices 5.1 à 5.8 illustrent progressivement le conteneur Spring et l’injection de dépendances.

## Objectifs pédagogiques

- Distinguer IoC et DI et comprendre l’`ApplicationContext`.
- Déclarer des composants et injecter une abstraction par constructeur.
- Résoudre une ambiguïté avec `@Qualifier` et `@Primary`.
- Observer l’initialisation paresseuse, les scopes et le cycle de vie.
- Déclarer un objet non annoté avec `@Configuration` et `@Bean`.

## Technologies

Java **25**, Spring Boot **4.0.0**, Maven, Spring Web MVC (`spring-boot-starter-webmvc`), Tomcat embarqué et annotations de cycle de vie `jakarta.annotation.*`.

## Structure du projet

```text
springcoredemo/
├── src/main/java/com/ioc/springcoredemo/
│   ├── SpringcoredemoApplication.java
│   ├── common/       # Coach, Cricket, Baseball, Tennis, Track et SwimCoach
│   ├── config/       # SportConfig : @Bean("aquatic")
│   └── rest/         # DemoController
├── src/main/resources/application.properties
├── captures/         # 15 captures PNG originales
├── pom.xml
├── .gitignore
├── README.md
├── REPONSES.md       # 8 questions exactes et tableau des scopes
├── GUIDE-CAPTURES.md
└── TP02-ioc-injection-dependances.pdf
```

Tous les packages applicatifs sont sous `com.ioc.springcoredemo` et sont scannés automatiquement. La branche `main` reprend les sources, le POM et la configuration de `ex58` ; les ajouts de finalisation concernent la documentation et les livrables.

## Lancement

Prérequis : JDK 25 et Maven disponibles dans le `PATH`. Depuis la racine :

```bash
java -version
mvn -version
mvn clean verify
mvn spring-boot:run
```

Dans un second terminal :

```bash
curl http://localhost:8080/dailyworkout
```

Sur `main` et `ex58`, la réponse attendue est `Swim 1000 meters as a warm up`. Arrêter avec **Ctrl+C** pour permettre les callbacks de destruction. Pour utiliser un autre port :

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

Le JAR peut aussi être lancé après compilation :

```bash
java -jar target/springcoredemo-0.0.1-SNAPSHOT.jar
```

## Étapes conservées dans Git

Les tags désignent les états historiques, pas des fonctionnalités simultanément actives sur `main`. Les résultats ci-dessous sont les résultats attendus d’après le code de chaque tag ; l’audit visuel des preuves est détaillé plus bas.

| Tag | Notion / action | Résultat attendu |
|---|---|---|
| `ex51` | Interface, composant, injection constructeur ; `/dailyworkout` | `Practice fast bowling for 15 minutes` |
| `ex52` | Quatre candidats `Coach`, sans sélection | Échec intentionnel du démarrage : ambiguïté de beans |
| `ex53` | `@Qualifier("cricketCoach")` ; `/dailyworkout` | Réponse cricket |
| `ex54` | `@Primary` sur TrackCoach ; `/dailyworkout` | `Run a hard 5k!` |
| `ex54-priority` | Qualifier cricket avec primaire Track ; `/dailyworkout` | Réponse cricket : qualifier prioritaire |
| `ex55` | `@Lazy` sur TennisCoach non injecté | Pas de construction de Tennis au démarrage |
| `ex55-global` | Lazy global ; observer avant/après `/dailyworkout` | Création du coach demandé différée au premier appel |
| `ex56-singleton` | Deux injections cricket ; `/check` | `Comparing beans: myCoach == anotherCoach, true` |
| `ex56-prototype` | Cricket en prototype ; `/check` | `Comparing beans: myCoach == anotherCoach, false` |
| `ex57` | Cricket singleton avec callbacks | Constructeur → initialisation → nettoyage à l’arrêt |
| `ex58` | `@Bean("aquatic")` et injection SwimCoach | `Swim 1000 meters as a warm up` |

`/dailyworkout` est déclaré à toutes les étapes, mais `ex52` ne démarre volontairement pas. `/check` est déclaré dans `ex56-singleton`, `ex56-prototype` et `ex57`. Il est absent de `ex58` et de `main` (HTTP 404).

L’échec de `ex52` est une expérience pédagogique, pas un défaut de la version finale. L’expérience supplémentaire avec deux `@Primary` provoquerait aussi une ambiguïté ; elle n’a ni tag ni capture dédiés.

### Reproduire un exercice

Cloner le dépôt avec son historique :

```bash
git clone https://github.com/Ammar-El-Maghevry/tp02-spring-core-24259.git
cd tp02-spring-core-24259
git tag
git status
git switch --detach ex56-singleton
git describe --tags --exact-match
mvn clean spring-boot:run
```

Dans un second terminal :

```bash
curl http://localhost:8080/check
```

Arrêter l’application avant de changer de tag. Remplacer `ex56-singleton` par le tag souhaité et choisir l’endpoint du tableau. `clean` évite de conserver des classes compilées provenant d’une autre étape. Après les expériences :

```bash
git switch main
mvn clean spring-boot:run
```

Vérifier que les modifications locales sont sauvegardées avant de changer d’étape. Les tags historiques ne contiennent pas les documents et captures ajoutés à la finalisation : consulter ceux de `main` sur GitHub. Pour essayer la variante tennis, partir de `ex53`, remplacer temporairement le qualifier par `tennisCoach` et relancer : la réponse attendue est `Practice your backhand volley`. Cette variante n’est pas conservée dans un tag. La variante prototype avec callbacks part de `ex57` en ajoutant le scope prototype à CricketCoach ; l’absence de nettoyage est à observer lors d’un arrêt propre. Sauvegarder ces variantes dans une branche de travail avant de revenir à `main`.

## Captures disponibles et cohérence des preuves

Les **15 images ont été ouvertes et lues**. Les noms et les fichiers originaux ont été conservés, sans retouche. Une invite de shell peut être restée sur l’étape précédente ; elle n’est pas une preuve suffisante de la version exécutée.

| Exercice | Capture | Contenu réellement visible / limite |
|---|---|---|
| 5.1 | [ex51-dailyworkout.png](captures/ex51-dailyworkout.png) | Appel `/dailyworkout`, réponse cricket. |
| 5.2 | [ex52-NoUniqueBeanDefinitionException.png](captures/ex52-NoUniqueBeanDefinitionException.png) | Diagnostic « single bean, but 4 were found », quatre ids et échec. Stacktrace complète et nom littéral de l’exception non visibles. |
| 5.3 | [ex53-qualifier.png](captures/ex53-qualifier.png) | Réponse cricket sous `ex53`. |
| 5.3 | [ex53-tennisCoach.png](captures/ex53-tennisCoach.png) | Affiche encore la réponse cricket : ne prouve pas la variante tennis. |
| 5.4 | [ex54-primary.png](captures/ex54-primary.png) | `Run a hard 5k!`. |
| 5.4 | [ex54-priority.png](captures/ex54-priority.png) | Réponse cricket ; invite `ex54`, sélection à rapprocher du code de `ex54-priority`. |
| 5.5 | [ex55-lazy.png](captures/ex55-lazy.png) | Constructeurs Baseball, Cricket et Track, sans Tennis au démarrage. |
| 5.5 | [ex55-global-before.png](captures/ex55-global-before.png) | Démarrage sans constructeur de coach visible ; début d’initialisation du DispatcherServlet en bas. |
| 5.5 | [ex55-global-after.png](captures/ex55-global-after.png) | Réponse cricket sous `ex54-priority` ; ne démontre pas la création lazy globale après appel. |
| 5.6 | [ex56-singleton.png](captures/ex56-singleton.png) | `git describe` donne `ex56-singleton`, `/check` vaut `true`. |
| 5.6 | [ex56-prototype.png](captures/ex56-prototype.png) | `git describe` donne `ex56-prototype`, `/check` vaut `false`. |
| 5.7 | [ex57-startup.png](captures/ex57-startup.png) | Constructeur Cricket puis `In doMyStartupStuff()`. |
| 5.7 | [ex57-destroy.png](captures/ex57-destroy.png) | Arrêt gracieux puis `In doMyCleanupStuff()`. |
| 5.7 | [ex57-prototype.png](captures/ex57-prototype.png) | Deux constructions et initialisations Cricket ; arrêt absent, donc absence de nettoyage non démontrée. |
| 5.8 | [ex58-swimcoach.png](captures/ex58-swimcoach.png) | Réponse natation ; invite `ex56-prototype`, ne certifie pas le tag exécuté. Réponse finale confirmée séparément lors de la vérification. |

## Vérifications de finalisation — 31 octobre 2026

- Environnement observé : OpenJDK `25.0.5.0.3`, Maven `3.10.0`.
- Packages, imports, annotations et POM relus : cohérents avec le sujet, starter Web MVC 4.0.0, imports `jakarta.annotation`.
- `mvn clean verify` : **BUILD SUCCESS**. Maven indique **No tests to run** : le projet ne contient pas de tests automatisés.
- Démarrage réel du JAR final sur un port local libre : `/dailyworkout` répond **HTTP 200**, corps exact `Swim 1000 meters as a warm up`.
- `/check` répond **HTTP 404**, conformément au contrôleur final.
- Les traces de création Cricket puis `In doMyStartupStuff()` ont été observées. Après SIGTERM, arrêt gracieux et `In doMyCleanupStuff()` observés ; processus terminé (code 143 associé au signal).
- Les anciennes étapes n’ont pas été réexécutées lors de cette finalisation : leur code et leurs captures ont été examinés. Les variantes tennis, lazy global après appel, prototype à l’arrêt et double primaire restent insuffisamment documentées par les captures existantes.

## Rendu

Les réponses théoriques figurent dans [REPONSES.md](REPONSES.md). Le sujet PDF est conservé à la racine. Le dépôt Git conserve l’historique et les onze tags.

L’archive locale `TP02_Ammar_24259.zip` contient le projet final, la documentation, le sujet et les captures, sans `target/`, `.git/`, caches ou secrets. Elle est exclue de Git pour éviter une copie binaire redondante. Pour reproduire les tags, utiliser le dépôt Git : le ZIP de sources ne contient pas l’historique.
