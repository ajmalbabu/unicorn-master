package com.unicorn.common.domain;

import java.io.Serializable;

public class PersistenceActorCreateCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    private String persistenceActorSpringBeanName;
    private String persistenceActorId;

    public PersistenceActorCreateCommand() {
    }

    public PersistenceActorCreateCommand(String persistenceActorSpringBeanName, String persistenceActorId) {
        this.persistenceActorSpringBeanName = persistenceActorSpringBeanName;
        this.persistenceActorId = persistenceActorId;
    }

    public String getPersistenceActorSpringBeanName() {
        return persistenceActorSpringBeanName;
    }

    public void setPersistenceActorSpringBeanName(String persistenceActorSpringBeanName) {
        this.persistenceActorSpringBeanName = persistenceActorSpringBeanName;
    }

    public String getPersistenceActorId() {
        return persistenceActorId;
    }

    public void setPersistenceActorId(String persistenceActorId) {
        this.persistenceActorId = persistenceActorId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersistenceActorCreateCommand that = (PersistenceActorCreateCommand) o;

        if (persistenceActorSpringBeanName != null ? !persistenceActorSpringBeanName.equals(that.persistenceActorSpringBeanName) : that.persistenceActorSpringBeanName != null)
            return false;
        return persistenceActorId != null ? persistenceActorId.equals(that.persistenceActorId) : that.persistenceActorId == null;

    }

    @Override
    public int hashCode() {
        int result = persistenceActorSpringBeanName != null ? persistenceActorSpringBeanName.hashCode() : 0;
        result = 31 * result + (persistenceActorId != null ? persistenceActorId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PersistenceActorCreateCommand{" +
                "persistenceActorSpringBeanName='" + persistenceActorSpringBeanName + '\'' +
                ", persistenceActorId='" + persistenceActorId + '\'' +
                '}';
    }
}
