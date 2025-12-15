package kernel.bo;

import java.io.Serializable;
import java.util.Date;

import org.springframework.util.ClassUtils;

/**
 * 实体抽象类
 * 
 */
public class EntityObject<PK> implements Serializable {

    private static final long serialVersionUID = -6624393812017741464L;

    private PK id;

    private int entityVersion;
    
    private Date timestamp;

    public PK getId() {
        return id;
    }

    public void setId(PK id) {
        this.id = id;
    }

    public int getEntityVersion() {
        return entityVersion;
    }

    public void setEntityVersion(int entityVersion) {
        this.entityVersion = entityVersion;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    // @Override
    public String toString() {
        return ClassUtils.getShortName(getClass()) + ": id=" + getId();
    }

    /**
     * Attempt to establish identity based on id if both exist. If either id
     * does not exist use Object.equals().
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof EntityObject)) {
            return false;
        }
        // 即便是继承关系，由于数据逻辑存在依赖关系，必须是同class才行
        if(!this.getClass().equals(other.getClass())){
            return false;
        }
        EntityObject entity = (EntityObject) other;
        if (id == null || entity.getId() == null) {
            return false;
        }
        return id.equals(entity.getId());
    }

    /**
     * Use ID if it exists to establish hash code, otherwise fall back to
     * Object.hashCode(). Based on the same information as equals, so if that
     * changes, this will. N.B. this follows the contract of Object.hashCode(),
     * but will cause problems for anyone adding an unsaved {@link Entity} to a
     * Set because Set.contains() will almost certainly return false for the
     * {@link Entity} after it is saved. Spring Batch does not store any of its
     * entities in Sets as a matter of course, so internally this is consistent.
     * Clients should not be exposed to unsaved entities.
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        if (id == null) {
            return super.hashCode();
        }
        return 39 + 29 * getClass().hashCode() + 87 * id.hashCode();
    }

}
