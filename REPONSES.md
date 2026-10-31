# Ammar — Matricule 24259 — TP02 Spring Core

Les questions ci-dessous sont celles de la section 6 du sujet `TP02-ioc-injection-dependances.pdf` (page 14). Les captures originales ont été lues ; leurs limites sont détaillées dans le README.

## 1. Expliquez avec vos mots la différence entre IoC et DI. En quoi le conteneur Spring « inverse-t-il le contrôle » par rapport à un new CricketCoach() classique ?

L’IoC (Inversion of Control) est le principe qui consiste à confier la création et l’assemblage des objets à un conteneur externe. La DI (Dependency Injection) est le mécanisme utilisé pour fournir à un objet ses dépendances. Avec Spring, ce conteneur est l’`ApplicationContext` et les objets qu’il gère sont des beans.

Si `DemoController` faisait lui-même `new CricketCoach()`, il choisirait une implémentation précise. Avec la DI, il reçoit un `Coach` dans son constructeur : Spring choisit et fournit le bean configuré. Le contrôle de cet assemblage passe donc du contrôleur au conteneur. Dans la version finale, le contrôleur reçoit le bean `aquatic`, sans construire lui-même un `SwimCoach`.

La capture `ex51-dailyworkout.png` montre la réponse `Practice fast bowling for 15 minutes` : le contrôleur délègue bien le travail au coach de cricket à cette étape.

## 2. Pourquoi l’injection par constructeur est-elle recommandée par rapport à l’injection par champ ? Citez au moins deux arguments (immuabilité, testabilité…).

L’injection par constructeur (Constructor Injection) rend les dépendances obligatoires visibles dès la création de l’objet. Elle permet de déclarer la référence `final`, comme `myCoach` dans notre contrôleur, pour empêcher sa réaffectation. Cela ne rend pas automatiquement l’objet injecté lui-même immuable.

Elle facilite aussi les tests : on peut créer `new DemoController(unCoachDeTest)` sans démarrer Spring ni accéder à un champ privé par réflexion. L’injection par champ cache davantage les dépendances et laisse un objet créé manuellement sans ses dépendances injectées. L’injection par setter convient plutôt aux dépendances optionnelles ou modifiables.

Notre contrôleur possède un seul constructeur : `@Autowired` est donc facultatif depuis Spring 4.3, ce qui explique son absence dans le code final.

## 3. Dans quelle situation précise obtient-on NoUniqueBeanDefinitionException ? Donnez deux façons différentes de la résoudre et indiquez laquelle est prioritaire si les deux sont présentes.

Cette exception apparaît lorsqu’une dépendance unique est demandée mais que plusieurs beans compatibles restent candidats sans qu’un choix unique puisse être établi. Dans `ex52`, le contrôleur demande un `Coach` et quatre composants existent : `baseballCoach`, `cricketCoach`, `tennisCoach` et `trackCoach`.

Deux solutions utilisées dans le TP sont :

- `@Qualifier("cricketCoach")` sur le paramètre du constructeur pour sélectionner explicitement ce bean ;
- `@Primary` sur `TrackCoach` pour en faire le choix par défaut lorsqu’aucun qualifier ne sélectionne un autre candidat.

Le qualifier filtre les candidats avant le choix du primaire : dans notre cas, `@Qualifier("cricketCoach")` l’emporte sur `@Primary` placé sur `TrackCoach`. La capture `ex54-primary.png` affiche `Run a hard 5k!`, tandis que `ex54-priority.png` affiche la réponse du cricket. Les configurations correspondantes sont conservées dans `ex54` et `ex54-priority`.

La capture `ex52-NoUniqueBeanDefinitionException.png` montre le diagnostic Spring « required a single bean, but 4 were found » et les quatre noms. Elle ne montre pas la stacktrace complète ni le nom littéral de l’exception. Le cas de deux beans `@Primary` pour cette injection unique recréerait une ambiguïté ; aucune capture fournie ne démontre cette expérience.

## 4. Comment Spring calcule-t-il l’id par défaut d’un bean ? Comment le forcer manuellement ?

Pour une classe détectée par component scanning, Spring utilise généralement son nom simple avec la première lettre en minuscule : `CricketCoach` devient `cricketCoach`. La convention JavaBeans conserve toutefois les acronymes dont les deux premières lettres sont majuscules, par exemple `URLCoach`.

On peut imposer un nom avec `@Component("monCoach")`. Pour une méthode `@Bean`, le nom par défaut est celui de la méthode. Dans `SportConfig`, `@Bean("aquatic")` impose explicitement l’id `aquatic` à l’objet créé par `swimCoach()`. Le contrôleur utilise ce même id avec `@Qualifier("aquatic")`.

## 5. Quels sont les avantages et inconvénients de l’initialisation paresseuse ( @Lazy ) ? Pourquoi « à utiliser avec parcimonie » ?

`@Lazy` sur un bean retarde sa création jusqu’à ce qu’il soit demandé au conteneur. Cela peut accélérer le démarrage et éviter d’allouer les ressources de beans qui ne seront jamais utilisés. En revanche, la première utilisation peut être plus lente et certaines erreurs de configuration ne sont découvertes qu’à ce moment-là. Un démarrage réussi ne garantit donc plus que tous les beans pourront être créés : c’est pourquoi il faut l’utiliser avec parcimonie.

Dans le projet, `TennisCoach` porte `@Lazy` et le contrôleur utilise un autre coach. La capture `ex55-lazy.png` montre les constructeurs de Baseball, Cricket et Track au démarrage, sans celui de Tennis. Un bean lazy demandé directement par un singleton eager serait tout de même créé pour satisfaire cette dépendance au démarrage.

