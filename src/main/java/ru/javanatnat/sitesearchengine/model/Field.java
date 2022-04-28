package ru.javanatnat.sitesearchengine.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("t_field")
public class Field {
    @Id
    private final Long id;
    private final String name;
    private final String selector;
    private final BigDecimal weight;

    @PersistenceConstructor
    public Field(Long id, String name, String selector, BigDecimal weight) {
        this.id = id;
        this.name = name;
        this.selector = selector;
        this.weight = weight;
    }

    public Field(String name, String selector, BigDecimal weight) {
        this(null, name, selector, weight);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSelector() {
        return selector;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Field{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", selector='" + selector + '\'' +
                ", weight=" + weight +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (! (o instanceof Field field)) {
            return false;
        }

        return selector.equals(field.selector);
    }

    @Override
    public int hashCode() {
        return selector.hashCode();
    }
}
