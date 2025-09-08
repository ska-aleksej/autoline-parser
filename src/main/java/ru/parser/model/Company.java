package ru.parser.model;

public class Company {
    private String name;
    private String link;
    private CompanyDetails details;

    public Company(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public CompanyDetails getDetails() {
        return details;
    }

    public void setDetails(CompanyDetails details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "Company{" +
                "name='" + name + '\'' +
                ", link='" + link + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return link.equals(company.link);
    }

    @Override
    public int hashCode() {
        return link.hashCode();
    }
}
