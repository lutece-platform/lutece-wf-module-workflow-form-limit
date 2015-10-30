# lutece-wf-module-workflow-limit

Module-workflow-limit est une tâche du workflow qui peut 
être configurée à s'exécuter automatiquement|manuellement.

Cette tâche fonctionne avec les plugins : 
-- plugin-workflow
-- plugin-form
-- plugin-directory
-- module-form-exportdirectory
La tâche défins une valeur entière : Quota Max.

Fonctionnel : 
- en back office, la tâche est configurée avec une valeur entière : Quota Max.
- Un utilisateur en front-office peut demander une réservation sur le quota disponible via le formulaire associé au workflow.
- Le nombre de quota sera alors décrémenté (tâche en exécution automatique ou manuelle) au fur et à mesure que les utilisateurs front-office font des réservations.
- Lorsque le nombre Quota Max est atteint, alors le formulaire associé au workflow est désactivé.
- L'administrateur de la tâche peut modifier le quota de la configuration et dans ce cas le nombre de quota disponible sera la somme des quotas déjà alloués additionné à la nouvelle valeur définie.