Le mode global `spring.main.lazy-initialization=true` est conservé dans le tag `ex55-global`, puis désactivé pour les exercices suivants. `ex55-global-before.png` montre un démarrage sans trace de constructeur de coach dans la portion visible. Cependant, `ex55-global-after.png` montre une réponse de cricket sous `ex54-priority`, pas une trace de création après appel dans `ex55-global`. La paire ne prouve donc pas complètement cette expérience ; je ne présente pas cette création différée comme un résultat vérifié.

## 6. Un endpoint /check renvoie true puis, après une modification, false . Qu’avez-vous changé, et qu’est-ce que cela prouve sur la portée du bean ?

Dans l’exercice 5.6, le constructeur reçoit deux fois `@Qualifier("cricketCoach") Coach`. Sans annotation de portée, ce bean est singleton : Spring fournit la même instance et la comparaison `myCoach == anotherCoach` vaut `true`.

En ajoutant `@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)` à `CricketCoach`, chaque résolution de cette dépendance crée une nouvelle instance : les deux références diffèrent et la comparaison vaut `false`. Les captures `ex56-singleton.png` et `ex56-prototype.png` montrent respectivement ces deux résultats et la sortie de `git describe` confirme les tags, même si l’invite du shell affiche encore l’étape précédente.

Le prototype signifie « une instance par demande au conteneur », pas automatiquement « une instance par appel HTTP ». Un contrôleur singleton conserve les instances injectées lors de sa construction. L’endpoint `/check` existe dans les tags `ex56-singleton`, `ex56-prototype` et `ex57`, mais plus dans la version finale `ex58`.

### Tableau comparatif des cinq scopes demandés

| Scope | Description | Durée / contexte |
|---|---|---|
| `singleton` (défaut) | Une instance par définition de bean et par conteneur Spring. | Partagée jusqu’à la fermeture du conteneur. |
| `prototype` | Une nouvelle instance à chaque demande au conteneur. | Spring initialise l’objet, puis le client gère sa fin de vie. |
| `request` | Une instance par requête HTTP. | Contexte web ; fin de la requête. |
| `session` | Une instance par session HTTP. | Contexte web ; fin ou expiration de la session. |
| `application` | Une instance par `ServletContext`. | Contexte web ; durée de l’application web. |

Les scopes web sont expliqués ici théoriquement ; ils ne sont pas implémentés ni testés dans ce projet.

## 7. Donnez l’ordre exact des étapes du cycle de vie d’un bean singleton. Pourquoi @PreDestroy n’est-il pas appelé pour un bean prototype ?

Pour les annotations et les beans simples du TP, l’ordre est :

1. Résolution des paramètres nécessaires et appel du constructeur ; les dépendances de constructeur sont fournies à ce moment-là.
2. Injection des éventuels champs et setters.
3. Exécution de la méthode `@PostConstruct`, après l’injection des dépendances.
4. Utilisation du bean initialisé.
5. Exécution de `@PreDestroy` lors de la fermeture normale du conteneur, avant la destruction du singleton.

Spring possède aussi d’autres callbacks et des post-processeurs ; la séquence ci-dessus décrit ceux mobilisés dans ce TP. Dans `CricketCoach`, les imports corrects sont `jakarta.annotation.PostConstruct` et `jakarta.annotation.PreDestroy`.

`ex57-startup.png` montre `In constructor: CricketCoach` puis `In doMyStartupStuff()`. `ex57-destroy.png` montre l’arrêt gracieux suivi de `In doMyCleanupStuff()`. Pour un prototype, Spring crée, injecte et initialise l’objet, mais ne suit pas ensuite sa destruction : il n’appelle donc pas automatiquement `@PreDestroy`. Le client doit gérer le nettoyage.

`ex57-prototype.png` montre deux constructions et deux initialisations de CricketCoach, mais pas l’arrêt de l’application. Elle ne suffit pas à démontrer l’absence de `@PreDestroy` à l’arrêt. Le tag `ex57` conserve bien la version singleton ; la variante prototype avec callbacks n’a pas de tag dédié.

## 8. Citez trois cas où l’on préfère @Bean (dans une @Configuration ) plutôt que @Component directement sur la classe.

On préfère `@Bean` dans les cas suivants :

1. La classe vient d’une bibliothèque externe et on ne peut pas lui ajouter `@Component`.
2. Sa construction nécessite une configuration particulière, une factory ou plusieurs opérations de préparation.
3. On veut déclarer plusieurs beans du même type, avec des paramètres et des noms différents.

`@Configuration` indique une classe de configuration Spring ; une méthode `@Bean` fournit un objet dont Spring gère ensuite la portée et le cycle de vie. Dans notre projet, `SwimCoach` n’a pas de `@Component` : `SportConfig` l’enregistre ainsi :

```java
@Bean("aquatic")
public Coach swimCoach() {
    return new SwimCoach();
}
```

Le `new` se trouve dans la configuration, pas dans le contrôleur. L’objet retourné devient un bean Spring. La capture `ex58-swimcoach.png` affiche `Swim 1000 meters as a warm up`, mais son invite indique encore `ex56-prototype` : elle prouve la réponse visible, pas à elle seule le tag exécuté. Lors de la finalisation, un démarrage réel du JAR de `main`, dont les sources sont identiques à `ex58`, a confirmé cette réponse avec HTTP 200. Le processus lancé pour cette vérification a ensuite été arrêté proprement.
