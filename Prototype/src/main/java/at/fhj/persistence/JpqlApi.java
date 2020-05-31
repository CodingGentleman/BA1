package at.fhj.persistence;

import java.util.List;

public interface JpqlApi {
    <T> List<T> getResultList(Class<T> clazz, String hql, Object...  params);
    <T> T getSingleResult(Class<T> clazz, String hql, Object...  params);
}
