package ua.edu.chdtu.deanoffice.api.general.mapper.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

public class SetParameterizedType implements ParameterizedType {

    private Type type;

    public SetParameterizedType(Type type) {
        this.type = type;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return new Type[]{type};
    }

    @Override
    public Type getRawType() {
        return Set.class;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
