package gitBackuper.Models;

public class Repository {
    private String name;
    private String fullName;

    public Repository(String name, String fullName) {
        this.name = name;
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name).append("\t");
        sb.append("Full name: ").append(fullName);

        return sb.toString();
    }
}
