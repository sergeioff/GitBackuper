package gitBackuper.ui;

import gitBackuper.api.GitHub;
import gitBackuper.api.models.Repository;
import gitBackuper.api.models.User;
import gitBackuper.models.Backup;
import gitBackuper.models.BackupsList;
import gitBackuper.utils.BackupService;
import gitBackuper.utils.TokenLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides console user interface.
 * Interacts with user.
 */
public class ConsoleUserInterface {
    private final String INVALID_OPTION_MESSAGE = "Invalid option! Please try again.";
    private final String NO_AVAILABLE_BACKUPS = "There are no available backups.";

    private final BufferedReader reader;
    private final GitHub gitHubApi;
    private final BackupService backupService;
    private User currentUser;

    /**
     * Initialisation of GitHub api, BackupService and keyboard reader.
     */
    public ConsoleUserInterface() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        String token = TokenLoader.loadToken();
        gitHubApi = new GitHub(token);
        backupService = new BackupService();

        if (token != null) {
            try {
                currentUser = gitHubApi.getCurrentUser();
            } catch (IOException e) {
                currentUser = null;
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Main menu of program. Shows UI. Interacts with user input.
     */
    public void showUI() {
        if (currentUser != null) {
            System.out.println("Hello, " + currentUser.getName());
        }

        String selectedAction;
        do {
            System.out.println("What would you like to do:");
            System.out.println("1) make backup of remote repository");
            System.out.println("2) show existing backups");
            System.out.println("3) remove existing backups of repository");
            System.out.println("4) restore files from backup");
            System.out.println("5) remove all backups");
            System.out.println("6) exit");
            System.out.println("Your choice: ");

            selectedAction = "6";
            try {
                selectedAction = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }

            switch (selectedAction) {
                case "1":
                    try {
                        makeBackupOfRepository();
                    } catch (IOException e) {
                        System.out.println("Failed to make a repository backup. Cause: " + e.getMessage());
                    }
                    break;
                case "2":
                    showExistingBackups();
                    break;
                case "3":
                    try {
                        removeExistingBackupsOfRepository();
                    } catch (IOException e) {
                        System.out.println("Failed to remove existing backups of repository. Cause: " + e.getMessage());
                    }
                    break;
                case "4":
                    try {
                        assemblyBackup();
                    } catch (IOException e) {
                        System.out.println("Failed to restore backup. Cause: " + e.getMessage());
                    }
                    break;
                case "5":
                    try {
                        removeAllBackups();
                    } catch (IOException e) {
                        System.out.println("Failed to remove all backups. Cause: " + e.getMessage());
                    }
                    break;
                case "6":
                    System.out.println("Bye.");
                    break;
                default:
                    System.out.println(INVALID_OPTION_MESSAGE);
            }
        } while (!"6".equals(selectedAction));
    }

    /**
     * Makes backup of repository. Provides to user availability to select repository for backup.
     * @throws IOException if failed to make backup of repository.
     */
    private void makeBackupOfRepository() throws IOException {
        Repository selectedRepository = selectRepositoryToBackup();

        if (selectedRepository == null) {
            System.out.println("Failed to select repository!");
            return;
        }

        if (backupService.isBackupExists(selectedRepository)) {
            System.out.println("Backup already exists!");
            return;
        }

        System.out.println("Making backup...");
        backupService.makeBackupOfRepository(selectedRepository);
        System.out.println("Backup done!");
    }

    /**
     * Shows existing backups to user.
     */
    private void showExistingBackups() {
        BackupsList backupsList = backupService.getBackups();
        Map<String, List<Backup>> backups = backupsList.getBackups();

        if (backups.size() < 1) {
            System.out.println(NO_AVAILABLE_BACKUPS);
            return;
        }

        System.out.println("=== Existing backups ===");
        for (Map.Entry<String, List<Backup>> entry : backups.entrySet()) {
            System.out.println("<- " + entry.getKey() + " ->");

            for (Backup backup : entry.getValue()) {
                System.out.println("\t" + backup.getPushedAt());
            }
        }
    }

    /**
     * Assembles files from backup. Provides to user availability to select repository for assemble.
     * @throws IOException if failed to assembly backup.
     */
    private void assemblyBackup() throws IOException {
        Map<String, List<Backup>> allBackups = backupService.getBackups().getBackups();

        if (allBackups.size() < 1) {
            System.out.println(NO_AVAILABLE_BACKUPS);
            return;
        }

        List<String> backupedRepositories = new ArrayList<>(allBackups.keySet());

        System.out.println("Backuped repositories:");
        for (int i = 0; i < backupedRepositories.size(); i++) {
            System.out.printf("%d) %s\n", i + 1, backupedRepositories.get(i));
        }

        try {
            int selectedRepositoryIdx = Integer.parseInt(reader.readLine());
            selectedRepositoryIdx--;

            String selectedRepositoryName = backupedRepositories.get(selectedRepositoryIdx);

            List<Backup> backupsOfRepository = allBackups.get(selectedRepositoryName);

            System.out.println("Select backup to restore:");

            for (int i = 0; i < backupsOfRepository.size(); i++) {
                System.out.printf("%d) %s\n", i + 1, backupsOfRepository.get(i).getPushedAt());
            }

            System.out.print("Your choice: ");

            int selectedBackupIdx = Integer.parseInt(reader.readLine());
            selectedBackupIdx--;

            Backup backup = backupsOfRepository.get(selectedBackupIdx);

            System.out.println("Assembling...");
            backupService.assemblyBackup(backup);
            System.out.println("Done.");
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println(INVALID_OPTION_MESSAGE);
        }

    }

    /**
     * Removes all existing backups of repository. Provides to user availability to select repository.
     * @throws IOException if failed to remove backups.
     */
    private void removeExistingBackupsOfRepository() throws IOException {
        Set<String> setOfRepositoryNames = backupService.getBackups().getBackups().keySet();
        List<String> repositoryNames = new ArrayList<>(setOfRepositoryNames);

        if (repositoryNames.size() < 1) {
            System.out.println(NO_AVAILABLE_BACKUPS);
            return;
        }

        System.out.println("=== Removing backups of repository ===");

        System.out.println("Select repository:");
        for (int i = 0; i < repositoryNames.size(); i++) {
            System.out.printf("%d) %s\n", i + 1, repositoryNames.get(i));
        }

        System.out.println("0) cancel");

        try {
            int repositoryToRemoveIdx = Integer.parseInt(reader.readLine());

            if (repositoryToRemoveIdx == 0) {
                return;
            }

            repositoryToRemoveIdx--;

            String repositoryToRemoveName = repositoryNames.get(repositoryToRemoveIdx);
            System.out.println("Removing...");
            backupService.removeRepositoryBackups(repositoryToRemoveName);
            System.out.println("Done");
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println(INVALID_OPTION_MESSAGE);
            removeExistingBackupsOfRepository();
        }
    }

    /**
     * Removes all existing backups.
     * @throws IOException if failed to remove backups.
     */
    private void removeAllBackups() throws IOException {
        if (backupService.getBackups().getBackups().size() < 1) {
            System.out.println(NO_AVAILABLE_BACKUPS);
            return;
        }

        System.out.println("Removing...");
        backupService.removeAllBackups();
        System.out.println("Done.");
    }

    /**
     * Interacts with user. Provides repository selection.
     * @return selected repository
     * @throws IOException if failed to get repository over internet.
     */
    private Repository selectRepositoryToBackup() throws IOException {
        if (currentUser != null) {
            System.out.println("0) select from repositories of " + currentUser.getName());
        }
        System.out.println("1) select from repositories by username");
        System.out.println("2) select repository by url");
        System.out.println("3) cancel");

        String selectedOption = reader.readLine();

        String username = null;
        switch (selectedOption) {
            case "0":
                username = currentUser.getLogin();
            case "1":
                if (username == null) {
                    System.out.print("Enter username: ");
                    username = reader.readLine();
                }

                List<Repository> repositories;

                try {
                    repositories = gitHubApi.getUserRepositoriesWithoutFiles(username);
                } catch (IOException e) {
                    System.out.println("Failed to get repositories of specified user!");
                    return selectRepositoryToBackup();
                }

                if (repositories == null) {
                    throw new IllegalStateException();
                }

                if (repositories.size() < 1) {
                    System.out.println(username + " doesn't have repositories.");
                    return selectRepositoryToBackup();
                }

                System.out.println("List of available repositories:");
                for (int i = 0; i < repositories.size(); i++) {
                    System.out.printf("%d) %s\n", i + 1, repositories.get(i).getName());
                }

                System.out.print("Select repository: ");
                try {
                    int selectedRepositoryIdx = Integer.parseInt(reader.readLine());
                    selectedRepositoryIdx--;
                    Repository repo = repositories.get(selectedRepositoryIdx);
                    return gitHubApi.getRepository(username, repo.getName());
                } catch (IndexOutOfBoundsException | NumberFormatException e) {
                    System.out.println(INVALID_OPTION_MESSAGE);
                    return selectRepositoryToBackup();
                }
            case "2":
                System.out.print("Enter url of repository: ");
                String repositoryUrl = reader.readLine();
                try {
                    return gitHubApi.getRepository(repositoryUrl);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid url! Please try again.");
                    return selectRepositoryToBackup();
                }
            case "3":
                return null;
            default:
                System.out.println(INVALID_OPTION_MESSAGE);
                return selectRepositoryToBackup();
        }
    }
}
