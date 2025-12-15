package project.mall.activity.core;

import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/**
 * @author caster
 */
public class ActivityHandlerAssignableTypeFilter extends AbstractTypeHierarchyTraversingFilter {
    private final Class<?> targetType;


    /**
     * Create a new AssignableTypeFilter for the given type.
     * @param targetType the type to match
     */
    public ActivityHandlerAssignableTypeFilter(Class<?> targetType) {
        super(true, true);
        this.targetType = targetType;
    }

    /**
     * Return the {@code type} that this instance is using to filter candidates.
     * @since 5.0
     */
    public final Class<?> getTargetType() {
        return this.targetType;
    }

    @Override
    protected boolean matchClassName(String className) {
        if (this.targetType.getName().equals(className)) {
			// 不支持将接口类设置为 bean
        	return false;
		}

		return matchTargetType(className);
    }

    @Override
    @Nullable
    protected Boolean matchSuperClass(String superClassName) {
        return matchTargetType(superClassName);
    }

    @Override
    @Nullable
    protected Boolean matchInterface(String interfaceName) {
        return matchTargetType(interfaceName);
    }

    @Nullable
    protected Boolean matchTargetType(String typeName) {
        if (this.targetType.getName().equals(typeName)) {
        	// 剔除接口类
            return false;
        } else if (Object.class.getName().equals(typeName)) {
            return false;
        } else if (typeName.startsWith("project.mall.activity")) {
            try {
                Class<?> clazz = ClassUtils.forName(typeName, getClass().getClassLoader());
                boolean isMatch = this.targetType.isAssignableFrom(clazz);
                return isMatch;
            }
            catch (Throwable ex) {
                // Class not regularly loadable - can't determine a match that way.
            }
        }
        return false;
    }

}
