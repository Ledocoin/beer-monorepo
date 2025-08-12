package org.example.sideservicefbeerproj.component;

import lombok.NonNull;
import org.example.sideservicefbeerproj.entity.EmployeeEntity;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

@Component
public class EmployeeEntityCallBack implements BeforeConvertCallback<EmployeeEntity> {

    @Override
    public EmployeeEntity onBeforeConvert(@NonNull EmployeeEntity entity,@NonNull String collection) {
        if(entity.getId() == null || entity.getId().isBlank()){
            entity.setId(IdGenerator.generate());
        }
        return entity;
    }
}
