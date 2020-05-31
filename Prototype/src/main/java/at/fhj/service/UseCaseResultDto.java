package at.fhj.service;

public class UseCaseResultDto {
    private String name;
    private long criteria;
    private long jpql;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCriteria() {
        return criteria;
    }

    public void setCriteria(long criteria) {
        this.criteria = criteria;
    }

    public long getJpql() {
        return jpql;
    }

    public void setJpql(long jpql) {
        this.jpql = jpql;
    }
}
